/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.exceptions;

/**
 *
 * @author alamgir
 */
public class SubscriptionExpireException extends Exception{
    public SubscriptionExpireException(){
        this("Subscription expired.");
    }
    public SubscriptionExpireException(String message){
        super(message);
    }
    
}
