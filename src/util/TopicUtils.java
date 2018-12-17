package util;

import model.*;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace05;
import util.helper.EntrySearcher;
import util.helper.TransactionBuilder;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

/**
 * @Author Luke McCann
 * @UniversityNumber U1364096
 * @University The University of Huddersfield
 *
 * @References https://books.google.co.uk/
 *              books?id=OhRy5xcJYbMC&pg=PA216&lpg=PA216&dq=javaspaces+l
 *                      ease.absolute&source=bl&ots=
 *                          2PZAH3YnZK&sig=MUl7tf9nHPGH7scmZZ
 *                              nOA3wloxM&hl=en&sa=X&ved=2ahUKEwjX8Zycv
 *                                  6DfAhU0UhUIHTIvDP8Q6AEwAHoECAQQAQ#v=onepage&q=
 *                                      javaspaces%20lease.absolute&f=false
 *
 *              https://docs.oracle.com/javase/7/docs/
 *              api/java/rmi/RemoteException.html
 *
 * TopicUtils -
 *      utility class for handling topic-specific logic
 */
public class TopicUtils
{
    private JavaSpace05 space = SpaceUtils.getSpace();
    private EntrySearcher e_searcher = new EntrySearcher();
    private static PostUtils postUtils = PostUtils.getPostUtils();

    private static TopicUtils topicUtils;
    private static final long DEFAULT_TOPIC_LEASE = Lease.FOREVER;

    private TopicUtils() {}

    public static TopicUtils getTopicUtils()
    {
        if(topicUtils != null) return topicUtils;
        return topicUtils = new TopicUtils();
    }

    /**
     * Writes a TopicEntry object into space
     *
     * @param topic - topic to be added
     * @return <code>true</code> if successful
     *          else <code>false</code>
     */
    public Lease createTopic(TopicEntry topic)
    {
        Transaction transaction = TransactionBuilder.getTransaction(3000);
        Lease success = null;

        try
        {
            if(topic.getTitle() != null &&
                    topic.getOwner() != null &&
                    topic.getID() != null)
            {
                if(!topicExists(topic, transaction))
                {
                    success = space.write(topic, transaction, DEFAULT_TOPIC_LEASE);
                    transaction.commit();

                   // System.out.println(topic.getTitle());
                }
                else
                {
                    // topic exists
                    System.err.println("Error: " + topic.getTitle() +  " already exists!");
                    JOptionPane.showMessageDialog(null,
                            topic.getTitle() + " already exists.");
                }
            }
            else
            {
                System.err.println("Error: " +
                        topic.getTitle() +
                        " contains null fields!");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            try
            {
                transaction.abort();
            }
            catch(Exception x)
            {
                x.printStackTrace();
                System.err.println("Error: " + x.getMessage());
            }

        }

        return success;
    }


    // Registration

    /**
     * Adds a user to a topic using the DummyUserInTopic model.
     *
     * @param user - the user to add
     * @param topic - the topic to join
     */
    public void registerUserTo(UserEntry user, TopicEntry topic)
    {
        DummyUserInTopic userInTopic =
                new DummyUserInTopic(topic, user);

        try
        {
            Transaction transaction = TransactionBuilder.getTransaction();
            if(space.readIfExists(userInTopic, transaction, 3000) == null)
            {
                // dummy user does not exist in space
                space.write(userInTopic, transaction, Lease.FOREVER);
            }
            transaction.commit();
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,
                    "Failed to join " + user.getUsername() + " to " + topic.getTitle(),
                    "Failure", JOptionPane.ERROR_MESSAGE);

            System.err.println("Error: " + e.getMessage());
            e.getMessage();
        }
    }


    // Deletion

