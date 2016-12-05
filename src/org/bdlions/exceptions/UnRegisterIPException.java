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
public class UnRegisterIPException extends Exception{
    public UnRegisterIPException(){
        this("request from invalid ip address.");
    }
    public UnRegisterIPException(String message){
        super(message);
    }
    
}
