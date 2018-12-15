package model;

import net.jini.core.entry.Entry;

import java.util.UUID;

/**
 *
 *
 * @References https://docs.oracle.com/
 *              javase/7/docs/api/java/util/UUID.html
 */
public class TopicEntry implements Entry
{
    public UUID id;
    public String title;
    public String noSpecialTitle;
    public UserEntry owner;

    public TopicEntry() {}

    public TopicEntry(String title, UserEntry owner)
    {
        this.id = UUID.randomUUID();
        this.title = title;
        this.owner = owner;

        removeSpecialChars(title);
    }

    /**
     * Replaces any sequence of non-letters with a single whitespace
     * intended for comparing topics to ensure clones cannot be made.
     *
     * @param title the title to convert
     */
    public void removeSpecialChars(String title)
    {
        this.noSpecialTitle =
                title.replaceAll(
                        "\"[^a-zA-Z]+", "").toLowerCase();
    }

    // Getters and Setters (Convenience Methods)
    public UUID getID() {return this.id;}
    public void setID(UUID id) {this.id = this.id;}

    public String getTitle() {return this.title;}
    public void setTitle(String title) {this.title = title;}

    public UserEntry getOwner() {return this.owner;}
    public void setOwner(UserEntry owner) {this.owner = owner;}

    public String getNoSpecialTitle() {return this.noSpecialTitle;}

}
