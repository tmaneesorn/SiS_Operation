package sis.com.sis.sis_app.SaleOrders;

import android.util.Log;

public class Constants {

    public static final boolean debug = true;

    public static final String logTag = "SALEORDER";


    public static final String SOLD_TO_CODE = "sold_to_code";
    public static final String SOLD_TO_NAME = "sold_to_name";
    public static final String SHIP_TO_CODE = "ship_to_code";
    public static final String SHIP_TO_NAME = "ship_to_name";
    public static final String SALE_ORDER_ITEMS = "sale_order_items";
    public static final String SALE_ORDER_LISTS = "sale_order";
    public static final String PAYMENT_TERM = "payment_term";
    public static final String BANK = "bank";
    public static final Double VAT = 0.07;
    public static final Double INCLUDE_VAT = 1.07;
    public static final int DEFAULT_TIMEOUT = 120 * 1000;



    public static final String SUPPORT_URL = "http://www.sisthai.com/sis/page/xpage.php";
//    public static final String API_HOST = "https://mobileapp.sisthai.com/SiSAndroid_Centre.nsf/";
    public static final String API_HOST = "https://mobileapi.sisthai.com:9000/api/";
    public static final String SIS_SECRET = "chinte203";


    public static void doLog(String logString)
    {
        if (debug)
        {
            Log.e(logTag, logString);
        }
    }
}
