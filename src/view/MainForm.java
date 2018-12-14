package view;

import controller.MenuController;
import model.UserEntry;

import javax.swing.*;
import java.awt.*;

public class MainForm extends JFrame
{
    private JPanel pnl_main;
    private JScrollPane scrl_topicList;
    private JTable tbl_topicList;
    private JPanel pnl_buttons;
    private JButton btn_join;
    private JButton btn_delete;
    private JButton refreshButton;
    private JButton btn_logout;
    private JButton btn_create;

    private UserEntry user;
    private MenuController controller;

    public MainForm(UserEntry user)
    {
        this.user = user;
        init();
    }

    private void init()
    {
        controller = new MenuController(this);
        setPreferredSize(new Dimension(500, 750));
        getContentPane().add(pnl_main);
        setTitle("BulletinBoard - Logged in as: " + user.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }
}
