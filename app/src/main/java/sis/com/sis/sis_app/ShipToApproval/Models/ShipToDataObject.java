package sis.com.sis.sis_app.ShipToApproval.Models;

import java.io.Serializable;

public class ShipToDataObject implements Serializable {

    public String shipto_addr_rank;
    public String shipto_addr_name;
    public String shipto_addr_contactperson;
    public String shipto_addr_phone;
    public String shipto_addr_buildno;
    public String shipto_addr_road;
    public String shipto_addr_district;
    public String shipto_addr_province;
    public String shipto_addr_postcode;

    @Override
    public String toString()
    {
        return String.format("(%s, %s, %s, %s, %s, %s, %s, %s, %s)", shipto_addr_rank, shipto_addr_name, shipto_addr_contactperson, shipto_addr_phone, shipto_addr_buildno, shipto_addr_road, shipto_addr_district, shipto_addr_province, shipto_addr_postcode);
    }

}
