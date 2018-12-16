package util;

import model.PostEntry;
import model.TopicEntry;
import model.UserEntry;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.space.JavaSpace05;
import util.helper.EntrySearcher;
import util.helper.TransactionBuilder;

import javax.swing.*;
import java.util.Iterator;
import java.util.List;

public class PostUtils
{
    private static PostUtils postUtils;
    private static EntrySearcher e_searcher = new EntrySearcher();
    private static TopicUtils topicUtils = TopicUtils.getTopicUtils();
    private JavaSpace05 space = SpaceUtils.getSpace();

    private PostUtils() {}

    public static PostUtils getPostUtils()
    {
        if(postUtils != null) return postUtils;
        return postUtils = new PostUtils();
    }

    public Lease sendPublicMessage(PostEntry post)
    {
        Lease success = null;
        if(post == null)
        {
            System.err.println("Failed to create post.");
        }
        else
        {
            try
            {
                if(post.getTopic() == null)
                {
                    JOptionPane.showMessageDialog(null,
                            "Topic does not exist!");
                }
                else if(post.getAuthor() == null)
                {
                    JOptionPane.showMessageDialog(null,
                            "Author does not exist.");
                }
                else
                {
                    Transaction transaction =
                            TransactionBuilder.getTransaction();

                    if(topicUtils.topicExists(post.getTopic(), transaction))
                    {
                        success = space.write(post, transaction, Lease.FOREVER);
                    }
                    transaction.commit();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return success;
    }

//    public Lease sendPrivateMessage(PostEntry post, UserEntry recipient)
//    {
//
//    }

    /**
     * Gets all posts a user has made in the current topic
     *
     * @param topic - the topic to search
     * @param author - the author of the posts
     *
     * @return list of all posts
     *
     * @Reference https://stackoverflow.com/questions/
     *             5228687/java-best-way-to-iterate-through
     *                 -a-collection-here-arraylist
     */
    public List<PostEntry> getAllUsersPosts(UserEntry author, TopicEntry topic)
    {
        PostEntry post = new PostEntry(topic);

        List<PostEntry> postCollection =
                e_searcher.readAllMatchingEntries(space, post);

        Iterator<PostEntry> i = postCollection.iterator();

        while(i.hasNext())
        {
            post = i.next();
            if(post.getRecipient() != null &&
                    (!post.getRecipient().getID().equals(author.getID()) &&
                            !post.getAuthor().getID().equals(author.getID())))
            {
                // post is private
                i.remove();
            }
        }
        return postCollection;
    }

}
