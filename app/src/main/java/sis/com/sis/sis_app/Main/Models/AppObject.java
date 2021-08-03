package sis.com.sis.sis_app.Main.Models;

import java.io.Serializable;

public class AppObject implements Serializable {
//    public int id;
    public String app_name;
    public String app_icon;

    @Override
    public String toString()
    {
        return String.format("(%s, %s)", app_name, app_icon);
    }
}