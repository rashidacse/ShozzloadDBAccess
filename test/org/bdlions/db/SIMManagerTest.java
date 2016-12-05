package org.bdlions.db;

import java.sql.SQLException;
import java.util.List;
import org.bdlions.bean.SIMInfo;
import org.bdlions.bean.SIMSMSInfo;
import org.bdlions.bean.SIMServiceInfo;
import org.bdlions.constants.Services;
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
public class SIMManagerTest {
    
    public SIMManagerTest() {
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
    public void addSIMTest() throws DBSetupException, SQLException{
        SIMManager simManager = new SIMManager();
        SIMServiceInfo simServiceInfo = new SIMServiceInfo();
        simServiceInfo.setCurrentBalance(3000);
        simServiceInfo.setId(Services.SIM_SERVICE_TYPE_ID_BKASH);
        simServiceInfo.setCategoryId(Services.PACKAGE_TYPE_ID_AGENT);
        
        SIMInfo simInfo = new SIMInfo();
        simInfo.setSimNo("8801712341213");
        simInfo.setDescription("Chittagong Branch");
        simInfo.getSimServiceList().add(simServiceInfo);
        simManager.addSIM(simInfo);
    }
    
    //@Test
    public void getSIMInfo() throws DBSetupException, SQLException{
        SIMManager simManager = new SIMManager();
        SIMInfo simInfo = simManager.getSIMServiceInfo("8801678112509");
        System.out.println(simInfo.getSimNo());
    }
    
    //@Test
    public void getAllSIMs() throws DBSetupException, SQLException{
        SIMManager simManager = new SIMManager();
        List<SIMInfo> simList = simManager.getAllSIMsServices("demols1");
        System.out.println(simList.size());
    }
    
    //@Test
    public void updateSIMInfoTest() throws DBSetupException, SQLException{
        SIMManager simManager = new SIMManager();
        SIMServiceInfo simServiceInfo = new SIMServiceInfo();
        simServiceInfo.setCurrentBalance(3002);
        simServiceInfo.setId(Services.SIM_SERVICE_TYPE_ID_BKASH);
        simServiceInfo.setCategoryId(Services.PACKAGE_TYPE_ID_AGENT);
        
        SIMInfo simInfo = new SIMInfo();
        simInfo.setSimNo("8801712341213");
        simInfo.setDescription("Chittagong Branch2");
        simInfo.getSimServiceList().add(simServiceInfo);
        simManager.updateSIMInfo(simInfo);
    }
    
    //@Test
    public void addSIMMessageTest() throws DBSetupException, SQLException
    {
        try {
            SIMManager simManager = new SIMManager();
            SIMSMSInfo simSMSInfo = new SIMSMSInfo();
            simSMSInfo.setSimNo("01712341213");
            simSMSInfo.setSender("flexiload");
            simSMSInfo.setSms("Recharge request of TK 13.0 for mobile no. 1784863147, transaction ID BD51072117460432 is successful. Your account balance is TK 37.78.");
            simManager.addSIMMessage(simSMSInfo);
        } catch (Exception e) {
        }
    }
    
    //@Test
    public void getSIMMessagesTest() throws DBSetupException, SQLException
    {
        try {
            SIMManager simManager = new SIMManager();
            List<SIMSMSInfo> simSMSList = simManager.getSIMMessages("8801712341213", 0, 1572626560, 3, 2);
            System.out.println(simSMSList.size());
        } catch (Exception ex) {
        }
    }
    @Test
    public void getSIMTotalMessagesTest() throws DBSetupException, SQLException
    {
        try {
            SIMManager simManager = new SIMManager();
            int counter = simManager.getSIMTotalMessages("8801712341213", 0, 1572626560);
            System.out.println(counter);
        } catch (Exception ex) {
        }
    }
    //@Test
    public void getAllSIMMessagesTest() throws DBSetupException, SQLException
    {
        try {
            SIMManager simManager = new SIMManager();
            List<SIMSMSInfo> simSMSList = simManager.getAllSIMMessages("8801712341213", 0, 1572626560);
            System.out.println(simSMSList.size());
        } catch (Exception ex) {
        }
    }
}
