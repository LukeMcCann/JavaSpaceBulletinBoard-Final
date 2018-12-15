package util;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace05;

import javax.swing.*;
import java.net.ConnectException;
import java.net.InetAddress;

/**
 * @Author Gary Allen
 *
 * @Modifier Luke McCann
 * @UniversityNumber U1364096
 * @University The University of Huddersfield
 *
 * SpaceUtils - handles JavaSpace and Transaction setup
 */
public class SpaceUtils
{
   private static String DEFAULT_HOSTNAME = "Jarvis-W65-67SC";
   private static InetAddress IP;

    /**
     * Sets the current Host
     */
   public static void setHost(String hostname)
   {
       if(hostname.isEmpty())
       {
           try
           {
               IP = InetAddress.getLocalHost();
               DEFAULT_HOSTNAME = IP.getHostName();
           }
           catch (Exception e)
           {
               e.printStackTrace();
               JOptionPane.showMessageDialog(null,
                       "Unknown Host!" +
                               "\n\nHost: " + DEFAULT_HOSTNAME);
           }
       }
       else
       {
           DEFAULT_HOSTNAME = hostname;
       }

       getSpace();
   }

   public static String getHostname()
   {
       return DEFAULT_HOSTNAME;
   }

   public static void setHostname(String host)
   {
       DEFAULT_HOSTNAME = host;
   }


    /**
     * Get a reference to the space
     * @param hostname - name of space host
     * @return a reference to the space
     */
    public static JavaSpace05 getSpace(String hostname)
    {
        if(System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }

        JavaSpace05 space = null;
        try
        {
            LookupLocator ll = new LookupLocator("jini://" + hostname);
            ServiceRegistrar sr = ll.getRegistrar();

            Class c = Class.forName("net.jini.space.JavaSpace05");
            Class[] classTemplate = {c};

            space = (JavaSpace05) sr.lookup(new ServiceTemplate(
                    null, classTemplate, null));
        }
        catch(Exception e)
        {
            System.err.println("Error: " + e);
            e.printStackTrace();

            if(e.getClass().equals(ConnectException.class))
            {
                JOptionPane.showMessageDialog(null,
                        "Connection Refused!  " +
                                "\nPlease ensure a space is running on host before logging in." +
                                 "\n\nHost: " + hostname + "\nip: " + IP);
            }
        }
        return space;
    }

    // Overload - provides default hostname
    public static JavaSpace05 getSpace() {return getSpace(DEFAULT_HOSTNAME);}


    /**
     * Get the transaction manager
     * @param hostname - name of the space host
     * @return a transaction manager
     */
    public static TransactionManager getManager(String hostname)
    {
        if(System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }

        TransactionManager tm = null;
        try
        {
            LookupLocator ll = new LookupLocator("jini://" + hostname);
            ServiceRegistrar sr = ll.getRegistrar();

            Class c = Class.forName("net.jini.core.transaction.server.TransactionManager");
            Class[] template = {c};

            tm = (TransactionManager) sr.lookup(new ServiceTemplate(
                    null, template, null));
        }
        catch(Exception e)
        {
            System.err.println("Error: " + e);
            e.printStackTrace();
        }
        return tm;
    }

    // Overload - provides default hostname
    public static TransactionManager getManager() {return getManager(DEFAULT_HOSTNAME);}

}
