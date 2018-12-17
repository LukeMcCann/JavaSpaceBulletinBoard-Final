package util.helper;

import net.jini.core.entry.Entry;
import net.jini.core.transaction.Transaction;
import net.jini.space.JavaSpace05;
import net.jini.space.MatchSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Luke McCann
 * @UniversityNumber U1364096
 * @University The University of Huddersfield
 *
 * @References javase/tutorial/extra/generics/methods.html
 *
 *             https://river.apache.org/release-doc/3.0.0/api/net/
 *             jini/space/JavaSpace05.html
 *
 * EntrySearcher -
 *            Contains logic for searching the space for existing entries.
 */
public class EntrySearcher
{
    // No arg constructor
    public EntrySearcher() {}

    // Read Methods - these methods retrieve a copy from the space.

    /**
     * Reads all entries that match the provided template in a space.
     *
     * Utilises JavaSpace05 contents() method
     *
     * @param space - the space to search
     * @param transaction - the transaction to use
     * @param template - the template of the search object
     * @param <T> - Entry object to read (Java Generics)
     *
     * @return All entry object in the space which match the template
     */
    public <T extends Entry> List<T> readAllMatchingEntries(JavaSpace05 space, Transaction transaction, T template)
    {
        // create a empty list
        List<T> entryCollection = new ArrayList<T>();

        // create a template to find
        List<T> templateList =
                new ArrayList<T>(1);

        try
        {
            // add template to templateList
            templateList.add(template);

            // create a matchset to readAll (JavaSpaces05)
            MatchSet matchSet = space.contents(
                    templateList, transaction, 900, Long.MAX_VALUE);

            T entry = (T) matchSet.next();

            // add all entries to entryCollection
            while(entry != null)
            {
                entryCollection.add(entry);
                entry = (T) matchSet.next();
            }
//
            // while this solution works, it is improved by newer implementation
            // which uses less repetitive transaction building.
            // the code remains as there may be a better way of doing this
//            try
//            {
//                transaction.commit();
//            }
//            catch(Exception a)
//            {
//                a.printStackTrace();
//                System.err.println("Transaction failed to commit.");
//            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            try
            {
                transaction.abort();
            }
            catch (Exception x)
            {
                x.printStackTrace();
                System.err.println("Transaction failed to abort!");
            }
        }
        return entryCollection;
    }

    /**
     * Overload of the previous method, the transaction commit has been moved here
     * as the previous method which has been commented out required new transactions
     * to be passed each time.
     *
     * @param space - space to search
     * @param template - template to match with
     * @param <T> - Entry object to read (Java Generics)
     *
     * @return all entry objects which match the template.
     */
    public <T extends Entry> List<T> readAllMatchingEntries(JavaSpace05 space, T template)
    {
        // building here means it does not have to be done every tme
        // I found this to be more efficient than the previous method
        Transaction transaction =
                TransactionBuilder.getTransaction();

        List<T> entryCollection =
                readAllMatchingEntries(space, transaction, template);

        try
        {
            transaction.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return entryCollection;
    }


    // Take Methods - these methods take object from the space.

    /**
     * Takes all entries from the space that match the template.
     *
     * @param space - space to search
     * @param transaction - the transaction use
     * @param template - the tempalte to find
     * @param <T> - Entry objects to take (Java Generics)
     *
     * @return All entry objects which were removed from the space.
     */
    public <T extends Entry> List<T> takeAllMatchingEntries(JavaSpace05 space, Transaction transaction, T template)
    {
        List<T> entryCollection = new ArrayList<T>();
        List<T> templateList = new ArrayList<T>(1);

        try
        {
            templateList.add(template);
            entryCollection.addAll(space.take(
                    templateList, transaction, 900, Long.MAX_VALUE));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("Failed to take all: " +
                    template.getClass().getSimpleName());
        }
        return entryCollection;
    }
}
