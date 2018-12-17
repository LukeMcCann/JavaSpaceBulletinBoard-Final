package util.helper;

import model.UserEntry;
import net.jini.core.transaction.Transaction;
import net.jini.space.JavaSpace05;
import util.SpaceUtils;

/**
 * @Author Luke McCann
 * @UniversityNumber U1364096
 * @University The University of Huddersfield
 *
 * SpaceSearcher -
 *          Contains methods for searching specific entries in  a space.
 */
import java.util.UUID;

public class SpaceSearcher extends EntrySearcher
{
    private static final JavaSpace05 space = SpaceUtils.getSpace();
    private static SpaceSearcher searcher;

    // No arg constructor
    private SpaceSearcher() {}

    public static SpaceSearcher getSpaceSearcher()
    {
        if(searcher != null) return searcher;
        return searcher = new SpaceSearcher();
    }


    // Read Methods
    /**
     * Retrieves a specified UserEntry from the space.
     * Note: this is a read and will not remove users.
     *
     * @param username - the username of the UserEntry to find
     * @param transaction - the transaction to utilise
     *
     * @return the matching UserEntry in the space
     */
    public UserEntry getUserByUsername(String username, Transaction transaction)
    {
        // create template and set to match
        UserEntry template = new UserEntry();
        template.setUsername(username);
        UserEntry existingUser = null;
        try
        {
            // if exists, read from space
            existingUser = (UserEntry)
                      space.readIfExists(template, transaction, 3000);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return existingUser;
    }

    // Overload of previous, nullifies transaction
    public UserEntry getUserByUsername(String username) {return getUserByUsername(username, null);}

    /**
     * Retrieves a user by their SecuredUsername
     * Good for matching users indefinitely
     *
     * @param username - the SecureUsername of the UserEntry to retrieve
     * @param transaction - the transaction to utilise
     * @return the UserEntry which matches the template
     */
    public UserEntry getUserBySecureUsername(String username, Transaction transaction)
    {
        // create template and set to match
        UserEntry template = new UserEntry();
        template.setSecureUsername(username);
        UserEntry existingUser = null;
        try
        {
            // if exists, read from space
            existingUser = (UserEntry)
                    space.readIfExists(template, transaction, 3000);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return existingUser;
    }

    public UserEntry getUserBySecureUsername(String username) {return getUserBySecureUsername(username, null);}

}
