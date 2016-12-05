/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bdlions.constants.Services;

/**
 *
 * @author nazmul hasan
 */
public class TransactionInfo {
    private int id;
    //this field will be used to map transactionId when user will send multiple transaction request at a time
    private String referenceId;
    //transaction id of our system
    private String transactionId;
    private String APIKey;
    private double balanceIn;
    private double balanceOut;
    private int transactionTypeId;
    private int serviceId;
    private int processTypeId;
    private int packageId;
    private int transactionStatusId;
    private String senderCellNumber="";
    //transaction id from operator or mobile banking system
    private String trxIdOperator = "";
    private String cellNumber;
    private String description;
    private int createdOn;
    private int modifiedOn;
    private String liveTestFlag = "";
    private boolean editable = Boolean.FALSE;
    public TransactionInfo()
    {
        balanceIn = 0;
        balanceOut = 0;
        cellNumber = "";
        description = "";
        packageId = Services.PACKAGE_TYPE_ID_PREPAID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
    
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAPIKey() {
        return APIKey;
    }

    public void setAPIKey(String APIKey) {
        this.APIKey = APIKey;
    }

    public double getBalanceIn() {
        return balanceIn;
    }

    public void setBalanceIn(double balanceIn) {
        this.balanceIn = balanceIn;
    }

    public double getBalanceOut() {
        return balanceOut;
    }

    public void setBalanceOut(double balanceOut) {
        this.balanceOut = balanceOut;
    }

    public int getTransactionTypeId() {
        return transactionTypeId;
    }

    public void setTransactionTypeId(int transactionTypeId) {
        this.transactionTypeId = transactionTypeId;
    }

    public int getTransactionStatusId() {
        return transactionStatusId;
    }

    public void setTransactionStatusId(int transactionStatusId) {
        this.transactionStatusId = transactionStatusId;
    }

    public String getSenderCellNumber() {
        return senderCellNumber;
    }

    public void setSenderCellNumber(String senderCellNumber) {
        this.senderCellNumber = senderCellNumber;
    }
    
    public String getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(int createdOn) {
        this.createdOn = createdOn;
    }

    public int getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(int modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public String getLiveTestFlag() {
        return liveTestFlag;
    }

    public void setLiveTestFlag(String liveTestFlag) {
        this.liveTestFlag = liveTestFlag;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getTrxIdOperator() {
        return trxIdOperator;
    }

    public void setTrxIdOperator(String trxIdOperator) {
        this.trxIdOperator = trxIdOperator;
    }

    public int getProcessTypeId() {
        return processTypeId;
    }

    public void setProcessTypeId(int processTypeId) {
        this.processTypeId = processTypeId;
    }
    
    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return json;
    }
}
