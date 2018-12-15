package util;

import model.TopicEntry;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace05;
import util.helper.EntrySearcher;
import util.helper.SpaceSearcher;
import util.helper.TransactionBuilder;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.List;

/**
 *
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
 */
public class TopicUtils
{
    private JavaSpace05 space = SpaceUtils.getSpace();
    private SpaceSearcher searcher = SpaceSearcher.getSpaceSearcher();
    private EntrySearcher e_searcher = new EntrySearcher();
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
        Transaction transaction = TransactionBuilder.getTransaction();
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
                }
                else
                {
                    // topic exists
                    System.err.println("Error: Topic already exists!");
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

    private boolean topicExists(TopicEntry topic, Transaction transaction)
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

    /**
     * Retrieves all of the TopicEntry objects in the space
     *
     * @return A arrayList of TopicEntry objects.
     */
    public List<TopicEntry> getTopics()
    {
        return e_searcher.readAllMatchingEntries(space, new TopicEntry());
    }


}
