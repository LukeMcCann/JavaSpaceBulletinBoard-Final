package _testCase;

import model.PostEntry;
import model.TopicEntry;
import model.UserEntry;
import net.jini.core.lease.Lease;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import util.PostUtils;
import util.TopicUtils;
import util.UserUtils;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PostUtilsTest
{
    private List<Lease> testList;
    private PostUtils postUtils = PostUtils.getPostUtils();
    private TopicUtils topicUtils = TopicUtils.getTopicUtils();
    private UserUtils userUtils = UserUtils.getUserutils();

    private UserEntry user;
    private TopicEntry topic;
    private PostEntry post;
    private String random;

    @Before
    public void setup()
    {
        user = new UserEntry("$$$", "$$$");
        topic = new TopicEntry("$$$", user);
        post = new PostEntry(user, topic, "Test");
        random = RandomStringUtils.randomAlphabetic(100);
    }
    @Test
    public void deleteAllPosts() {
    }

    @Test
    public void checkNonExistantTopicMessagesFail()
    {
        try
        {
            testList.add(postUtils.sendPublicMessage(post));
        }
        catch (Exception e) {
                fail();
                System.out.println("This should fails");
        }
    }

    @Test
    public void testMessageToUserOutsideTopicFails()
    {
        UserEntry recipient = new UserEntry("^^^", "^^^");

        boolean exceptionThrown = false;

        try {
            testList.add(topicUtils.createTopic(topic));

            testList.add(userUtils.createTestUser(user));
            testList.add(userUtils.createTestUser(recipient));
            testList.add(topicUtils.addDebugTopicUser(topic, user));

            post = new PostEntry(user, recipient, topic, "test message");
        }
        catch (Exception e)
        {
            fail("Failed.");

            return;
        }

        try
        {
            postUtils.sendPublicMessage(post);
        }
        catch (Exception e)
        {
            exceptionThrown = true;

            List<PostEntry> messagesForToUser = postUtils.getAllUsersPosts(user, topic);
            assertEquals(0, messagesForToUser.size());
        }

        assertTrue(exceptionThrown);
    }
}
