package org.bdlions.db.repositories;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bdlions.bean.SIMInfo;
import org.bdlions.bean.SIMSMSInfo;
import org.bdlions.bean.SIMServiceInfo;
import org.bdlions.db.query.QueryField;
import org.bdlions.db.query.QueryManager;
import org.bdlions.db.query.helper.EasyStatement;
import org.bdlions.exceptions.DBSetupException;
import org.bdlions.utility.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nazmul hasan
 */
public class SIM {
    private final Logger logger = LoggerFactory.getLogger(SIM.class);
    private Connection connection;
    /***
     * Restrict to call without connection
     */
    private SIM(){}
    public SIM(Connection connection)
    {
        this.connection = connection;
    }
    
    /**
     * This method will check whether a SIM exists or not
     * @param simNo
     * @return boolean
     * @throws DBSetupException
     * @throws SQLException
     * @author nazmul hasan on 22nd september 2016
     */
    public boolean checkSIM(String simNo) throws DBSetupException, SQLException
    {
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.GET_SIM_INFO);){
            stmt.setString(QueryField.SIM_NO, simNo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * This method will add a new SIM into the database
     * @param simInfo, SIMInfo
     * @throws DBSetupException
     * @throws SQLException
     */
    public void addSIM(SIMInfo simInfo) throws DBSetupException, SQLException
    {
        int currentTime = Utils.getCurrentUnixTime();
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.ADD_SIM)) {
            stmt.setString(QueryField.SIM_NO, simInfo.getSimNo());
            stmt.setString(QueryField.IDENTIFIER, simInfo.getIdentifier());
            stmt.setString(QueryField.DESCRIPTION, simInfo.getDescription());
            stmt.setInt(QueryField.STATUS, simInfo.getStatus());
            stmt.setInt(QueryField.CREATED_ON, currentTime);
            stmt.setInt(QueryField.MODIFIED_ON, currentTime);
            stmt.executeUpdate();
        }
        for(int counter = 0; counter < simInfo.getSimServiceList().size(); counter++)
        {
            SIMServiceInfo simServiceInfo = simInfo.getSimServiceList().get(counter);
            try (EasyStatement stmt = new EasyStatement(connection, QueryManager.ADD_SIM_SERVICE)) {
                stmt.setString(QueryField.SIM_NO, simInfo.getSimNo());
                stmt.setInt(QueryField.SERVICE_ID, simServiceInfo.getId());
                stmt.setInt(QueryField.CATEGORY_ID, simServiceInfo.getCategoryId());
                stmt.setDouble(QueryField.CURRENT_BALANCE, simServiceInfo.getCurrentBalance());
                stmt.setInt(QueryField.CREATED_ON, currentTime);
                stmt.setInt(QueryField.MODIFIED_ON, currentTime);
                stmt.executeUpdate();
            }
        }        
    }
    
    /**
     * This method will return all sims
     * @param identifier
     * @return List, sim list
     * @throws DBSetupException
     * @throws SQLException
     */
    public List<SIMInfo> getAllSIMs(String identifier) throws DBSetupException, SQLException
    {
        List<SIMInfo> simList = new ArrayList<>();
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.GET_ALL_SIMS);){
            stmt.setString(QueryField.IDENTIFIER, identifier);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SIMInfo simInfo = new SIMInfo();
                simInfo.setSimNo(rs.getString(QueryField.SIM_NO));
                simInfo.setIdentifier(rs.getString(QueryField.IDENTIFIER));
                simInfo.setDescription(rs.getString(QueryField.DESCRIPTION));
                simInfo.setStatus(rs.getInt(QueryField.STATUS));                
                simList.add(simInfo);
            }
        }
        return simList;
    }
    
    /**
     * This method will return all sims with services
     * @param identifier
     * @return List, sim list
     * @throws DBSetupException
     * @throws SQLException
     */
    public List<SIMInfo> getAllSIMsServices(String identifier) throws DBSetupException, SQLException
    {
        List<SIMInfo> simList = new ArrayList<>();
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.GET_ALL_SIMS_SERVICES);){
            stmt.setString(QueryField.IDENTIFIER, identifier);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SIMInfo simInfo = new SIMInfo();
                simInfo.setSimNo(rs.getString(QueryField.SIM_NO));
                simInfo.setIdentifier(rs.getString(QueryField.IDENTIFIER));
                simInfo.setDescription(rs.getString(QueryField.DESCRIPTION));
                simInfo.setStatus(rs.getInt(QueryField.STATUS));
                SIMServiceInfo simServiceInfo = new SIMServiceInfo();
                simServiceInfo.setId(rs.getInt(QueryField.SERVICE_ID));
                simServiceInfo.setCategoryId(rs.getInt(QueryField.CATEGORY_ID));
                simServiceInfo.setCurrentBalance(rs.getDouble(QueryField.CURRENT_BALANCE));
                simServiceInfo.setCreatedOn(rs.getInt(QueryField.CREATED_ON));
                simServiceInfo.setModifiedOn(rs.getInt(QueryField.MODIFIED_ON));
                simInfo.getSimServiceList().add(simServiceInfo);
                simList.add(simInfo);
            }
        }
        return simList;
    }
    
    /**
     * This method will return sim service info
     * @param simNo, sim no
     * @return SIMInfo, sim info
     * @exception DBSetupException
     * @exception SQLException
     */
    public SIMInfo getSIMServiceInfo(String simNo) throws DBSetupException, SQLException
    {
        SIMInfo simInfo = null;
        List<SIMServiceInfo> simServiceList = new ArrayList<>();
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.GET_SIM_SERVICE_INFO);){
            stmt.setString(QueryField.SIM_NO, simNo);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if(simInfo == null)
                {
                    simInfo = new SIMInfo();
                    simInfo.setSimNo(rs.getString("sim_no"));
                    simInfo.setIdentifier(rs.getString("identifier"));
                    simInfo.setDescription(rs.getString("description"));
                    simInfo.setStatus(rs.getInt("status"));
                }                
                SIMServiceInfo simServiceInfo = new SIMServiceInfo();
                simServiceInfo.setCurrentBalance(rs.getDouble("current_balance"));
                simServiceInfo.setId(rs.getInt("service_id"));
                simServiceInfo.setCategoryId(rs.getInt("category_id"));
                simServiceInfo.setCreatedOn(rs.getInt(QueryField.CREATED_ON));
                simServiceInfo.setModifiedOn(rs.getInt(QueryField.MODIFIED_ON));
                simServiceList.add(simServiceInfo);
            }
        }
        if(simInfo != null && simServiceList.size() > 0)
        {
            simInfo.setSimServiceList(simServiceList);
        }
        return simInfo;
    }
    
    /**
     * This method will update SIM Info
     *@param simInfo, SIM Info
     * @exception DBSetupException
     * @exception SQLException
     */
    public void updateSIMInfo(SIMInfo simInfo) throws DBSetupException, SQLException
    {
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.UPDATE_SIM_INFO);){
            stmt.setString(QueryField.IDENTIFIER, simInfo.getIdentifier());
            stmt.setString(QueryField.DESCRIPTION, simInfo.getDescription());
            stmt.setInt(QueryField.STATUS, simInfo.getStatus());
            stmt.setString(QueryField.SIM_NO, simInfo.getSimNo());
            stmt.executeUpdate();        
        }
    }
    
    /**
     * This method will update SIM Current Balance
     *@param simInfo, SIM Info
     * @exception DBSetupException
     * @exception SQLException
     */
    public void updateSIMServiceBalanceInfo(SIMInfo simInfo) throws DBSetupException, SQLException
    {
        int currentTime = Utils.getCurrentUnixTime();
        if(simInfo.getSimServiceList().size() > 0)
        {
            SIMServiceInfo simServiceInfo = simInfo.getSimServiceList().get(0);
            try (EasyStatement stmt = new EasyStatement(connection, QueryManager.UPDATE_SIM_SERVICE_BALANCE_INFO);){
                stmt.setDouble(QueryField.CURRENT_BALANCE, simServiceInfo.getCurrentBalance());
                stmt.setInt(QueryField.MODIFIED_ON, currentTime);
                stmt.setString(QueryField.SIM_NO, simInfo.getSimNo());
                stmt.setInt(QueryField.SERVICE_ID, simServiceInfo.getId());
                stmt.executeUpdate();        
            }
        }        
    }  
    
    /**
     * This method will add sim sms into the database
     * @param simSMSInfo
     * @throws DBSetupException
     * @throws SQLException
     */
    public void addSIMMessage(SIMSMSInfo simSMSInfo) throws DBSetupException, SQLException
    {
        //saving sms into a text file
        try {
            SecureRandom random = new SecureRandom();
            String fileName = simSMSInfo.getSimNo()+"_"+new BigInteger(130, random).toString(32);
            PrintWriter writer = new PrintWriter("sms/"+fileName+".txt", "UTF-8");
            writer.println(simSMSInfo.getSender());
            writer.println( simSMSInfo.getSms());
            writer.close();
        } catch (Exception ex) {
            logger.debug(ex.toString());
        }
        int currentTime = Utils.getCurrentUnixTime();
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.ADD_SIM_MESSAGE)) {
            stmt.setString(QueryField.COUNTRY_CODE, simSMSInfo.getCountryCode());
            stmt.setString(QueryField.SIM_NO, simSMSInfo.getSimNo());
            stmt.setString(QueryField.SENDER, simSMSInfo.getSender());
            stmt.setString(QueryField.SMS, simSMSInfo.getSms());
            stmt.setInt(QueryField.CREATED_ON,currentTime);
            stmt.setInt(QueryField.MODIFIED_ON,currentTime);
            stmt.executeUpdate();
        }
    }
    
    /**
     * This method will return sim messages
     * @param simNo
     * @param startTime
     * @param endTime
     * @param offset
     * @param limit
     * @return list
     * @throws DBSetupException
     * @throws SQLException
     * @author nazmul hasan on 17th September 2016
     */
    public List<SIMSMSInfo> getSIMMessages(String simNo, int startTime, int endTime, int offset, int limit) throws DBSetupException, SQLException
    {
        List<SIMSMSInfo> simSMSList = new ArrayList<>();
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.GET_SIM_MESSAGES);){
            stmt.setString(QueryField.SIM_NO, simNo);
            stmt.setInt(QueryField.START_TIME, startTime);
            stmt.setInt(QueryField.END_TIME, endTime);
            stmt.setInt(QueryField.OFFSET, offset);
            stmt.setInt(QueryField.LIMIT, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SIMSMSInfo simSMSInfo = new SIMSMSInfo();
                simSMSInfo.setId(rs.getInt(QueryField.ID));
                simSMSInfo.setSimNo(rs.getString(QueryField.SIM_NO));
                simSMSInfo.setSender(rs.getString(QueryField.SENDER));
                simSMSInfo.setSms(rs.getString(QueryField.SMS));
                simSMSInfo.setCreatedOn(rs.getInt(QueryField.CREATED_ON));
                simSMSList.add(simSMSInfo);
            }
        }
        return simSMSList;
    }
    /**
     * This method will return counter of all sim messages
     * @param simNo
     * @param startTime
     * @param endTime
     * @return integer
     * @throws DBSetupException
     * @throws SQLException
     * @author nazmul hasan on 17th September 2016
     */
    public int getSIMTotalMessages(String simNo, int startTime, int endTime) throws DBSetupException, SQLException
    {
        int counter = 0;
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.GET_SIM_TOTAL_MESSAGES);){
            stmt.setString(QueryField.SIM_NO, simNo);
            stmt.setInt(QueryField.START_TIME, startTime);
            stmt.setInt(QueryField.END_TIME, endTime);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                counter = rs.getInt("total_messages");
            }
        }
        return counter;
    }
    /**
     * This method will return all sim messages
     * @param simNo
     * @param startTime
     * @param endTime
     * @return list
     * @throws DBSetupException
     * @throws SQLException
     * @author nazmul hasan on 17th September 2016
     */
    public List<SIMSMSInfo> getAllSIMMessages(String simNo, int startTime, int endTime) throws DBSetupException, SQLException
    {
        List<SIMSMSInfo> simSMSList = new ArrayList<>();
        try (EasyStatement stmt = new EasyStatement(connection, QueryManager.GET_ALL_SIM_MESSAGES);){
            stmt.setString(QueryField.SIM_NO, simNo);
            stmt.setInt(QueryField.START_TIME, startTime);
            stmt.setInt(QueryField.END_TIME, endTime);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SIMSMSInfo simSMSInfo = new SIMSMSInfo();
                simSMSInfo.setId(rs.getInt(QueryField.ID));
                simSMSInfo.setSimNo(rs.getString(QueryField.SIM_NO));
                simSMSInfo.setSender(rs.getString(QueryField.SENDER));
                simSMSInfo.setSms(rs.getString(QueryField.SMS));
                simSMSInfo.setCreatedOn(rs.getInt(QueryField.CREATED_ON));
                simSMSList.add(simSMSInfo);
            }
        }
        return simSMSList;
    }
}
