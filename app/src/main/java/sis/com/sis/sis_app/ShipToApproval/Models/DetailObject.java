package sis.com.sis.sis_app.ShipToApproval.Models;

import java.io.Serializable;

public class DetailObject implements Serializable {

    public int id;
    public String order_detail_no;
    public String order_detail_org;
    public String order_detail_type;
    public String order_detail_cuscode;
    public String order_detail_shiptocode;
    public String order_detail_priceblock;
    public String order_detail_reasonshipto;
    public String order_detail_total;
    public String order_detail_profit;
    public String order_detail_profit_percent;

    @Override
    public String toString()
    {
        return String.format("(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", order_detail_no, order_detail_org, order_detail_type, order_detail_cuscode, order_detail_shiptocode, order_detail_priceblock, order_detail_reasonshipto, order_detail_total, order_detail_profit, order_detail_profit_percent);
    }
}
