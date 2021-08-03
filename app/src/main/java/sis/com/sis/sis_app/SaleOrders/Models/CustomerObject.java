package sis.com.sis.sis_app.SaleOrders.Models;

import java.io.Serializable;

public class CustomerObject implements Serializable {

    public String custcode;
    public String name;
    public String nickname;
    public String soldto;
    public String address;

    @Override
    public String toString()
    {
        return String.format("(%s, %s, %s, %s, %s)", custcode, name, nickname, soldto, address);
    }

}
