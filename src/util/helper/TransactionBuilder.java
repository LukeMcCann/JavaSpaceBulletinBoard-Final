package util.helper;

import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import util.SpaceUtils;

public class TransactionBuilder
{
    private static final long DEFAULT_LEASE= 3000l;

    private TransactionBuilder() {}

    /**
     *
     * @param lease
     * @return
     *
     * @Reference JavaSpaces Principles, Patterns and Practice
     */
    public static Transaction getTransaction(long lease)
    {
        // get manager
        TransactionManager tm =
                SpaceUtils.getManager();

        Transaction.Created trc = null;

        try
        {
            trc = TransactionFactory.create(tm, lease);
        }
        catch(Exception e)
        {
            System.err.println("Failed to create transaction!");
            e.printStackTrace();
        }
        return trc.transaction;
    }

    public static Transaction getTransaction() {return getTransaction(DEFAULT_LEASE);}
}
