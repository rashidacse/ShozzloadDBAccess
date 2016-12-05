package org.bdlions.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nazmul hasan
 */
public class SMSTransactionInfo {
    private int id;
    private int serviceId;
    private String transactionId;
    private String APIKey;
    private int transactionStatusId;
    private String sms;
    private String senderCellNumber;
    private List<String> cellNumberList;
    private int createdOn;
    private int modifiedOn;
    private String liveTestFlag = "";
    public SMSTransactionInfo()
    {
        cellNumberList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getTransactionStatusId() {
        return transactionStatusId;
    }

    public void setTransactionStatusId(int transactionStatusId) {
        this.transactionStatusId = transactionStatusId;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getSenderCellNumber() {
        return senderCellNumber;
    }

    public void setSenderCellNumber(String senderCellNumber) {
        this.senderCellNumber = senderCellNumber;
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

    public List<String> getCellNumberList() {
        return cellNumberList;
    }

    public void setCellNumberList(List<String> cellNumberList) {
        this.cellNumberList = cellNumberList;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    } 

    public String getLiveTestFlag() {
        return liveTestFlag;
    }

    public void setLiveTestFlag(String liveTestFlag) {
        this.liveTestFlag = liveTestFlag;
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
