package sis.com.sis.sis_app.ShipToApproval.Models;

import java.io.Serializable;

public class ItemObject implements Serializable
{
    public String item_part;
    public String item_desc;
    public int item_qty;
    public double item_netprice;

    @Override
    public String toString()
    {
        return String.format("(%s, %s, %d, %f)", item_part, item_desc, item_qty, item_netprice);
    }

}
