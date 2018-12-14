package util.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

/**
 *
 * @References https://www.baeldung.com/java-password-hashing
 *             https://stackoverflow.com/questions/
 *             2860943/how-can-i-hash-a-password-in-java
 */
public class CipherUtils
{
    private static final Random RANDOM = new SecureRandom();
    private static final String CHARS =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int KEYLENGTH = 256;
    private static final int ITERATIONS = 15000;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

    private  CipherUtils() {}

    /**
     * Calculates a salt value - one way cryptographic funciton
     *
     * @param length - length of salt to generate
     * @return returnVal - the calculated salt
     */
    public static String getSalt(int length)
    {
        StringBuilder returnString = new StringBuilder(length);

        for(int i = 0; i < length; i++)
        {
            returnString.append(
                    CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return new String(returnString);
    }

    /**
     *Generate password hash using salt
     *
     * @param password - the password to be hashed
     * @param salt - the salt to hash the password
     * @return the encoded password
     */
    public static byte[] hash(char[] password, byte[] salt)
    {
        PBEKeySpec pb_spec = new PBEKeySpec(
                password, salt, ITERATIONS, KEYLENGTH);

        Arrays.fill(password, Character.MIN_VALUE);
        try
        {
            SecretKeyFactory skf =
                    SecretKeyFactory.getInstance(ALGORITHM);

            return skf.generateSecret(pb_spec).getEncoded();

        }
        catch (Exception e)
        {
            throw new AssertionError("Error hashing password: " + e.getMessage(), e);
        }
        finally
        {
            pb_spec.clearPassword();
        }
    }

    /**
     * Uses the hash method to generate and return a secured password
     *
     * @param password - password to be secured.
     * @param salt - the salt to generate
     * @return the hashed password
     */
    public static String generateSecurePassword(String password, String salt)
    {
        String returnString = null;
        try
        {
            byte[] hashedPassword = hash(
                    password.toCharArray(), salt.getBytes());

            returnString =
                    Base64.getEncoder().encodeToString(hashedPassword);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return returnString;
    }

    /**
     *
     * @param password
     * @param hashed
     * @param salt
     * @return
     */
    public static boolean verifyPassword(String password, String hashed, String salt)
    {
        boolean isCorrect = false;

        try
        {
            String newHashedPassword =
                    generateSecurePassword(password, salt);

            isCorrect =
                    newHashedPassword.equalsIgnoreCase(hashed);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return isCorrect;
    }
}