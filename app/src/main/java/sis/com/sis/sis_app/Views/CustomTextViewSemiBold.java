package sis.com.sis.sis_app.Views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import sis.com.sis.sis_app.R;

public class CustomTextViewSemiBold extends AppCompatTextView
{
    public CustomTextViewSemiBold(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        String language = context.getString(R.string.language);
        if (language.equals("thai"))
        {

        } else
        {
            Typeface typeface = ResourcesCompat.getFont(context, R.font.semibold);
            this.setTypeface(typeface);
        }
    }
}
