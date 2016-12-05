/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db.repositories;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bdlions.bean.SMSTransactionInfo;
import org.bdlions.bean.TransactionInfo;
import org.bdlions.bean.UserServiceInfo;
import org.bdlions.constants.Transactions;
import org.bdlions.db.Database;
import org.bdlions.db.query.QueryField;
import org.bdlions.db.query.QueryManager;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.exceptions.DBSetupException;
import org.bdlions.utility.Utils;

/**
 *
 * @author nazmul hasan
 */
public class Transaction {
    private Connection connection;
    /***
     * Restrict to call without connection
     */
    private Transaction(){}
    public Transaction(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * This method will return user service info based on API Key
     * @param APIKey, API Key
     * @return UserServiceInfo, user service info
     * @throws DBSetupException
     * @throws SQLException
     */
    public UserServiceInfo getUserServiceInfo(String APIKey) throws DBSetupException, SQLException
    {
        UserServiceInfo userServiceInfo = new UserServiceInfo();
        try (EasyStatement stmt = new EasyStatement(this.connection, QueryManager.GET_USER_SERVICE_INFO);){
            stmt.setString(QueryField.API_KEY, APIKey);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                userServiceInfo.setServiceId(rs.getInt(QueryField.SERVICE_ID));
                //if required set other fields
            }
        }
        return userServiceInfo;
    }
    
    /**
     * This method will create a new transaciton
     * @param transactionInfo, transaction info
     * @throws DBSetupException
     * @throws SQLException
     * @return String, transaction id
     */
    public String createTransaction(TransactionInfo transactionInfo) throws DBSetupException, SQLException
    {
        int currentTime = Utils.getCurrentUnixTime();
        String transactionId = Utils.getTransactionId();
        try (EasyStatement stmt = new EasyStatement(this.connection, QueryManager.CREATE_TRANSACTION)) {
            stmt.setString(QueryField.TRANSACTION_ID, transactionId);
            stmt.setString(QueryField.API_KEY, transactionInfo.getAPIKey());
            stmt.setDouble(QueryField.BALANCE_IN, transactionInfo.getBalanceIn());
            stmt.setDouble(QueryField.BALANCE_OUT, transactionInfo.getBalanceOut());            
            stmt.setInt(QueryField.TRANSACTION_TYPE_ID, transactionInfo.getTransactionTypeId());
            stmt.setInt(QueryField.TRANSACTION_STATUS_ID, transactionInfo.getTransactionStatusId());
            stmt.setInt(QueryField.PACKAGE_ID, transactionInfo.getPackageId());
            stmt.setString(QueryField.TRANSACTION_CELL_NUMBER, transactionInfo.getCellNumber());
            stmt.setString(QueryField.TRANSACTION_DESCRIPTION, transactionInfo.getDescription());
            stmt.setBoolean(QueryField.EDITABLE, transactionInfo.isEditable());
            stmt.setInt(QueryField.CREATED_ON, currentTime);
            stmt.setInt(QueryField.MODIFIED_ON, currentTime);
            stmt.executeUpdate();
        }
        return transactionId;
    }
    
    /**
     * This method will add sms details
     * @param smsTransactionInfo sms transaction info
     * @throws DBSetupException
     * @throws java.sql.SQLException
     * @author nazmul hasan on 17th April 2016
     */
    public void createSMSDetails(SMSTransactionInfo smsTransactionInfo) throws DBSetupException, SQLException
    {
        int currentTime = Utils.getCurrentUnixTime();
        try (EasyStatement stmt = new EasyStatement(this.connection, QueryManager.ADD_SMS_DETAILS)) {
            stmt.setString(QueryField.TRANSACTION_ID, smsTransactionInfo.getTransactionId());
            stmt.setString(QueryField.API_KEY, smsTransactionInfo.getAPIKey());
            stmt.setString(QueryField.SMS, smsTransactionInfo.getSms());
            stmt.setInt(QueryField.CREATED_ON, currentTime);
            stmt.setInt(QueryField.MODIFIED_ON, currentTime);
            stmt.executeUpdate();
        }
    }
    
