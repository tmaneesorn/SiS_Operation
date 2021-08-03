package sis.com.sis.sis_app.Views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import sis.com.sis.sis_app.R;

public class CustomDialogLoading {

    public static CustomDialogLoading customProgress = null;

    private Dialog mDialog;
    private CustomTextViewBold tvTitle;
    private ImageView imageView;
    private CustomTextView tvDescription;
    private CustomButtonBold btnAction;



    public static CustomDialogLoading getInstance()
    {
        if (customProgress == null)
        {
            customProgress = new CustomDialogLoading();
        }
        return customProgress;
    }



    public void showProgress(Context context, String title, String description, Drawable icon, boolean hasButton, boolean cancelable, boolean animated)
    {

        mDialog = new Dialog(context);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_custom);

        tvTitle = (CustomTextViewBold) mDialog.findViewById(R.id.tvTitle);
        imageView = (ImageView) mDialog.findViewById(R.id.imageView);
        tvDescription = (CustomTextView) mDialog.findViewById(R.id.tvDescription);
        btnAction = (CustomButtonBold) mDialog.findViewById(R.id.btnAction);

        if (title == null)
        {
            tvTitle.setVisibility(View.GONE);
        } else
        {
            tvTitle.setText("" + title);
        }
        if (description == null)
        {
            tvDescription.setVisibility(View.GONE);
        } else
        {
            tvDescription.setText("" + description);
        }
        if (icon == null)
        {
            imageView.setVisibility(View.GONE);
        } else
        {
            imageView.setImageDrawable(icon);
            try
            {
                imageView.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
            } catch (Exception e)
            {

            }

            if (animated)
            {
                RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(5000);
                rotate.setInterpolator(new LinearInterpolator());
                rotate.setRepeatCount(Animation.INFINITE);
                imageView.startAnimation(rotate);
            }

        }
        if (hasButton == false)
        {
            btnAction.setVisibility(View.GONE);
        } else
        {
            btnAction.setText("OK");
        }
        btnAction.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                hideProgress();
            }
        });
        mDialog.setCancelable(cancelable);
        mDialog.setCanceledOnTouchOutside(cancelable);
        mDialog.show();
    }




    public void setCancelable(boolean cancelable)
    {
        mDialog.setCancelable(cancelable);
    }

    public void setCanceledOnTouchOutside(boolean cancelable)
    {
        mDialog.setCanceledOnTouchOutside(cancelable);
    }


    public void hideProgress()
    {
        if (mDialog != null)
        {
            mDialog.dismiss();
            mDialog = null;
        }
    }


}
