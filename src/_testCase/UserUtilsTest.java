package _testCase;

import model.UserEntry;
import net.jini.core.lease.Lease;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import util.UserUtils;

import java.util.List;

import static org.junit.Assert.*;
public class UserUtilsTest
{
    private UserUtils userUtils = UserUtils.getUserutils();
    private List<Lease> testList;

    @Before
    public void setup()
    {
    }

    @Test
    public void renewUserLease()
    {
        String random =
                RandomStringUtils.randomAlphabetic(100);

        UserEntry user = new UserEntry(random, random);

        try
        {
            System.out.println("\n------------------------------\n");
            System.out.println("Lease Renewal -");
            Lease ogLease = userUtils.createUser(user);
            testList.add(ogLease);

            Lease extLease = userUtils.renewUserLease(user);
            testList.add(extLease);

            assertTrue(ogLease.getExpiration() < extLease.getExpiration());
            System.out.println("Original Lease: " + ogLease.getExpiration());
            System.out.println("Extended Lease: " + extLease.getExpiration());
        }
        catch (Exception e) {
            fail("Unexpected error");
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }
}
