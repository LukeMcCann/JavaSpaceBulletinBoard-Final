package controller;

import model.*;

import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.export.Exporter;
import net.jini.space.JavaSpace05;
import org.apache.commons.lang3.StringUtils;
import util.PostUtils;
import util.SpaceUtils;
import util.TopicUtils;
import util.UserUtils;
import util.listener.MessageRecievedListener;
import util.listener.TopicRemovedListener;
import util.listener.UserAddedListener;
import util.listener.UserRemovedListener;
import view.TopicForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Luke McCann
 * @UniversityNumber U1364096
 * @University The University of Huddersfield
 *
 * TopicController -
 *              Handles all of the logic inside of a topic.
 *
 * @References  https://docs.oracle.com/
 *                 javase/tutorial/uiswing/components/table.html
 */
public class TopicController
{
    private PostUtils postUtils = PostUtils.getPostUtils();
    private UserUtils userUtils = UserUtils.getUserutils();
    private TopicUtils topicUtils = TopicUtils.getTopicUtils();

    private TopicForm topicForm;
    private UserEntry user;
    private TopicEntry topic;

    private List<PostEntry> directMessageList;
    private DefaultTableModel postListModel;
    private DefaultTableModel userListModel;
    private DefaultTableModel privateListModel;

    private RemoteEventListener userAddedListener;
    private RemoteEventListener userRemovedListener;
    private RemoteEventListener messageReceivedListener;
    private RemoteEventListener topicRemovedListener;

    private EventRegistration userAddedRegister;
    private EventRegistration userRemovedRegister;
    private EventRegistration topicRemovedRegister;
    private EventRegistration messageReceivedRegister;

    public TopicController(TopicForm form, UserEntry user, TopicEntry topic)
    {
        this.topicForm = form;
        this.user = user;
        this.topic = topic;
        topicUtils.registerUserTo(user, topic); // regitser user to topic
        registerMessageReceivedListener();
        registerUserAddedListener();
        registerTopicRemovedListener(null, this);
        registerUserRemovedListener();
    }


    // Messaging

    /**
     * Handles the event triggered by the send button
     *
     * @param content - the content of the post
     * @param author - the author of the post
     * @param recipient - the recipient of the post
     * @param topic - the topic the post is for
     */
    public void sendButtonPress(String content, UserEntry author, UserEntry recipient, TopicEntry topic)
    {
        if(StringUtils.isNotBlank(content) &&
                author != null &&
                topic != null)
        {
            if(recipient == null)
            {
                postUtils.sendPublicMessage(new PostEntry(author, topic, content));
            }
            else
            {
                postUtils.sendPrivateMessage(new PostEntry(author, recipient, topic, content));
            }
        }
        else
        {
            JOptionPane.showMessageDialog(null,
                    "Failed to send message!");
        }
    }

    /**
     * Forces private message send and prevents public messages
     * Used for the secret chat to prevent accidental sending to public
     *
     * @param author - the author of the post
     * @param recipient - the recipient of the post
     * @param topic - the topic the post is for
     * @param content - the content of the post
     */
    public void forcePrivateSend(UserEntry author, UserEntry recipient, TopicEntry topic, String content)
    {
        if(recipient == null)
        {
            JOptionPane.showMessageDialog(null,
                    "No recipient!");
        }
        else
        {
            if(StringUtils.isNotBlank(content))
            {
                postUtils.sendPrivateMessage(new PostEntry(author, recipient, topic, content));
            }
            else
            {
                JOptionPane.showMessageDialog(null,
                        "Please enter a message!");
            }
        }
    }


    // Create Table Models

