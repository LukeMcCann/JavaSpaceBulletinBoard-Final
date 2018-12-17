package util.listener;

import controller.TopicController;
import model.DummyUserInTopic;
import model.DummyUserRemoved;
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
 *            https://river.apache.org/release-doc/2.2.2/api/net/jini/space/JavaSpace.html
 *
 * UserRemovedListener -
 *             Listener class for RemoteEvents regarding users leaving topics.
 */
public class UserRemovedListener implements RemoteEventListener
{
    private TopicController controller;
    private JFrame frame;

    public UserRemovedListener(TopicController controller)
    {
        super();
        this.controller = controller;
//        this.frame=frame;
    }

    /**
     * Listen for user being removed from topic.
     *
     * @param remoteEvent - the event to listen for
     * @throws UnknownEventException
     *      - the recipient does not recognize the combination of event identifier and event source
     *
     * @throws RemoteException
     */
    public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException
    {
        try
        {
            // get the user that triggered event
            AvailabilityEvent avt = (AvailabilityEvent) remoteEvent;
            DummyUserRemoved removedUser = (DummyUserRemoved) avt.getEntry();
            DefaultTableModel userListModel = controller.getUserListModel();

            // find user in table model and remove
            for(int i = 0; i < userListModel.getRowCount(); i++)
            {
                String nameOfUser = (String) userListModel.getValueAt(i, 0);
                if(nameOfUser.equals(removedUser.getUser().getUsername()))
                {
                    userListModel.removeRow(i);
                    break;
                }
            }

        }
        catch (Exception  e)
        {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
