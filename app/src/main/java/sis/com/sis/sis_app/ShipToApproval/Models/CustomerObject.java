package sis.com.sis.sis_app.ShipToApproval.Models;

import java.io.Serializable;

public class CustomerObject implements Serializable {

    public String cus_addr_rank;
    public String cus_addr_name;
    public String cus_addr_contactperson;
    public String cus_addr_phone;
    public String cus_addr_buildno;
    public String cus_addr_road;
    public String cus_addr_district;
    public String cus_addr_province;
    public String cus_addr_postcode;

    @Override
    public String toString()
    {
        return String.format("(%s, %s, %s, %s, %s, %s, %s, %s, %s)", cus_addr_rank, cus_addr_name, cus_addr_contactperson, cus_addr_phone, cus_addr_buildno, cus_addr_road, cus_addr_district, cus_addr_district, cus_addr_province, cus_addr_postcode);
    }
}
