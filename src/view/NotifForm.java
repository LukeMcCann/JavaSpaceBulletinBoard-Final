package view;

import controller.MenuController;
import controller.TopicController;
import model.UserEntry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class NotifForm extends JFrame
{
    private JPanel pnl_main;
    private JScrollPane scrl_notif;
    private JTable tbl_notifs;
    private JButton btn_remove;

    private UserEntry user;
    private MenuController controller;

    private DefaultTableModel model;

    public NotifForm(UserEntry user, MenuController controller, DefaultTableModel model)
    {
        this.user = user;
        this.controller = controller;
        this.model = model;

        getContentPane().add(pnl_main);
        init();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(new Dimension(1000, 750));
        setTitle("Notifications - Logged in as: " + user.getUsername());
        setVisible(true);

        listen();
    }

    private void init()
    {
        makeTableUneditable(tbl_notifs);
        tbl_notifs.setModel(model);
    }

    private void makeTableUneditable(JTable table)
    {
        for(int i = 0; i < table.getColumnCount(); i++)
        {
            Class<?> columnClass = table.getColumnClass(i);
            table.setDefaultEditor(columnClass, null);
        }
    }

    private void listen()
    {
        scrl_notif.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                refresh();
            }
        });
    }

    private void refresh()
    {
        controller.updateNotifModel(tbl_notifs);
        init();
    }
}
