package sis.com.sis.sis_app.SaleOrders.Models;

import java.io.Serializable;

public class ArticleKITObject implements Serializable {

    public String sku;
    public String name;
    public String qty;
    public String stock;
    public String price;
    public String discount;

    @Override
    public String toString()
    {
        return String.format("(%s, %s, %s, %s, %s, %s)", sku, name, qty, stock, price, discount);
    }
}
