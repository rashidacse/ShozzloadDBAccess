package org.bdlions.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nazmul
 */
public class SIMSMSListInfo {
    int counter = 0;
    List<SIMSMSInfo> simSMSList;
    public SIMSMSListInfo()
    {
        simSMSList = new ArrayList<>();
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public List<SIMSMSInfo> getSimSMSList() {
        return simSMSList;
    }

    public void setSimSMSList(List<SIMSMSInfo> simSMSList) {
        this.simSMSList = simSMSList;
    }
    
    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return json;
    }
}
