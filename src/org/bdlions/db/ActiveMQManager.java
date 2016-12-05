/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.db;

import org.bdlions.constants.Transactions;
import org.bdlions.utility.ServerPropertyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nazmul hasan
 */
public class ActiveMQManager implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(ActiveMQManager.class.getName());
    private Thread t;
    BufferManager bufferManager;
    private String threadName;
    long sleepTime;
    public ActiveMQManager(BufferManager bufferManager, String threadName)
    {
        this.bufferManager = bufferManager;
        this.threadName = threadName;
        try
        {
            sleepTime = Long.parseLong(ServerPropertyProvider.get("BUFFER_CHECK_TIME_ACTIVEMQ"));
        }
        catch(Exception ex)
        {
            logger.debug("Invalid sleep time:"+ex.toString());
        }
    }
    
    @Override
    public void run() 
    {
        while(true)
        {
            try
            {
                bufferManager.processBuffer(null, Transactions.BUFFER_PROCESS_TYPE_ACTIVEMQ);
                Thread.sleep(sleepTime);
            }
            catch (Exception ex) {
                logger.error(ex.toString());
            } finally {

            }
            
        }
        //pQManager.getService(threadName);
    }
        
    
    public void start ()
    {
        if (t == null)
        {
           t = new Thread (this, threadName);
           t.start ();
        }
    }
}
