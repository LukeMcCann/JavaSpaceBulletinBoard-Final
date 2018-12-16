package view;

import controller.MenuController;
import model.TopicEntry;
import model.UserEntry;
import org.w3c.dom.html.HTMLObjectElement;
import util.TopicUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.UUID;

public class MainForm extends JFrame
{
    private JPanel pnl_main;
    private JScrollPane scrl_topicList;
    private JTable tbl_topicList;
    private JPanel pnl_buttons;
    private JButton btn_join;
    private JButton btn_delete;
    private JButton btn_refresh;
    private JButton btn_logout;
    private JButton btn_create;

    private UserEntry user;
    private MenuController controller;
    private DefaultTableModel topicsModel;
    private TopicUtils topicUtils = TopicUtils.getTopicUtils();
    private static final int OWNER_INDEX = 2;
    private static final int TOPIC_INDEX = 3;
    private int selectedIndex;
    private static DefaultTableCellRenderer dtcr;


    public MainForm(UserEntry user)
    {
        this.user = user;
        init();
    }

    private void makeCellsUneditable(JTable table)
    {
        for (int i = 0; i < table.getColumnCount(); i++)
        {
            Class<?> columnClass = table.getColumnClass(i);
            table.setDefaultEditor(columnClass, null);
        }
    }

    private void init()
    {
        controller = new MenuController(this, user);
        getContentPane().add(pnl_main);

        topicsModel = controller.createTopicModel();
        tbl_topicList.setModel(topicsModel);

        tbl_topicList.removeColumn(tbl_topicList.getColumnModel().getColumn(TOPIC_INDEX));
        tbl_topicList.removeColumn(tbl_topicList.getColumnModel().getColumn(OWNER_INDEX));

        tbl_topicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        makeCellsUneditable(tbl_topicList);
        listen();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(500, 750));
        setTitle("BulletinBoard - Logged in as: " + user.getUsername());

        pack();
        setVisible(true);
    }

    private void listen()
    {
        btn_create.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                controller.newTopicButtonPress(tbl_topicList);
            }
        });

        btn_refresh.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                controller.refresh(tbl_topicList, TOPIC_INDEX, OWNER_INDEX);
            }
        });

        btn_join.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                int selectedIndex =
                        tbl_topicList.getSelectedRow();

                if(selectedIndex == -1)
                {
                    JOptionPane.showMessageDialog(MainForm.this,
                            "Failed to join topic: " + "None Selected");
                }
                else
                {
                    // topic selected
                    String topicJoin =
                            tbl_topicList.getModel().getValueAt(
                                    selectedIndex, 0).toString();

                    System.out.println(topicUtils.getTopicByTitle(topicJoin).getTitle());
//                    System.out.println(selectedIndex);
//                    System.out.println(topicJoinID);

                    TopicEntry selectedTopic =
                            topicUtils.getTopicByTitle(topicJoin);

                    String title =
                            selectedTopic.getTitle();

                    System.out.println(title);

                    int response = JOptionPane.showConfirmDialog(
                            MainForm.this,
                            "Join: " + title + "?",
                            "Joining: " + title,
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null);

                    if(response == 0)
                    {
                        // join
                        controller.joinTopicButtonPress(selectedTopic);
                    }
                }
            }
        });

        /**
         * Listener for deleting a topic
         *
         * Checks the index and compares user id to the id in the owner column
         * if the topic exists and the user owns it it is deleted
         */
        btn_delete.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                selectedIndex =
                        tbl_topicList.getSelectedRow();

                System.out.println(selectedIndex);
                if(selectedIndex != -1)
                {
                    if(user.getID().equals(
                            tbl_topicList.getModel().getValueAt(
                                    selectedIndex, OWNER_INDEX)))
                    {
                        String title =
                                tbl_topicList.getModel().getValueAt(
                                        selectedIndex, 0).toString();

                        TopicEntry topicToDelete = topicUtils.getTopicByTitle(title);

                        int response = JOptionPane.showConfirmDialog(MainForm.this,
                                "Delete: " + topicToDelete.getTitle() + "?",
                                "Confirm Deletion",
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null);

                        if(response == JOptionPane.YES_OPTION)
                        {
                            controller.deleteButtonPress(topicToDelete);
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(MainForm.this,
                                    "Delete cancelled.");
                        }
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(MainForm.this,
                                "You do not own this topic!");
                    }
                }
                else
                {
                    JOptionPane.showInternalMessageDialog(MainForm.this,
                            "Failed to delete topic.  " +
                                    "No topic selected",
                            "Topic Deletion Failed",
                            JOptionPane.ERROR_MESSAGE, null);
                }
                controller.refresh(tbl_topicList, TOPIC_INDEX, OWNER_INDEX );
            }
        });


        tbl_topicList.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);

                selectedIndex =
                        tbl_topicList.getSelectedRow();

                if(selectedIndex != -1)
                {
                    btn_join.setEnabled(true);

//                    System.out.println(topicsModel.getValueAt(selectedIndex, 0));
//                    System.out.println(topicsModel.getValueAt(selectedIndex, 0));
                    if(tbl_topicList.getModel().getValueAt(
                            selectedIndex, OWNER_INDEX).equals(user.getID()))
                    {
                        btn_delete.setEnabled(true);
                    }
                    else
                    {
                        btn_delete.setEnabled(false);
                    }
                }
                else
                {
                    btn_delete.setEnabled(false);
                    btn_join.setEnabled(false);
                }
            }
        });

    }
}