    /**
     * This method will add sms transaction
     * @param smsTransactionInfo sms transaction info
     * @throws DBSetupException
     * @throws SQLException
     * @author nazmul hasan on 17th April 2016
     */
    public void createSMSTransaction(SMSTransactionInfo smsTransactionInfo) throws DBSetupException, SQLException
    {
        int currentTime = Utils.getCurrentUnixTime();
        List<String> cellNumberList = smsTransactionInfo.getCellNumberList();
        int length = cellNumberList.size();
        //try to use insert batch instead of loop
        for(int counter = 0; counter < length ; counter++)
        {
            String cellNumber = cellNumberList.get(counter);
            try (EasyStatement stmt = new EasyStatement(this.connection, QueryManager.CREATE_SMS_TRANSACTION)) {
                stmt.setString(QueryField.TRANSACTION_ID, smsTransactionInfo.getTransactionId());
                stmt.setString(QueryField.TRANSACTION_CELL_NUMBER, cellNumber);
                stmt.setInt(QueryField.TRANSACTION_STATUS_ID, smsTransactionInfo.getTransactionStatusId());
                stmt.setInt(QueryField.CREATED_ON, currentTime);
                stmt.setInt(QueryField.MODIFIED_ON, currentTime);
                stmt.executeUpdate();
            }
        }
        
    }
    
    /**
     * This method will update transaction status
     * @param transactionInfo, transaction info
     * @throws DBSetupException
     * @throws SQLException
     */
    public void updateTransactionStatus(TransactionInfo transactionInfo) throws DBSetupException, SQLException
    {
        try (EasyStatement stmt = new EasyStatement(this.connection, QueryManager.UPDATE_TRANSACTION_STATUS);){
            stmt.setInt(QueryField.TRANSACTION_STATUS_ID, transactionInfo.getTransactionStatusId());
            stmt.setString(QueryField.TRANSACTION_ID, transactionInfo.getTransactionId());
            stmt.executeUpdate();        
        }
    }
    
