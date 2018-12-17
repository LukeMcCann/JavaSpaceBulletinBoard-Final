package util.notifs;

import controller.TopicController;
import model.DummyUserInTopic;
import model.TopicEntry;
import model.UserEntry;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.space.AvailabilityEvent;

import javax.swing.table.DefaultTableModel;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 *
 * @Reference JavaSpaces: Principles and Practice
 */
public class UserAddedNotif implements RemoteEventListener
{
    private TopicController controller;

    private UserAddedNotif(TopicController controller)
    {
        super();
        this.controller = controller;
    }

    /**
     * Listen for user being added,
     *
     * @param event - the event to occur
     * @throws UnknownEventException
     * @throws RemoteException
     */
    @Override
    public void notify(RemoteEvent event) throws UnknownEventException, RemoteException
    {
        try
        {
            AvailabilityEvent avt = (AvailabilityEvent) event;
            DummyUserInTopic userInTopic = (DummyUserInTopic) avt.getEntry();
            UserEntry user = userInTopic.getUser();

            // add user to list
            if (!inUserList(user))
            {
                Object[] columns = { user.getUsername(), user.getID() };

                controller.getUserListModel().addRow(columns);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

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
        boolean inList = false;
        try
        {
            for (int i = 0; i < userModel.getRowCount(); i++)
            {
                UUID idInTable = (UUID) userModel.getValueAt(i, 1);

                if (!idInTable.equals(user.getID()))
                {
                    return inList;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return inList = true;
    }
}
