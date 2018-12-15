package model;

import net.jini.core.entry.Entry;

public class DummyUserRemoved implements Entry
{
    public TopicEntry topic;
    public UserEntry user;

    // No arg constructor
    public DummyUserRemoved() {}

    public DummyUserRemoved(TopicEntry topic) {this.topic = topic;}

    public DummyUserRemoved(TopicEntry topic, UserEntry user)
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
