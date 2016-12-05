/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.sessions;

import java.util.HashMap;

/**
 *
 * @author nazmul hasan
 */
public class SessionManager {
    private HashMap sessionMap = new HashMap();
    public void addSessionItem(String sessionKey, SessionObject sessionObject)
    {
        sessionMap.put(sessionKey, sessionObject);
    }
    public boolean isSessionValid(String sessionKey, SessionObject sessionObject)
    {
        SessionObject sessionObject2 = (SessionObject) sessionMap.get(sessionKey);
        return sessionObject.getUserId().equals(sessionObject2.getUserId()) && sessionObject.getAPIKey().equals(sessionObject2.getAPIKey());
    }    
}
