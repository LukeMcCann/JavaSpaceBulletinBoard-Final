package controller;

import model.UserEntry;
import net.jini.space.JavaSpace05;
import org.apache.commons.lang3.StringUtils;
import util.SpaceUtils;
import util.UserUtils;
import view.LoginForm;

import javax.swing.*;

public class LoginController
{
    private LoginForm loginForm;
    private static final UserUtils userUtils = UserUtils.getUserutils();
    private JavaSpace05 space = SpaceUtils.getSpace(); // debugging

    public LoginController(LoginForm form) {this.loginForm = form;}

    public void registerUser(String username, String password)
    {
        // check text fields are not blank
        if(StringUtils.isNotBlank(username) ||
                StringUtils.isNotBlank(password))
        {
            // Check length of password
            if(password.length() >= 6 &&
                    password.length() <= 25)
            {
                // check username length
                if(username.length() >= 3 &&
                        username.length() <= 20)
                {
                    // continue
                    String confirmDialogResponse = confirmPassword();

//                    System.out.println("Dialog: " + confirmDialogResponse);
//                    System.out.println("Password: "+password);
                    if(password.equals(confirmDialogResponse))
                    {
                        // passwords match
                        // save user to space
                        try
                        {
                            // create new entry
                            //TODO: encrypt password
                            UserEntry user = new UserEntry();
                            user.setUsername(username);
                            user.setPassword(password);

                            if(user.getSecureUsername().length() > 3)
                            {
                                // there are at least 3 non-special characters
                                // create user
                                userUtils.createUser(user);

                                // remove loginForm
                                loginForm.setVisible(false);
                                loginForm.dispose();

                                // Create MainForm
//                                UserEntry debug = (UserEntry) space.readIfExists(user, null, 3000);
//                                System.out.println("User: " + debug +" Successfully added!");
//                                System.out.println("Main Form Created!");
                            }
                            else
                            {
                                JOptionPane.showMessageDialog(loginForm,
                                        "Username must have at least 3 non-special characters. ");
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(loginForm,
                                "Passwords did not match!");
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(loginForm,
                            "Username must be 3 - 20 characters. ");
                }
            }
            else
            {
                JOptionPane.showMessageDialog(loginForm,
                        "Password must be 6 - 25 characters. ");
            }
        }
        else
        {
            JOptionPane.showMessageDialog(loginForm,
                    "A username and password are required.");
        }
    }

    public void loginUser()
    {

    }

    /**
     * Opens a confirmation dialog to confirm password
     * @return the entered password
     *
     * @Reference https://stackoverflow.com/
     *            questions/8881213/joptionpane-to-get-password
     */
    private String confirmPassword()
    {
        JPasswordField tf_password = new JPasswordField();
        int okCxl = JOptionPane.showConfirmDialog(null,
                tf_password, "Confirm Password: ",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // Debug: System.out.println("Dialog" + tf_password.getPassword());
        if (okCxl == JOptionPane.OK_OPTION)
        {
            return new String(tf_password.getPassword());
        }
        return null;
    }

}
