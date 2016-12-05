/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db.repositories;

import java.sql.Connection;
import java.sql.SQLException;
import org.bdlions.bean.UserServiceInfo;
import org.bdlions.db.query.QueryField;
import org.bdlions.db.query.QueryManager;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.exceptions.DBSetupException;
import org.bdlions.utility.Utils;

/**
 *
 * @author alamgir
 */
public class Service {

    private Connection connection;
    /***
     * Restrict to call without connection
     */
    private Service(){}
    public Service(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * This method will add subscriber service info
     *
     * @param userServiceInfo, subscriber service info
     * @throws DBSetupException
     * @throws SQLException
     */
    public void addService(UserServiceInfo userServiceInfo) throws DBSetupException, SQLException {
        //right now api key is generated from the admin panel
        //String APIKey = Utils.getAPIKey();
        //userServiceInfo.setAPIKey(APIKey);
        
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.ADD_SUBSCRIBER_SERVICE);){
            stmt.setString(QueryField.SUBSCRIBER_USER_ID, userServiceInfo.getUserId());
            stmt.setInt(QueryField.SERVICE_ID, userServiceInfo.getServiceId());
            stmt.setString(QueryField.API_KEY, userServiceInfo.getAPIKey());
            stmt.setInt(QueryField.REGISTRATION_DATE, userServiceInfo.getRegistrationDate());
            stmt.setInt(QueryField.EXPIRED_DATE, userServiceInfo.getExpiredDate());
            stmt.executeUpdate();
        }
        //adding callback function for the APIKey of this service
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.ADD_CALLBACK_FUNCTION);){
            stmt.setString(QueryField.API_KEY, userServiceInfo.getAPIKey());
            stmt.setString(QueryField.CALLBACK_FUNCTION, userServiceInfo.getCallbackFunction());
            stmt.executeUpdate();
        }
    }
}
