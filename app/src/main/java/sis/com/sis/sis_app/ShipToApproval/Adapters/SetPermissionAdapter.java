package sis.com.sis.sis_app.ShipToApproval.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import sis.com.sis.sis_app.ShipToApproval.Models.SetPermissionObject;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.Views.CustomTextView;

public class SetPermissionAdapter extends BaseAdapter {

    private Context mContext;
    private List<SetPermissionObject> mList;
    private static LayoutInflater inflater=null;

    private SetPermissionAdapter.ListViewItemClickListener mListener;



    public SetPermissionAdapter(Context context, List<SetPermissionObject> list)
    {
        mContext=context;
        mList=list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setListViewItemClickListener(SetPermissionAdapter.ListViewItemClickListener listener)
    {
        mListener = listener;
    }

    public class Holder implements View.OnClickListener
    {
        private ShipToListAdapter.ListViewItemClickListener mListener;

        LinearLayout lilMain;
        CustomTextView tvNameApp;
        ImageView ivLogoApp;

        @Override
        public void onClick(View view)
        {

        }
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

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        SetPermissionAdapter.Holder holder=new SetPermissionAdapter.Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.shipto_listview_setting_item, null);

        holder.lilMain=(LinearLayout) rowView.findViewById(R.id.linearLayoutAppList);
        holder.tvNameApp=(CustomTextView) rowView.findViewById(R.id.textViewAppList);
        holder.ivLogoApp=(ImageView) rowView.findViewById(R.id.imageViewAppList);



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


        SetPermissionObject object = mList.get(position);

        holder.tvNameApp.setText(object.appName);
        holder.ivLogoApp.setImageResource(object.image);

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
