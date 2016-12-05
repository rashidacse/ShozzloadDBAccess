/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.activemq;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alamgir
 */
public class MessageQServer {

    private final String _brokerName = "messageQBroker";
    private final String _dataDirectoryName = "data";

    private BrokerService broker = null;

    static Logger _logger = LoggerFactory.getLogger(MessageQServer.class.getName());

    private static MessageQServer _queingServer;

    private MessageQServer() {

       
    }

    public static synchronized MessageQServer getInstance() {
        if (_queingServer == null) {
            _queingServer = new MessageQServer();
        }
        return _queingServer;
    }

    public synchronized void start(){
        try {
            broker = new BrokerService();

            TransportConnector connector = new TransportConnector();
            connector.setUri(new URI("tcp://0.0.0.0:61616?useJmx=true"));
            broker.addConnector(connector);

            broker.setDataDirectory(_dataDirectoryName);
            broker.setBrokerName(_brokerName);

//            JDBCPersistenceAdapter jdbcPersistenceAdapter = new JDBCPersistenceAdapter();
//            MysqlDataSource dataSource = new MysqlDataSource();
//
//            dataSource.setDatabaseName(_dbName);
//            dataSource.setUser(_userName);
//            dataSource.setPassword(_password);
//            dataSource.setPort(Integer.parseInt(_port));
//            dataSource.setServerName(_host);
//
//            jdbcPersistenceAdapter.setDataSource(dataSource);
//
//            broker.setPersistenceAdapter(jdbcPersistenceAdapter);
            broker.start();
            
            _logger.debug("MessageQ is ready to consume.");
            System.out.println("MessageQ is ready to consume.");

        } catch (URISyntaxException | IOException ex) {
            
        } catch (Exception ex) {
            
        }
    }

    public synchronized void stop(){
        try {
            if (broker != null) {
                broker.stop();
            }
        } catch (Exception ex) {
            
        }
    }
}
