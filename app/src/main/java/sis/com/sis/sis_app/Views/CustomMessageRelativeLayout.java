package sis.com.sis.sis_app.Views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import sis.com.sis.sis_app.R;


public class CustomMessageRelativeLayout extends RelativeLayout
{
    ImageView imageView;
    CustomTextViewBold tv_title;
    CustomTextView tv_subtitle;

    RotateAnimation rotate;

    public CustomMessageRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.relativelayout_information_message, this);

        imageView = rowView.findViewById(R.id.imageView);
        tv_title = rowView.findViewById(R.id.tv_info_message);
        tv_subtitle = rowView.findViewById(R.id.tv_info_submessage);
    }

    public void show(String title, String subtitle, Drawable image)
    {
        show(title, subtitle, image, false);
    }


    public void show(String title, String subtitle, Drawable image, boolean animated)
    {
        this.setVisibility(View.VISIBLE);
        tv_title.setText(title);
        tv_subtitle.setText(subtitle);
        imageView.setBackgroundResource(0);
        imageView.setImageDrawable(image);

        if (animated)
        {
            if (rotate == null) rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(5000);
            rotate.setInterpolator(new LinearInterpolator());
            rotate.setRepeatCount(Animation.INFINITE);
            imageView.startAnimation(rotate);

        } else
        {
            if (rotate != null) rotate.cancel();
        }


    }

    public void hide()
    {
        this.setVisibility(View.GONE);
    }

}