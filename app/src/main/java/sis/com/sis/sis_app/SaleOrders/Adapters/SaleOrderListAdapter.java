package sis.com.sis.sis_app.SaleOrders.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.SaleOrders.Constants;
import sis.com.sis.sis_app.SaleOrders.Models.SaleOrderObject;
import sis.com.sis.sis_app.ShipToApproval.Models.ShipToObject;
import sis.com.sis.sis_app.Views.CustomTextView;
import sis.com.sis.sis_app.Views.CustomTextViewBold;


public class SaleOrderListAdapter extends BaseAdapter {

    private Context mContext;
    private List<SaleOrderObject> mList;
    private static LayoutInflater inflater=null;

    private ListViewItemClickListener mListener;


    public SaleOrderListAdapter(Context context, List<SaleOrderObject> list)
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
        CustomTextViewBold textViewCustomerName;
        CustomTextViewBold textViewCustomerCode;
        CustomTextViewBold textViewStatus;
        CustomTextView textViewDate;
        CustomTextView textViewMobileSO;
        CustomTextView textViewSAPSO;
        CustomTextView textViewDO;
        CustomTextView textViewInvoice;
        CustomTextView textViewTotalAmount;

        @Override
        public void onClick(View view)
        {

        }
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        SaleOrderListAdapter.Holder holder=new SaleOrderListAdapter.Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.saleorder_listview_sale_order_item, null);

        holder.textViewCustomerName=(CustomTextViewBold) rowView.findViewById(R.id.textViewCustomerName);
        holder.textViewCustomerCode=(CustomTextViewBold) rowView.findViewById(R.id.textViewCustomerCode);
        holder.textViewStatus=(CustomTextViewBold) rowView.findViewById(R.id.textViewStatus);
        holder.textViewDate=(CustomTextView) rowView.findViewById(R.id.textViewDate);
        holder.textViewMobileSO=(CustomTextView) rowView.findViewById(R.id.textViewMobileSO);
        holder.textViewSAPSO=(CustomTextView) rowView.findViewById(R.id.textViewSAPSO);
        holder.textViewDO=(CustomTextView) rowView.findViewById(R.id.textViewDO);
        holder.textViewInvoice=(CustomTextView) rowView.findViewById(R.id.textViewInvoice);
        holder.textViewTotalAmount=(CustomTextView) rowView.findViewById(R.id.textViewTotalAmount);

        SaleOrderObject object = mList.get(position);

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

//        if (object.so_special_approle == 1){
//            holder.linearLayout.setVisibility(View.VISIBLE);
//            holder.tvUrgentTitle.setText("ขอให้ SM อนุมัติด่วน!");
//            holder.tvUrgent.setText(object.so_reason4fast);
//        }
//        else if (object.so_special_approle == 2) {
//            holder.linearLayout.setVisibility(View.VISIBLE);
//            holder.tvUrgentTitle.setText("ขอให้ GM อนุมัติด่วน!");
//            holder.tvUrgent.setText(object.so_reason4fast);
//        }
//        else if (object.so_special_approle == 3) {
//            holder.linearLayout.setVisibility(View.VISIBLE);
//            holder.tvUrgentTitle.setText("ขอให้ MD อนุมัติด่วน!");
//            holder.tvUrgent.setText(object.so_reason4fast);
//        }

        holder.textViewCustomerName.setText(object.customer_name);
        holder.textViewCustomerCode.setText(object.customer_code);
        holder.textViewStatus.setText(object.status);
        holder.textViewDate.setText(object.date);
        holder.textViewMobileSO.setText(object.mobile_so);
        holder.textViewSAPSO.setText(object.sap_so);
        holder.textViewDO.setText(object.sap_do);
        holder.textViewInvoice.setText(object.sap_inv);
        holder.textViewTotalAmount.setText(object.total_price);

        Constants.doLog("Status " + object.status);
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
//        holder.textViewStatus.setText(object.so_status);
//
        if (object.status.equals("221")){
            holder.textViewStatus.setText("Canceled");
            holder.textViewStatus.setBackgroundResource(R.drawable.button_red_round);
        }
        else if (object.status.equals("225")){
            holder.textViewStatus.setText("Wait Create SO");
            holder.textViewStatus.setBackgroundResource(R.drawable.button_yellow_round);
        }
        else if (object.status.equals("226.1")){
            holder.textViewStatus.setText("Wait SO");
            holder.textViewStatus.setBackgroundResource(R.drawable.button_yellow_round);
        }
        else if (object.status.equals("226.2")){
            holder.textViewStatus.setText("Wait DO");
            holder.textViewStatus.setBackgroundResource(R.drawable.button_yellow_round);
        }
        else if (object.status.equals("226.3")){
            holder.textViewStatus.setText("Wait INV");
            holder.textViewStatus.setBackgroundResource(R.drawable.button_yellow_round);
        }
        else if (object.status.equals("227")){
            holder.textViewStatus.setText("Completed");
            holder.textViewStatus.setBackgroundResource(R.drawable.button_green_round);
        }
        else {
            holder.textViewStatus.setText("Not Found");
            holder.textViewStatus.setBackgroundResource(R.drawable.button_red_round);
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
