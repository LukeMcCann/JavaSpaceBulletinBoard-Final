package model;

import net.jini.core.entry.Entry;
import util.UserUtils;

import java.util.UUID;

public class UserEntry implements Entry
{
    public UUID id;
    public String username;
    public String secureUsername; // username with no special characters
    public String password; // hashed
    public String salt;

    public UserEntry() {}

    public UserEntry(String username, String password, String salt)
    {
        this.id = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.salt = salt;

        genrateSecureUsername(username);
    }

    public UserEntry(String username, String password)
    {
        this.id = UUID.randomUUID();
        this.username = username;
        this.password = password;

        genrateSecureUsername(username);
    }
    /**
     *
     * @param username
     * @return
     *
     * @Reference kriblogapp1.appspot.com/studymaterial/
     *            j2se/logical/how-to-remov#e-special-characters-
     *            from-a-string-in-java.html
     */
    public void genrateSecureUsername(String username)
    {
        this.secureUsername =
                username.replaceAll(
                        "[^a-zA-Z0-9]", "").toLowerCase();
    }
    /**
     *
     * @param other
     * @return
     *
     * @Reference https://stackoverflow.com/
     *            questions/16069106/how-to-compare-two-java-objects
     */
    @Override
    public boolean equals(Object other)
    {
        if(other instanceof UserEntry &&
                other != null)
        {
            UserEntry otherUser =
                    (UserEntry) other;

            if(otherUser.getID().equals(this.getID()) &&
                    otherUser.getSecureUsername().equals(this.secureUsername) &&
                    otherUser.getUsername().equals(this.username))
            {
                return true;
            }

        }
        return false;
    }

    // Getters/Setters (Convenience Methods)
    public UUID getID() {return this.id;}
    public void setID(UUID id) {this.id = id;}

    public String getUsername() {return this.username;}
    public void setUsername(String username) {this.username = username; genrateSecureUsername(username);}

    public String getSecureUsername() {return this.secureUsername;}
    public void setSecureUsername(String username) {this.secureUsername = username;}

    public String getPassword() {return this.password;}
    public void setPassword(String password) {this.password = password;}

    public String getSalt() {return this.salt;}
    public void setSalt(String salt) {this.salt = salt;}

}
