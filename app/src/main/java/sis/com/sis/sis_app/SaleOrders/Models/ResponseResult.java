package sis.com.sis.sis_app.SaleOrders.Models;

import java.util.List;

import sis.com.sis.sis_app.ShipToApproval.Models.ItemObject;

public class ResponseResult
{
    public int status_code;
    public String status_msg;
    public List<ItemObject> item_list;
    public List<SaleOrderObject> sale_orders;
    public List<ArticleObject> articles;
    public List<CustomerObject> soldto;
    public List<CustomerObject> shipto;
    public List<CheckStatusObject> data;

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();

        if (sale_orders != null) str.append(sale_orders.toString());
        if (item_list != null) str.append(item_list.toString());
        if (articles != null) str.append(articles.toString());
        if (soldto != null) str.append(soldto.toString());
        if (shipto != null) str.append(shipto.toString());
        if (data != null) str.append(data.toString());

        return str.toString();
    }
}
