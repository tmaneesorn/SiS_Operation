package sis.com.sis.sis_app;

import android.util.Log;

public class Constants {

    public static final boolean debug = true;

    public static final String logTag = "MAINAPP";

    public static final String SUPPORT_URL = "http://www.sisthai.com/sis/page/xpage.php";
    public static final String API_HOST = "https://mobileapp.sisthai.com/SiSAndroid_Centre.nsf/";

    public static final String email = "email";
    public static final String username = "username";
    public static final String password = "password";
    public static final String login = "login";
    public static final String user_code = "user_code";

    public static void doLog(String logString)
    {
        if (debug)
        {
            Log.e(logTag, logString);
        }
    }
}
