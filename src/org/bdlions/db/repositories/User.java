/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db.repositories;

import java.sql.Connection;
import java.sql.SQLException;
import org.bdlions.bean.UserInfo;
import org.bdlions.db.query.QueryField;
import org.bdlions.db.query.QueryManager;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.exceptions.DBSetupException;
import org.bdlions.utility.Utils;

/**
 *
 * @author nazmul hasan
 */
public class User {
    private Connection connection;
    /***
     * Restrict to call without connection
     */
    private User(){}
    public User(Connection connection) {
        this.connection = connection;
    }
    /**
     * This method will create a new user
     *
     * @param userInfo, user info
     * @return String, user id
     * @throws DBSetupException
     * @throws SQLException
     */
    public String createUser(UserInfo userInfo) throws DBSetupException, SQLException {
        int currentTime = Utils.getCurrentUnixTime();
        String userId = Utils.getRandomString();
        String subscriberId = userInfo.getSubscriberId();
        if(subscriberId == null)
        {
            subscriberId = userId;
        }
        userInfo.setUserId(userId);
        
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.CREATE_USER)) {
            stmt.setString(QueryField.USER_ID, userInfo.getUserId());
            stmt.setString(QueryField.SUBSCRIBER_USER_ID, subscriberId);
            stmt.setString(QueryField.REFERENCE_USERNAME, userInfo.getReferenceUserName());            
            stmt.setInt(QueryField.CREATED_ON, currentTime);
            stmt.setInt(QueryField.MODIFIED_ON, currentTime);
            stmt.executeUpdate();
        }
        
        return userId;
    }
}
