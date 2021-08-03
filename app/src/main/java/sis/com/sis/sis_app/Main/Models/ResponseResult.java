package sis.com.sis.sis_app.Main.Models;

import java.util.List;

import sis.com.sis.sis_app.ShipToApproval.Models.ApprovedByObject;
import sis.com.sis.sis_app.ShipToApproval.Models.AttachObject;
import sis.com.sis.sis_app.ShipToApproval.Models.CustomerObject;
import sis.com.sis.sis_app.ShipToApproval.Models.DetailObject;
import sis.com.sis.sis_app.ShipToApproval.Models.HeaderDataObject;
import sis.com.sis.sis_app.ShipToApproval.Models.ItemObject;
import sis.com.sis.sis_app.ShipToApproval.Models.NotesObject;
import sis.com.sis.sis_app.ShipToApproval.Models.ShipToDataObject;
import sis.com.sis.sis_app.ShipToApproval.Models.ShipToObject;
import sis.com.sis.sis_app.ShipToApproval.Models.User;

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
