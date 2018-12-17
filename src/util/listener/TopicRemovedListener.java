package util.listener;

import java.rmi.RemoteException;
import java.util.UUID;

import javax.swing.table.DefaultTableModel;

import controller.MenuController;
import controller.TopicController;
import model.DummyTopicDeleted;
import model.TopicEntry;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.space.AvailabilityEvent;

public class TopicRemovedListener implements RemoteEventListener
{
    private MenuController m_controller;
    private TopicController t_controller;

    public TopicRemovedListener(MenuController m_controller, TopicController t_controller)
    {
        super();
        this.m_controller = m_controller;
        this.t_controller = t_controller;
    }

    /**
     * Listens for a topic being removed
     *
     * @param remoteEvent
     * @throws UnknownEventException
     * @throws RemoteException
     */
    @Override
    public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException
    {
        try
        {
            AvailabilityEvent availEvent = (AvailabilityEvent) remoteEvent;
            DummyTopicDeleted topicDeleted = (DummyTopicDeleted) availEvent.getEntry();

            if(m_controller == null)
            {
                t_controller.deleteTopic();
            }
            else
            {
                DefaultTableModel topicModel = m_controller.getNotifsModel();

                for (int i = 0; i < topicModel.getRowCount(); i++)
                {
                    String topicTitle = (String) topicModel.getValueAt(i, 0);
                    TopicEntry topicRemoved = topicDeleted.getTopic();

                    String topicDeletedTitle = topicRemoved.getTitle();

                    if (topicTitle.equals(topicDeletedTitle))
                    {
                        topicModel.removeRow(i);
                        break;
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.err.println("Failed to remove topic from list or notify users.");
            e.printStackTrace();
        }
    }
}
