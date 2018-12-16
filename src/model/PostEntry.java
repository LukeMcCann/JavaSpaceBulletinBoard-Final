package model;

import net.jini.core.entry.Entry;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class PostEntry implements Entry
{
    public UUID id;
    public TopicEntry topic;
    public UserEntry author;
    public UserEntry recipient;
    public String content;

    public String postedAt;

    // No arg constructor
    public  PostEntry() {}

    public PostEntry(TopicEntry topic) {this.topic = topic;}

    public PostEntry(UserEntry author, TopicEntry topic, String content)
    {
        this.topic = topic;
        this.author = author;
        this.content = content;
        generateTimeStamp();
    }

    public PostEntry(UserEntry author, UserEntry recipient, TopicEntry topic, String content)
    {
//        this.id = UUID.randomUUID();
        generateTimeStamp();

        this.author = author;
        this.recipient = recipient;
        this.content = content;
        this.topic = topic;
    }

    /**
     * Creates a formatess date-time stamp from the current time
     */
    public void generateTimeStamp()
    {
        Date date = new Date();
        String format = "hh:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(format);
        this.postedAt = dateFormat.format(date);
    }

    // Getters and Setters (Convenience Methods)
    public UUID getID() {return this.id;}
    public void setID(UUID id) {this.id = id;}

    public TopicEntry getTopic() {return this.topic;}
    public void setTopic(TopicEntry topic) {this.topic = topic;}

    public String getPostedAt() {return this.postedAt;}

    public UserEntry getAuthor() {return this.author;}
    public void setAuthor(UserEntry author) {this.author = author;}

    public UserEntry getRecipient() {return recipient;}
    public void setRecipient(UserEntry recipient) {this.recipient = recipient;}

    public String getContent() {return this.content;}
    public void setContent(String content) {this.content = content;}
}
