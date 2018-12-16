package controller;

import model.DummyUserInTopic;
import model.PostEntry;
import model.TopicEntry;
import model.UserEntry;
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

    public TopicController(TopicForm form, UserEntry user, TopicEntry topic)
    {
        this.topicForm = form;
        this.user = user;
        this.topic = topic;
        this.topicUtils = TopicUtils.getTopicUtils();
        topicUtils.registerUserTo(user, topic); // regitser user to topic
    }

    public void sendButtonPress()
    {
        JOptionPane.showMessageDialog(null,
                "TODO");
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
                directMessageList.add(post);
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

    /**
     * Safely removes entries
     */
    public void closeServices()
    {
        topicUtils.removeUserFrom(user, topic);
        topicForm.dispose();
    }

    public List<PostEntry> getDirectMessages()
    {
        return directMessageList;
    }

}
