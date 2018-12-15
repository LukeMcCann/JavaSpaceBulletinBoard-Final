package view;

import controller.LoginController;
import org.apache.commons.lang3.StringUtils;
import util.SpaceUtils;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginForm extends JFrame
{
    private JPanel pnl_main;
    private JLabel lbl_username;
    private JTextField tf_username;
    private JLabel lbl_password;
    private JPasswordField tf_password;
    private JButton btn_login;
    private JButton registerButton;
    private JButton btn_host;
    private JTextField tf_host;

    private LoginController controller;

    public LoginForm()
    {
        init();
        listen();
    }

    private void init()
    {
        controller = new LoginController(this);
        tf_host.setText(SpaceUtils.getHostname());
        setPreferredSize(new Dimension(500, 250));
        getContentPane().add(pnl_main);
        setTitle("Login/Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        tf_username.requestFocus();
        setVisible(true);
    }

    private void listen()
    {
        registerButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                controller.registerUser(tf_username.getText(),
                        new String(tf_password.getPassword()));
            }
        });

        btn_login.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                controller.loginUser(tf_username.getText(),
                        new String(tf_password.getPassword()));
            }
        });


        btn_host.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                String response = tf_host.getText();

                if(response.isEmpty())
                {
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Please enter a host address.");
                }
                else
                {
                    SpaceUtils.setHostname(response);
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "New host: " + response);
                }
            }
        });

        tf_host.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e)
            {
                super.keyTyped(e);
                String response = tf_host.getText();

                if(response.isEmpty())
                {
                    btn_host.setEnabled(false);
                }
                else
                {
                    btn_host.setEnabled(true);
                }
            }
        });

    }
}