    /**
     * Creates the data model for userList
     *
     * @return The finished user model.
     *
     * @Reference https://docs.oracle.com/
     *              javase/tutorial/uiswing/components/table.html
     */
    public DefaultTableModel createUsersModel()
    {
        Object[] columns =
                {
                        "Username"
                };

        List<DummyUserInTopic> userCollection =
                topicUtils.getAllUsersFromTopic(topic);

        Object[][] content = {};

        if(userCollection != null && userCollection.size() > 0)
        {
            content = new Object[userCollection.size()][1];

            for(int i = 0; i < userCollection.size(); i++)
            {
                content[i][0] = userCollection.get(i).getUser().getUsername();
            }
        }
        userListModel = new DefaultTableModel(content, columns);
        return userListModel;

    }

    /**
     * Creates the data model for the SecretChat table
     *
     * @return The finished user model.
     *
     * @Reference https://docs.oracle.com/
     *              javase/tutorial/uiswing/components/table.html
     */
    public DefaultTableModel createPrivateChatModel()
    {
        Object[] columns =
                {
                        "TimeStamp", "TO", "FROM", "Message"
                };

        List<PostEntry> privatePostCollection =
                postUtils.getPrivatePostsForUser(user, topic);

        Object[][] content = {};

        if(privatePostCollection != null && privatePostCollection.size() > 0)
        {
            content = new Object[privatePostCollection.size()][5];

            for(int i = 0; i < privatePostCollection.size(); i++)
            {
                PostEntry post = privatePostCollection.get(i);
                post.generateTimeStamp();

                content[i][0] = privatePostCollection.get(i).getPostedAt();
                content[i][1] = privatePostCollection.get(i).getAuthor().getUsername();
                content[i][2] = privatePostCollection.get(i).getRecipient().getUsername();
                content[i][3] = privatePostCollection.get(i).getContent();
            }
        }
        privateListModel = new DefaultTableModel(content, columns);
        return privateListModel;
    }

    /**
     * Creates the data model for postList which is shown to the user.
     * It filters out any posts the user is unauthorised to see.
     *
     * @return The finished table model.
     *
     * @Reference https://docs.oracle.com/
     *              javase/tutorial/uiswing/components/table.html
     */
    public DefaultTableModel createPostsModel()
    {
        Object[] columns =
                {
                "TimeStamp", "User", "Post"
                };

        List<PostEntry> postCollection =
                postUtils.getAllUsersPosts(user, topic);

        // iterate over list
        for(int i = 0; i < postCollection.size(); i++)
        {
            PostEntry post = postCollection.get(i);

            if(post.getRecipient() != null)
            {
                // there is a recipient, message is private
                postCollection.remove(i);
//                directMessageList.add(post);
            }
        }

        // create multidimensional array
        Object[][] content = {};

        if(postCollection != null && postCollection.size() > 0)
        {
            content = new Object[postCollection.size()][3];

            for(int i = 0; i < postCollection.size(); i++)
            {
                PostEntry post = postCollection.get(i);
                post.generateTimeStamp();

                content[i][0] = post.getPostedAt();
                content[i][1] = post.getAuthor().getUsername();
                content[i][2] = post.getContent();
            }
        }
        postListModel = new DefaultTableModel(content, columns);
        return postListModel;
    }


    // Convenience Utility methods