    /**
     * This method will return transaction id of our system based on sms information of stk feature
     * @param transactionInfo, transaction info
     * @return String transaction id
     * @throws DBSetupException
     * @throws SQLException
     */
    public String getTransactionIdLSSTK(TransactionInfo transactionInfo) throws DBSetupException, SQLException
    {
        String transactionId = "";
        try (EasyStatement stmt = new EasyStatement(this.connection, QueryManager.GET_TRANSACTION_ID_LS_STK);){
            stmt.setString(QueryField.API_KEY, transactionInfo.getAPIKey());
            stmt.setString(QueryField.TRANSACTION_CELL_NUMBER, transactionInfo.getCellNumber());
            stmt.setDouble(QueryField.BALANCE_OUT, transactionInfo.getBalanceOut());
            stmt.setInt(QueryField.TRANSACTION_STATUS_ID, Transactions.TRANSACTION_STATUS_PROCESSED);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactionId = rs.getString(QueryField.TRANSACTION_ID);
            }
        }
        return transactionId;
    }
    
    /**
     * This method will update transaction status, sender cell number and operator transaction id
     * @param transactionInfo, transaction info
     * @throws DBSetupException
     * @throws SQLException
     */
    public void updateTransactionStatusLS(TransactionInfo transactionInfo) throws DBSetupException, SQLException
    {
        try (EasyStatement stmt = new EasyStatement(this.connection, QueryManager.UPDATE_TRANSACTION_STATUS_LS);){
            stmt.setInt(QueryField.TRANSACTION_STATUS_ID, transactionInfo.getTransactionStatusId());
            stmt.setString(QueryField.SENDER_CELL_NUMBER, transactionInfo.getSenderCellNumber());
            stmt.setString(QueryField.TRANSACTION_ID_OPERATOR, transactionInfo.getTrxIdOperator());
            stmt.setString(QueryField.TRANSACTION_ID, transactionInfo.getTransactionId());
            stmt.executeUpdate();        
        }
    }
    
    /**
     * This method will return current available balance of an api key
     * @param APIKey, api key
     * @throws DBSetupException
     * @throws SQLException
     * @return double, current available balance
     */
    public double getAvailableBalance(String APIKey) throws DBSetupException, SQLException
    {
        double currentBalance = 0;
        try (EasyStatement stmt = new EasyStatement(this.connection, QueryManager.GET_CURRENT_BALANCE);){
            stmt.setString(QueryField.API_KEY, APIKey);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                currentBalance = rs.getDouble(QueryField.CURRENT_BALANCE);
            }
        } 
        return currentBalance;
    }
    
    public TransactionInfo getTransactionInfo(String transactionId)  throws DBSetupException, SQLException
    {
        TransactionInfo transactionInfo = new TransactionInfo();
        try (EasyStatement stmt = new EasyStatement(this.connection, QueryManager.GET_TRANSACTION_INFO);){
            stmt.setString(QueryField.TRANSACTION_ID, transactionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactionInfo.setTransactionId(rs.getString(QueryField.TRANSACTION_ID));
                transactionInfo.setAPIKey(rs.getString(QueryField.API_KEY));
                transactionInfo.setBalanceIn(rs.getDouble(QueryField.BALANCE_IN));
                transactionInfo.setBalanceOut(rs.getDouble(QueryField.BALANCE_OUT));
                transactionInfo.setTransactionStatusId(rs.getInt(QueryField.TRANSACTION_STATUS_ID));
                transactionInfo.setTransactionTypeId(rs.getInt(QueryField.TRANSACTION_TYPE_ID));
                transactionInfo.setPackageId(rs.getInt(QueryField.PACKAGE_ID));
                transactionInfo.setCellNumber(rs.getString(QueryField.TRANSACTION_CELL_NUMBER));
                transactionInfo.setDescription(rs.getString(QueryField.TRANSACTION_DESCRIPTION));
                transactionInfo.setEditable(rs.getBoolean(QueryField.EDITABLE));
            }
        }
        return transactionInfo;
    }
    
    public List<TransactionInfo> getEditableTransactionList()  throws DBSetupException, SQLException
    {
        List<TransactionInfo> transactionList = new ArrayList<>();
        try (EasyStatement stmt = new EasyStatement(this.connection, QueryManager.GET_EDITABLE_TRANSACTION_INFO);){
            stmt.setBoolean(QueryField.EDITABLE, Boolean.TRUE);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TransactionInfo transactionInfo = new TransactionInfo();
                transactionInfo.setTransactionId(rs.getString(QueryField.TRANSACTION_ID));
                transactionInfo.setServiceId(rs.getInt(QueryField.SERVICE_ID));
                transactionInfo.setProcessTypeId(rs.getInt(QueryField.PROCESS_TYPE_ID));
                transactionInfo.setAPIKey(rs.getString(QueryField.API_KEY));
                transactionInfo.setBalanceIn(rs.getDouble(QueryField.BALANCE_IN));
                transactionInfo.setBalanceOut(rs.getDouble(QueryField.BALANCE_OUT));
                transactionInfo.setTransactionStatusId(rs.getInt(QueryField.TRANSACTION_STATUS_ID));
                transactionInfo.setTransactionTypeId(rs.getInt(QueryField.TRANSACTION_TYPE_ID));
                transactionInfo.setPackageId(rs.getInt(QueryField.PACKAGE_ID));
                transactionInfo.setCellNumber(rs.getString(QueryField.TRANSACTION_CELL_NUMBER));
                transactionInfo.setDescription(rs.getString(QueryField.TRANSACTION_DESCRIPTION));
                transactionInfo.setCreatedOn(rs.getInt(QueryField.CREATED_ON));
                transactionInfo.setEditable(rs.getBoolean(QueryField.EDITABLE));
                transactionList.add(transactionInfo);
            }
        }
        return transactionList;
    }
    
    public void updateTransactionInfo(TransactionInfo transactionInfo) throws DBSetupException, SQLException
    {
        try (EasyStatement stmt = new EasyStatement(this.connection, QueryManager.UPDATE_TRANSACTION_INFO);){
            stmt.setString(QueryField.API_KEY, transactionInfo.getAPIKey());
            stmt.setDouble(QueryField.BALANCE_IN, transactionInfo.getBalanceIn());
            stmt.setDouble(QueryField.BALANCE_OUT, transactionInfo.getBalanceOut());
            stmt.setInt(QueryField.TRANSACTION_STATUS_ID, transactionInfo.getTransactionStatusId());
            stmt.setInt(QueryField.TRANSACTION_TYPE_ID, transactionInfo.getTransactionTypeId());
            stmt.setInt(QueryField.PACKAGE_ID, transactionInfo.getPackageId());
            stmt.setString(QueryField.TRANSACTION_CELL_NUMBER, transactionInfo.getCellNumber());
            stmt.setString(QueryField.TRANSACTION_DESCRIPTION, transactionInfo.getDescription());
            stmt.setBoolean(QueryField.EDITABLE, transactionInfo.isEditable());
            stmt.setString(QueryField.TRANSACTION_ID, transactionInfo.getTransactionId());
            stmt.executeUpdate();        
        }
    }
}
