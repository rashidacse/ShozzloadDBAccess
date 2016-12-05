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
public class DBSetupException extends Exception{
    public DBSetupException(){
        this("Database setup incomplete.");
    }
    public DBSetupException(String message){
        super(message);
    }
    
}