package util;

import controller.TopicController;
import model.DummyUserInTopic;
import model.DummyUserRemoved;
import model.TopicEntry;
import model.UserEntry;
import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.JavaSpace05;
import util.helper.SpaceSearcher;
import util.helper.TransactionBuilder;
import util.listener.UserAddedListener;
import util.listener.UserRemovedListener;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * @Author Luke McCann
 * @UniversityNumber U1364096
 * @University The University of Huddersfield
 *
 *
 * @References https://river.apache.org/release-doc/
 *              3.0.0/api/net/jini/space/JavaSpace05.html
 *
 *              JavaSpaces: Principles, Patterns and Practice
 *              David Gelernter
 *
 *              https://river.apache.org/release-doc/3.0.0/api/net/jini/space/JavaSpace.html#notify-net.jini.core.entry.Entry-net.jini.core.
 *              transaction.Transaction-net.jini.core.event.RemoteEventListener-long-java.rmi.
 *              MarshalledObject-
 *
 *              https://river.apache.org/release-doc/3.0.0/api/net/jini/jeri/BasicJeriExporter.html
 *
 *
 * UserUtils -
 *        Utility class for handling user-specific logic
 */
public class UserUtils
{
    private static UserUtils userutils;

    private static SpaceSearcher searcher = SpaceSearcher.getSpaceSearcher();
    private static final JavaSpace05 space = SpaceUtils.getSpace();

    private static final long ONE_MONTH = 2629746000l; // int not accurate enough

    private RemoteEventListener userAddedListener;
    private RemoteEventListener userRemovedListener;

    private EventRegistration userAddedRegister;
    private EventRegistration userRemovedRegister;

    // No arg constructor
    private  UserUtils() {}

    // Convenience getter
    public static UserUtils getUserutils()
    {
        if(userutils != null) return userutils;
        return userutils = new UserUtils();
    }


    // User Registration

    /**
     * Writes a new userEntry to the space
     * @param user
     * @return <code>true</code> if successful, else
     *          <code>false</code>
     */
    public Lease createUser(UserEntry user)
    {
        Lease success = null;
        Transaction transaction =
                TransactionBuilder.getTransaction();

        try
        {
            // check if user is in space
            if(searcher.getUserBySecureUsername(user.getSecureUsername(),
                    transaction) == null)
            {
                // write to space
                success = space.write(user, transaction, ONE_MONTH);
                transaction.commit();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * Renews a users lease.
     *
     * @param user - the user whose lease to renew.
     * @return null if fails - testing purposes
     */
    public Lease renewUserLease(UserEntry user)
    {
        Lease success = null;
        if(user.getUsername() != null &&
                user.getSecureUsername() != null)
        {
            Transaction transaction = TransactionBuilder.getTransaction();

            UserEntry spaceUser =
                    searcher.getUserByUsername(user.getUsername(), transaction);

            if(spaceUser != null)
            {
                try
                {
                    // take from space
                    space.takeIfExists(spaceUser, transaction, 1000);
                    // write back with new lease
                    space.write(user, transaction, ONE_MONTH);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }


    // Notifications

    /**
     * Listens for users being added to a topic
     *
     * @param topic - the topic to listen on
     *
     * @References JavaSpaces: Principles, Patterns and Practice
     *              David Gelernter
     *
     *              https://river.apache.org/release-doc/3.0.0/api/net/jini/space/JavaSpace.html#notify-net.jini.core.entry.Entry-net.jini.core.
     *              transaction.Transaction-net.jini.core.event.RemoteEventListener-long-java.rmi.
     *              MarshalledObject-
     *
     *              https://river.apache.org/release-doc/3.0.0/api/net/jini/jeri/BasicJeriExporter.html
     */
    public void listenForUserAddedToTopic(TopicEntry topic, TopicController controller)
    {
        DummyUserInTopic template = new DummyUserInTopic(topic);
        ArrayList<DummyUserInTopic> templateCollection = new ArrayList<>(1);
        templateCollection.add(template);

        // create the exporter
        Exporter exporter = getBasicJeriExporter();

        try
        {

            // register as remote object
            UserAddedListener eventListener =
                    new UserAddedListener(controller);

            // export reference ot remote listener
            userAddedListener =
                    (RemoteEventListener) exporter.export(eventListener);

            // get register for event triggered
            userAddedRegister
                     = space.registerForAvailabilityEvent(
                             templateCollection, null,
                            true, userAddedListener, Lease.FOREVER, null);
        }
        catch(Exception e)
        {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Listens for users being removed from a topic
     *
     * @param topic - the topic to listen on
     *
     * @References JavaSpaces: Principles, Patterns and Practice
     *              David Gelernter
     *
     *              https://river.apache.org/release-doc/3.0.0/api/net/jini/space/JavaSpace.html#notify-net.jini.core.entry.Entry-net.jini.core.
     *              transaction.Transaction-net.jini.core.event.RemoteEventListener-long-java.rmi.
     *              MarshalledObject-
     *
     *              https://river.apache.org/release-doc/3.0.0/api/net/jini/jeri/BasicJeriExporter.html
     */
    public void listenForUserRemovedFromTopic(TopicEntry topic, TopicController controller)
    {
        DummyUserRemoved template = new DummyUserRemoved(topic);
        ArrayList<DummyUserRemoved> templateCollection = new ArrayList<>(1);
        templateCollection.add(template);

        Exporter exporter = getBasicJeriExporter();

        try
        {
            UserRemovedListener eventListener =
                    new UserRemovedListener(controller);

            userRemovedListener = (RemoteEventListener) exporter.export(eventListener);

            userRemovedRegister
                    = space.registerForAvailabilityEvent(
                            templateCollection, null,
                         true, userRemovedListener, Lease.FOREVER, null);

        }
        catch (Exception e)
        {
            System.err.println("Error: remove user failed!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Utility Methods

    /**
     * Creates a basic exporter.
     * @return the created exporter
     */
    public BasicJeriExporter getBasicJeriExporter()
    {
        return new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
                new BasicILFactory(),false, true);
    }


    // Test Methods

    /**
     * Create a test user that exists for three minutes
     *
     * @param user - the UserEntry to create
     *
     * @return the created UserEntry
     *
     * @throws TransactionException
     * @throws RemoteException
     */
    public Lease createTestUser(UserEntry user) throws TransactionException, RemoteException
    {int THREE_MINUTES = 3000*60; return space.write(user, null, THREE_MINUTES);}
}
