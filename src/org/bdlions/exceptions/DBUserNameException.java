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
public class DBUserNameException extends Exception{
    public DBUserNameException(){
        this("Database user name is illegal/null.");
    }
    public DBUserNameException(String message){
        super(message);
    }
    
}
