package sis.com.sis.sis_app.ShipToApproval.Models;

import java.util.List;

import sis.com.sis.sis_app.Main.Models.User;

public class ResponseResult
{
    public int status;
    public String doc_flow;
    public int doc_onetime;
    public int notes_txt;
    public int total_qty;
    public double total_netprice;
    public User user;
    public DetailObject order_detail;
    public HeaderDataObject header_data;
    public CustomerObject customer_data;
    public ShipToDataObject shipto_data;
    public ApprovedByObject approved_data;
    public AttachObject att_shipto_data;
    public List<ItemObject> item_list;
    public List<ShipToObject> so_list;
    public List<NotesObject> notes_path;
    public List<NotesObject> po_path;
    public List<NotesObject> idcard_path;
    public List<NotesObject> map_path;


    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();

        if (so_list != null) str.append(so_list.toString());
        if (item_list != null) str.append(item_list.toString());
        if (user != null) str.append(user.toString());
        if (order_detail != null) str.append(order_detail.toString());
        if (header_data != null) str.append(header_data.toString());
        if (customer_data != null) str.append(customer_data.toString());
        if (shipto_data != null) str.append(shipto_data.toString());
        if (approved_data != null) str.append(approved_data.toString());
        if (att_shipto_data != null) str.append(att_shipto_data.toString());
        if (notes_path != null) str.append(notes_path.toString());
        if (po_path != null) str.append(po_path.toString());
        if (idcard_path != null) str.append(idcard_path.toString());
        if (map_path != null) str.append(map_path.toString());
//        if (checkins != null) str.append(checkins.toString());
//        if (salary != null) str.append(salary.toString());
//        if (overtimerequests != null) str.append(overtimerequests.toString());
//        if (leaverequests != null) str.append(leaverequests.toString());
//        if (events != null) str.append(events.toString());

        return str.toString();
    }
}
