package org.bdlions.activemq;
        
import java.net.URI;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public class Broker {
    static Logger _logger = LoggerFactory.getLogger(Broker.class.getName());
    public Broker()
    {
    
    }
    /**
    * Broker for android local server
    */
    public void startBroker()
    {
        try {
            BrokerService broker = new BrokerService();
            TransportConnector connector2 = new TransportConnector();
            //connector2.setName("mqtt");
            connector2.setUri(new URI("mqtt://0.0.0.0:61612?wireFormat.maxFrameSize=100000"));
            broker.addConnector(connector2);
            broker.start();
            _logger.debug("Broker has started.");
            System.out.println("Broker has started.");
        } catch (Exception ex) {
            _logger.debug(ex.toString());
        }
    }
}
