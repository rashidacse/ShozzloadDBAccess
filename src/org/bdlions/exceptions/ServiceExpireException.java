/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.exceptions;

/**
 *
 * @author nazmul hasan
 */
public class ServiceExpireException extends Exception{
    public ServiceExpireException(){
        this("Service expired.");
    }
    public ServiceExpireException(String message){
        super(message);
    }
}
