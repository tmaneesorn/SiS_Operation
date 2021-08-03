package sis.com.sis.sis_app.ShipToApproval.Models;

import java.io.Serializable;

public class AttachObject implements Serializable {

    public String shipto_po;
    public String shipto_idcard;
    public String shipto_map;
    public String shipto_expense;
    public String shipto_customercase;

    @Override
    public String toString()
    {
        return String.format("(%s, %s, %s, %s, %s)", shipto_po, shipto_idcard, shipto_map, shipto_expense, shipto_customercase);
    }
}
