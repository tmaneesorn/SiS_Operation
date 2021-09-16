package sis.com.sis.sis_app.CheckOrderStatus.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import sis.com.sis.sis_app.CheckOrderStatus.Models.CheckStatusObject;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.Views.CustomTextView;
import sis.com.sis.sis_app.Views.CustomTextViewBold;

public class SearchSaleOrderListAdapter extends BaseAdapter {

    private Context mContext;
    private List<CheckStatusObject> mList;
    private static LayoutInflater inflater=null;

    private ListViewItemClickListener mListener;

    public SearchSaleOrderListAdapter(Context context, List<CheckStatusObject> list)
    {
        mContext=context;
        mList=list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setListViewItemClickListener(SearchSaleOrderListAdapter.ListViewItemClickListener listener)
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

    public class Holder implements View.OnClickListener {
        private SearchSaleOrderListAdapter.ListViewItemClickListener mListener;

        CustomTextView textViewCustomerName;
        CustomTextView textViewReason;
        CustomTextViewBold textViewSaleNo;
        CustomTextViewBold textViewStatus;
        CustomTextViewBold textViewTopicReason;
        LinearLayout linearLayoutReason;


        @Override
        public void onClick(View view)
        {

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchSaleOrderListAdapter.Holder holder;
        View rowView;

        if (convertView == null) {
            holder = new SearchSaleOrderListAdapter.Holder();

            convertView = inflater.inflate(R.layout.checkorder_listview_order, null);
            holder.textViewReason=(CustomTextView) convertView.findViewById(R.id.textViewReason);
            holder.textViewCustomerName=(CustomTextView) convertView.findViewById(R.id.textViewCustomerName);
            holder.textViewSaleNo=(CustomTextViewBold) convertView.findViewById(R.id.textViewSaleNo);
            holder.textViewStatus=(CustomTextViewBold) convertView.findViewById(R.id.textViewStatus);
            holder.textViewTopicReason=(CustomTextViewBold) convertView.findViewById(R.id.textViewTopicReason);
            holder.linearLayoutReason=(LinearLayout) convertView.findViewById(R.id.linearLayoutReason);


            rowView = convertView;
            convertView.setTag(holder);

        } else {
            holder = (SearchSaleOrderListAdapter.Holder) convertView.getTag();
            rowView = convertView;
        }


        CheckStatusObject object = mList.get(position);
        String custcodename = object.soldto + "/" + object.soldtoname;
        holder.textViewReason.setText(object.reason);
        holder.textViewCustomerName.setText(custcodename);
        holder.textViewSaleNo.setText(object.sono);
//        holder.textViewStatus.setText(object.status);
        if (object.status.equals("Completely processed")){
            if (object.docflow.get("invno").equals("")){
                holder.linearLayoutReason.setVisibility(View.GONE);
                holder.textViewStatus.setBackgroundResource(R.drawable.button_yellow_round);
                holder.textViewStatus.setText("On Process");
            }else{
            holder.linearLayoutReason.setVisibility(View.GONE);
            holder.textViewStatus.setBackgroundResource(R.drawable.button_green_round);
            holder.textViewStatus.setText("Completely");}}

        if (object.status.equals("Partially processed")) {
            holder.linearLayoutReason.setVisibility(View.VISIBLE);
            holder.textViewStatus.setBackgroundResource(R.drawable.button_yellow_round);
            holder.textViewStatus.setText("On Process");}

        if (object.status.equals("Not Relevant")) {
            holder.linearLayoutReason.setVisibility(View.VISIBLE);
            holder.textViewStatus.setBackgroundResource(R.drawable.button_yellow_round);
            holder.textViewStatus.setText("On Process");}

        if (object.status.equals("Not yet processed")) {
            holder.linearLayoutReason.setVisibility(View.VISIBLE);
            holder.textViewStatus.setBackgroundResource(R.drawable.button_red_round);
            holder.textViewStatus.setText("Not Process");}

        if (object.deliveryblock.equals("Not yet processed")) {holder.textViewReason.setText("Delivery Block"); }
        else{if (object.deliveryblock.equals("Partially processed")) { holder.textViewReason.setText("Delivery Block");}
        else{if (object.deliveryblock.equals("Not Relevant")) { holder.textViewReason.setText("Delivery Block");}
        else{if (object.shiptoblock.equals("")){ holder.textViewReason.setText("ShipTo Block");}
        else{if (object.creditblock.equals("Credit check was executed, document not OK")){ holder.textViewReason.setText("Credit Block");
        }}}}}



//        holder.checkboxSelected.setTag(Integer.valueOf(position));
//        holder.checkboxSelected.setChecked(mChecked[position]);
//        holder.checkboxSelected.setOnCheckedChangeListener(mListener);

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

        void listViewDidScrollToEnd();
    }

}
