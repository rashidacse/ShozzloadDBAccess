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
public class DatabaseNameException extends Exception{
    public DatabaseNameException(){
        this("Database name is illegal/null.");
    }
    public DatabaseNameException(String message){
        super(message);
    }
    
}
