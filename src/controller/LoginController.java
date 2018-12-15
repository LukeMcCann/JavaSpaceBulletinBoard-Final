package controller;

import com.sun.tools.javac.Main;
import model.UserEntry;
import net.jini.space.JavaSpace05;
import org.apache.commons.lang3.StringUtils;
import util.SpaceUtils;
import util.UserUtils;
import util.helper.SpaceSearcher;
import util.security.CipherUtils;
import view.LoginForm;
import view.MainForm;

import javax.swing.*;

public class LoginController
{
    private LoginForm loginForm;
    private static final UserUtils userUtils = UserUtils.getUserutils();
    private static SpaceSearcher spaceSearcher = SpaceSearcher.getSpaceSearcher();
//    private JavaSpace05 space = SpaceUtils.getSpace(); // debugging

    public LoginController(LoginForm form) {this.loginForm = form;}

    public void registerUser(String username, String password)
    {
        // check text fields are not blank
        if(StringUtils.isNotBlank(username) &&
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
                            // encrypt password
                            UserEntry user = new UserEntry();
                            user.setUsername(username);
                            user.setSalt(CipherUtils.getSalt(30));
                            user.setPassword(
                                    CipherUtils.generateSecurePassword(
                                            password, user.getSalt()));

                            if(user.getSecureUsername().length() > 3)
                            {
                                // there are at least 3 non-special characters
                                // create user
                                userUtils.createUser(user);

                                JOptionPane.showMessageDialog(loginForm,
                                        "Welcome " + user.getUsername() + "!");

                                // remove loginForm
                                loginForm.setVisible(false);
                                loginForm.dispose();

                                // Debug:
//                                UserEntry debug = (UserEntry) space.readIfExists(user, null, 3000);
//                                System.out.println("User: " + debug.getUsername() +" Successfully added!");
//                                System.out.println("Main Form Created!");

                                // Create MainForm
                                new MainForm(user);

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

    public void loginUser(String username, String password)
    {
        // check password and username entered
        if(StringUtils.isNotBlank(username) &&
                StringUtils.isNotBlank(password))
        {
            // username and password not blank: continue
            // check user exists
            UserEntry template = new UserEntry();
            template.setUsername(username);

            UserEntry existingUser =
                    spaceSearcher.getUserByUsername(username);

            if(existingUser != null)
            {
                // user exists
                try
                {
                    boolean correctPassword = CipherUtils.verifyPassword(password,
                            existingUser.getPassword(), existingUser.getSalt());

                    if(correctPassword)
                    {
                        // login successful
                        JOptionPane.showMessageDialog(loginForm,
                                "Welcome Back " + existingUser.getUsername()+ "!");

                        loginForm.setVisible(false);
                        loginForm.dispose();

                        // create new main form
                        new MainForm(existingUser);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(loginForm,
                                "Incorrect username or password!");
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
                        "User does not exist! Please register.");
            }
        }
        else
        {
            JOptionPane.showMessageDialog(loginForm,
                    "Please enter a valid username and password.");
        }
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
