package sis.com.sis.sis_app.Main.Models;

import java.util.List;

public class ResponseResult
{
    public int status;
    public List<AppObject> access_app;


    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();

        if (access_app != null) str.append(access_app.toString());
//        if (checkins != null) str.append(checkins.toString());
//        if (salary != null) str.append(salary.toString());
//        if (overtimerequests != null) str.append(overtimerequests.toString());
//        if (leaverequests != null) str.append(leaverequests.toString());
//        if (events != null) str.append(events.toString());

        return str.toString();
    }
}
