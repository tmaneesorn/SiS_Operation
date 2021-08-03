package sis.com.sis.sis_app.ShipToApproval.Models;

import java.io.Serializable;

public class HeaderDataObject implements Serializable {

    public String header_data_date;
    public String header_data_pono;
    public String header_data_podate;
    public String header_data_sale;
    public String header_data_credit;
    public String header_data_team;
    public String header_data_payterm;

    @Override
    public String toString()
    {
        return String.format("(%s, %s, %s, %s, %s, %s, %s)", header_data_date, header_data_pono, header_data_podate, header_data_sale, header_data_credit, header_data_team, header_data_payterm);
    }
}
