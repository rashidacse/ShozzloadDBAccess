/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bdlions.activemq.Producer;
import org.bdlions.bean.SMSTransactionInfo;
import org.bdlions.bean.TransactionInfo;
import org.bdlions.bean.UserServiceInfo;
import org.bdlions.callback.CallbackTransactionManager;
import org.bdlions.constants.ResponseCodes;
import org.bdlions.constants.Transactions;
import org.bdlions.db.repositories.Transaction;
import org.bdlions.exceptions.DBSetupException;
import org.bdlions.utility.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nazmul hasan
 */
public class TransactionManager {
    private Transaction transaction;
    private String transactionId;
    private int responseCode;
    private final Logger logger = LoggerFactory.getLogger(TransactionManager.class);
    public TransactionManager()
    {
    
    }
    
    public String getTransactionId()
    {
        return this.transactionId;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
    
    public int getResponseCode()
    {
        return this.responseCode;
    }
    
    /**
    * This method will return user service info
    * @param APIKey APIKey
    * @return UserServiceInfo UserServiceInfo
    */
    public UserServiceInfo getUserServiceInfo(String APIKey)
    {
        UserServiceInfo userServiceInfo = new UserServiceInfo();
        Connection connection = null;
        
        try
        {
            connection = Database.getInstance().getConnection();
            transaction = new Transaction(connection);
            userServiceInfo = transaction.getUserServiceInfo(APIKey);
            this.responseCode = ResponseCodes.SUCCESS;
            connection.close();
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
        return userServiceInfo;
    }
    
    /**
     * This method will add a user payment as transaction
     * @param transactionInfo, transaction info
     */
    public void addUserPayment(TransactionInfo transactionInfo)
    {
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            transaction = new Transaction(connection);
            
            transactionInfo.setTransactionStatusId(Transactions.TRANSACTION_STATUS_SUCCESS);
            transactionInfo.setTransactionTypeId(Transactions.TRANSACTION_TYPE_ADD_USER_PAYMENT);
            this.transactionId = transaction.createTransaction(transactionInfo);            
            this.responseCode = ResponseCodes.SUCCESS;
            connection.close();
        } catch (SQLException ex) {
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
    }
    
    /**
     * This method will add a transaction
     * @param transactionInfo, transaction info
     */
    public void addTransaction(TransactionInfo transactionInfo)
    {
        Connection connection = null;
        try {
            if(transactionInfo.getLiveTestFlag().equals(Transactions.TRANSACTION_FLAG_WEBSERVER_TEST))
            {
                this.responseCode = ResponseCodes.SUCCESS;
                this.transactionId = Utils.getTransactionId();
                return;            
            }
            connection = Database.getInstance().getConnection();
            connection.setAutoCommit(false);
            transaction = new Transaction(connection);
            
            //check available balance of the user if required
            
            transactionInfo.setTransactionStatusId(Transactions.TRANSACTION_STATUS_PENDING);
            transactionInfo.setTransactionTypeId(Transactions.TRANSACTION_TYPE_USE_SERVICE);
            this.transactionId = transaction.createTransaction(transactionInfo);  
            transactionInfo.setTransactionId(this.transactionId);
            
            UserServiceInfo userServiceInfo = transaction.getUserServiceInfo(transactionInfo.getAPIKey());
            transactionInfo.setServiceId(userServiceInfo.getServiceId());
            
//            if(transactionInfo.getLiveTestFlag().equals(Transactions.TRANSACTION_FLAG_LOCALSERVER_TEST) || transactionInfo.getLiveTestFlag().equals(Transactions.TRANSACTION_FLAG_LIVE))
//            {
//                //activemq to enqueue a new transaction
//                Producer producer = new Producer();
//                System.out.println(transactionInfo.toString());
//                producer.setMessage(transactionInfo.toString());
//                producer.setServiceQueueName(transactionInfo.getServiceId());
//                producer.produce();
//            }
            
            this.responseCode = ResponseCodes.SUCCESS;
            
            connection.commit();
            connection.close();
        } catch (SQLException ex) {
            try {
                if(connection != null){
                    connection.rollback();
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
        catch (Exception ex) {            
            try {
                if(connection != null){
                    connection.rollback();
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }
            this.responseCode = ResponseCodes.ERROR_CODE_SERVER_EXCEPTION;
            logger.error(ex.getMessage());
        }        
    }
    
    /**
     * This method will send multiple sms
     * @param smsTranactionInfo, sms transaction info
    */
    public void addSMSTransaction(SMSTransactionInfo smsTranactionInfo)
    {
        Connection connection = null;
        try {
            if(smsTranactionInfo.getLiveTestFlag().equals(Transactions.TRANSACTION_FLAG_WEBSERVER_TEST))
            {
                this.responseCode = ResponseCodes.SUCCESS;
                this.transactionId = Utils.getTransactionId();
                return;            
            }
            connection = Database.getInstance().getConnection();
            connection.setAutoCommit(false);
            transaction = new Transaction(connection);
            
            UserServiceInfo userServiceInfo = transaction.getUserServiceInfo(smsTranactionInfo.getAPIKey());
            smsTranactionInfo.setServiceId(userServiceInfo.getServiceId());
            if(smsTranactionInfo.getServiceId() == 0)
            {
                this.responseCode = ResponseCodes.ERROR_CODE_UNAUTHENTICATED_SERVICE;
                try {
                    connection.rollback();
                    connection.close();
                } catch (SQLException ex1) {
                    logger.error(ex1.getMessage());
                }
                return;
            }
            
            //check available balance of the user if required
            //right now user balance is not deducted from the database for sending sms
            TransactionInfo transactionInfo = new TransactionInfo();
            transactionInfo.setAPIKey(smsTranactionInfo.getAPIKey());
            transactionInfo.setTransactionStatusId(Transactions.TRANSACTION_STATUS_PENDING);
            transactionInfo.setTransactionTypeId(Transactions.TRANSACTION_TYPE_USE_SERVICE);
            this.transactionId = transaction.createTransaction(transactionInfo);  
            transactionInfo.setTransactionId(this.transactionId);
            
            smsTranactionInfo.setTransactionId(transactionId);
            transaction.createSMSDetails(smsTranactionInfo);
            
            smsTranactionInfo.setTransactionStatusId(Transactions.TRANSACTION_STATUS_PENDING);
            transaction.createSMSTransaction(smsTranactionInfo);            
            
            System.out.println(smsTranactionInfo.toString());
            
            if(smsTranactionInfo.getLiveTestFlag().equals(Transactions.TRANSACTION_FLAG_LOCALSERVER_TEST) || smsTranactionInfo.getLiveTestFlag().equals(Transactions.TRANSACTION_FLAG_LIVE))
            {                
                //activemq to enqueue a new transaction
                Producer producer = new Producer();
                producer.setMessage(smsTranactionInfo.toString());
                //Set local server identifier based on api key.///////////////
                String localServerIdentifier = "";
                producer.setServiceQueueName(smsTranactionInfo.getServiceId(), localServerIdentifier);
                producer.produce();                
            }            
            this.responseCode = ResponseCodes.SUCCESS;            
            connection.commit();
            connection.close();
        } catch (SQLException ex) {
            try {
                if(connection != null){
                    connection.rollback();
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
        catch (Exception ex) {            
            try {
                if(connection != null){
                    connection.rollback();
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }
            this.responseCode = ResponseCodes.ERROR_CODE_SERVER_EXCEPTION;
            logger.error(ex.getMessage());
        }        
    }
    
    /**
     * This method will update transaction acknowledge status
     * @param transactionInfo, transaction info
     */
    public void updateTransactionStatusAck(TransactionInfo transactionInfo)
    {
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            transaction = new Transaction(connection);
            
            transaction.updateTransactionStatus(transactionInfo); 
            
            //send the acknowledge to the webserver after supporting this status id
            AuthManager authManager = new AuthManager();
            String baseURL = authManager.getBaseURLTransactionId(transactionInfo.getTransactionId());
            CallbackTransactionManager callbackTransactionManager = new CallbackTransactionManager();
            callbackTransactionManager.setBaseURL(baseURL);
            callbackTransactionManager.updateTransactionStatus(transactionInfo.getTransactionId(), transactionInfo.getTransactionStatusId(), transactionInfo.getSenderCellNumber(), transactionInfo.getTrxIdOperator());
            
            this.responseCode = ResponseCodes.SUCCESS;
            connection.close();
        } catch (SQLException ex) {
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
    }
    
    /**
     * This method will update transaction status
     * @param transactionInfo, transaction info
     */
    public void updateTransactionStatus(TransactionInfo transactionInfo)
    {
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            transaction = new Transaction(connection);
            
            transaction.updateTransactionStatusLS(transactionInfo); 
            
            AuthManager authManager = new AuthManager();
            String baseURL = authManager.getBaseURLTransactionId(transactionInfo.getTransactionId());
            CallbackTransactionManager callbackTransactionManager = new CallbackTransactionManager();
            callbackTransactionManager.setBaseURL(baseURL);
            callbackTransactionManager.updateTransactionStatus(transactionInfo.getTransactionId(), transactionInfo.getTransactionStatusId(), transactionInfo.getSenderCellNumber(), transactionInfo.getTrxIdOperator());
            this.responseCode = ResponseCodes.SUCCESS;
            connection.close();
        } catch (SQLException ex) {
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
    }
    
    /**
     * This method will update transaction status based on stk feature
     * @param transactionInfo, transaction info
     */
    public void updateLSSTKTransactionStatus(TransactionInfo transactionInfo)
    {
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            transaction = new Transaction(connection);
            
            String transactionId = transaction.getTransactionIdLSSTK(transactionInfo); 
            if(transactionId != null && !transactionId.isEmpty())
            {
                transactionInfo.setTransactionId(transactionId);
                transaction.updateTransactionStatusLS(transactionInfo);
                AuthManager authManager = new AuthManager();
                String baseURL = authManager.getBaseURLTransactionId(transactionInfo.getTransactionId());
                CallbackTransactionManager callbackTransactionManager = new CallbackTransactionManager();
                callbackTransactionManager.setBaseURL(baseURL);
                callbackTransactionManager.updateTransactionStatus(transactionInfo.getTransactionId(), transactionInfo.getTransactionStatusId(), transactionInfo.getSenderCellNumber(), transactionInfo.getTrxIdOperator());
                this.responseCode = ResponseCodes.SUCCESS;
            }
            else
            {
                this.responseCode = ResponseCodes.ERROR_CODE_UPDATE_TRANSACTION_STATUS_FAILED;
            }
            connection.close();
        } catch (SQLException ex) {
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
    }
    
    public TransactionInfo getTransactionInfo(String transactionId)
    {
        TransactionInfo transactionInfo = new TransactionInfo();
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            transaction = new Transaction(connection);            
            transactionInfo = transaction.getTransactionInfo(transactionId); 
            this.responseCode = ResponseCodes.SUCCESS;
            connection.close();
        } catch (SQLException ex) {
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
        return transactionInfo;
    }
    
    public void updateTransactionInfo(TransactionInfo transactionInfo)
    {
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            transaction = new Transaction(connection);            
            transaction.updateTransactionInfo(transactionInfo); 
            this.responseCode = ResponseCodes.SUCCESS;
            connection.close();
        } catch (SQLException ex) {
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
    }
    
    public List<TransactionInfo> getEditableTransactionList()
    {
        List<TransactionInfo> transactionList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            transaction = new Transaction(connection);            
            transactionList = transaction.getEditableTransactionList(); 
            this.responseCode = ResponseCodes.SUCCESS;
            
        } catch (SQLException ex) {
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
        return transactionList;
    }
}
