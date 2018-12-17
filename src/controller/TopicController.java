package controller;

import model.DummyUserInTopic;
import model.PostEntry;
import model.TopicEntry;
import model.UserEntry;
import org.apache.commons.lang3.StringUtils;
import util.PostUtils;
import util.TopicUtils;
import util.UserUtils;
import view.TopicForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class TopicController
{
    private PostUtils postUtils = PostUtils.getPostUtils();
    private UserUtils userUtils = UserUtils.getUserutils();
    private TopicUtils topicUtils;
    private TopicForm topicForm;
    private UserEntry user;
    private TopicEntry topic;

    private List<PostEntry> directMessageList;
    private DefaultTableModel postListModel;
    private DefaultTableModel userListModel;
    private DefaultTableModel privateListModel;

    public TopicController(TopicForm form, UserEntry user, TopicEntry topic)
    {
        this.topicForm = form;
        this.user = user;
        this.topic = topic;
        this.topicUtils = TopicUtils.getTopicUtils();
        topicUtils.registerUserTo(user, topic); // regitser user to topic
    }

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

    /**
     * Safely removes entries
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

    public List<PostEntry> getDirectMessages()
    {
        return directMessageList;
    }

    /**
     * Hanldes user leaving topic
     *
     * @param user - User to leave
     * @param topic - the topic to leave
     */
    public void leaveTopicPress(UserEntry user, TopicEntry topic)
    {
        topicUtils.removeUserFrom(user, topic);
    }




    // Convenience methods
    public DefaultTableModel getUserListModel()
    {
        return userListModel;
    }

    // While similar the following work more effectively in this way
    public void refresh(JTable post, JTable user)
    {
        postListModel = createUsersModel();
        post.setModel(postListModel);

        userListModel = createUsersModel();
        user.setModel(userListModel);
    }
}
