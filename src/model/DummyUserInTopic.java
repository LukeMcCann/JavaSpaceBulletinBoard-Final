package model;

import net.jini.core.entry.Entry;

public class DummyUserInTopic implements Entry
{
    public TopicEntry topic;
    public UserEntry user;

    // No arg constructor
    public DummyUserInTopic() {}

    public DummyUserInTopic(TopicEntry topic) {this.topic = topic;}

    public DummyUserInTopic(TopicEntry topic, UserEntry user)
    {
        this.topic = topic;
        this.user = user;
    }

    // Getters and Setters (Convenience Methods)
    public UserEntry getUser() {return this.user;}
    public void setUser(UserEntry user) {this.user = user;}
    public TopicEntry getTopic() {return this.topic;}
    public void setTopic(TopicEntry topic) {this.topic = topic;}


}
