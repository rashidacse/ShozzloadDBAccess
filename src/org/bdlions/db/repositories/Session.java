/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db.repositories;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bdlions.bean.SessionInfo;
import org.bdlions.bean.UserInfo;
import org.bdlions.db.query.QueryField;
import org.bdlions.db.query.QueryManager;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.exceptions.DBSetupException;
import org.bdlions.exceptions.ServiceExpireException;
import org.bdlions.exceptions.SubscriptionExpireException;
import org.bdlions.utility.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nazmul hasan
 */
public class Session {
    private Connection connection;
    private final Logger logger = LoggerFactory.getLogger(EasyStatement.class);
    /***
     * Restrict to call without connection
     */
    private Session(){}
    public Session(Connection connection)
    {
        this.connection = connection;
    }
    
    /**
     * This method will return session info
     * @param userInfo, user info
     * @param APIKey, api key
     * @throws SubscriptionExpireException
     * @throws ServiceExpireException
     * @throws DBSetupException
     * @throws SQLException
     * @return SessionInfo, session info
     */
    public SessionInfo getSessionInfo(UserInfo userInfo, String APIKey) throws SubscriptionExpireException, ServiceExpireException, DBSetupException, SQLException
    {
        SessionInfo sessionInfo = new SessionInfo();
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.GET_USER_SUBSCRIBER_SERVICE_INFO);){
            stmt.setString(QueryField.REFERENCE_USERNAME, userInfo.getReferenceUserName());
            stmt.setString(QueryField.IP_ADDRESS, userInfo.getIpAddress());
            stmt.setString(QueryField.API_KEY, APIKey);
            ResultSet rs = stmt.executeQuery();
            if(rs.next())
            {
                int subscriptionExpiredDate = Integer.parseInt(rs.getString(QueryField.SUBSCRIPTION_EXPIRED_DATE));
                int APIExpiredDate = Integer.parseInt(rs.getString(QueryField.API_EXPIRED_DATE));
                int currentDate = 1;
                if(subscriptionExpiredDate <= currentDate)
                {
                    //Subscription period is expired
                    logger.error("Subscription expired.");
                    throw new SubscriptionExpireException();
                }
                if(APIExpiredDate <= currentDate)
                {
                    //api key is expired
                    logger.error("API key expired.");
                    throw new ServiceExpireException();
                }
                sessionInfo.setUserId(rs.getString(QueryField.MEMBER_USER_ID));
                sessionInfo.setSessionId(Utils.getSessionId());
            }
            else
            {
                //invalid reference user or ipaddress or api key
                logger.error("Unauthenticated user to get session id.");
            }
        }
        return sessionInfo;
    }
}
