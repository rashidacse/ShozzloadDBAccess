package org.bdlions.activemq;

import org.bdlions.constants.Services;
import org.bdlions.utility.ServerPropertyProvider;
import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nazmul
 */
public class ServerFuture {
    private static ServerFuture _serverFuture;
    static Logger _logger = LoggerFactory.getLogger(ServerFuture.class.getName());
    MQTT mqtt;
    FutureConnection connection;
    /**
     * Constructor
     */
    private ServerFuture()
    {
        try{
            mqtt = new MQTT();
            mqtt.setHost("127.0.0.1", 61612);

            connection = mqtt.futureConnection();
            Future<Void> f1 = connection.connect();
            f1.await();            
            System.out.println("Future Connection for server status:"+connection.isConnected());
            _logger.debug("Future Connection for server status:"+connection.isConnected());
         }
         catch(Exception ex)
         {
             _logger.error(ex.toString());
         }
    }
    
    public static ServerFuture getInstance()
    {
        if(_serverFuture == null)
        {
            _serverFuture = new ServerFuture();
        }
        return _serverFuture;
    }
    
    /**
     * Sending transaction to android local server
     * @param serviceId service id
     * @param localServerIdentifier, local server identifier
     * @param transactionInfo, transaction info
     */
    public void setTransaction(int serviceId, String localServerIdentifier, String transactionInfo)
    {
        String queueName = localServerIdentifier;
        if(serviceId == Services.SERVICE_TYPE_ID_BKASH_CASHIN)
        {
            queueName = localServerIdentifier+"_"+ServerPropertyProvider.get("SERVICE_QUEUE_BKASH_CASHIN");
        }
        try{
            Future<Void> f3 = connection.publish(queueName, transactionInfo.getBytes(), QoS.EXACTLY_ONCE, false);            
            System.out.println("Sending transaction to the android local server:"+transactionInfo+" to the queue:"+queueName);
         }
         catch(Exception ex)
         {
            _logger.error(ex.toString());
         }
    }
}
