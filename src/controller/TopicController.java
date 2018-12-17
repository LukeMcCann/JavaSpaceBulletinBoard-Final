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
    private DefaultTableModel privateListModel;
    private DefaultTableModel userListModel;

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
                // postUtils.sendPrivateMessage(new PostEntry(author, recipient, topic, content);
            }
        }
        else
        {
            JOptionPane.showMessageDialog(null,
                    "Failed to send message!");
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
                        "Username", "User_ID"
                };

        List<DummyUserInTopic> userCollection =
                topicUtils.getAllUsersFromTopic(topic);

        Object[][] content = {};

        if(userCollection != null && userCollection.size() > 0)
        {
            content = new Object[userCollection.size()][2];

            for(int i = 0; i < userCollection.size(); i++)
            {
                content[i][0] = userCollection.get(i).getUser().getUsername();
                content[i][1] = userCollection.get(i).getUser().getID();
            }
        }
        userListModel = new DefaultTableModel(content, columns);
        return userListModel;

    }

    public DefaultTableModel createPostsModel()
    {
        Object[] columns =
                {
                        "TimeStamp", "User",
                        "Post", "Post_ID"
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
            }
        }

        // create multidimensional array
        Object[][] content = {};

        if(postCollection != null && postCollection.size() > 0)
        {
            content = new Object[postCollection.size()][4];

            for(int i = 0; i < postCollection.size(); i++)
            {
                PostEntry post = postCollection.get(i);
                post.generateTimeStamp();

                content[i][0] = post.getPostedAt();
                content[i][1] = post.getAuthor().getUsername();
                content[i][2] = post.getContent();
                content[i][3] = post.getID();
            }
        }
        postListModel = new DefaultTableModel(content, columns);
        return postListModel;
    }


    public DefaultTableModel createPrivatePostsModel()
    {
        Object[] columns =
                {
                        "TimeStamp", "TO",
                        "FROM", "Message"
                };

        List<PostEntry> postCollection =
                postUtils.getAllUsersPosts(user, topic);

        // iterate over list
        for(int i = 0; i < postCollection.size(); i++)
        {
            PostEntry post = postCollection.get(i);

            if(post.getRecipient() == null)
            {
                // there is a recipient, message is public
                postCollection.remove(i);
            }
        }

        // create multidimensional array
        Object[][] content = {};

        if(postCollection != null && postCollection.size() > 0)
        {
            content = new Object[postCollection.size()][4];

            for(int i = 0; i < postCollection.size(); i++)
            {
                PostEntry post = postCollection.get(i);
                post.generateTimeStamp();

                content[i][0] = post.getPostedAt();
                content[i][1] = post.getRecipient().getUsername();
                content[i][2] = post.getAuthor().getUsername();
                content[i][3] = post.getContent();
            }
        }
        privateListModel = new DefaultTableModel(content, columns);
        return privateListModel;
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

    public void refresh(JTable postTable, JTable userTable)
    {
        userListModel = createUsersModel();
        userTable.setModel(userListModel);
        userTable.removeColumn(userTable.getColumnModel().getColumn(1));

        postListModel = createUsersModel();
        postTable.setModel(postListModel);
        postTable.removeColumn(postTable.getColumnModel().getColumn(3));
    }
}
