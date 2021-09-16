package sis.com.sis.sis_app.CheckOrderStatus.Models;

import java.util.List;


public class ResponseResult
{
    public int status_code;
    public String status_msg;
    public List<CheckStatusObject> datas;

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();

        if (datas != null) str.append(datas.toString());

        return str.toString();
    }
}
