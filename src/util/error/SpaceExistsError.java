package util.error;

import net.jini.space.JavaSpace05;

import javax.swing.*;

/**
 * @Author Luke McCann
 * @UniversityNumber U1364096
 * @University The University of Huddersfield
 *
 * SpaceExistsError -
 *          Logic for checking if a space exists and providing user feedback
 *          if it does not.
 *
 * This is a convenience utility created for easily switching hosts between the University network
 * and home.
 */
public class SpaceExistsError
{
    private static SpaceExistsError spaceExists;

    // constructor
    private SpaceExistsError() {}

    public static SpaceExistsError getSpaceExistsError()
    {
        if(spaceExists != null) return spaceExists;
        return spaceExists = new SpaceExistsError();
    }

    /**
     * Checks if a given space exists.
     *
     * @param space - the space to check
     *
     * @return <code>true</code> if exists, else <code>false</code>
     */
    public boolean spaceExists(JavaSpace05 space)
    {
        if(space != null)
        {
            return true;
        }
        return false;
    }

    /**
     * Shows an option pane and provides a error message for connection refused.
     *
     * @param frame - the parent frame of the message
     * @param host - the address where the space is hosted
     */
    public void getSpaceExistsWarning(JFrame frame, String host)
    {
        JOptionPane.showMessageDialog(frame,
                "Connection Refused! " +
                        "\nCheck space exists. " +
                        "\n\n Host: " + host);

        System.err.println("Error: Connection to " + host + " refused.");
    }
}
