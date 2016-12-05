package org.bdlions.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nazmul hasan
 */
public class UserInfo {
    private String userId;
    private String subscriberId;
    private String referenceUserName;
    private String subscriberReferenceUserName;
    private int createdOn;
    private int modifiedOn;
    private int registrationDate;
    private int expiredDate;
    private int maxMembers;
    private int currentMemers;
    private String ipAddress;
    private final Logger logger = LoggerFactory.getLogger(UserInfo.class);
    public UserInfo()
    {
    
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getReferenceUserName() {
        return referenceUserName;
    }

    public void setReferenceUserName(String referenceUserName) {
        this.referenceUserName = referenceUserName;
    }

    public String getSubscriberReferenceUserName() {
        return subscriberReferenceUserName;
    }

    public void setSubscriberReferenceUserName(String subscriberReferenceUserName) {
        this.subscriberReferenceUserName = subscriberReferenceUserName;
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

    public int getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(int registrationDate) {
        this.registrationDate = registrationDate;
    }

    public int getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(int expiredDate) {
        this.expiredDate = expiredDate;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getCurrentMemers() {
        return currentMemers;
    }

    public void setCurrentMemers(int currentMemers) {
        this.currentMemers = currentMemers;
    }
    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(this);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return json;
    }
}
