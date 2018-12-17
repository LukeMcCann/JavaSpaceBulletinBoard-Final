package util.listener;

import controller.TopicController;
import model.DummyTopicDeleted;
import model.DummyUserInTopic;
import model.UserEntry;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.space.AvailabilityEvent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.RemoteException;

/**
 * @Author Luke McCann
 * @UniversityNumber U1364096
 * @University The University of Huddersfield
 *
 * @Reference JavaSpaces: Principles and Practice
 *            David Gelertner
 *
 *            Java Distributed Systems:
 *            Marko Boger
 *
 * UserAddedListener -
 *             Listener class for RemoteEvents regarding users joining topics.
 */
public class UserAddedListener implements RemoteEventListener
{
    private TopicController controller;
    private JFrame frame;

    public UserAddedListener(TopicController controller)
    {
        super();
        this.controller = controller;
//        this.frame = frame;
    }

    /**
     * Listen for user being added to topic.
     *
     * @param event - the event to listen for.
     * @throws UnknownEventException
     * @throws RemoteException
     */
    @Override
    public void notify(RemoteEvent event) throws UnknownEventException, RemoteException
    {
        try
        {
            // get the user that triggered event
            AvailabilityEvent avt = (AvailabilityEvent) event;
            DummyUserInTopic userInTopic = (DummyUserInTopic) avt.getEntry();
            UserEntry user = userInTopic.getUser();

            // add user to list
            if (!inUserList(user))
            {
                Object[] columns = {
                        user.getUsername(), user.getID()
                };

                controller.getUserListModel().addRow(columns);
            }
        }
        catch (Exception  e)
        {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Utility Methods

    /**
     * Checks if the user is currently in the topic list
     *
     * @param user - user to check for
     * @return <code>true</code> if the user is in the list, else
     *          <code>false</code>
     */
    private boolean inUserList(UserEntry user)
    {
        DefaultTableModel userModel = controller.getUserListModel();
        try
        {
            for (int i = 0; i < userModel.getRowCount(); i++)
            {
                String inTable = (String) userModel.getValueAt(i, 0);

                if (!inTable.equals(user.getUsername()))
                {
                    return false;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }
}
