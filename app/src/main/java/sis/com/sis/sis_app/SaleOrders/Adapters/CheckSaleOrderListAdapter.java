package sis.com.sis.sis_app.SaleOrders.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import java.util.List;

import sis.com.sis.sis_app.Helpers.SharedPreferenceHelper;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.SaleOrders.Constants;
import sis.com.sis.sis_app.SaleOrders.Models.CheckStatusObject;
import sis.com.sis.sis_app.SaleOrders.Models.ResponseResult;
import sis.com.sis.sis_app.SaleOrders.Models.SaleOrderObject;
import sis.com.sis.sis_app.Views.CustomTextView;
import sis.com.sis.sis_app.Views.CustomTextViewBold;

public class CheckSaleOrderListAdapter extends BaseAdapter {

    private Context mContext;
    private List<CheckStatusObject> mList;
    private static LayoutInflater inflater=null;

    private CheckSaleOrderListAdapter.ListViewItemClickListener mListener;


    public CheckSaleOrderListAdapter(Context context, List<CheckStatusObject> list)
    {
        mContext=context;
        mList=list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setListViewItemClickListener(CheckSaleOrderListAdapter.ListViewItemClickListener listener)
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
        private CheckSaleOrderListAdapter.ListViewItemClickListener mListener;

//        CustomTextViewBold textViewStatus;
//        CustomTextView textViewOrder;
//        CustomTextView textViewSAPOrder;
//        CustomTextView textViewSAPDelivery;
//        CustomTextView textViewSAPInvoice;
//        RelativeLayout relativeLayoutSaleOrderStatus;
        LinearLayout linearLayoutCustomer;
        LinearLayout linearLayoutTotalAmount;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        CheckSaleOrderListAdapter.Holder holder=new CheckSaleOrderListAdapter.Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.saleorder_listview_sale_order_item, null);

//        holder.textViewStatus=(CustomTextViewBold) rowView.findViewById(R.id.textViewStatus);
//        holder.textViewOrder=(CustomTextView) rowView.findViewById(R.id.textViewOrder);
//        holder.textViewSAPOrder=(CustomTextView) rowView.findViewById(R.id.textViewSAPOrder);
//        holder.textViewSAPDelivery=(CustomTextView) rowView.findViewById(R.id.textViewSAPDelivery);
//        holder.textViewSAPInvoice=(CustomTextView) rowView.findViewById(R.id.textViewSAPInvoice);
//        holder.relativeLayoutSaleOrderStatus=(RelativeLayout) rowView.findViewById(R.id.relativeLayoutSaleOrderStatus);
//
//        CheckStatusObject object = mList.get(position);
        holder.linearLayoutTotalAmount=(LinearLayout) rowView.findViewById(R.id.linearLayoutTotalAmount);
        holder.linearLayoutCustomer=(LinearLayout) rowView.findViewById(R.id.linearLayoutCustomer);
        holder.textViewCustomerName=(CustomTextViewBold) rowView.findViewById(R.id.textViewCustomerName);
        holder.textViewCustomerCode=(CustomTextViewBold) rowView.findViewById(R.id.textViewCustomerCode);
        holder.textViewStatus=(CustomTextViewBold) rowView.findViewById(R.id.textViewStatus);
        holder.textViewDate=(CustomTextView) rowView.findViewById(R.id.textViewDate);
        holder.textViewMobileSO=(CustomTextView) rowView.findViewById(R.id.textViewMobileSO);
        holder.textViewSAPSO=(CustomTextView) rowView.findViewById(R.id.textViewSAPSO);
        holder.textViewDO=(CustomTextView) rowView.findViewById(R.id.textViewDO);
        holder.textViewInvoice=(CustomTextView) rowView.findViewById(R.id.textViewInvoice);
        holder.textViewTotalAmount=(CustomTextView) rowView.findViewById(R.id.textViewTotalAmount);

        CheckStatusObject object = mList.get(position);

        if (object.SAP_Order == null || object.SAP_Order.equals("")) {
            object.SAP_Order = "-";
        }
        if (object.SAP_Delivery == null || object.SAP_Delivery.equals("")) {
            object.SAP_Delivery = "-";
        }
        if (object.SAP_Invoice == null || object.SAP_Invoice.equals("")) {
            object.SAP_Invoice = "-";
        }

        String json_list = SharedPreferenceHelper.getSharedPreferenceString(mContext, Constants.SALE_ORDER_LISTS, "");

        Constants.doLog("LOG SALE_ORDER_LISTS : " + json_list);
        ResponseResult saleOrderResult = new ResponseResult();
        Gson gson = new Gson();

        saleOrderResult = gson.fromJson(new String(json_list), ResponseResult.class);
        for (SaleOrderObject item: saleOrderResult.sale_orders) {
            if (item.mobile_so.equals(object.order) || item.sap_so.equals(object.order)) {
                holder.textViewCustomerName.setText(item.customer_name);
                holder.textViewCustomerCode.setText(item.customer_code);
                holder.textViewDate.setText(item.date);
                holder.textViewMobileSO.setText(item.mobile_so);
                holder.textViewSAPSO.setText(object.SAP_Order);
                holder.textViewDO.setText(object.SAP_Delivery);
                holder.textViewInvoice.setText(object.SAP_Invoice);
                holder.textViewTotalAmount.setText(item.total_price);
            }
            else {
                holder.linearLayoutCustomer.setVisibility(View.GONE);
                holder.textViewDate.setVisibility(View.GONE);
                holder.textViewMobileSO.setText(object.order);
                holder.textViewSAPSO.setText(object.SAP_Order);
                holder.textViewDO.setText(object.SAP_Delivery);
                holder.textViewInvoice.setText(object.SAP_Invoice);
                holder.linearLayoutTotalAmount.setVisibility(View.GONE);
            }
        }

        if (object.code.equals("221")){
            holder.textViewStatus.setText("Canceled");
            holder.textViewStatus.setBackgroundResource(R.drawable.button_red_round);
        }
        else if (object.code.equals("225")){
            holder.textViewStatus.setText("Wait Create SO");
            holder.textViewStatus.setBackgroundResource(R.drawable.button_yellow_round);
        }
        else if (object.code.equals("226.1")){
            holder.textViewStatus.setText("Wait SO");
            holder.textViewStatus.setBackgroundResource(R.drawable.button_yellow_round);
        }
        else if (object.code.equals("226.2")){
            holder.textViewStatus.setText("Wait DO");
            holder.textViewStatus.setBackgroundResource(R.drawable.button_yellow_round);
        }
        else if (object.code.equals("226.3")){
            holder.textViewStatus.setText("Wait INV");
            holder.textViewStatus.setBackgroundResource(R.drawable.button_yellow_round);
        }
        else if (object.code.equals("227")){
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
