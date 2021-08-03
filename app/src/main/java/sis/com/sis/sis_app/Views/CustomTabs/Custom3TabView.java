package sis.com.sis.sis_app.Views.CustomTabs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.Views.CustomButtonBold;

public class Custom3TabView  extends LinearLayout
{
    private AttributeSet attrs;
    private CustomButtonBold btn1,btn2,btn3;
    private RelativeLayout hairline1, hairline2, hairline3;
    private int selectedTab = 0;

    public Custom3TabView(Context context) {
        super(context);
        init(context);
    }

    public Custom3TabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Custom3TabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context){
        LayoutInflater.from(context).inflate(R.layout.tabview_3, this);
        btn1 = (CustomButtonBold) findViewById(R.id.btn1);
        btn2 = (CustomButtonBold) findViewById(R.id.btn2);
        btn3 = (CustomButtonBold) findViewById(R.id.btn3);

        hairline1 = (RelativeLayout) findViewById(R.id.hairline1);
        hairline2 = (RelativeLayout) findViewById(R.id.hairline2);
        hairline3 = (RelativeLayout) findViewById(R.id.hairline3);

        setSelectedTab(context, 0);

        btn1.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                setSelectedTab(context,0);
                if (listener != null) {
                    listener.onTabChanged(0);
                }
            }
        });

        btn2.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                setSelectedTab(context,1);
                if (listener != null) {
                    listener.onTabChanged(1);
                }
            }
        });

        btn3.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                setSelectedTab(context,2);
                if (listener != null) {
                    listener.onTabChanged(2);
                }
            }
        });
    }

    public int getSelectedTab()
    {
        return selectedTab;
    }


    public void setTab1Text(String title)
    {
        btn1.setText(title);
    }
    public void setTab2Text(String title)
    {
        btn2.setText(title);
    }
    public void setTab3Text(String title)
    {
        btn3.setText(title);
    }


    public void setSelectedTab(Context context, int position)
    {
        selectedTab = position;
        if (position == 0)
        {

            btn1.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            btn2.setTextColor(context.getResources().getColor(R.color.colorGreyMedium));
            btn3.setTextColor(context.getResources().getColor(R.color.colorGreyMedium));
            hairline1.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            hairline2.setBackgroundColor(context.getResources().getColor(R.color.colorGreyMedium));
            hairline3.setBackgroundColor(context.getResources().getColor(R.color.colorGreyMedium));
        }
        if (position == 1)
        {
            btn1.setTextColor(context.getResources().getColor(R.color.colorGreyMedium));
            btn2.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            btn3.setTextColor(context.getResources().getColor(R.color.colorGreyMedium));
            hairline1.setBackgroundColor(context.getResources().getColor(R.color.colorGreyMedium));
            hairline2.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            hairline3.setBackgroundColor(context.getResources().getColor(R.color.colorGreyMedium));
        }
        if (position == 2)
        {
            btn1.setTextColor(context.getResources().getColor(R.color.colorGreyMedium));
            btn2.setTextColor(context.getResources().getColor(R.color.colorGreyMedium));
            btn3.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            hairline1.setBackgroundColor(context.getResources().getColor(R.color.colorGreyMedium));
            hairline2.setBackgroundColor(context.getResources().getColor(R.color.colorGreyMedium));
            hairline3.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }
    }

    private Custom3TabView.NewTabDialogListener listener;
    public void setTabListener(Custom3TabView.NewTabDialogListener listener){
        this.listener = listener;
    }

    public interface NewTabDialogListener {
        void onTabChanged(int position);
    }

}