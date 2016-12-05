package org.bdlions.db;

import java.util.List;
import org.bdlions.activemq.Producer;
import org.bdlions.activemq.ServerFuture;
import org.bdlions.bean.TransactionInfo;
import org.bdlions.callback.CallbackTransactionManager;
import org.bdlions.constants.ResponseCodes;
import org.bdlions.constants.Services;
import org.bdlions.constants.Transactions;
import org.bdlions.utility.ServerPropertyProvider;
import org.bdlions.utility.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nazmul
 */
public class BufferManager {
    private static final Logger logger = LoggerFactory.getLogger(BufferManager.class.getName());
    static TransactionManager transactionManager = new TransactionManager();
    static int bufferTime;
    private static String localServerIdentifier = "";

    public void setLocalServerIdentifier(String localServerIdentifier) {
        this.localServerIdentifier = localServerIdentifier;
    }
    
    public BufferManager()
    {
        try
        {
            bufferTime = Integer.parseInt(ServerPropertyProvider.get("BUFFER_TRANSACTION_WAIT_TIME"));
        }
        catch(Exception ex)
        {
            logger.debug("Invalid transaction buffer time:"+ex.toString());
        }
    }
    
    public TransactionManager getTransactionManager()
    {
        return transactionManager;
    }
    
    synchronized public static void processBuffer(TransactionInfo transactionInfo, int processType)
    {
        if(processType == Transactions.BUFFER_PROCESS_TYPE_ADD_TRANSACTION)
        {
            System.out.println("ProcessType:"+processType);
            //add transaction
            transactionManager.addTransaction(transactionInfo);
        }
        else if(processType == Transactions.BUFFER_PROCESS_TYPE_UPDATE_TRANSACTION)
        {
            System.out.println("ProcessType:"+processType);
            //update transaction
            TransactionInfo updatedTransactionInfo = transactionManager.getTransactionInfo(transactionInfo.getTransactionId());
            if(updatedTransactionInfo.isEditable())
            {
                updatedTransactionInfo.setBalanceOut(transactionInfo.getBalanceOut());
                updatedTransactionInfo.setCellNumber(transactionInfo.getCellNumber());
                transactionManager.updateTransactionInfo(updatedTransactionInfo);
            }
            else
            {
                transactionManager.setResponseCode(ResponseCodes.ERROR_CODE_UPDATE_TRANSACTION_NOT_ALLOWED);
            }            
        }
        else if(processType == Transactions.BUFFER_PROCESS_TYPE_ACTIVEMQ)
        {
            System.out.println("ProcessType:"+processType);
            List<TransactionInfo> transactionList = transactionManager.getEditableTransactionList();
            int transactionListSize = transactionList.size();
            if(transactionListSize > 0)
            {
                AuthManager authManager = new AuthManager();
                String lsIdentifier = "";
                String baseURL = "";
                for(int counter = 0; counter < transactionList.size(); counter++)
                {
                    TransactionInfo editableTransactionInfo = new TransactionInfo();
                    editableTransactionInfo = transactionList.get(counter);
                    System.out.println(editableTransactionInfo.getTransactionId());
                    int currentTime = Utils.getCurrentUnixTime();
                    int createdOn = editableTransactionInfo.getCreatedOn();
                    if(currentTime >= createdOn + bufferTime)
                    {
                        System.out.println("We need to update editable status of this transaction.");
                        editableTransactionInfo.setEditable(Boolean.FALSE);
                        transactionManager.updateTransactionInfo(editableTransactionInfo);
                        //forward the transaction to activemq
                        lsIdentifier = authManager.getLSIdentifier(editableTransactionInfo.getAPIKey());
                        baseURL = authManager.getBaseURLTransactionId(editableTransactionInfo.getTransactionId());
                        
                        if(lsIdentifier != null && !lsIdentifier.equals(""))
                        {
                            try
                            {
                                //if(editableTransactionInfo.getLiveTestFlag().equals(Transactions.TRANSACTION_FLAG_LOCALSERVER_TEST) || editableTransactionInfo.getLiveTestFlag().equals(Transactions.TRANSACTION_FLAG_LIVE))
                                //right now for demols1 we are testing android local server
                                //if(!lsIdentifier.equals("demols1"))
                                if(editableTransactionInfo.getProcessTypeId() == Services.PROCESS_TYPE_ID_ACTIVE_MQ)
                                {
                                    //activemq to enqueue a new transaction
                                    Producer producer = new Producer();
                                    System.out.println(editableTransactionInfo.toString());
                                    producer.setMessage(editableTransactionInfo.toString());
                                    producer.setServiceQueueName(editableTransactionInfo.getServiceId(), lsIdentifier);
                                    System.out.println("Queue name:"+producer.getServiceQueueName());
                                    producer.produce();
                                }
                                else if(editableTransactionInfo.getProcessTypeId() == Services.PROCESS_TYPE_ID_MQTT)
                                {
                                    ServerFuture.getInstance().setTransaction(editableTransactionInfo.getServiceId(), lsIdentifier, editableTransactionInfo.toString());
                                }
                            }
                            catch(Exception ex)
                            {
                                logger.error(ex.toString());
                            }
                            //execute callback function to update editable at webserver
                            try
                            {
                                CallbackTransactionManager callbackTransactionManager = new CallbackTransactionManager();
                                callbackTransactionManager.setBaseURL(baseURL);
                                callbackTransactionManager.updateTransactionEditableStatus(editableTransactionInfo.getTransactionId(), editableTransactionInfo.isEditable());
                            }
                            catch(Exception ex)
                            {
                                logger.error(ex.toString());
                            }
                        }                        
                    }

                }
            }            
        }  
        else if(processType == Transactions.BUFFER_PROCESS_TYPE_MQTT_STOP_SIM)
        {
            try {
                //right now by default we have hardcoded for service id bkash cashin
                ServerFuture.getInstance().setTransaction(Services.SERVICE_TYPE_ID_BKASH_CASHIN, localServerIdentifier, transactionInfo.toString());
            } catch (Exception ex) {
                logger.debug(ex.toString());
            }
        }
    }
}
