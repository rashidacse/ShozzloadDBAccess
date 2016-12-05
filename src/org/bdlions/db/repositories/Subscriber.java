/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db.repositories;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bdlions.bean.UserInfo;
import org.bdlions.db.query.QueryField;
import org.bdlions.db.query.QueryManager;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.exceptions.DBSetupException;
import org.bdlions.utility.Utils;

/**
 *
 * @author alamgir
 */
public class Subscriber {

    private Connection connection;
    /***
     * Restrict to call without connection
     */
    private Subscriber(){}
    public Subscriber(Connection connection) {
        this.connection = connection;
    }

    /**
     * This method will create a new subscriber
     *
     * @param userInfo, user info
     * @throws DBSetupException
     * @throws SQLException
     */
    public void createSubscriber(UserInfo userInfo) throws DBSetupException, SQLException {
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.CREATE_SUBSCRIBER)) {
            stmt.setString(QueryField.USER_ID, userInfo.getUserId());
            stmt.setInt(QueryField.REGISTRATION_DATE, userInfo.getRegistrationDate());
            stmt.setInt(QueryField.EXPIRED_DATE, userInfo.getExpiredDate());
            stmt.setInt(QueryField.MAX_MEMBERS, userInfo.getMaxMembers());
            stmt.setString(QueryField.IP_ADDRESS, userInfo.getIpAddress());
            stmt.executeUpdate();
        }
        
    }

    /**
     * This method will return subscriber info based on given user name
     *
     * @param userInfo, user info
     * @throws DBSetupException,
     * @throws SQLException
     * @return UserInfo, user info including details of the user
     */
    public UserInfo getSubscriberInfo(UserInfo userInfo) throws DBSetupException, SQLException {
        UserInfo subscriberInfo = new UserInfo();
        subscriberInfo.setSubscriberReferenceUserName(userInfo.getSubscriberReferenceUserName());
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.GET_SUBSCRIBER_INFO);){
            stmt.setString(QueryField.REFERENCE_USERNAME, userInfo.getSubscriberReferenceUserName());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                subscriberInfo.setUserId(rs.getString(QueryField.USER_ID));
                subscriberInfo.setSubscriberId(rs.getString(QueryField.USER_ID));
                subscriberInfo.setMaxMembers(rs.getInt(QueryField.MAX_MEMBERS));
                subscriberInfo.setRegistrationDate(rs.getInt(QueryField.REGISTRATION_DATE));
                subscriberInfo.setExpiredDate(rs.getInt(QueryField.EXPIRED_DATE));
            }
        } 
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.GET_SUBSCRIBER_TOTAL_MEMBERS);){
            stmt.setString(QueryField.SUBSCRIBER_USER_ID, subscriberInfo.getUserId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                subscriberInfo.setCurrentMemers(rs.getInt(QueryField.CURRENT_MEMBERS));
            }
        }
        return subscriberInfo;
    }
}
