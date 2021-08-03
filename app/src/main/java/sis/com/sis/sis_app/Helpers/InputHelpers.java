package sis.com.sis.sis_app.Helpers;

import android.widget.EditText;

public class InputHelpers
{

    public static boolean isEmpty(EditText etText)
    {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

}