    /**
     * Deletes a TopicEntry checking if the user requesting deletion owns it
     * if they do not, notifies the user they cannot delete it.
     *
     * @param topic
     * @param user
     */
    public void delete(TopicEntry topic, UserEntry user)
    {
        if(topic.getOwner().equals(user))
        {
            // the user is the topic owner
            if(topic.getID() != null &&
                    topic.getOwner()!= null &&
                    topic.getTitle() != null)
            {
                try
                {
                    Transaction transaction =
                            TransactionBuilder.getTransaction();

                    space.takeIfExists(topic, transaction, 3000);

                    // delete all users in topic
                    removeAllFromTopic(topic, transaction);

                    // delete all posts
                    postUtils.deleteAllPosts(topic, transaction);

                    transaction.commit();

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }


    // Getters

    /**
     * Get all posts for a specific user in a specific topic
     *
     * @param topic - topic to search
     * @param user - user to find posts for
     * @return a list of all the users posts
     */
    public List<PostEntry> getAllPostsForUserInTopic(TopicEntry topic, UserEntry user)
    {
        List<PostEntry> postCollection =
                e_searcher.readAllMatchingEntries(space, new PostEntry(topic));

        Iterator<PostEntry> i = postCollection.iterator();

        while(i.hasNext())
        {
            PostEntry post = i.next();
            if(post.getRecipient() != null &&
                    (!post.getRecipient().getUsername().equals(user.getUsername()) &&
                            !post.getAuthor().getUsername().equals(user.getUsername())))
            {
                i.remove();
            }
        }
        return postCollection;
    }

    /**
     * Retrieves all of the TopicEntry objects in the space
     *
     * @return A arrayList of TopicEntry objects.
     */
    public List<TopicEntry> getTopics()
    {
        return e_searcher.readAllMatchingEntries(space, new TopicEntry());
    }

    /**
     * Retrieves a topic by its title
     *
     * @param title
     * @return
     */
    public TopicEntry getTopicByTitle(String title)
    {
        TopicEntry template = new TopicEntry();
        template.setTitle(title);

        try
        {
            template = (TopicEntry)
                    space.readIfExists(template, null, 1000);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return template;
    }


    // Removal Methods

    /**
     * Removes a user from a topic safely and iteratively
     *
     * @param user - user to remove
     * @param topic - topic to remove user from
     */
    public void removeUserFrom(UserEntry user, TopicEntry topic)
    {
        Transaction transaction = TransactionBuilder.getTransaction();
        boolean inTopic = true;
        DummyUserInTopic template = new DummyUserInTopic(topic, user);

        try
        {
            // remove all copies
          //  original solution, works but not as effective if multiple copies
            // e_searcher.takeAllMatchingEntries(space, transaction, template);

            while(space.takeIfExists(template, transaction, 3000) != null)
            {
               // space.takeIfExists(template,transaction 3000);
                inTopic = false;
            }

            if(!inTopic)
            {
                DummyUserRemoved removedUser =
                        new DummyUserRemoved(template.getUser(),
                                template.getTopic());

                // write template to space for listener (3min)
                space.write(removedUser, transaction, 3000*60);
            }
            transaction.commit();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Removes all users from a topic
     *
     * @param topic - the topic to remove from
     * @param transaction - the transaction to use
     *
     * @Reference JavaSpaces: Prinicples
     */
    public void removeAllFromTopic(TopicEntry topic, Transaction transaction)
    {
        DummyUserInTopic template = new DummyUserInTopic();

        try
        {
            while(space.readIfExists(template, transaction, 3000) != null)
            {
                space.takeIfExists(template, transaction, 3000);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    // Getters

    /**
     * Get a list of all the users currently in a topic
     *
     * @param topic - topic to search in
     *
     * @return List of all users in that topic
     */
    public List<DummyUserInTopic> getAllUsersFromTopic(TopicEntry topic)
    {
        return e_searcher.readAllMatchingEntries(space, new DummyUserInTopic(topic));
    }


    // Utility Methods

    /**
     * Checks whether or nto a TopicEntry exists in the space.
     *
     * @param topic - the topic to find
     * @param transaction - the transaction to utilise
     *
     * @return <code>true</code> if exists, else <code>false</code>
     */
    public boolean topicExists(TopicEntry topic, Transaction transaction)
    {
        TopicEntry template = new TopicEntry();
        template.setTitle(topic.getTitle());
        try
        {
            TopicEntry titlesMatch = (TopicEntry)
                    space.readIfExists(template, transaction, 3000);

            template = new TopicEntry(); // reset template
            template.setID(topic.getID());

            TopicEntry idsMatch = (TopicEntry)
                    space.readIfExists(template, transaction, 3000);

            if(idsMatch == null ||
                    titlesMatch == null)
            {
                return false;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        }
        return true;
    }


    // Test Methods

    /**
     * Adds a topic to the space for three minutes
     * for testing purposes
     *
     * @param topic - topic to add
     * @return <code>true</code> if successful, else <code>false</code>
     * @throws RemoteException
     * @throws TransactionException
     */
    public Lease addTestTopic(TopicEntry topic) throws RemoteException, TransactionException
    {int THREE_MINUTES = 1000*3; return space.write(topic, null, THREE_MINUTES);}

    /**
     * Adds a DummyUserInTopic object to the space for 9 minutes for testing purposes.
     *
     * @param topic - the topic to add
     * @param user - the user to add
     *
     * @return null if not successful
     * @throws RemoteException
     * @throws TransactionException
     */
    public Lease addDebugTopicUser(TopicEntry topic, UserEntry user) throws RemoteException, TransactionException
    {return space.write(new DummyUserInTopic(topic, user), null, 1000*3*60);}


}
