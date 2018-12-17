package util.helper;

import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import util.SpaceUtils;

/**
 * @Author Luke McCann
 * @UniversityNumber U1364096
 * @University The University of Huddersfield
 *
 * TransactionBuilder -
 *              Contains the logic for building a transaction
 *              makes transactions considerably easier
 *              with less repetition.
 *
 * @Reference JavaSpaces Principles, Patterns and Practice
 *            David Gelernter
 */
public class TransactionBuilder
{
    private static final long DEFAULT_LEASE= 3000l; // int was not reliable

    // No arg constructor
    private TransactionBuilder() {}

    /**
     * Creates a new transaction utilising the TransactionManager.
     *
     * @param lease - the lease time of the transaction.
     * @return the built transaction.
     *
     * @Reference JavaSpaces Principles, Patterns and Practice
     *            David Gelernter
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
