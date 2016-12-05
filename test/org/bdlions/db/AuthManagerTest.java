package org.bdlions.db;

import java.sql.SQLException;
import org.bdlions.exceptions.DBSetupException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 *
 * @author nazmul hasan
 */
public class AuthManagerTest {
    
    public AuthManagerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

        
    //@Test
    public void getBaseURLopCodeTest() throws DBSetupException, SQLException{
        AuthManager authManager = new AuthManager();
        String baseURL = authManager.getBaseURLOPCode("c1");
        System.out.println(baseURL);
    }
    
    @Test
    public void getBaseURLTransactionIdTest() throws DBSetupException, SQLException{
        AuthManager authManager = new AuthManager();
        String baseURL = authManager.getBaseURLTransactionId("kiu300t54djglqf6g83gdilvh5");
        System.out.println(baseURL);
    }
    
    //@Test
    public void getLSIdentifierTest() throws DBSetupException, SQLException{
        AuthManager authManager = new AuthManager();
        String lsIdentifier = authManager.getLSIdentifier("key1");
        System.out.println(lsIdentifier);
    }
}
