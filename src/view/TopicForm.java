package view;

import controller.MenuController;
import controller.TopicController;
import model.TopicEntry;
import model.UserEntry;
import org.apache.commons.lang3.StringUtils;
import util.UserUtils;
import util.helper.CellWrapRenderer;
import util.helper.SpaceSearcher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TopicForm extends JFrame
{
    private JPanel pnl_main;
    private JScrollPane scrl_postList;
    private JTable tbl_userList;
    private JTextArea ta_message;
    private JPanel pnl_buttons;
    private JButton btn_send;
    private JButton btn_secret;
    private JTable tbl_postList;
    private JScrollPane srcl_ustList;

    private UserEntry user;
    private TopicEntry topic;
    private static int MAX_MESSAGE_SIZE = 280;
    private TopicController controller;

    private DefaultTableModel postListModel;
    private DefaultTableModel usersListModel;

    private SpaceSearcher sp_searcher = SpaceSearcher.getSpaceSearcher();
    private UserEntry selectedUser;
    private UserEntry previousSelectedUser;

    public TopicForm(UserEntry user, TopicEntry topic)
    {
        this.controller = new TopicController(this, user, topic);
        this.user = user;
        this.topic = topic;

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
        btn_secret.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(getSelectedUser() == null)
                {
                    JOptionPane.showMessageDialog(TopicForm.this,
                            "Please select a user!");
                }
                else
                {
                    new SecretChatForm(user, getSelectedUser(), topic, controller);
                }
            }
        });
    }

    private void postListSetup()
    {
        postListModel = controller.createPostsModel();
        tbl_postList.setModel(postListModel);
        tbl_postList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        makeTableUneditable(tbl_postList);

        tbl_postList.getColumnModel().getColumn(0).setPreferredWidth(170);
        tbl_postList.getColumnModel().getColumn(1).setPreferredWidth(170);
        tbl_postList.getColumnModel().getColumn(2).setPreferredWidth(169);

        tbl_postList.getColumnModel().getColumn(2).setCellRenderer(new CellWrapRenderer());
    }

    private void userListSetup()
    {
        usersListModel = controller.createUsersModel();
        tbl_userList.setModel(usersListModel);
        tbl_userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl_userList.getColumnModel().getColumn(0).setPreferredWidth(450);
    }

    private void refresh()
    {
        controller.refresh(tbl_postList, tbl_userList);
        postListSetup();
        userListSetup();
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

    private UserEntry getSelectedUser()
    {
        UserEntry selectedUser = null;
        int selectedIndex = tbl_userList.getSelectedRow();
        if(tbl_userList.getSelectedRow() != -1)
        {
            // a user is selected
            selectedUser = sp_searcher.getUserByUsername(
                            tbl_userList.getModel().getValueAt(selectedIndex, 0).toString());
        }
            return selectedUser;
    }

    /**
     * Gets the message from the TextArea removing the TO:
     *
     * @param ta - the text area to get from
     * @return - the standalong message
     */
    private String getMessageFromField(JTextArea ta)
    {
        String message = null;
        Pattern pattern = Pattern.compile(";");
        Matcher matcher = pattern.matcher(ta.getText());
        if(matcher.find())
        {
            message = ta.getText().substring(
                    matcher.start()+1, ta_message.getText().length());

            System.out.println(message);
            return message;
        }
        return message;
    }

    private void listen()
    {
        btn_send.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                String message = ta_message.getText();
                controller.sendButtonPress(message, user, getSelectedUser(), topic);

                refresh();
            }
        });
        /**
         * Limits the character count for messages.
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
                    }
                }
            }
        });

        ta_message.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                super.focusGained(e);
                if(ta_message.getText().equals("Write your message..."))
                {
                    ta_message.setText(null);
                }
                refresh();
            }


            @Override
            public void focusLost(FocusEvent e)
            {
                super.focusLost(e);
                if(StringUtils.isBlank(ta_message.getText()) ||
                        ta_message.getText().isEmpty() ||
                        ta_message.getText() == null ||
                        ta_message.getText().equals("TO: "))
                {
                    ta_message.setText("Write your message...");
                }
            }
        });

        tbl_postList.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                refresh();
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

        tbl_userList.addMouseListener(new MouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent mouseEvent)
            {
                int selectedIndex = tbl_userList.getSelectedRow();
                if(tbl_userList.getSelectedRow() != -1)
                {
                    // a user is selected
                    UserEntry selectedUser =
                            sp_searcher.getUserByUsername(
                                    tbl_userList.getModel().getValueAt(selectedIndex, 0).toString());
                    try
                    {
                        if(ta_message.getText().equals("Write your message...")) {ta_message.setText(null);}
                        ta_message.getDocument().insertString(0, "TO: " +
                                selectedUser.getUsername() +" ; \n", null);

                    }
                    catch (BadLocationException be)
                    {
                        be.printStackTrace();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                refresh();
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
            }
        });
    }

}
