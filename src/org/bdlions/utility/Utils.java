package org.bdlions.utility;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 *
 * @author nazmul hasan
 */
public class Utils {
    
    /**
     * This method will return current unix time in seconds
     * @return int, current unix time
     */
    public static int getCurrentUnixTime()
    {
        return (int) (System.currentTimeMillis() / 1000L);
    }
    
    /**
     * This method will return a random string
     * @return String, random string
     */
    public static String getRandomString()
    {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
    
    /**
     * This method will return a random string as API Key
     * @return String, random string
     */
    public static String getAPIKey()
    {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
    
    /**
     * This method will return a session id
     * @return String, random string
     */
    public static String getSessionId()
    {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
    
    /**
     * This method will return a transaction id
     * @return String, random string
     */
    public static String getTransactionId()
    {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
}
