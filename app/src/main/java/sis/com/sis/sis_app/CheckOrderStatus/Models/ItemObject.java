package sis.com.sis.sis_app.CheckOrderStatus.Models;

import java.io.Serializable;

public class ItemObject implements Serializable {

    public String article;
    public String desc;
    public String qty;
    public String totalprice;
    public String block;

    @Override
    public String toString()
    {
        return String.format("(%s, %s, %s, %s, %s)", article, desc, qty, totalprice, block);
    }
}
