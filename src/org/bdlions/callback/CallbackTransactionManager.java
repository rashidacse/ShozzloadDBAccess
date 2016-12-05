/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.callback;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.bdlions.utility.ServerPropertyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nazmul hasan
 */
public class CallbackTransactionManager {
    private static final String USER_AGENT = "Mozilla/5.0";
    private String baseURL;
    private static final Logger logger = LoggerFactory.getLogger(CallbackTransactionManager.class.getName());
    public CallbackTransactionManager()
    {
        
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }
    
    public void updateTransactionStatus(String transactionId, int statusId, String senderCellNumber, String trxIdOperator)
    {
        logger.debug("baseURL:"+baseURL);
        logger.debug("transactionId:"+transactionId);
        logger.debug("statusId:"+statusId);
        logger.debug("senderCellNumber:"+senderCellNumber);
        logger.debug("trxIdOperator:"+trxIdOperator);
        try {
            String transactionStatusPath = ServerPropertyProvider.get("CALLBACK_URL");
            URL obj = new URL(this.baseURL+transactionStatusPath);
            logger.debug("url:"+baseURL+transactionStatusPath);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            
            String urlParameters = "transaction_id=" + transactionId + "&status_id=" + statusId+ "&sender_cell_number=" + senderCellNumber+ "&trx_id_operator=" + trxIdOperator;

            // Send post request
            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(urlParameters);
                wr.flush();
                
                int responseCode = con.getResponseCode();
                if (responseCode == 200) {
                    try (BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()))) {
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        
                        String result = response.toString();
                        //if (result != null && result.equals(ResponseCode.SUCCESS)) {
                        if (result != null) {
                            //check valid response with response code
                            System.out.println("CallbackTransactionManager->updateTransactionStatus:response:"+result);
                            logger.debug("CallbackTransactionManager->updateTransactionStatus:response:"+result);
                        }
                        else
                        {
                            System.out.println("Error while updating transaction status with result : "+result);
                            logger.error("Error while updating transaction status with result : "+result);
                        }
                    }
                }
                else if (responseCode == 404) {
                    System.out.println("Transaction status update server is unavailable");
                    logger.error("Transaction status update server is unavailable");
                }
                else{
                    System.out.println("Unknown error. Error code: " + responseCode);
                    logger.error("Unknown error. Error code: " + responseCode);
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            logger.error(ex.getMessage());
        }
    }
    
    public void updateTransactionEditableStatus(String transactionId, boolean editable)
    {
        try {
            String transactionEditableStatusPath = ServerPropertyProvider.get("CALLBACK_URL_UPDATE_TRANSACTION_EDITABLE_STATUS");
            URL obj = new URL(this.baseURL+transactionEditableStatusPath);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            
            String urlParameters = "transaction_id=" + transactionId + "&editable=" + editable;

            // Send post request
            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(urlParameters);
                wr.flush();
                
                int responseCode = con.getResponseCode();
                if (responseCode == 200) {
                    try (BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()))) {
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        
                        String result = response.toString();
                        //if (result != null && result.equals(ResponseCode.SUCCESS)) {
                        if (result != null) {
                            //check valid response with response code
                            System.out.println("CallbackTransactionManager->updateTransactionEditableStatus:response:"+result);
                            logger.debug("CallbackTransactionManager->updateTransactionEditableStatus:response:"+result);
                        }
                        else
                        {
                            System.out.println("Error while updating transaction editable status with result : "+result);
                            logger.error("Error while updating transaction editable status with result : "+result);
                        }
                    }
                }
                else if (responseCode == 404) {
                    System.out.println("Transaction editable status update server is unavailable");
                    logger.error("Transaction editable status update server is unavailable");
                }
                else{
                    System.out.println("Unknown error. Error code: " + responseCode);
                    logger.error("Unknown error. Error code: " + responseCode);
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            logger.error(ex.getMessage());
        }
    }
}
