package controller;

import model.DummyTopicDeleted;
import model.DummyUserInTopic;
import model.TopicEntry;
import model.UserEntry;
import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.export.Exporter;
import net.jini.space.JavaSpace05;
import org.apache.commons.lang3.StringUtils;
import util.SpaceUtils;
import util.TopicUtils;
import util.UserUtils;
import util.helper.EntrySearcher;
import util.helper.SpaceSearcher;
import util.helper.TransactionBuilder;
import util.listener.TopicAddedListener;
import util.listener.TopicRemovedListener;
import view.LoginForm;
import view.MainForm;
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
 * MenuController -
 *          Handles all logic for the MainMenu page
 *          this is the page where all topics are displayed.
 *
 * @Reference https://stackoverflow.com/questions/
 *                11095802/populate-jtable-using-list
 *
 *                https://www.programiz.com/
 *                java-programming/multidimensional-array
 *
 *                https://docs.oracle.com/javase/7/docs/api/
 *                javax/swing/table/DefaultTableModel.html
 *
 *                https://alvinalexander.com/
 *                java/joptionpane-showinputdialog-examples
 */
public class MenuController
{
    private TopicUtils topicUtils = TopicUtils.getTopicUtils();
    private UserUtils userUtils = UserUtils.getUserutils();

    private MainForm mainForm;
    private UserEntry user;

    private DefaultTableModel topicsModel;
    private DefaultTableModel notifsModel;

    private TopicController controller;

    private EventRegistration topicAddedRegister;
    private EventRegistration topicRemovedRegister;
    private RemoteEventListener topicAddedListener;
    private RemoteEventListener topicRemovedListener;

    private static List<TopicEntry> topicOfInterest = new ArrayList<>();

    private EntrySearcher e_searcher = new EntrySearcher();
    private SpaceSearcher s_searcher = SpaceSearcher.getSpaceSearcher();

    // Constructor
    public MenuController(MainForm form, UserEntry user)
    {
        this.mainForm = form;
        this.user = user;
        controller = controller;
        registerTopicRemovedListener();
        registerTopicAddedListener();
    }

