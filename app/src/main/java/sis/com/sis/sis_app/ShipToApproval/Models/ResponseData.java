package sis.com.sis.sis_app.ShipToApproval.Models;

import java.util.List;

import sis.com.sis.sis_app.Main.Models.User;

public class ResponseData
{
//    public Company company;
    public User user;
    public List<ShipToObject> shipToObjects;
    public List<ItemObject> itemObjects;
//    public NewsItem newsitem;
//    public Document document;
//
//    public List<Colleagues> colleagues;
//    public List<Interview> interviews;
//    public List<Checkin> checkins;
//    public List<Salary> salary;
//    public List<Overtime> overtimerequests;
//    public List<Leave> leaverequests;
//    public List<EventObject> events;

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();

        if (user != null) str.append(user.toString());
        if (shipToObjects != null) str.append(shipToObjects.toString());
        if (itemObjects != null) str.append(itemObjects.toString());
//        if (newsitem != null) str.append(newsitem.toString());
//        if (documents != null) str.append(documents.toString());
//        if (colleagues != null) str.append(colleagues.toString());
//        if (interviews != null) str.append(interviews.toString());
//        if (checkins != null) str.append(checkins.toString());
//        if (salary != null) str.append(salary.toString());
//        if (overtimerequests != null) str.append(overtimerequests.toString());
//        if (leaverequests != null) str.append(leaverequests.toString());
//        if (events != null) str.append(events.toString());

        return str.toString();
    }

}
