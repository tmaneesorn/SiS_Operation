package sis.com.sis.sis_app.ShipToApproval.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import sis.com.sis.sis_app.ShipToApproval.Models.ShipToObject;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.Views.CustomTextView;
import sis.com.sis.sis_app.Views.CustomTextViewBold;


public class ShipToListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ShipToObject> mList;
    private static LayoutInflater inflater=null;

    private ListViewItemClickListener mListener;


    public ShipToListAdapter(Context context, List<ShipToObject> list)
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

        LinearLayout linearLayout;
        RelativeLayout relativeLayout;
        CustomTextView tvUrgentTitle;
        CustomTextView tvUrgent;
        CustomTextViewBold tvSaleNo;
        CustomTextView tvCustomerName;
        CustomTextView tvSale;
        CustomTextView tvAmount;
        CustomTextView tvProfit;
        CustomTextView tvProfitPercent;
        CustomTextViewBold tvStatus;

        @Override
        public void onClick(View view)
        {

        }
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ShipToListAdapter.Holder holder=new ShipToListAdapter.Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.shipto_listview_ship_to_item, null);

        holder.tvUrgentTitle=(CustomTextView) rowView.findViewById(R.id.textViewUrgent);
        holder.relativeLayout=(RelativeLayout) rowView.findViewById(R.id.relativeLayoutShipToOrder);
        holder.linearLayout=(LinearLayout) rowView.findViewById(R.id.linearLayoutUrgent);
        holder.tvUrgent=(CustomTextView) rowView.findViewById(R.id.textViewUrgentReason);
        holder.tvSaleNo=(CustomTextViewBold) rowView.findViewById(R.id.textViewSaleNo);
        holder.tvCustomerName=(CustomTextView) rowView.findViewById(R.id.textViewCustomerName);
        holder.tvSale=(CustomTextView) rowView.findViewById(R.id.textViewSale);
//        holder.tvAmount=(CustomTextView) rowView.findViewById(R.id.textViewAmount);
//        holder.tvProfit=(CustomTextView) rowView.findViewById(R.id.textViewProfit);
//        holder.tvProfitPercent=(CustomTextView) rowView.findViewById(R.id.textViewProfitPercent);
        holder.tvStatus=(CustomTextViewBold) rowView.findViewById(R.id.textViewStatus);

        ShipToObject object = mList.get(position);

//        String amount;
//        String profit;
//        String profit_percent;
//        if (object.so_total != 0){
//            amount = NumberFormat.getInstance().format(object.so_total);
//        }
//        else {
//            amount = "0.00";
//        }
//        if (object.so_profit != 0){
////            profit = new DecimalFormat("#,###.00").format(object.so_profit);
//            profit = NumberFormat.getInstance().format(object.so_profit);
//        }
//        else {
//            profit = "0.00";
//        }
//        if (object.so_profit_percent != 0){
//            profit_percent = NumberFormat.getInstance().format(object.so_profit_percent);
//        }
//        else {
//            profit_percent = "0.00";
//        }

        if (object.so_special_approle == 1){
            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.tvUrgentTitle.setText("ขอให้ SM อนุมัติด่วน!");
            holder.tvUrgent.setText(object.so_reason4fast);
        }
        else if (object.so_special_approle == 2) {
            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.tvUrgentTitle.setText("ขอให้ GM อนุมัติด่วน!");
            holder.tvUrgent.setText(object.so_reason4fast);
        }
        else if (object.so_special_approle == 3) {
            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.tvUrgentTitle.setText("ขอให้ MD อนุมัติด่วน!");
            holder.tvUrgent.setText(object.so_reason4fast);
        }

        holder.tvSaleNo.setText(object.so_no);
        holder.tvCustomerName.setText(object.so_customername);
        holder.tvSale.setText(object.so_tele_ob);
//        holder.tvAmount.setText(amount);
//        holder.tvProfit.setText(object.so_profit);
//        holder.tvProfitPercent.setText(object.so_profit_percent+" %");

//        if (object.so_total < 0) {
//            holder.tvAmount.setText(amount);
//            holder.tvProfit.setTextColor(mContext.getResources().getColor(R.color.colorRed));
//        }
//
//        if (object.so_profit < 0) {
//            holder.tvProfit.setText(profit);
//            holder.tvProfit.setTextColor(mContext.getResources().getColor(R.color.colorRed));
//            holder.tvProfitPercent.setText(profit_percent + "%");
//            holder.tvProfitPercent.setTextColor(mContext.getResources().getColor(R.color.colorRed));
//        }
//        else {
//            holder.tvProfit.setText(profit);
//            holder.tvProfit.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
//            holder.tvProfitPercent.setText(profit_percent + "%");
//            holder.tvProfitPercent.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
//        }
        holder.tvStatus.setText(object.so_status);

        if (holder.tvStatus.getText().equals("Approve")){
            holder.tvStatus.setText(R.string.main_document_status_approve);
            holder.tvStatus.setBackgroundResource(R.drawable.button_green_round);
        }
        else if (holder.tvStatus.getText().equals("Wait for Approve")){
            holder.tvStatus.setText(R.string.main_document_status_waiting);
            holder.tvStatus.setBackgroundResource(R.drawable.button_yellow_round);
        }
        else {
            holder.tvStatus.setText(R.string.main_document_status_not_approve);
            holder.tvStatus.setBackgroundResource(R.drawable.button_red_round);
        }

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
