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
public class MaxMemberRegException extends Exception{
    public MaxMemberRegException(){
        this("subscriber already created maximum members");
    }
    public MaxMemberRegException(String message){
        super(message);
    }
    
}
