package sis.com.sis.sis_app.Main.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.util.List;

import sis.com.sis.sis_app.Helpers.PicassoTrustAll;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.Main.Models.AppObject;
import sis.com.sis.sis_app.Views.CustomTextView;


public class AppListAdapter extends BaseAdapter {

    private Context mContext;
    private List<AppObject> mList;
    private static LayoutInflater inflater=null;

    private ListViewItemClickListener mListener;



    public AppListAdapter(Context context, List<AppObject> list)
    {

        mContext=context;
        mList=list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListViewItemClickListener(ListViewItemClickListener listener)
    {
        mListener = listener;
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder implements View.OnClickListener
    {
        private ListViewItemClickListener mListener;

        LinearLayout lilMain;
        CustomTextView tvNameApp;
        ImageView ivLogoApp;

        @Override
        public void onClick(View view)
        {

        }
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        AppListAdapter.Holder holder=new AppListAdapter.Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.main_listview_app_item, null);

        holder.lilMain=(LinearLayout) rowView.findViewById(R.id.linearLayoutAppList);
        holder.tvNameApp=(CustomTextView) rowView.findViewById(R.id.textViewApplication);
        holder.ivLogoApp=(ImageView) rowView.findViewById(R.id.imageViewApplication);

        // SET BACKGROUND
//        if (mList.size() == 1)
//        {
//            holder.rlMain.setBackground(mContext.getDrawable(R.drawable.background_more_singleitem));
//        } else if (mList.size() == 2)
//        {
//            if (position == 0)
//            {
//                holder.rlMain.setBackground(mContext.getDrawable(R.drawable.background_more_multi_item_top));
//            } else
//            {
//                holder.rlMain.setBackground(mContext.getDrawable(R.drawable.background_more_multi_item_bottom));
//            }
//        } else
//        {
//            if (position == 0)
//            {
//                holder.rlMain.setBackground(mContext.getDrawable(R.drawable.background_more_multi_item_top));
//            } else if (position < mList.size()-1)
//            {
//                holder.rlMain.setBackground(mContext.getDrawable(R.drawable.background_more_multi_item_mid));
//            } else
//            {
//                holder.rlMain.setBackground(mContext.getDrawable(R.drawable.background_more_multi_item_bottom));
//            }
//        }


        AppObject object = mList.get(position);

        holder.tvNameApp.setText(object.app_name);
//        holder.ivLogoApp.setImageResource(object.app_icon);

        PicassoTrustAll.getInstance(mContext).load(object.app_icon).into(holder.ivLogoApp);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(v, position);
            }
        });
        return rowView;
    }

    public interface ListViewItemClickListener {

        void onItemClick(View view, int position);
    }
}
