package sis.com.sis.sis_app.SaleOrders.Models;

import java.io.Serializable;

public class ArticleObject implements Serializable {

    public String sku;
    public String name;
    public String nickname;
    public String price;
    public String unitprice;
    public String price4customer;
    public String stock;
    public String qty;
    public String discount;
    public String category;
    public boolean checked;

    @Override
    public String toString()
    {
        return String.format("(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %b)", sku, name, nickname, price, unitprice, price4customer, stock, qty, discount, category, checked);
    }
}
