package sis.com.sis.sis_app.ShipToApproval.Models;

import java.io.Serializable;
import java.util.List;

public class ResponseAuth implements Serializable {

    public int status;
    public User user;
    public List<SaleOrderObject> so_list;
}
