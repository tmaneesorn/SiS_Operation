package sis.com.sis.sis_app.SaleOrders.Models;

import java.io.Serializable;
import java.util.List;

public class SaleOrderObject implements Serializable {

    public String customer_name;
    public String customer_code;
    public String shipto_name;
    public String shipto_code;
    public String date;
    public String mobile_so;
    public String sap_so;
    public String sap_do;
    public String sap_inv;
    public String status;
    public String total_price;
    public String net_total_price;
    public String payment;
    public List<ArticleObject> items;

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();

        if (items != null) str.append(items.toString());

        return String.format("(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", customer_name, customer_code, shipto_name, shipto_code, date, mobile_so, sap_so, sap_do, sap_inv, status, total_price, net_total_price, payment) + str.toString();
    }
}
