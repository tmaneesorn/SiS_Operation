package sis.com.sis.sis_app.ShipToApproval;

import android.util.Log;

public class Constants {

    public static final boolean debug = true;

    public static final String logTag = "SHIPTO";


    public static final String SUPPORT_URL = "http://www.sisthai.com/sis/page/xpage.php";
    public static final String API_HOST = "https://mobileapp.sisthai.com/SiSAndroid_Centre.nsf/";


    public static void doLog(String logString)
    {
        if (debug)
        {
            Log.e(logTag, logString);
        }
    }
}
