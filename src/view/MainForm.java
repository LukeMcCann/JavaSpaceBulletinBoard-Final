package view;

import controller.MenuController;
import model.UserEntry;
import org.w3c.dom.html.HTMLObjectElement;

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
    private static final int OWNER_ID = 2;
    private static final int TOPIC_ID = 3;
    private static DefaultTableCellRenderer dtcr;


    public MainForm(UserEntry user)
    {
        this.user = user;
        init();
        setTopicModel();
        listen();
    }

    private void init()
    {
        controller = new MenuController(this, this.user);
        setPreferredSize(new Dimension(500, 750));
        tbl_topicList.setShowHorizontalLines(true);
        tbl_topicList.setShowVerticalLines(true);

        setTableRules();
        getContentPane().add(pnl_main);
        setTitle("BulletinBoard - Logged in as: " + user.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    private void setTopicModel()
    {
        topicsModel = controller.createTopicModel();

        try
        {
            tbl_topicList.setModel(topicsModel);
            tbl_topicList.removeColumn(
                    tbl_topicList.getColumnModel().getColumn(TOPIC_ID));

            tbl_topicList.removeColumn(
                    tbl_topicList.getColumnModel().getColumn(OWNER_ID));


            tbl_topicList.setVisible(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(rootPane,
                    "Error " + e.getMessage());
        }

    }

    private void setTableRules()
    {
        tbl_topicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }


    private void listen()
    {
        btn_create.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                controller.newTopicButtonPress();
                controller.refresh(tbl_topicList, TOPIC_ID, OWNER_ID);
            }
        });

        btn_refresh.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                controller.refresh(tbl_topicList, TOPIC_ID, OWNER_ID);
            }
        });


        tbl_topicList.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                if(tbl_topicList.getSelectedRow() > -1)
                {
                    int selectedIndex = tbl_topicList.getSelectedRow();
                    btn_join.setEnabled(true);

                    if(topicsModel.getValueAt(selectedIndex,
                            OWNER_ID).equals(user.getID()))
                    {
                        btn_delete.setEnabled(true);
                    }
//                    System.out.println(tbl_topicList.getSelectedRow());
//                    System.out.println(selectedIndex);
//                    System.out.println(OWNER_ID + " : " + user.getID() );
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
