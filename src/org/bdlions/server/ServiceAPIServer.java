/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.ArrayList;
import java.util.List;
import org.bdlions.bean.SIMInfo;
import org.bdlions.bean.SIMSMSInfo;
import org.bdlions.bean.SIMServiceInfo;
import org.bdlions.bean.SMSTransactionInfo;
import org.bdlions.bean.TransactionInfo;
import org.bdlions.constants.ResponseCodes;
import org.bdlions.constants.Services;
import org.bdlions.constants.Transactions;
import org.bdlions.db.BufferManager;
import org.bdlions.db.SIMManager;
import org.bdlions.db.TransactionManager;
import org.bdlions.response.ResultEvent;
import org.bdlions.utility.Email;
import org.bdlions.utility.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alamgir
 */
public class ServiceAPIServer extends AbstractVerticle {
    private final Logger logger = LoggerFactory.getLogger(ServiceAPIServer.class);
    @Override
    public void start() {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route("/").handler((RoutingContext routingContext) -> {
            HttpServerResponse response = routingContext.response();
            response.end("ServiceAPI server");
        });
        
        //router.route("/addtransaction").handler((RoutingContext routingContext) -> {
        router.route("/addtransaction*").handler(BodyHandler.create());
        router.post("/addtransaction").handler((RoutingContext routingContext) -> {
            HttpServerResponse response = routingContext.response();
            ResultEvent resultEvent = new ResultEvent();
            String userId = "";
            String sessionId = "";
            //validate userId and sessionId from the hashmap
            
            String APIKey = routingContext.request().getParam("APIKey");
            String amount = routingContext.request().getParam("amount");
            String cellNumber = routingContext.request().getParam("cell_no");
            String packageId = routingContext.request().getParam("package_id");
            String description = routingContext.request().getParam("description");
            String liveTestFlag = routingContext.request().getParam("livetestflag");
            TransactionInfo transactionInfo = new TransactionInfo();
            transactionInfo.setAPIKey(APIKey);
            transactionInfo.setCellNumber(cellNumber);
            transactionInfo.setDescription(description);
            transactionInfo.setLiveTestFlag(liveTestFlag);
            transactionInfo.setEditable(Boolean.TRUE);
            try
            {
                transactionInfo.setPackageId(Integer.parseInt(packageId));
            }
            catch(Exception ex)
            {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_OPERATOR_PACKAGE_ID);
                logger.error(ex.getMessage());
                response.end(resultEvent.toString());
                return;
            }
            try
            {
                transactionInfo.setBalanceOut(Double.parseDouble(amount));
            }
            catch(Exception ex)
            {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_AMOUNT);
                logger.error(ex.getMessage());
                response.end(resultEvent.toString());
                return;
            }
            
            //for web server test transaction we are returning back from here
            if(transactionInfo.getLiveTestFlag().equals(Transactions.TRANSACTION_FLAG_WEBSERVER_TEST))
            {
                resultEvent.setResponseCode(ResponseCodes.SUCCESS);
                transactionInfo.setTransactionId(Utils.getTransactionId());
                resultEvent.setResult(transactionInfo);
                response.end(resultEvent.toString());
                return;           
            }
            
            BufferManager bufferManager = new BufferManager();
            bufferManager.processBuffer(transactionInfo, Transactions.BUFFER_PROCESS_TYPE_ADD_TRANSACTION);
            int responseCode = bufferManager.getTransactionManager().getResponseCode();
            
            resultEvent.setResponseCode(responseCode);
            if(responseCode == ResponseCodes.SUCCESS)
            {
                transactionInfo.setTransactionId(bufferManager.getTransactionManager().getTransactionId());
                resultEvent.setResult(transactionInfo);
            }
            response.end(resultEvent.toString());
        });
        
        router.route("/addmultipletransactions*").handler(BodyHandler.create());
        router.post("/addmultipletransactions").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            HttpServerResponse response = routingContext.response();
            List<TransactionInfo> transactionInfoList = new ArrayList<>();
            String transactionList = routingContext.request().getParam("transction_list");
            String liveTestFlag = routingContext.request().getParam("livetestflag");
            JsonArray transactionArray = new JsonArray(transactionList);
            BufferManager bufferManager = new BufferManager();
            for(int counter = 0 ; counter < transactionArray.size(); counter++)
            {
                JsonObject jsonObject = new JsonObject(transactionArray.getValue(counter).toString());
                String id = jsonObject.getString("id"); 
                String cellNo = jsonObject.getString("cell_no"); 
                String APIKey = jsonObject.getString("APIKey"); 
                String packageId = jsonObject.getString("operator_type_id");
                String amount = jsonObject.getString("amount");
                
                TransactionInfo transactionInfo = new TransactionInfo();
                transactionInfo.setEditable(Boolean.TRUE);
                transactionInfo.setAPIKey(APIKey);
                try
                {
                    transactionInfo.setPackageId(Integer.parseInt(packageId));
                }
                catch(Exception ex)
                {
                    resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_OPERATOR_PACKAGE_ID);
                    logger.error(ex.getMessage());
                    response.end(resultEvent.toString());
                    return;
                }
                try
                {
                    transactionInfo.setBalanceOut(Double.parseDouble(amount));
                }
                catch(Exception ex)
                {
                    resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_AMOUNT);
                    logger.error(ex.getMessage());
                    response.end(resultEvent.toString());
                    return;
                }
                transactionInfo.setLiveTestFlag(liveTestFlag);
                transactionInfo.setCellNumber(cellNo);
                transactionInfo.setReferenceId(id);
                //for web server test transaction we are returning back from here
                if(transactionInfo.getLiveTestFlag().equals(Transactions.TRANSACTION_FLAG_WEBSERVER_TEST))
                {
                    transactionInfo.setTransactionId(Utils.getTransactionId());                    
                }
                else
                {
                    //transactionInfo.setTransactionId(Utils.getTransactionId());                
                    //UserServiceInfo userServiceInfo = transactionManager.getUserServiceInfo(APIKey);
                    //transactionInfo.setServiceId(userServiceInfo.getServiceId());
                    //transactionManager.addTransaction(transactionInfo);
                    bufferManager.processBuffer(transactionInfo, Transactions.BUFFER_PROCESS_TYPE_ADD_TRANSACTION);
                    int responseCode = bufferManager.getTransactionManager().getResponseCode();
                    if(responseCode == ResponseCodes.SUCCESS)
                    {
                        transactionInfo.setTransactionId(bufferManager.getTransactionManager().getTransactionId());
                    } 
                    //what will you do if response code is not success?                    
                }     
                transactionInfoList.add(transactionInfo);
            }            
            resultEvent.setResult(transactionInfoList);
            resultEvent.setResponseCode(ResponseCodes.SUCCESS);            
            
            response.end(resultEvent.toString());
        });
        
        router.route("/updatetransactioninfo*").handler(BodyHandler.create());
        router.post("/updatetransactioninfo").handler((RoutingContext routingContext) -> {
            HttpServerResponse response = routingContext.response();
            ResultEvent resultEvent = new ResultEvent();
            String transactionId = routingContext.request().getParam("transaction_id");
            String amount = routingContext.request().getParam("amount");
            String cellNumber = routingContext.request().getParam("cell_no");
            TransactionInfo transactionInfo = new TransactionInfo();
            transactionInfo.setTransactionId(transactionId);
            transactionInfo.setCellNumber(cellNumber);
            transactionInfo.setEditable(Boolean.TRUE);
            try
            {
                transactionInfo.setBalanceOut(Double.parseDouble(amount));
            }
            catch(Exception ex)
            {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_INVALID_AMOUNT);
                logger.error(ex.getMessage());
                response.end(resultEvent.toString());
                return;
            }
              
            
            BufferManager bufferManager = new BufferManager();
            bufferManager.processBuffer(transactionInfo, Transactions.BUFFER_PROCESS_TYPE_UPDATE_TRANSACTION);
            int responseCode = bufferManager.getTransactionManager().getResponseCode();
            resultEvent.setResponseCode(responseCode);
            if(responseCode == ResponseCodes.SUCCESS)
            {
                resultEvent.setResult(transactionInfo);
            }
            response.end(resultEvent.toString());
        });
        
        //updating transaction acknowledge status from local server
        router.route("/transactionstatusack*").handler(BodyHandler.create());
        router.post("/transactionstatusack").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            //our system transaction id
            String transactionId = routingContext.request().getParam("transactionid");
            //transaction status id
            String statusIdStr = routingContext.request().getParam("statusid");
            System.out.println("Updating transaction status ack ----------------transactionId:"+transactionId+",statusIdStr:"+statusIdStr);
            int statusId = 0;
            try
            {
                statusId = Integer.parseInt(statusIdStr);
            }
            catch(Exception ex)
            {
                logger.error(ex.getMessage());
            }
            //updating transaction status acknowledgement
            try
            {
                TransactionInfo transactionInfo = new TransactionInfo();
                transactionInfo.setTransactionId(transactionId);
                transactionInfo.setTransactionStatusId(statusId);
                
                TransactionManager transactionManager = new TransactionManager();
                transactionManager.updateTransactionStatusAck(transactionInfo);

                int responseCode = transactionManager.getResponseCode();
                resultEvent.setResponseCode(responseCode);  
            }
            catch(Exception ex)
            {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_UPDATE_TRANSACTION_STATUS_ACK_FAILED);
                logger.error(ex.getMessage());
            }            
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());            
        });
        
        //updating transaction status with other parameters for ussd call at local server
        router.route("/updatetransactionstatus*").handler(BodyHandler.create());
        router.post("/updatetransactionstatus").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            //our system transaction id
            String transactionId = routingContext.request().getParam("transactionid");
            //transaction status id
            String statusIdStr = routingContext.request().getParam("statusid");
            // transaction id from operator
            String trxIdOperator = routingContext.request().getParam("trxidoperator");            
            //sender cell number
            String senderCellNumber = routingContext.request().getParam("sendercellnumber");
            //sim service id of sender
            String serviceIdStr = routingContext.request().getParam("serviceid");
            //current balance of sender
            String balanceStr = routingContext.request().getParam("balance");   
            
            System.out.println("Updating transaction status----------------transactionId:"+transactionId+",statusIdStr:"+statusIdStr+",trxIdOperator:"+trxIdOperator+",senderCellNumber:"+senderCellNumber+",serviceIdStr:"+serviceIdStr+",balanceStr:"+balanceStr);
            
            int statusId = 0;
            try
            {
                statusId = Integer.parseInt(statusIdStr);
            }
            catch(Exception ex)
            {
                logger.error(ex.getMessage());
            }
            double balance = 0;
            try
            {
                balance = Double.parseDouble(balanceStr);
            }
            catch(Exception ex)
            {
                logger.error(ex.getMessage());
            }
            
            int serviceId = 0;
            try
            {
                serviceId = Integer.parseInt(serviceIdStr);
            }
            catch(Exception ex)
            {
                logger.error(ex.getMessage());
            }
            //updating transaction status with sender cell number and operator transaction id
            try
            {
                TransactionInfo transactionInfo = new TransactionInfo();
                transactionInfo.setTransactionId(transactionId);
                transactionInfo.setTransactionStatusId(statusId);
                transactionInfo.setSenderCellNumber(senderCellNumber);
                transactionInfo.setTrxIdOperator(trxIdOperator);

                TransactionManager transactionManager = new TransactionManager();
                transactionManager.updateTransactionStatus(transactionInfo);

                int responseCode = transactionManager.getResponseCode();
                resultEvent.setResponseCode(responseCode);  
            }
            catch(Exception ex)
            {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_UPDATE_TRANSACTION_STATUS_FAILED);
                logger.error(ex.getMessage());
            }
            //updating sim current balance
            try
            {
                SIMManager simManager = new SIMManager();
                SIMInfo simInfo = new SIMInfo();
                simInfo.setSimNo(senderCellNumber);
                SIMServiceInfo simServiceInfo = new SIMServiceInfo();
                simServiceInfo.setCurrentBalance(balance);
                simServiceInfo.setId(serviceId);
                simInfo.getSimServiceList().add(simServiceInfo);
                simManager.updateSIMServiceBalanceInfo(simInfo);

                int responseCode = simManager.getResponseCode();
                resultEvent.setResponseCode(responseCode); 
            }
            catch(Exception ex)
            {
                logger.error(ex.getMessage());
            }
            
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());
            
        });
        
        router.route("/updatestktransactionstatus*").handler(BodyHandler.create());
        router.post("/updatestktransactionstatus").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            String serviceIdStr = routingContext.request().getParam("serviceId");
            //expected format of cell number 8801711123456
            String senderCellNumber = routingContext.request().getParam("sendercellnumber");   
            String currentBalanceStr = routingContext.request().getParam("currentbalance");
            String APIKey = routingContext.request().getParam("apikey");  
            //expected format of cell number 01711123456
            String cellNumber = routingContext.request().getParam("cellnumber");
            String balanceStr = routingContext.request().getParam("balance");
            //transaction id from operators
            String transactionId = routingContext.request().getParam("transactionid");
            String statusIdStr = routingContext.request().getParam("statusid");
            String sender = routingContext.request().getParam("sender");
            String sms = routingContext.request().getParam("sms");
            System.out.println("Updating stk transaction status----------------serviceIdStr:"+serviceIdStr+",senderCellNumber:"+senderCellNumber+",currentBalanceStr:"+currentBalanceStr+",APIKey:"+APIKey+",cellNumber:"+cellNumber+",balanceStr:"+balanceStr+",transactionId:"+transactionId+",statusIdStr:"+statusIdStr+",sender:"+sender+",sms:"+sms);
            int serviceId = 0;
            try
            {
                serviceId = Integer.parseInt(serviceIdStr);
            }
            catch(Exception ex)
            {
                logger.debug("Invalid serviceId at updatestktransactionstatus-senderCellNumber:"+senderCellNumber+",currentBalance:"+currentBalanceStr+",APIKey:"+APIKey+",cellNumber:"+cellNumber+",balance:"+balanceStr+",transactionId:"+transactionId+",statusId:"+statusIdStr+",serviceId:"+serviceIdStr);
                logger.debug(ex.getMessage());
            }
            int statusId = 0;
            try
            {
                statusId = Integer.parseInt(statusIdStr);
            }
            catch(Exception ex)
            {
                logger.debug("Invalid statusId at updatestktransactionstatus-senderCellNumber:"+senderCellNumber+",currentBalance:"+currentBalanceStr+",APIKey:"+APIKey+",cellNumber:"+cellNumber+",balance:"+balanceStr+",transactionId:"+transactionId+",statusId:"+statusIdStr+",serviceId:"+serviceIdStr);
                logger.debug(ex.getMessage());
            }
            double currentBalance = 0;
            try
            {
                currentBalance = Double.parseDouble(currentBalanceStr);
            }
            catch(Exception ex)
            {
                logger.debug("Invalid current balance at updatestktransactionstatus-senderCellNumber:"+senderCellNumber+",currentBalance:"+currentBalanceStr+",APIKey:"+APIKey+",cellNumber:"+cellNumber+",balance:"+balanceStr+",transactionId:"+transactionId+",statusId:"+statusIdStr+",serviceId:"+serviceIdStr);
                logger.debug(ex.getMessage());
            }
            double balance = 0;
            try
            {
                balance = Double.parseDouble(balanceStr);
            }
            catch(Exception ex)
            {
                logger.debug("Invalid balance at updatestktransactionstatus-senderCellNumber:"+senderCellNumber+",currentBalance:"+currentBalanceStr+",APIKey:"+APIKey+",cellNumber:"+cellNumber+",balance:"+balanceStr+",transactionId:"+transactionId+",statusId:"+statusIdStr+",serviceId:"+serviceIdStr);
                logger.debug(ex.getMessage());
            }
            
            try
            {
                TransactionInfo transactionInfo = new TransactionInfo();
                //updating transaction info
                transactionInfo.setSenderCellNumber(senderCellNumber);
                transactionInfo.setAPIKey(APIKey);
                transactionInfo.setCellNumber(cellNumber);
                transactionInfo.setBalanceOut(balance);
                transactionInfo.setTrxIdOperator(transactionId);
                transactionInfo.setTransactionStatusId(statusId);

                TransactionManager transactionManager = new TransactionManager();
                transactionManager.updateLSSTKTransactionStatus(transactionInfo);
                int responseCode = transactionManager.getResponseCode();
                if(responseCode == ResponseCodes.SUCCESS)
                {
                    try
                    {
                        //updating SIM current balance for this service
                        SIMManager simManager = new SIMManager();
                        SIMInfo simInfo = new SIMInfo();
                        simInfo.setSimNo(senderCellNumber);
                        SIMServiceInfo simServiceInfo = new SIMServiceInfo();
                        simServiceInfo.setCurrentBalance(currentBalance);
                        simServiceInfo.setId(serviceId);
                        simInfo.getSimServiceList().add(simServiceInfo);
                        simManager.updateSIMServiceBalanceInfo(simInfo);

                        responseCode = simManager.getResponseCode();
                        resultEvent.setResponseCode(responseCode); 
                        
                        SIMSMSInfo simSMSInfo = new SIMSMSInfo();
                        simSMSInfo.setSimNo(senderCellNumber);
                        simSMSInfo.setSender(sender);
                        simSMSInfo.setSms(sms);
                        simManager.addSIMMessage(simSMSInfo);
                    }
                    catch(Exception ex)
                    {
                        resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_SIM_BALANCE_UPDATE_FAILED);
                        logger.error(ex.getMessage());
                    }
                }                
                resultEvent.setResponseCode(responseCode);  
            }
            catch(Exception ex)
            {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_UPDATE_TRANSACTION_STATUS_FAILED);
                logger.error(ex.getMessage());
            }
            
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());
            
        });
        
        //this method will update sim balance of a service
        router.route("/updatesimbalance*").handler(BodyHandler.create());
        router.post("/updatesimbalance").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            //sim service id
            String serviceIdStr = routingContext.request().getParam("serviceid");
            //8801711123456
            String senderCellNumber = routingContext.request().getParam("sendercellnumber");
            //current balance of the sim under the sim service
            String balanceStr = routingContext.request().getParam("balance");
            //sms sender title
            String sender = routingContext.request().getParam("sender");
            //sms text
            String sms = routingContext.request().getParam("sms");
            System.out.println("Updating sim balance----------------serviceIdStr:"+serviceIdStr+",senderCellNumber:"+senderCellNumber+",balanceStr:"+balanceStr+",sender:"+sender+",sms:"+sms);
            double balance = 0;
            try
            {
                balance = Double.parseDouble(balanceStr);
            }
            catch(Exception ex)
            {
                logger.debug("Invalid balance at updatesimbalance-senderCellNumber:"+senderCellNumber+",balance:"+balanceStr+",serviceId:"+serviceIdStr+",sender:"+sender+",sms:"+sms);
                logger.debug(ex.getMessage());
            }  
            int serviceId = 0;
            try
            {
                serviceId = Integer.parseInt(serviceIdStr);
            }
            catch(Exception ex)
            {
                logger.debug("Invalid serviceId at updatesimbalance-senderCellNumber:"+senderCellNumber+",balance:"+balanceStr+",serviceId:"+serviceIdStr+",sender:"+sender+",sms:"+sms);
                logger.debug(ex.getMessage());
            }
            try
            {
                SIMManager simManager = new SIMManager();
                SIMInfo simInfo = new SIMInfo();
                simInfo.setSimNo(senderCellNumber);
                SIMServiceInfo simServiceInfo = new SIMServiceInfo();
                simServiceInfo.setCurrentBalance(balance);
                simServiceInfo.setId(serviceId);
                simInfo.getSimServiceList().add(simServiceInfo);
                simManager.updateSIMServiceBalanceInfo(simInfo);

                int responseCode = simManager.getResponseCode();
                resultEvent.setResponseCode(responseCode);
                //saving sim sms if exists (there will sms for stk feature)
                if(sender != null && !sender.isEmpty() && sms != null && !sms.isEmpty())
                {
                    SIMSMSInfo simSMSInfo = new SIMSMSInfo();
                    simSMSInfo.setSimNo(senderCellNumber);
                    simSMSInfo.setSender(sender);
                    simSMSInfo.setSms(sms);
                    simManager.addSIMMessage(simSMSInfo);
                }                
            }
            catch(Exception ex)
            {
                logger.debug(ex.getMessage());
            }            
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());            
        });
        
        //this method will update sim balance of a service
        router.route("/savesimsms*").handler(BodyHandler.create());
        router.post("/savesimsms").handler((RoutingContext routingContext) -> {
            ResultEvent resultEvent = new ResultEvent();
            //expected format is 8801711123456
            String senderCellNumber = routingContext.request().getParam("sendercellnumber");
            //sms sender title
            String sender = routingContext.request().getParam("sender");
            //sms text
            String sms = routingContext.request().getParam("sms");
            System.out.println("Saving sim sms----------------senderCellNumber:"+senderCellNumber+",sender:"+sender+",sms:"+sms);
            try
            {
                SIMManager simManager = new SIMManager();
                SIMSMSInfo simSMSInfo = new SIMSMSInfo();
                simSMSInfo.setSimNo(senderCellNumber);
                simSMSInfo.setSender(sender);
                simSMSInfo.setSms(sms);
                simManager.addSIMMessage(simSMSInfo);
                int responseCode = simManager.getResponseCode();
                resultEvent.setResponseCode(responseCode);
            }
            catch(Exception ex)
            {
                logger.debug(ex.getMessage());
            }            
            HttpServerResponse response = routingContext.response();
            response.end(resultEvent.toString());            
        });
        /**
         * post method to send bulk sms
         * @param cellnumberlist, cell number list
         * @param sms, sms body
         * @param APIKey, APIKey
         * @param livetestflag, flag of transaction
        */
        router.route("/sendsms*").handler(BodyHandler.create());
        router.post("/sendsms").handler((RoutingContext routingContext) -> {
            HttpServerResponse response = routingContext.response();
            ResultEvent resultEvent = new ResultEvent();
            SMSTransactionInfo smsTransactionInfo = new SMSTransactionInfo();
            String cellNumberList = routingContext.request().getParam("cellnumberlist");
            String sms = routingContext.request().getParam("sms");
            String APIKey = routingContext.request().getParam("APIKey");
            String liveTestFlag = routingContext.request().getParam("livetestflag");
            smsTransactionInfo.setLiveTestFlag(liveTestFlag);
            smsTransactionInfo.setSms(sms);
            smsTransactionInfo.setAPIKey(APIKey);
            JsonArray cellNumberArray = new JsonArray(cellNumberList);
            //JsonArray ja = new JsonArray();
            for(int counter = 0 ; counter < cellNumberArray.size(); counter++)
            {
                JsonObject jsonObject = new JsonObject(cellNumberArray.getValue(counter).toString());
                String id = jsonObject.getString("id");
                String cellNo = jsonObject.getString("cell_no");                
                smsTransactionInfo.getCellNumberList().add(cellNo);
                
                
                //System.out.println(id);
                //System.out.println(cellNo);
                
                //JsonObject jO = new JsonObject();
                //jO.put("id", id);
                //jO.put("cell_no", cellNo);
                
                //ja.add(jO);
            }
            try
            {
                TransactionManager transactionManager = new TransactionManager();
                transactionManager.addSMSTransaction(smsTransactionInfo);
                int responseCode = transactionManager.getResponseCode();
                resultEvent.setResponseCode(responseCode);
                if(responseCode == ResponseCodes.SUCCESS)
                {
                    smsTransactionInfo.setTransactionId(transactionManager.getTransactionId());
                    resultEvent.setResult(smsTransactionInfo);
                }
            }
            catch(Exception ex)
            {
                resultEvent.setResponseCode(ResponseCodes.ERROR_CODE_WEBSERVICE_PROCESS_EXCEPTION);
                logger.error(ex.toString());
            }
            response.end(resultEvent.toString());            
        });
        
        router.route("/sendemail*").handler(BodyHandler.create());
        router.post("/sendemail").handler((RoutingContext routingContext) -> {
            HttpServerResponse response = routingContext.response();
            ResultEvent resultEvent = new ResultEvent();
            String receiverEmail = routingContext.request().getParam("email");
            String message = routingContext.request().getParam("message");
            Email email = new Email();
            email.sendEmail(receiverEmail, message);
            response.end(resultEvent.toString());            
        });
        
        server.requestHandler(router::accept).listen(3030);
    }
}
