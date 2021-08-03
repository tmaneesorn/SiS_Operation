package sis.com.sis.sis_app.SaleOrders.Models;

import java.io.Serializable;

public class CheckStatusObject implements Serializable {

    public String order;
    public String msg;
    public String SAP_Order;
    public String SAP_Delivery;
    public String SAP_Invoice;
    public String code;

    @Override
    public String toString()
    {
        return String.format("(%s, %s, %s, %s, %s, %s)", order, msg, SAP_Order, SAP_Delivery, SAP_Invoice, code);
    }
}
