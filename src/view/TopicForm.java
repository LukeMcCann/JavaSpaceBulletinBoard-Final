package view;

import model.TopicEntry;
import model.UserEntry;

import javax.swing.*;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TopicForm extends JFrame
{
    private JPanel pnl_main;
    private JScrollPane scrl_userList;
    private JTable tbl_userList;
    private JTextArea ta_message;
    private JPanel pnl_buttons;
    private JButton btn_send;
    private JButton sendPrivateButton;
    private JTable tbl_postList;
    private JScrollPane srcl_postList;

    private UserEntry user;
    private TopicEntry topic;
    private static int MAX_MESSAGE_SIZE = 50;
//    private TopicController controller;

    public TopicForm(UserEntry user, TopicEntry topic)
    {
//        this.controller = new TopicController(this, user, topic);
        this.user = user;
        this.topic = topic;

        setRules();
        setPreferredSize(new Dimension(500, 750));

        pack();
        setVisible(true);
    }

    private void setRules()
    {
        ta_message.setWrapStyleWord(true);
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
    }

}
