package util.error;

import net.jini.space.JavaSpace05;

import javax.swing.*;

public class SpaceExistsError
{
    private static SpaceExistsError spaceExists;

    private SpaceExistsError() {}

    public static SpaceExistsError getSpaceExistsError()
    {
        if(spaceExists != null) return spaceExists;
        return spaceExists = new SpaceExistsError();
    }

    public boolean spaceExists(JavaSpace05 space)
    {
        if(space != null)
        {
            return true;
        }
        return false;
    }

    public void getSpaceExistsWarning(JFrame frame, String host)
    {
        JOptionPane.showMessageDialog(frame,
                "Connection Refused! " +
                        "\nCheck space exists. " +
                        "\n\n Host: " + host);
    }
}
