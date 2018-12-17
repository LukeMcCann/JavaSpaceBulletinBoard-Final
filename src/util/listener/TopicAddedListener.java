package util.listener;

import controller.MenuController;
import model.TopicEntry;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.space.AvailabilityEvent;

import java.io.Serializable;

public class TopicAddedListener implements RemoteEventListener, Serializable
{

    private final MenuController controller;

    public TopicAddedListener(MenuController controller)
    {
        super();
        this.controller = controller;
    }


    /**
     * Listens for users being added
     * @param event - the event trigger
     */
    @Override
    public void notify(RemoteEvent event)
    {
        try
        {
            AvailabilityEvent availEvent = (AvailabilityEvent) event;
            TopicEntry topic = (TopicEntry) availEvent.getEntry();

            // Add the topic data to the MainMenuController's topic list.
            Object[] row = {
                    topic.getTitle(), topic.getOwner().getUsername(), topic.getID()
            };

            controller.getNotifsModel().addRow(row);
        }
        catch (Exception e)
        {
            System.err.println("Failed to run notify method for Topic Creation");
            e.printStackTrace();
        }
    }
}