    /**
     * Adds a new TopicEntry to the space.
     *
     * @param title - title of the topic to create.
     */
    public void createTopic(String title)
    {
        TopicEntry topic = new TopicEntry(title, user);

        try
        {
            topicUtils.createTopic(topic);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainForm,
                    "Failed to create topic: " + topic.getTitle());
        }
    }


    // Create Table Models

    /**
     * Creates a DefaultTableModel of all topic data.
     * ID's are no longer utilised due to conflicts in UUID generation.
     * They have been hidden until a better solution is found, for now
     * utilises the title as a identifier with checks to ensure no two topics
     * can contain the same title.
     *
     * @Reference https://stackoverflow.com/questions/
     *                11095802/populate-jtable-using-list
     *
     *                https://www.programiz.com/
     *                java-programming/multidimensional-array
     *
     *                https://docs.oracle.com/javase/7/docs/api/
     *                javax/swing/table/DefaultTableModel.html
     */
    public DefaultTableModel createTopicModel()
    {
        // set columns
        Object[] columns = {
                "Topic", "Owner",
                "Topic_ID", "Owner_ID"
        };

        // get a list of all topics in space
        List<TopicEntry> topicCollection =
                topicUtils.getTopics();

        // create multidimensional array
        Object[][] content = {};

        // put all items from collection into array
        if(topicCollection != null && topicCollection.size() > 0)
        {
            // create new object array of correct dimensions
            content = new Object[topicCollection.size()][5];
            for(int i = 0; i < topicCollection.size(); i++)
            {
                // iterate through topicCollection adding to content array
                content[i][0] = topicCollection.get(i).getTitle();
                content[i][1] = topicCollection.get(i).getOwner().getUsername();
                content[i][2] = topicCollection.get(i).getOwner().getID();
                content[i][3] = topicCollection.get(i).getID();
            }
        }
        topicsModel =
                new DefaultTableModel(content, columns);

        return topicsModel;
    }

    public DefaultTableModel createNotifModel()
    {

        // set columns
        Object[] columns = {
                "Topic", "Owner", "Topic_ID"
        };

        // create multidimensional array
        Object[][] content = {};

        // put all items from collection into array
        if(topicOfInterest != null && topicOfInterest.size() > 0)
        {
            // create new object array of correct dimensions
            content = new Object[topicOfInterest.size()][3];
            for(int i = 0; i < topicOfInterest.size(); i++)
            {
                // iterate through topicCollection adding to content array
                content[i][0] = topicOfInterest.get(i).getTitle();
                content[i][1] = topicOfInterest.get(i).getOwner().getUsername();
                content[i][2] = topicOfInterest.get(i).getID();
            }
        }
        notifsModel =
                new DefaultTableModel(content, columns);

        return notifsModel;
    }



    // New Topic

    /**
     * Handles the button press for a new topic.
     * Checks the title is valid, creates topic and refreshes the TableModel.
     *
     * @Reference https://alvinalexander.com/
     *             java/joptionpane-showinputdialog-examples
     */
    public void newTopicButtonPress(JTable table)
    {
        String title = JOptionPane.showInputDialog(mainForm,
                user.getUsername() +
                        " please enter a title for your new topic!");

        if(StringUtils.isNotBlank(title))
        {
                createTopic(title);
                refresh(table, 3,2);
        }
        else
        {
            // title is blank
            JOptionPane.showMessageDialog(mainForm,
                    "Title cannot be blank.");
        }
    }


    // Delete

    /**
     * Handles the button press for delete.
     *
     * @param topic - the topic to be deleted.
     */
    public void deleteButtonPress(TopicEntry topic)
    {
        if(topic != null)
        {
            topicUtils.delete(topic, user);
        }
        else
        {
            JOptionPane.showMessageDialog(mainForm,
                    "Failed to delete topic.  Topic may not exist!");
        }
    }


    // Remove

    /**
     * Removes the UserEntry from all topics
     */
    private void leaveAllTopics()
    {
        JavaSpace05 space = SpaceUtils.getSpace();
        DummyUserInTopic template = new DummyUserInTopic();
        template.getUser().setUsername(user.getUsername());

        try
        {
            List<DummyUserInTopic> users = e_searcher.readAllMatchingEntries(space, template);
            for(DummyUserInTopic x : users)
            {
                topicUtils.removeUserFrom(x.getUser(), x.getTopic());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    //Join

    /**
     * Handles the join button press.
     *
     * @param topic - topic to join
     */
    public void joinTopicButtonPress(TopicEntry topic)
    {
        if(topic != null)
        {
            joinTopic(topic);
        }
        else
        {
            JOptionPane.showMessageDialog(mainForm,
                    "Failed to join topic.  Topic does not exist!");
        }
    }

    /**
     * Checks if a user is already in a topic
     * if <code>false</code> join topic, <code>else</code> notify user.
     *
     * @param topic - the topic to join.
     */
    private void joinTopic(TopicEntry topic)
    {
        if(userInTopic(topic))
        {
            JOptionPane.showMessageDialog(mainForm,
                    "You are already in this topic.");
            return;
        }
        else
        {
            new TopicForm(this.user, topic);
        }
    }


    // Convenience/Utility Methods

    /**
     * Iterates through a list of DummyUserInTopic entries checking if any match.
     *
     * @param topic - the topic to search for user
     *
     * @return <code>true</code> if user is found else <code>false</code>
     */
    private boolean userInTopic(TopicEntry topic)
    {
        List<DummyUserInTopic> users =
                topicUtils.getAllUsersFromTopic(topic);

        for(DummyUserInTopic user : users)
        {
            if(user.getUser().equals(this.user))
            {
                // current user found in topic
                return true;
            }
        }
        return false;
    }


    // Listeners

    private void registerTopicAddedListener()
    {
        JavaSpace05 space = SpaceUtils.getSpace();
        TopicEntry template = new TopicEntry();
        ArrayList<TopicEntry> templateCollection = new ArrayList<>(1);
        templateCollection.add(template);
        Exporter exporter = userUtils.getBasicJeriExporter();
        try
        {
            TopicAddedListener eventListener = new TopicAddedListener(this);
            topicAddedListener = (RemoteEventListener) exporter.export(eventListener);

            topicAddedRegister
                    = space.registerForAvailabilityEvent(templateCollection, null, true,
                    topicAddedListener, Lease.FOREVER, null);
        }
        catch (Exception e)
        {
            System.err.println("Failed to get new topic(s)");
            e.printStackTrace();
        }
    }

    /**
     * Register topic removed event lsitener
     */
    private void registerTopicRemovedListener() {
        JavaSpace05 space = SpaceUtils.getSpace();
        DummyTopicDeleted template = new DummyTopicDeleted();
        ArrayList<DummyTopicDeleted> templates = new ArrayList<>(1);
        templates.add(template);
        Exporter exporter = userUtils.getBasicJeriExporter();
        try
        {
            TopicRemovedListener eventListener = new TopicRemovedListener(this, null);
            topicRemovedListener = (RemoteEventListener) exporter.export(eventListener);

            topicAddedRegister =
                    space.registerForAvailabilityEvent(templates, null, true,
                    topicRemovedListener, Lease.FOREVER, null);
        }
        catch (Exception e)
        {
            System.err.println("Failed to get topic.");
            e.printStackTrace();
        }
    }

    /**
     * Refreshes the topic list.
     *
     * @param table - the table to refresh
     * @param index1 - ID column ot be removed
     * @param index2 - ID column to be removed
     */
    public void refresh(JTable table, int index1, int index2)
    {
        topicsModel = createTopicModel();
        table.setModel(topicsModel);

        table.removeColumn(table.getColumnModel().getColumn(index1));
        table.removeColumn(table.getColumnModel().getColumn(index2));
    }

    public void updateNotifModel(JTable table)
    {
        notifsModel = createNotifModel();
        table.setModel(notifsModel);
    }

    public void markButtonPress(String topic)
    {
        TopicEntry selectedTopic = topicUtils.getTopicByTitle(topic);
        markAsTopicOfInterest(selectedTopic);
        findAllLooselyMatchingInSpace(selectedTopic);
        createNotifModel();
    }

    /**
     * @param topic - the topic to mark
     */
    public void markAsTopicOfInterest(TopicEntry topic)
    {
        TopicEntry template = new TopicEntry();
        template.noSpecialTitle = topic.noSpecialTitle;
        findAllLooselyMatchingInSpace(template);
    }

    /**
     * Finds all topics with similartitles
     *
     * @param template - the template to find
     */
    public void findAllLooselyMatchingInSpace(TopicEntry template)
    {
        try
        {
            Transaction transaction = TransactionBuilder.getTransaction();
            List<TopicEntry> interestList =
                    e_searcher.readAllMatchingEntries(SpaceUtils.getSpace(), transaction, template);

            for(int i = 0; i < interestList.size(); i++)
            {
                TopicEntry topic = interestList.get(i);
                topicOfInterest.add(topic);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    // Notify

    public void logout()
    {
        leaveAllTopics();
        mainForm.dispose();
        new LoginForm();
    }


    // Getters

    public DefaultTableModel getTopicsModel()
    {
        return topicsModel;
    }

    public DefaultTableModel getNotifsModel()
    {
        return notifsModel;
    }
}
