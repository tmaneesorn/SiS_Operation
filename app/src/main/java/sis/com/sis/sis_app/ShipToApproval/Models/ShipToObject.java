package sis.com.sis.sis_app.ShipToApproval.Models;

import java.io.Serializable;

public class ShipToObject implements Serializable
{
    public String so_no;
    public String so_customername;
    public String so_tele_ob;
    public double so_total;
    public double so_profit;
    public double so_profit_percent;
    public int so_special_approle;
    public String so_status;
    public String so_reason4fast;

    @Override
    public String toString()
    {
        return String.format("(%s, %s, %s, %f, %f, %f, %d, %s, %s)", so_no, so_customername, so_tele_ob, so_total, so_profit, so_profit_percent, so_special_approle, so_status, so_reason4fast);
    }

}
