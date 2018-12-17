package controller;

import model.UserEntry;
import org.apache.commons.lang3.StringUtils;
import util.SpaceUtils;
import util.UserUtils;
import util.error.SpaceExistsError;
import util.helper.SpaceSearcher;
import util.security.CipherUtils;
import view.LoginForm;
import view.MainForm;

import javax.swing.*;
import java.util.UUID;

/**
 * @Author Luke McCann
 * @UniversityNumber U1364096
 * @University The University of Huddersfield
 *
 * LoginController -
 *          Contains methods for controlling all login logic.
 *
 * Debug statements have been commented out.
 *
 * @References https://stackoverflow.com/
 *              questions/8881213/joptionpane-to-get-password
 */
public class LoginController
{
    private LoginForm loginForm;
    private static final UserUtils userUtils = UserUtils.getUserutils();
    private static final SpaceExistsError spaceExists = SpaceExistsError.getSpaceExistsError();
    private static SpaceSearcher spaceSearcher = SpaceSearcher.getSpaceSearcher();
    // private JavaSpace05 space = SpaceUtils.getSpace(); // debugging

    // Constructor
    public LoginController(LoginForm form) {this.loginForm = form;}


    // Registration/Login

    /**
     * Checks a user is valid and adds a UserEntry to the space
     * Utilises encryption for security.
     *
     * Will notify the user if a username is taken.
     *
     * @param username - the username to set (A secured version is saved as a comparator)
     * @param password - the password to set (A hashed copy will be saved)
     */
    public void registerUser(String username, String password)
    {
        if(!spaceExists.spaceExists(SpaceUtils.getSpace()))
        {
            spaceExists.getSpaceExistsWarning(loginForm,
                    "");
        }
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
                            user.setID(UUID.randomUUID());
                            user.setSalt(CipherUtils.getSalt(30));
                            user.setPassword(
                                    CipherUtils.generateSecurePassword(
                                            password, user.getSalt()));

                            if(user.getSecureUsername().length() > 3)
                            {
                                // there are at least 3 non-special characters
                                // create user
                                if(userUtils.createUser(user) != null)
                                {

                                    JOptionPane.showMessageDialog(loginForm,
                                            "Welcome " + user.getUsername() + "!");

                                    // remove loginForm
                                    loginForm.setVisible(false);
                                    loginForm.dispose();
                                    // Lease is renewed at every login
                                    userUtils.renewUserLease(user);

                                    // Debug:
//                                UserEntry debug = (UserEntry) space.readIfExists(user, null, 3000);
//                                System.out.println("User: " + debug.getUsername() +" Successfully added!");
//                                System.out.println("Main Form Created!");

                                    // Create MainForm
                                    new MainForm(user);
                                }
                                else
                                {
                                    // user already exists
                                    JOptionPane.showMessageDialog(loginForm,
                                            user.getUsername() + " is taken.");
                                }
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

    /**
     * Checks if a user exists in the space
     * <code>if user exists && password correct</code> logs the user in
     * <code>else</code> asks the user to register
     *
     * @param username
     * @param password
     */
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


    // Dialogs/Confirmation

    /**
     * Opens a confirmation dialog for user to confirm password.
     *
     * @return the entered password
     *
     * @References https://stackoverflow.com/
     *            questions/8881213/joptionpane-to-get-password
     */
    private String confirmPassword()
    {
        JPasswordField tf_password = new JPasswordField();
        int response = JOptionPane.showConfirmDialog(null,
                tf_password, "Confirm Password: ",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // Debug: System.out.println("Dialog" + tf_password.getPassword());
        if (response == JOptionPane.OK_OPTION)
        {
            return new String(tf_password.getPassword());
        }
        return null;
    }

}
