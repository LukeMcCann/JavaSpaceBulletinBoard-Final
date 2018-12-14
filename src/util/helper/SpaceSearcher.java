package util.helper;

import model.UserEntry;
import net.jini.core.transaction.Transaction;
import net.jini.space.JavaSpace05;
import util.SpaceUtils;
import util.UserUtils;

import java.util.UUID;

public class SpaceSearcher
{
    private static final JavaSpace05 space = SpaceUtils.getSpace();
    private static SpaceSearcher searcher;

    private SpaceSearcher() {}

    public static SpaceSearcher getSpaceSearcher()
    {
        if(searcher != null) return searcher;
        return searcher = new SpaceSearcher();
    }

    /**
     *
     * @param username
     * @param transaction
     * @return
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

    public UserEntry getUserByUsername(String username) {return getUserByUsername(username, null);}


    public UserEntry getUserByID(UUID id, Transaction transaction)
    {
        // create template and set to match
        UserEntry template = new UserEntry();
        template.setID(id);
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

    public UserEntry getUserByID(UUID id) {return getUserByID(id, null);}



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
