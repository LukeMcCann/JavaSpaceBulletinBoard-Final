package _testCase;

import model.DummyUserInTopic;
import model.PostEntry;
import model.TopicEntry;
import model.UserEntry;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace05;
import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import util.PostUtils;
import util.SpaceUtils;
import util.TopicUtils;
import util.UserUtils;
import util.helper.EntrySearcher;
import java.util.List;

import static org.junit.Assert.*;

public class TopicUtilsTest
{
    private UserEntry user;
    private TopicEntry topic;
    private PostEntry post;
    private PostUtils postUtils;
    private TopicUtils topicUtils;
    private UserUtils userUtils;

    private List<Lease> testList;
    private EntrySearcher e_searcher;

    private JavaSpace05 space = SpaceUtils.getSpace();
    private String random;

    @Before
    public void setup()
    {

        user = new UserEntry("$$$", "$$$");
        topic = new TopicEntry("$$$", user);
        post = new PostEntry(user, topic, "Test");
        random = RandomStringUtils.randomAlphabetic(100);
    }

    @org.junit.Test
    public void removeAllFromTopic()
    {
        TopicEntry marvel = new TopicEntry(random, user);
        UserEntry user1 = new UserEntry(random + "Thor", random + "1");
        UserEntry user2 = new UserEntry(random + "Hulk", random + "2");
        UserEntry user3 = new UserEntry(random + "Hawk-eye", random + "3");
        UserEntry user4 = new UserEntry(random + "Iron Man", random + "4");
        UserEntry user5 = new UserEntry(random + "Venom", random + "5");
        UserEntry user6 = new UserEntry(random + "Spiderman", random + "6");

        try
        {
            testList.add(userUtils.createTestUser(user1));
            testList.add(userUtils.createTestUser(user2));
            testList.add(userUtils.createTestUser(user3));
            testList.add(userUtils.createTestUser(user4));
            testList.add(userUtils.createTestUser(user5));
            testList.add(userUtils.createTestUser(user6));

            testList.add(topicUtils.createTopic(topic));

            testList.add(topicUtils.addDebugTopicUser(topic, user1));
            testList.add(topicUtils.addDebugTopicUser(topic, user2));
            testList.add(topicUtils.addDebugTopicUser(topic, user3));
            testList.add(topicUtils.addDebugTopicUser(topic, user4));
            testList.add(topicUtils.addDebugTopicUser(topic, user5));
            testList.add(topicUtils.addDebugTopicUser(topic, user6));

            topicUtils.removeAllFromTopic(marvel, null);
            List<DummyUserInTopic> users = topicUtils.getAllUsersFromTopic(marvel);

            assertEquals(0, users.size());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    @org.junit.Test
    public void addTestTopic()
    {
        try
        {
            topicUtils.addTestTopic(topic);
            List<TopicEntry> topics = topicUtils.getTopics();
            assertEquals(1, topics.size());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            e.getMessage();
        }
    }

    // Deleting topic removes all messages
    @Test
    public void testDeletingTopicRemovesMessages()
    {
        TopicEntry topic = new TopicEntry(random, user);
        post.setContent("HelloWorld");
        post.setAuthor(user);
        post.setRecipient(user);
        post.generateTimeStamp();

        try {
            testList.add(userUtils.createTestUser(user));
            testList.add(topicUtils.createTopic(topic));

            int postsToPrint = 25;
            for (int i = 0; i < postsToPrint; i++)
            {
                testList.add(postUtils.sendPublicMessage(post));
            }
            assertEquals("Posts did not send.", postsToPrint,
                    e_searcher.readAllMatchingEntries(space, post).size());

            topicUtils.delete(topic, user);

            assertEquals("Posts persisted after topic deletion.", 0,
                    e_searcher.readAllMatchingEntries(space, post).size());

        }
        catch (AssertionError | Exception e)
        {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    // Test getting all TopicUsers
    @Test
    public void testGetAllTopicUsers()
    {

        TopicEntry marvel = new TopicEntry(random, user);
        UserEntry user1 = new UserEntry(random + "Thor", random + "1");
        UserEntry user2 = new UserEntry(random + "Hulk", random + "2");
        UserEntry user3 = new UserEntry(random + "Hawk-eye", random + "3");
        UserEntry user4 = new UserEntry(random + "Iron Man", random + "4");
        UserEntry user5 = new UserEntry(random + "Venom", random + "5");
        UserEntry user6 = new UserEntry(random + "Spiderman", random + "6");


        try
        {
            System.out.println("\n----------------------\n");
            System.out.println("User: " + user1.getUsername());
            System.out.println("User: " + user2.getUsername());
            System.out.println("User: " + user3.getUsername());
            System.out.println("User: " + user4.getUsername());
            System.out.println("User: " + user5.getUsername());
            System.out.println("User: " + user6.getUsername());

            testList.add(userUtils.createTestUser(user1));
            testList.add(userUtils.createTestUser(user2));
            testList.add(userUtils.createTestUser(user3));
            testList.add(userUtils.createTestUser(user4));
            testList.add(userUtils.createTestUser(user5));
            testList.add(userUtils.createTestUser(user6));

            testList.add(topicUtils.createTopic(marvel));

            testList.add(topicUtils.addDebugTopicUser(marvel, user1));
            testList.add(topicUtils.addDebugTopicUser(marvel, user2));
            testList.add(topicUtils.addDebugTopicUser(marvel, user3));
            testList.add(topicUtils.addDebugTopicUser(marvel, user4));
            testList.add(topicUtils.addDebugTopicUser(marvel, user5));
            testList.add(topicUtils.addDebugTopicUser(marvel, user6));

            assertEquals("Failed to add users.", 6,
                    e_searcher.readAllMatchingEntries(
                            space, new DummyUserInTopic(topic,
                                    null)).size());

            assertEquals(
                    "Failed to get all users in topic", 6,
                    topicUtils.getAllUsersFromTopic(topic).size());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }
}
