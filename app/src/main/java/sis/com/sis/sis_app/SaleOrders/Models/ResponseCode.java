package sis.com.sis.sis_app.SaleOrders.Models;

public class ResponseCode
{
    public int errorCode;
    public String errorMessage;

    @Override
    public String toString() {
        return String.format("(%d, %s)", errorCode, errorMessage);
    }

}
