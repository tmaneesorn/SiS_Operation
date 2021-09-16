package sis.com.sis.sis_app.CheckOrderStatus.Models;

import java.io.Serializable;

public class DetailObject implements Serializable {

    public String dono;
    public String dodate;
    public String dotime;
    public String pickno;
    public String pickdate;
    public String pictime;
    public String pgino;
    public String pgidate;
    public String pgitime;
    public String invno;
    public String invdate;
    public String invtime;

    @Override
    public String toString()
    {
        return String.format("(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", dono, dodate, dotime, pickno, pickdate,pictime,pgino,pgidate,pgitime,invno,invdate,invtime);
    }
}
