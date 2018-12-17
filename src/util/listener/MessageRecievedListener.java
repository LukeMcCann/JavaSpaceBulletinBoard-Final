package util.listener;

import java.io.Serializable;
import controller.TopicController;
import model.PostEntry;
import model.TopicEntry;
import model.UserEntry;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.space.AvailabilityEvent;

public class MessageRecievedListener implements RemoteEventListener, Serializable
{

    private UserEntry user;
    private final TopicController controller;

    public MessageRecievedListener(TopicController controller, TopicEntry topic, UserEntry user)
    {
        super();

        this.controller = controller;
        this.user = user;
    }

    /**
     * Listens for received messages
     * @param event - the event which triggered
     */
    public void notify(RemoteEvent event)
    {
        AvailabilityEvent availEvent = (AvailabilityEvent) event;
        try
        {

            // get the post that triggered event
            PostEntry post = (PostEntry) availEvent.getEntry();

            if (post.getRecipient() == null ||
                    post.getRecipient().getUsername().equals(user.getUsername())
                    || post.getAuthor().getUsername().equals(user.getUsername()))
            {

                String message = post.getContent();
                UserEntry author = post.getAuthor();

                if (author.getUsername().equals(user.getUsername()) &&
                        post.getRecipient() != null)
                {
                    message =
                            "TO: '" + post.getRecipient().getUsername()
                                    + "': " + message;

                    String timeStamp = post.getPostedAt();

                    Object[] row =
                            {
                                    timeStamp, author.getUsername(), message
                            };


                    if (controller.getPrivateListModel() != null)
                    {
                        controller.getPrivateListModel().addRow(row);
                    }
                }
                else
                {
                    String timeStamp = post.getPostedAt();

                    Object[] row =
                            {
                                    timeStamp, author.getUsername(), message
                            };


                    if (controller.getPostListModel() != null)
                    {
                        controller.getPostListModel().addRow(row);
                    }
                }
            }

        }
        catch (Exception e)
        {
            System.err.println("Failed to run notify method for Messages");
            e.printStackTrace();
        }
    }
}