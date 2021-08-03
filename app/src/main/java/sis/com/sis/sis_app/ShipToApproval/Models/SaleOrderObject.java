package sis.com.sis.sis_app.ShipToApproval.Models;

import java.io.Serializable;

public class SaleOrderObject implements Serializable {

    public String so_no;
    public String so_customername;
    public double so_total;
    public String so_profit;
    public String so_status;

    @Override
    public String toString()
    {
        return String.format("(%s, %s, %f, %f, %s)", so_no, so_customername, so_total, so_profit, so_status);
    }
}
