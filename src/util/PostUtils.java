package util;

import model.PostEntry;
import model.TopicEntry;
import model.UserEntry;
import net.jini.space.JavaSpace05;
import util.helper.EntrySearcher;

import java.util.Iterator;
import java.util.List;

public class PostUtils
{
    private static PostUtils postUtils;
    private static EntrySearcher e_searcher = new EntrySearcher();
    private JavaSpace05 space = SpaceUtils.getSpace();

    private PostUtils() {}

    public static PostUtils getPostUtils()
    {
        if(postUtils != null) return postUtils;
        return postUtils = new PostUtils();
    }

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
