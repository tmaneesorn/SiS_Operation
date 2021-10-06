package sis.com.sis.sis_app.CheckOrderStatus.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckStatusObject implements Serializable {

    public String sono;
    public String sodate;
    public String sotime;
    public String custpono;
    public String custpodate;
    public String soldto;
    public String soldtoname;
    public String shipto;
    public String shiptoname;
    public String shiptoaddr;
    public String deliveryblock;
    public String shiptoblock;
    public String creditblock;
    public String status;
    public String reason;
    public Map<String,String> docflow;
    public List<ItemObject> items;

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();

        if (docflow != null) str.append(docflow.toString());
        if (items != null) str.append(items.toString());
        return String.format("(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", sono,sodate,sotime,custpono,custpodate,soldto,soldtoname,shipto,shiptoname,shiptoaddr,deliveryblock,shiptoblock,creditblock,status,reason) + str.toString();
    }
}
