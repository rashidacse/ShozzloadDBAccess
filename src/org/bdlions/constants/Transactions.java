/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.constants;

/**
 *
 * @author nazmul hasan
 */
public class Transactions {
    public static final int TRANSACTION_TYPE_ADD_USER_PAYMENT = 1;
    public static final int TRANSACTION_TYPE_USE_SERVICE = 2;
    
    public static final int TRANSACTION_STATUS_PENDING = 1;
    public static final int TRANSACTION_STATUS_SUCCESS = 2;
    public static final int TRANSACTION_STATUS_FAILED = 3;
    public static final int TRANSACTION_STATUS_CANCELLED = 4;
    public static final int TRANSACTION_STATUS_PROCESSED = 5;
    
    public static final String TRANSACTION_FLAG_LIVE =             "LIVE";
    public static final String TRANSACTION_FLAG_WEBSERVER_TEST =   "WEBSERVERTEST";
    public static final String TRANSACTION_FLAG_WEBSERVICE_TEST =  "WEBSERVICETEST";
    public static final String TRANSACTION_FLAG_LOCALSERVER_TEST = "LOCALSERVERTEST";
    
    public static final int BUFFER_PROCESS_TYPE_ADD_TRANSACTION = 1;
    public static final int BUFFER_PROCESS_TYPE_UPDATE_TRANSACTION = 2;
    public static final int BUFFER_PROCESS_TYPE_ACTIVEMQ = 3;
    public static final int BUFFER_PROCESS_TYPE_MQTT_STOP_SIM = 4;
}
