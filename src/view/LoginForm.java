package view;

import controller.LoginController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame
{
    private JPanel pnl_main;
    private JLabel lbl_username;
    private JTextField tf_username;
    private JLabel lbl_password;
    private JPasswordField tf_password;
    private JButton btn_login;
    private JButton registerButton;

    private LoginController controller;

    public LoginForm()
    {
        init();
        listen();
    }

    private void init()
    {
        controller = new LoginController(this);
        setPreferredSize(new Dimension(500, 250));
        getContentPane().add(pnl_main);
        setTitle("Login/Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
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
    }
}
