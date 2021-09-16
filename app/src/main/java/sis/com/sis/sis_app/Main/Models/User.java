package sis.com.sis.sis_app.Main.Models;

import java.io.Serializable;

public class User implements Serializable {
    public int id;
    public String user_name;
    public String user_position;
    public String user_bu;
    public String user_dep;
    public String user_code;

    @Override
    public String toString()
    {
        return String.format("(%d, %s, %s, %s, %s, %s)", id, user_name, user_position, user_bu, user_dep , user_code);
    }
}
