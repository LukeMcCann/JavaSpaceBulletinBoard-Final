package view;

import controller.TopicController;
import model.TopicEntry;
import model.UserEntry;
import util.helper.CellWrapRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SecretChatForm extends JFrame
{
    private JPanel pnl_main;
    private JTable tbl_privateChat;
    private JScrollPane scrl_privateChat;
    private JPanel pnl_buttons;
    private JTextArea ta_chat;
    private JButton btn_send;

    private UserEntry user;
    private UserEntry otherUser;
    private TopicEntry topic;
    private TopicController controller;

    private DefaultTableModel privateModel;
    private int MAX_MESSAGE_SIZE = 300;

    public SecretChatForm(UserEntry user, UserEntry otherUser, TopicEntry topic, TopicController controller)
    {
        this.user = user;
        this.otherUser = otherUser;
        this.topic = topic;
        this.controller = controller;

        getContentPane().add(pnl_main);
        init();
        privateModelSetup();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(new Dimension(1000, 750));
        setTitle("Secret Chat - " + otherUser.getUsername());
        setVisible(true);

        listen();
    }

    private void privateModelSetup()
    {
        privateModel = controller.createPrivateChatModel();
        tbl_privateChat.setModel(privateModel);

        tbl_privateChat.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        makeTableUneditable(tbl_privateChat);

        tbl_privateChat.getColumnModel().getColumn(0).setPreferredWidth(170);
        tbl_privateChat.getColumnModel().getColumn(1).setPreferredWidth(170);
        tbl_privateChat.getColumnModel().getColumn(2).setPreferredWidth(169);

        tbl_privateChat.getColumnModel().getColumn(2).setCellRenderer(new CellWrapRenderer());
    }

    private void init()
    {
        ta_chat.setFont(new Font("Serif", Font.ITALIC, 16));
        ta_chat.setLineWrap(true);
        ta_chat.setWrapStyleWord(true);
    }

    private void makeTableUneditable(JTable table)
    {
        for(int i = 0; i < table.getColumnCount(); i++)
        {
            Class<?> columnClass = table.getColumnClass(i);
            table.setDefaultEditor(columnClass, null);
        }
    }

    public void listen()
    {
        btn_send.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                controller.forcePrivateSend(user, otherUser, topic, ta_chat.getText());
                privateModelSetup();
            }
        });

        ta_chat.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                super.keyTyped(e);
                if(ta_chat.getText().length() > MAX_MESSAGE_SIZE+1)
                {
                    e.consume();
                    String shortened = ta_chat.getText().substring(0, MAX_MESSAGE_SIZE);
                    ta_chat.setText(shortened);
                }
                else if(ta_chat.getText().length() > MAX_MESSAGE_SIZE)
                {
                    e.consume();
                }
            }
        });

    }
}