    /**
     * Safely closes services removing user from topics.
     */
    public void closeServices(TopicForm form)
    {
        try
        {
            topicUtils.removeUserFrom(user, topic);
            form.superDispose();
        }
        catch(Exception e)
        {
            System.err.println("Issue disposing window!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Listeners

    /**
     * Listens for users being added to the topic
     */
    private void registerUserAddedListener()
    {
        JavaSpace05 space = SpaceUtils.getSpace();
        DummyUserInTopic template = new DummyUserInTopic(topic);
        ArrayList<DummyUserInTopic> templateCollection = new ArrayList<>(1);
        templateCollection.add(template);
        Exporter exporter = userUtils.getBasicJeriExporter();
        try
        {
            UserAddedListener eventlistener = new UserAddedListener(this);
            userAddedListener = (RemoteEventListener) exporter.export(eventlistener);

            userAddedRegister
                    = space.registerForAvailabilityEvent(
                        templateCollection, null, true,
                        userAddedListener, Lease.FOREVER, null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Listens for users being removed from the topic
     */
    private void registerUserRemovedListener()
    {
        JavaSpace05 space = SpaceUtils.getSpace();
        DummyUserRemoved template = new DummyUserRemoved(topic);
        ArrayList<DummyUserRemoved> templateCollection = new ArrayList<>(1);
        templateCollection.add(template);
        Exporter exporter = userUtils.getBasicJeriExporter();

        try
        {
            UserRemovedListener eventListener = new UserRemovedListener(this);
            userRemovedListener = (RemoteEventListener) exporter.export(eventListener);

            userRemovedRegister
                    = space.registerForAvailabilityEvent(
                            templateCollection, null, true,
                            userRemovedListener, Lease.FOREVER, null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Register topic removed event lsitener
     */
    public void registerTopicRemovedListener(MenuController m_controller, TopicController t_controller)
    {
        JavaSpace05 space = SpaceUtils.getSpace();
        DummyTopicDeleted template = new DummyTopicDeleted(topic);
        ArrayList<DummyTopicDeleted> templates = new ArrayList<>(1);
        templates.add(template);
        Exporter exporter = userUtils.getBasicJeriExporter();
        try
        {
            TopicRemovedListener eventListener = new TopicRemovedListener(m_controller, t_controller);
            topicRemovedListener = (RemoteEventListener) exporter.export(eventListener);

            topicRemovedRegister
                    = space.registerForAvailabilityEvent(templates,
                    null, true, topicRemovedListener,
                    Lease.FOREVER, null);
        }
        catch (Exception e)
        {
            System.err.println("Failed to get new topic.");
            e.printStackTrace();
        }
    }

    /**
     * Register message received listener
     */
    public void registerMessageReceivedListener()
    {
        JavaSpace05 space = SpaceUtils.getSpace();
        PostEntry template = new PostEntry(topic);
        ArrayList<PostEntry> templates = new ArrayList<PostEntry>(1);
        templates.add(template);
        Exporter exporter = userUtils.getBasicJeriExporter();
        try
        {
            MessageRecievedListener eventListener = new MessageRecievedListener(this, topic, user);
            messageReceivedListener = (RemoteEventListener) exporter.export(eventListener);

            messageReceivedRegister
                    = space.registerForAvailabilityEvent(templates,
                    null, true, messageReceivedListener,
                    Lease.FOREVER, null);
        }
        catch (Exception e)
        {
            System.err.println("Failed to start message listener.");
            e.printStackTrace();
        }
    }


    // Deletion

    public void deleteTopic()
    {
        postListModel = null;

        JOptionPane.showMessageDialog(
                topicForm, topic.getOwner()
                        + " has deleted the topic.");

        windowSafeClose();
    }


    // Utility Methods

    public void windowSafeClose()
    {
        try
        {
            topicUtils.removeUserFrom(user, topic);
            userRemovedRegister.getLease().cancel();
            userAddedRegister.getLease().cancel();
            topicRemovedRegister.getLease().cancel();
            messageReceivedRegister.getLease().cancel();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Handles user leaving topic
     *
     * @param user - User to leave
     * @param topic - the topic to leave
     */
    public void leaveTopicPress(UserEntry user, TopicEntry topic)
    {
        topicUtils.removeUserFrom(user, topic);
    }

    // get the userListModel
    public DefaultTableModel getUserListModel()
    {
        return userListModel;
    }

    public DefaultTableModel getPostListModel()
    {
        return postListModel;
    }

    public DefaultTableModel getPrivateListModel()
    {
        return privateListModel;
    }

    // reloads the models
    public void refresh(JTable post, JTable user)
    {
        postListModel = createUsersModel();
        post.setModel(postListModel);
        userListModel = createUsersModel();
        user.setModel(userListModel);
    }
}
