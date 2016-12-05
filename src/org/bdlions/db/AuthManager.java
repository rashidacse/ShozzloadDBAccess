package org.bdlions.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.bdlions.bean.SessionInfo;
import org.bdlions.bean.UserInfo;
import org.bdlions.bean.UserServiceInfo;
import org.bdlions.constants.ResponseCodes;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.db.repositories.General;
import org.bdlions.db.repositories.User;
import org.bdlions.db.repositories.Service;
import org.bdlions.db.repositories.Session;
import org.bdlions.db.repositories.Subscriber;
import org.bdlions.db.repositories.Transaction;
import org.bdlions.exceptions.DBSetupException;
import org.bdlions.exceptions.MaxMemberRegException;
import org.bdlions.exceptions.ServiceExpireException;
import org.bdlions.exceptions.SubscriptionExpireException;
import org.bdlions.exceptions.UnRegisterIPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nazmul hasan
 */
public class AuthManager {

    private User user;
    private Service service;
    private Subscriber subscriber;
    private Session session;
    private Transaction transaction;
    private int responseCode;
    private final Logger logger = LoggerFactory.getLogger(EasyStatement.class);

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
    
    /**
     * This method will create a new subscriber
     *
     * @param userInfo, user info
     * @param userServiceInfoList, user service info list
     */
    public void createSubscriber(UserInfo userInfo, List<UserServiceInfo> userServiceInfoList) {
        //validate the userInfo where required fields are
        //referenceUserName, referenceUserPassword, registrationDate, expiredDate, maxMembers, ipAddress
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            connection.setAutoCommit(false);
            
            user = new User(connection);
            subscriber = new Subscriber(connection);
            service = new Service(connection);
            
            String userId = user.createUser(userInfo);
            userInfo.setUserId(userId);
            subscriber.createSubscriber(userInfo);
            
            for (UserServiceInfo userServiceInfo : userServiceInfoList) {
                //validate the userServiceInfo where required fields are
                //serviceId, registrationDate, expiredDate
                userServiceInfo.setUserId(userId);
                service.addService(userServiceInfo);
            }
            connection.commit();
            connection.close();
            this.responseCode = ResponseCodes.SUCCESS;
        } catch (SQLException ex) {
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
    
    public UserInfo getSubscriberInfo(UserInfo userInfo)
    {
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            subscriber = new Subscriber(connection);
            userInfo = subscriber.getSubscriberInfo(userInfo);
            connection.close();
            this.responseCode = ResponseCodes.SUCCESS;
        } catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            if(connection != null){
                try{
                    connection.close();
                }
                catch(SQLException ex1){
                    logger.error(ex1.getMessage());
                }
            }
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
        return userInfo;
    }

    /**
     * This method will create a new user under a subscriber
     *
     * @param userInfo, user info
     * @throws UnRegisterIPException
     * @throws SubscriptionExpireException
     * @throws MaxMemberRegException
     *
     */
    public void createUser(UserInfo userInfo) throws UnRegisterIPException, SubscriptionExpireException, MaxMemberRegException {
        //validate the userInfo where required fields are
        //referenceUserName, referenceUserPassword, ipaddress,

        //now a dummy time is used
        int currentTime = 1;
        Connection connection = null;
        //check where there maximum members under a subscriber is not exceeded
        try {
            connection = Database.getInstance().getConnection();
            connection.setAutoCommit(false);
            
            user = new User(connection);
            subscriber = new Subscriber(connection);
            
            UserInfo subscriberInfo = subscriber.getSubscriberInfo(userInfo);
            if (subscriberInfo.getUserId() == null) {
                //request from invalid ip address
                logger.error("request from invalid ip address.");
                throw new UnRegisterIPException();
            }
            if (subscriberInfo.getExpiredDate() < currentTime) {
                //subscription is expired
                logger.error("Subscription expired.");
                throw new SubscriptionExpireException();
            }
            if (subscriberInfo.getCurrentMemers() >= subscriberInfo.getMaxMembers()) {
                //subscriber already created maximum members
                logger.error("subscriber already created maximum members");
                throw new MaxMemberRegException();
            }
            userInfo.setSubscriberId(subscriberInfo.getUserId());
            String userId = user.createUser(userInfo);
            userInfo.setUserId(userId);
            
            connection.commit();
            connection.close();
            this.responseCode = ResponseCodes.SUCCESS;
        } catch (SQLException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SQL_EXCEPTION;
            logger.error(ex.getMessage());
            if(connection != null){
                try{
                    connection.rollback();
                    connection.close();
                }
                catch(SQLException ex1){
                    logger.error(ex1.getMessage());
                }
            }
        } catch (DBSetupException ex) {
            this.responseCode = ResponseCodes.ERROR_CODE_DB_SETUP_EXCEPTION;
            logger.error(ex.getMessage());
        }
    }
    
    /**
     * This method will return session info
     * @param userInfo, user info
     * @param APIKey, api key
     * @throws SubscriptionExpireException
     * @throws ServiceExpireException
     * @return String, session info
     */
    public String getSessionInfo(UserInfo userInfo, String APIKey) throws SubscriptionExpireException, ServiceExpireException
    {
        Connection connection = null;
        SessionInfo sessionInfo = new SessionInfo();
        try {
            connection = Database.getInstance().getConnection();
            session = new Session(connection); 
            transaction = new Transaction(connection);
            double availableBalance = transaction.getAvailableBalance(APIKey);
            if(availableBalance <= 0)
            {
                //insufficient balance
                return sessionInfo.toString();
            }
            sessionInfo = session.getSessionInfo(userInfo, APIKey);
            
            
            //put session info into the hashmap at service api server
            
            connection.close();
        } catch (SQLException ex) {
            try {
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }
        } catch (DBSetupException ex) {
            
        }
        return sessionInfo.toString();
    }
    
    public String getBaseURLOPCode(String opCode)
    {
        String baseURL = "";
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            General general = new General(connection);
            baseURL = general.getBaseURLOPCode(opCode);
            connection.close();
        } catch (SQLException ex) {
            try {
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }
            logger.error(ex.getMessage());
        } catch (DBSetupException ex) {
            logger.error(ex.getMessage());
        }
        return baseURL;
    }
    
    public String getBaseURLTransactionId(String transactionId)
    {
        String baseURL = "";
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            General general = new General(connection);
            baseURL = general.getBaseURLTransactionId(transactionId);
            connection.close();
        } catch (SQLException ex) {
            try {
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }
            logger.error(ex.getMessage());
        } catch (DBSetupException ex) {
            logger.error(ex.getMessage());
        }
        return baseURL;
    }
    
    public String getLSIdentifier(String apiKey)
    {
        String lsIdentifier = "";
        Connection connection = null;
        try {
            connection = Database.getInstance().getConnection();
            General general = new General(connection);
            lsIdentifier = general.getLSIdentifier(apiKey);
            connection.close();
        } catch (SQLException ex) {
            try {
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }
            logger.error(ex.getMessage());
        } catch (DBSetupException ex) {
            logger.error(ex.getMessage());
        }
        return lsIdentifier;
    }
    
    public static void main(String args[])
    {
        System.out.println((int) (System.currentTimeMillis() / 1000L));
    }
}
