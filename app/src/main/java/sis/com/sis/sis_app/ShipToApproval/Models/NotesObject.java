package sis.com.sis.sis_app.ShipToApproval.Models;

import java.io.Serializable;

public class NotesObject implements Serializable {

    public String file_name;
    public String path_full;
//    public String shipto_expense;
//    public String shipto_customercase;

    @Override
    public String toString()
    {
        return String.format("(%s, %s)", file_name, path_full);
    }
}
