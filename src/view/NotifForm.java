package view;

import controller.MenuController;
import controller.TopicController;
import model.UserEntry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

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
        btn_remove.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if(tbl_notifs.getSelectedRow() != -1)
                {
                   int response =  JOptionPane.showOptionDialog(null,
                            "Remove notifications for: " +
                                    tbl_notifs.getValueAt(tbl_notifs.getSelectedRow(), 0).toString(),
                            "Remove?", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, null, null);

                   if(response == JOptionPane.YES_OPTION)
                   {
                       model.removeRow(tbl_notifs.getSelectedRow());
                   }
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Please select a item to remove.");
                }
            }
        });
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
        scrl_notif.addMouseMotionListener(new MouseMotionAdapter()
        {
            @Override
            public void mouseMoved(MouseEvent e)
            {
                super.mouseMoved(e);
                controller.updateNotifModel(tbl_notifs);
            }
        });

        scrl_notif.addMouseListener(new MouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent mouseEvent)
            {
                controller.updateNotifModel(tbl_notifs);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                controller.updateNotifModel(tbl_notifs);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                controller.updateNotifModel(tbl_notifs);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                controller.updateNotifModel(tbl_notifs);
            }
        });
    }

}
