package sis.com.sis.sis_app.Views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;


public class ScrollDetectableListView extends ListView
{
    private OnScrollListener onScrollListener;
    private ListViewScrollListener listener;
    public boolean isLoading = false;


    public ScrollDetectableListView(Context context) {
        super(context);
        setScrollListener();
    }

    public ScrollDetectableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScrollListener();
    }

    public ScrollDetectableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setScrollListener();
    }

    public void setListViewScrollListener(ListViewScrollListener listener)
    {
        this.listener = listener;
    }


    private void setScrollListener()
    {
        if (onScrollListener == null)
        {
            onScrollListener = new OnScrollListener() {

                public void onScrollStateChanged(AbsListView view, int scrollState)
                {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if (!view.canScrollList(View.SCROLL_AXIS_VERTICAL) && scrollState == SCROLL_STATE_IDLE)
                        {
                            if (listener != null && !isLoading) listener.listViewDidScrollToEnd();
                        }
                    }
                }

                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
                {

                }
            };

            this.setOnScrollListener(onScrollListener);
        }

    }

    public interface ListViewScrollListener
    {
        public void listViewDidScrollToEnd();
    }


}