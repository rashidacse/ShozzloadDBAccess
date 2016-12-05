package org.bdlions.activemq;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.bdlions.constants.Services;
import org.bdlions.utility.ServerPropertyProvider;

public class Producer {
    private String message = null;
    private String serviceQueueName = "";

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setServiceQueueName(int serviceId, String localServerIdentifier)
    {
        //if we have several local server then dynamically provide the identifier of local server
        //String localServerIdentifier = "ls1";
        if(serviceId == Services.SERVICE_TYPE_ID_BKASH_CASHIN)
        {
            this.serviceQueueName = localServerIdentifier+"_"+ServerPropertyProvider.get("SERVICE_QUEUE_BKASH_CASHIN");
        }
        
        if(serviceId == Services.SERVICE_TYPE_ID_DBBL_CASHIN)
        {
            this.serviceQueueName = localServerIdentifier+"_"+ServerPropertyProvider.get("SERVICE_QUEUE_DBBLCASHIN");
        }
        if(serviceId == Services.SERVICE_TYPE_ID_MCASH_CASHIN)
        {
            this.serviceQueueName = ServerPropertyProvider.get("SERVICE_QUEUE_MCASHCASHIN");
        }
        if(serviceId == Services.SERVICE_TYPE_ID_UCASH_CASHIN)
        {
            this.serviceQueueName = localServerIdentifier+"_"+ServerPropertyProvider.get("SERVICE_QUEUE_UKASHCASHIN");
        }
        if(serviceId == Services.SERVICE_TYPE_ID_TOPUP_GP)
        {
            this.serviceQueueName = localServerIdentifier+"_"+ServerPropertyProvider.get("SERVICE_QUEUE_TOPUPGP");
        }
        if(serviceId == Services.SERVICE_TYPE_ID_TOPUP_ROBI)
        {
            this.serviceQueueName = localServerIdentifier+"_"+ServerPropertyProvider.get("SERVICE_QUEUE_TOPUPROBI");
        }
        if(serviceId == Services.SERVICE_TYPE_ID_TOPUP_BANGLALINK)
        {
            this.serviceQueueName = localServerIdentifier+"_"+ServerPropertyProvider.get("SERVICE_QUEUE_TOPUPBANGLALINK");
        }
        if(serviceId == Services.SERVICE_TYPE_ID_TOPUP_AIRTEL)
        {
            this.serviceQueueName = localServerIdentifier+"_"+ServerPropertyProvider.get("SERVICE_QUEUE_TOPUPAIRTEL");
        }
        if(serviceId == Services.SERVICE_TYPE_ID_TOPUP_TELETALK)
        {
            this.serviceQueueName = localServerIdentifier+"_"+ServerPropertyProvider.get("SERVICE_QUEUE_TOPUPTELETALK");
        }
        if(serviceId == Services.SERVICE_TYPE_ID_SEND_SMS)
        {
            this.serviceQueueName = localServerIdentifier+"_"+ServerPropertyProvider.get("SERVICE_QUEUE_SENDSMS");
        }
    }
    
    public void setCheckBalanceQueueName(String simNo)
    {
        this.serviceQueueName = simNo+"_"+ServerPropertyProvider.get("SERVICE_QUEUE_CHECK_BALANCE");
    }
    
    public String getServiceQueueName()
    {
        return this.serviceQueueName;
    }
    
    public void produce() throws Exception
    {
        // Create a connection factory referring to the broker host and port
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory
          (ServerPropertyProvider.get("PRODUCER_URL"));
        // Note that a new thread is created by createConnection, and it
        //  does not stop even if connection.stop() is called. We must
        //  shut down the JVM using System.exit() to end the program
        Connection connection = factory.createConnection();
        
        // Start the connection
        connection.start();
        
        // Create a non-transactional session with automatic acknowledgement
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        // Create a reference to the queue test_queue in this session. Note
        //  that ActiveMQ has auto-creation enabled by default, so this JMS
        //  destination will be created on the broker automatically
        Queue queue = session.createQueue(this.getServiceQueueName());
        
        // Create a producer for test_queue
        MessageProducer producer = session.createProducer(queue);
        
        // Create a simple text message and send it
        TextMessage message = session.createTextMessage (this.getMessage());
        producer.send(message);
        
        // Stop the connection — good practice but redundant here
        connection.stop();
    }
    
}
