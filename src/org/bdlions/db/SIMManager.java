package org.bdlions.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bdlions.activemq.Producer;
import org.bdlions.bean.SIMInfo;
import org.bdlions.bean.SIMSMSInfo;
import org.bdlions.bean.SIMServiceInfo;
import org.bdlions.bean.TransactionInfo;
import org.bdlions.constants.ResponseCodes;
import org.bdlions.constants.Services;
import org.bdlions.db.repositories.SIM;
import org.bdlions.exceptions.DBSetupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nazmul hasan
 */
public class SIMManager {
    private final Logger logger = LoggerFactory.getLogger(SIMManager.class);
    private int responseCode;
    private SIM sim;
    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
    
    /**
     * This method will add a new sim with services under that sim
     * @param simInfo, sim info
     * @author nazmul hasan on 11th June 2016
     */
    public void addSIM(SIMInfo simInfo)
    {
        Connection connection = null;
        try
        {
            connection = Database.getInstance().getConnection();  
            connection.setAutoCommit(false);
            sim = new SIM(connection);
            if(!sim.checkSIM(simInfo.getSimNo()))
            {                
                sim.addSIM(simInfo);
                this.responseCode = ResponseCodes.SUCCESS;                 
            }
            else
            {
                this.responseCode = ResponseCodes.ERROR_CODE_ADDSIM_SIMNO_ALREADY_EXISTS;
            } 
            connection.commit();
            connection.close(); 
        }
        catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            try {
                if(connection != null){
                    connection.rollback();
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }            
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
    }
    
    /**
     * This method will return SIM Service Info
     * @param simNo, sim number
     * @return SIMInfo
     * @author nazmul hasan on 11th June 2016
     */
    public SIMInfo getSIMServiceInfo(String simNo)
    {
        SIMInfo simInfo = null;
        Connection connection = null;
        try
        {
            connection = Database.getInstance().getConnection();
            sim = new SIM(connection);
            simInfo = sim.getSIMServiceInfo(simNo);
            connection.close();
        }
        catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            try {
                if(connection != null){
                    connection.rollback();
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }            
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
        return simInfo;
    }
    
    /**
     * This method will return all SIMs
     * @return sim list
     * @author nazmul hasan on 11th June 2016
     */
    public List<SIMInfo> getAllSIMs(String identifier) 
    {
        List<SIMInfo> simList = new ArrayList<>();
        Connection connection = null;
        try
        {
            connection = Database.getInstance().getConnection();
            sim = new SIM(connection);
            simList = sim.getAllSIMs(identifier);
            connection.close();
        }
        catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            try {
                if(connection != null){
                    connection.rollback();
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }            
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
        return simList;
    }
    
    /**
     * This method will return all SIMs with services
     * @return sim list
     * @author nazmul hasan on 11th June 2016
     */
    public List<SIMInfo> getAllSIMsServices(String identifier) 
    {
        List<SIMInfo> simList = new ArrayList<>();
        Connection connection = null;
        try
        {
            connection = Database.getInstance().getConnection();
            sim = new SIM(connection);
            simList = sim.getAllSIMsServices(identifier);
            connection.close();
        }
        catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            try {
                if(connection != null){
                    connection.rollback();
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }            
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
        return simList;
    }
    
    /**
     * This method will update SIM info
     * @param simInfo, SIM Info
     * @author nazmul hasan on 11th June 2016
     */
    public void updateSIMInfo(SIMInfo simInfo)
    {
        Connection connection = null;
        try
        {
            connection = Database.getInstance().getConnection();
            connection.setAutoCommit(false);
            sim = new SIM(connection);
            sim.updateSIMInfo(simInfo);
            //sim.updateSIMServiceBalanceInfo(simInfo);
            connection.commit();
            connection.close();
            this.responseCode = ResponseCodes.SUCCESS;
        }
        catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            try {
                if(connection != null){
                    connection.rollback();
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }            
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
    }
    
    public void updateSIMServiceBalanceInfo(SIMInfo simInfo)
    {
        Connection connection = null;
        try
        {
            connection = Database.getInstance().getConnection();
            sim = new SIM(connection);
            sim.updateSIMServiceBalanceInfo(simInfo);
            connection.close();
            this.responseCode = ResponseCodes.SUCCESS;
        }
        catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.toString());
            try {
                if(connection != null){
                    connection.rollback();
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.toString());
            }            
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.toString());
        }
    }
    
    /**
     * This method will send request to local server to generate current balance of services of a sim
     * @param simInfo
     * @author nazmul hasan on 22nd september 2016
     */
    public void generateSIMBalance(SIMInfo simInfo)
    {
        try
        {
            for(int counter = 0; counter < simInfo.getSimServiceList().size(); counter++)
            {
                SIMServiceInfo simServiceInfo = simInfo.getSimServiceList().get(counter);
                TransactionInfo transactionInfo = new TransactionInfo();
                boolean checkBalance = false;
                if(simServiceInfo.getId() == Services.SIM_SERVICE_TYPE_ID_BKASH)
                {                    
                    transactionInfo.setCellNumber(simInfo.getSimNo());
                    transactionInfo.setServiceId(Services.SERVICE_TYPE_ID_BKASH_CHECKBALANCE);
                    checkBalance = true;
                }
                else if(simServiceInfo.getId() == Services.SIM_SERVICE_TYPE_ID_GP)
                {
                    transactionInfo.setCellNumber(simInfo.getSimNo());
                    transactionInfo.setServiceId(Services.SERVICE_TYPE_ID_GP_CHECKBALANCE);
                    checkBalance = true;
                }
                else if(simServiceInfo.getId() == Services.SIM_SERVICE_TYPE_ID_ROBI)
                {
                    transactionInfo.setCellNumber(simInfo.getSimNo());
                    transactionInfo.setServiceId(Services.SERVICE_TYPE_ID_ROBI_CHECKBALANCE);
                    checkBalance = true;
                }
                else if(simServiceInfo.getId() == Services.SIM_SERVICE_TYPE_ID_TELETALK)
                {
                    transactionInfo.setCellNumber(simInfo.getSimNo());
                    transactionInfo.setServiceId(Services.SERVICE_TYPE_ID_TELETALK_CHECKBALANCE);
                    checkBalance = true;
                }
                else if(simServiceInfo.getId() == Services.SIM_SERVICE_TYPE_ID_AIRTEL)
                {
                    transactionInfo.setCellNumber(simInfo.getSimNo());
                    transactionInfo.setServiceId(Services.SERVICE_TYPE_ID_AIRTEL_CHECKBALANCE);
                    checkBalance = true;
                }
                else if(simServiceInfo.getId() == Services.SIM_SERVICE_TYPE_ID_BANGLALINK)
                {
                    transactionInfo.setCellNumber(simInfo.getSimNo());
                    transactionInfo.setServiceId(Services.SERVICE_TYPE_ID_BANGLALINK_CHECKBALANCE);
                    checkBalance = true;
                }
                if(checkBalance)
                {
                    try {
                        Producer producer = new Producer();
                        producer.setMessage(transactionInfo.toString());
                        System.out.println("Executing the check balance transaction:"+transactionInfo.toString());
                        producer.setCheckBalanceQueueName(simInfo.getSimNo());
                        producer.produce();
                    } 
                    catch (Exception ex) 
                    {
                        logger.debug(ex.toString());
                    }                    
                }
            }            
        }
        catch(Exception ex)
        {
            logger.error(ex.toString());
        }
    }
    
    /**
     * This method will ads sim message
     * @param simSMSInfo 
     */
    public void addSIMMessage(SIMSMSInfo simSMSInfo)
    {
        Connection connection = null;
        try
        {
            connection = Database.getInstance().getConnection();
            sim = new SIM(connection);
            sim.addSIMMessage(simSMSInfo);
            connection.close();
            this.responseCode = ResponseCodes.SUCCESS;
        }
        catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            try {
                if(connection != null){
                    //connection.rollback();
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }            
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
    }
    
    /**
     * This method will return sim message
     * @param simNo
     * @param startTime
     * @param endTime
     * @param offset
     * @param limit
     * @return List
     * @author nazmul hasan on 17th september 2016
     */
    public List<SIMSMSInfo> getSIMMessages(String simNo, int startTime, int endTime, int offset, int limit)
    {
        List<SIMSMSInfo> simSMSList = new ArrayList<>();
        Connection connection = null;
        try
        {
            connection = Database.getInstance().getConnection();
            sim = new SIM(connection);
            simSMSList = sim.getSIMMessages(simNo, startTime, endTime, offset ,limit);
            connection.close();
            this.responseCode = ResponseCodes.SUCCESS;
            return simSMSList;
        }
        catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            try {
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }            
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
        return simSMSList;
    }
    /**
     * This method will return all sim message
     * @param simNo
     * @param startTime
     * @param endTime
     * @return List
     * @author nazmul hasan on 17th september 2016
     */
    public int getSIMTotalMessages(String simNo, int startTime, int endTime)
    {
        int counter = 0;
        Connection connection = null;
        try
        {
            connection = Database.getInstance().getConnection();
            sim = new SIM(connection);
            counter = sim.getSIMTotalMessages(simNo, startTime, endTime);
            connection.close();
            this.responseCode = ResponseCodes.SUCCESS;
            return counter;
        }
        catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            try {
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }            
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
        return counter;
    }
    /**
     * This method will return all sim message
     * @param simNo
     * @param startTime
     * @param endTime
     * @return List
     * @author nazmul hasan on 17th september 2016
     */
    public List<SIMSMSInfo> getAllSIMMessages(String simNo, int startTime, int endTime)
    {
        List<SIMSMSInfo> simSMSList = new ArrayList<>();
        Connection connection = null;
        try
        {
            connection = Database.getInstance().getConnection();
            sim = new SIM(connection);
            simSMSList = sim.getAllSIMMessages(simNo, startTime, endTime);
            connection.close();
            this.responseCode = ResponseCodes.SUCCESS;
            return simSMSList;
        }
        catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            try {
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }            
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
        return simSMSList;
    }
}
