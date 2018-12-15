package controller;

import model.TopicEntry;
import model.UserEntry;
import org.apache.commons.lang3.StringUtils;
import util.TopicUtils;
import view.MainForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.UUID;

/**
 *
 *
 * @Reference https://stackoverflow.com/questions/
 *              11095802/populate-jtable-using-list
 */
public class MenuController
{
    private TopicUtils topicUtils = TopicUtils.getTopicUtils();
    private MainForm mainForm;
    private UserEntry user;
    private DefaultTableModel topicsModel;

    public MenuController(MainForm form, UserEntry user)
    {
        this.mainForm = form;
        this.user = user;
    }

    /**
     * Create a new topic
     *
     * @param title - title of the topic
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

    /**
     * Creates a DefaultTableModel for displaying topics in JTable.
     * This method should only be used when updating the model is neccessary
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
        if(topicCollection.size() > 0 &&
                topicCollection != null)
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


    // Button Handlers

    /**
     *
     * @Reference https://alvinalexander.com/
     *             java/joptionpane-showinputdialog-examples
     */
    public void newTopicButtonPress()
    {
        String title = JOptionPane.showInputDialog(mainForm,
                user.getUsername() +
                        " please enter a title for your new topic!");

        if(StringUtils.isNotBlank(title))
        {
            if(title.replaceAll("[^a-zA-Z0-9]", "").length() >= 3)
            {
                // title contains at least 3 normal chars
                createTopic(title);
            }
            else
            {
                JOptionPane.showMessageDialog(mainForm,
                        "Title must contain at leas 3 normal characters.");
            }
        }
        else
        {
            // title is blank
            JOptionPane.showMessageDialog(mainForm,
                    "Title cannot be blank.");
        }
    }

    public void deleteButtonPress(UUID topicID, int row)
    {
        JOptionPane.showMessageDialog(mainForm, "TODO: Implement Delete!");
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
}