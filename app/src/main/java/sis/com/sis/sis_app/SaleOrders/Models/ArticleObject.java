package sis.com.sis.sis_app.SaleOrders.Models;

import java.io.Serializable;
import java.util.List;

import sis.com.sis.sis_app.ShipToApproval.Models.ShipToObject;

public class ArticleObject implements Serializable {

    public String sku;
    public String name;
    public String nickname;
    public String price;
    public String unitprice;
    public String price4customer;
    public String stock;
    public String qty;
    public String loc;
    public String discount;
    public String category;
    public boolean checked;
    public List<ArticleKITObject> kititem;


    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();

        if (kititem != null) str.append(kititem.toString());

        return String.format("(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %b)", sku, name, nickname, price, unitprice, price4customer, stock, qty, loc, discount, category, checked) + str.toString();

    }
}
