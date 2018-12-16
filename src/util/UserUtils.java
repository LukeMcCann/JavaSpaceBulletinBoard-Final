package util;

import model.UserEntry;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace05;
import util.helper.SpaceSearcher;
import util.helper.TransactionBuilder;

import java.rmi.RemoteException;

public class UserUtils
{
    private static UserUtils userutils;
    private static SpaceSearcher searcher = SpaceSearcher.getSpaceSearcher();
    private static final JavaSpace05 space = SpaceUtils.getSpace();
    private static final long ONE_MONTH = 2629746000l;


    private  UserUtils() {}

    public static UserUtils getUserutils()
    {
        if(userutils != null) return userutils;
        return userutils = new UserUtils();
    }

    /**
     *
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
     * Create a test user that exists for three minutes
     * @param user
     * @return
     * @throws TransactionException
     * @throws RemoteException
     */
    public Lease createTestUser(UserEntry user) throws TransactionException, RemoteException
    {int THREE_MINUTES = 3000*60; return space.write(user, null, THREE_MINUTES);}
}
