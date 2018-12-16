package view;

import controller.MenuController;
import controller.TopicController;
import model.TopicEntry;
import model.UserEntry;
import org.apache.commons.lang3.StringUtils;
import util.UserUtils;
import util.helper.SpaceSearcher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.UUID;

public class TopicForm extends JFrame
{
    private JPanel pnl_main;
    private JScrollPane scrl_postList;
    private JTable tbl_userList;
    private JTextArea ta_message;
    private JPanel pnl_buttons;
    private JButton btn_send;
    private JButton sendPrivateButton;
    private JTable tbl_postList;
    private JScrollPane srcl_ustList;

    private UserEntry user;
    private TopicEntry topic;
    private static int MAX_MESSAGE_SIZE = 50;
    private TopicController controller;

    private DefaultTableModel postListModel;
    private DefaultTableModel usersListModel;

    private SpaceSearcher sp_searcher = SpaceSearcher.getSpaceSearcher();
    private UserEntry selectedUser;
    private UserEntry previousSelectedUser;

    public TopicForm(UserEntry user, TopicEntry topic)
    {
        this.controller = new TopicController(this, user, topic);

        getContentPane().add(pnl_main);
        postListSetup();

        userListSetup();
        makeTableUneditable(tbl_userList);

        setRules();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(new Dimension(1000, 750));
        setTitle("Topic - " + topic.getTitle());
        setVisible(true);

        listen();
    }

    private void postListSetup()
    {
        postListModel = controller.createPostsModel();
        tbl_postList.setModel(postListModel);
        tbl_postList.removeColumn(tbl_postList.getColumnModel().getColumn(3));
        tbl_postList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        makeTableUneditable(tbl_postList);

        tbl_postList.getColumnModel().getColumn(0).setPreferredWidth(170);
        tbl_postList.getColumnModel().getColumn(1).setPreferredWidth(170);
        tbl_postList.getColumnModel().getColumn(2).setPreferredWidth(169);
    }
    private void userListSetup()
    {
        usersListModel = controller.createUsersModel();
        tbl_userList.setModel(usersListModel);
        tbl_userList.removeColumn(tbl_userList.getColumnModel().getColumn(1));
        tbl_userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl_userList.getColumnModel().getColumn(0).setPreferredWidth(450);
    }

    @Override
    public void dispose()
    {
        controller.closeServices(this);
    }

    /**
     *
     * @Reference https://stackoverflow.com/questions/30699772/when-to-use-super-render-
     *                      super-disposesuper-pause-and-super-resizew-h
     */
    public void superDispose() {super.dispose();}

    private void makeTableUneditable(JTable table)
    {
        for(int i = 0; i < table.getColumnCount(); i++)
        {
            Class<?> columnClass = table.getColumnClass(i);
            table.setDefaultEditor(columnClass, null);
        }
    }

    private void setRules()
    {
        ta_message.setWrapStyleWord(true);
        tbl_postList.setShowHorizontalLines(true);
        tbl_postList.setShowVerticalLines(true);
        tbl_postList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl_userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl_userList.setShowVerticalLines(true);
        tbl_userList.setShowHorizontalLines(true);
        tbl_postList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbl_userList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        ta_message.setFont(new Font("Serif", Font.ITALIC, 16));
        ta_message.setLineWrap(true);
        ta_message.setWrapStyleWord(true);
    }

    private void refresh()
    {
        controller.refreshUserModel(
                usersListModel, tbl_userList, 1);
        controller.refreshPostModel(
                postListModel, tbl_postList, 3);
    }

    private void listen()
    {
        /**
         * Limits the character count for messages to 50.
         */
        ta_message.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                super.keyTyped(e);
                if(ta_message.getText().length() > MAX_MESSAGE_SIZE+1)
                {
                    e.consume();
                    String shortened = ta_message.getText().substring(0, MAX_MESSAGE_SIZE);
                    ta_message.setText(shortened);
                }
                else if(ta_message.getText().length() > MAX_MESSAGE_SIZE)
                {
                    e.consume();
                }
            }
        });

        tbl_userList.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                int selected = tbl_userList.getSelectedRow();

                if(selected != -1)
                {
                    if(selectedUser == null ||
                            StringUtils.isBlank(selectedUser.getUsername()))
                    {
                        // is first selected user
                        selectedUser = sp_searcher.getUserByUsername(
                            tbl_userList.getValueAt(selected, 0).toString());
                    }
                    else
                    {
                        previousSelectedUser = selectedUser;
                        selectedUser = null;
                        tbl_userList.getSelectionModel().clearSelection();
                    }
                }
            }
        });
    }

}
