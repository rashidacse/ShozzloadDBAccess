/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import org.bdlions.exceptions.DBSetupException;
import org.bdlions.exceptions.DBUserNameException;
import org.bdlions.exceptions.DatabaseNameException;
import org.bdlions.utility.DbPropertyProvider;
import org.bdlions.utility.DbSetupQueryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alamgir
 */
public class Database {
    private static Database _database;
    private String _connectionURL;
    private String _dbName = "gatekeeper_db";
    private String _driver = "com.mysql.jdbc.Driver";
    private String _userName = "root";
    private String _password = "";
    private String _host = "localhost";
    private String _port = "3306";
    static Logger _logger = LoggerFactory.getLogger(Database.class.getName());
    
    /***
     * 
     * @throws DBSetupException 
     */
    private Database() throws DBSetupException{
        
        _host = DbPropertyProvider.get("host");
        _dbName = DbPropertyProvider.get("dbName");
        _port = DbPropertyProvider.get("port");
        _userName = DbPropertyProvider.get("username");
        _password = DbPropertyProvider.get("password");
        
        
        _connectionURL = "jdbc:mysql://"+_host+":"+_port+"/";
        
        try{
            if(_userName == null || _userName.isEmpty()){
                throw new DBUserNameException();
            }
            if(_dbName == null || _dbName.isEmpty()){
                throw  new DatabaseNameException();
            }
            
            Class.forName(_driver); 
            try{
                if(!isDatabaseExists()){
                    setUpDatabase();
                }
            }
            catch(SQLException ex){
                throw new DBSetupException("Incomplete db setup error: " + ex.getMessage());
            }
            _connectionURL += _dbName;
        }
        catch(ClassNotFoundException | DBUserNameException | DatabaseNameException ex){
            _logger.info(ex.getMessage());
        }
    }
    
    /***
     * 
     * @return true/false
     * @throws SQLException 
     */
    private boolean isDatabaseExists() throws SQLException{
        boolean found;
        try (Connection connection = (Connection)DriverManager.getConnection(_connectionURL, _userName, _password)) {
            found = false;
            try (ResultSet resultSet = connection.getMetaData().getCatalogs()) {
                while (resultSet.next() && !resultSet.isLast()) {
                    if(resultSet.getString("TABLE_CAT").equalsIgnoreCase(_dbName)){
                        found = true;
                        break;
                    }
                }
            }
        }
        
        return found;
    }
    
    /***
     * initial setup database if not exist
     * @throws SQLException 
     */
    private void setUpDatabase() throws SQLException{
        Connection connection = getConnection();
        try (Statement statement = (Statement) connection.createStatement()) {
            //create database
            statement.executeUpdate(MessageFormat.format(DbSetupQueryProvider.get("db.create"), _dbName));
            statement.executeUpdate(MessageFormat.format(DbSetupQueryProvider.get("db.use"), _dbName));
            
            //create tables
            statement.executeUpdate(DbSetupQueryProvider.get("db.table.services"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.table.users"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.table.subscribers"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.table.subscribers_services"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.table.sim_services"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.table.sims"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.table.sims_services"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.table.sim_load_transactions"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.table.callback_functions"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.table.transaction_statuses"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.table.transaction_types"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.table.sms_details"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.table.sms_transactions"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.table.transactions"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.table.sims_messages"));
            
            //create relations
            statement.executeUpdate(DbSetupQueryProvider.get("db.rel.subscribers"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.rel.subscribers_services"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.rel.sims_services"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.rel.sim_load_transactions"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.rel.sms_transactions"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.rel.transactions"));
            
            //insert data
            statement.executeUpdate(DbSetupQueryProvider.get("db.defaultData.services"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.defaultData.sim_services"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.defaultData.users"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.defaultData.subscribers"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.defaultData.subscribers_services"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.defaultData.callback_functions"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.defaultData.transaction_statuses"));
            statement.executeUpdate(DbSetupQueryProvider.get("db.defaultData.transaction_types"));
            //statement.executeUpdate(DbSetupQueryProvider.get("db.defaultData.transactions"));
        }
    }
    
    /***
     * get the connection of the database with required username, password, dbname 
     * @return connection
     * @throws SQLException 
     */
    public Connection getConnection() throws SQLException{
        return (Connection) DriverManager.getConnection(_connectionURL, _userName, _password);
    }
    
    /**
     * get the instance of the database
     * singleton instance
     * 
     * @return
     * @throws DBSetupException 
     */
    public static synchronized Database getInstance() throws DBSetupException{
        if(_database == null){
            _database = new Database();
        }
        return _database;
    }
    
    
}
