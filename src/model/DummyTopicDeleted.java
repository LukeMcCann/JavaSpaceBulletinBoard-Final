package model;

import net.jini.core.entry.Entry;

public class DummyTopicDeleted implements Entry
{
    public TopicEntry topic;

    // No arg constructor
    public DummyTopicDeleted() {}

    public DummyTopicDeleted(TopicEntry topic) {this.topic = topic;}


    // Getters and Setters (Convenience Methods)
    public TopicEntry getTopic() {return this.topic;}
    public void setTopic(TopicEntry topic) {this.topic = topic;}
}
