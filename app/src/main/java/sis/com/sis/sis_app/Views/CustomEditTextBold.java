package sis.com.sis.sis_app.Views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;

import sis.com.sis.sis_app.R;

public class CustomEditTextBold extends AppCompatEditText
{
    public CustomEditTextBold(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        String language = context.getString(R.string.language);
        if (language.equals("thai"))
        {

        } else
        {
            Typeface typeface = ResourcesCompat.getFont(context, R.font.bold);
            this.setTypeface(typeface);
        }
    }
}
