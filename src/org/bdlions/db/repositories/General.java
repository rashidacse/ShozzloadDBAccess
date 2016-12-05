package org.bdlions.db.repositories;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bdlions.db.query.QueryField;
import org.bdlions.db.query.QueryManager;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.exceptions.DBSetupException;

/**
 *
 * @author nazmul hasan
 */
public class General {
    private Connection connection;
    /***
     * Restrict to call without connection
     */
    private General(){}
    public General(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * This method will return base url based on op code
     * @param opCode, op code
     * @return baseURL
     * @throws DBSetupException
     * @throws SQLException
     * @author nazmul hasan on 28th july 2016
     */
    public String getBaseURLOPCode(String opCode) throws DBSetupException, SQLException
    {
        String baseURL = "";
        try (EasyStatement stmt = new EasyStatement(this.connection, QueryManager.GET_BASE_URL_OP_CODE);){
            stmt.setString(QueryField.OP_CODE, opCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                baseURL = rs.getString(QueryField.BASE_URL);
            }
        }
        return baseURL;
    }
    
    public String getBaseURLTransactionId(String transactionId) throws DBSetupException, SQLException
    {
        String baseURL = "";
        try (EasyStatement stmt = new EasyStatement(this.connection, QueryManager.GET_BASE_URL_TRANSACTION_ID);){
            stmt.setString(QueryField.TRANSACTION_ID, transactionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                baseURL = rs.getString(QueryField.BASE_URL);
            }
        }
        return baseURL;
    }
    
    public String getLSIdentifier(String apiKey) throws DBSetupException, SQLException
    {
        String lsIdentifier = "";
        try (EasyStatement stmt = new EasyStatement(this.connection, QueryManager.GET_LOCAL_SERVER_IDENTIFIER);){
            stmt.setString(QueryField.API_KEY, apiKey);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                lsIdentifier = rs.getString(QueryField.LS_IDENTIFIER);
            }
        }
        return lsIdentifier;
    }
}
