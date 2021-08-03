package sis.com.sis.sis_app.SaleOrders.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import java.util.List;

import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.SaleOrders.Models.CustomerObject;
import sis.com.sis.sis_app.Views.CustomTextView;

public class CustomerListAdapter extends BaseAdapter {

    private Context mContext;
    private List<CustomerObject> mList;
    private static LayoutInflater inflater=null;

    private CustomerListAdapter.ListViewItemClickListener mListener;

    public CustomerListAdapter(Context context, List<CustomerObject> list)
    {
        mContext=context;
        mList=list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setListViewSoldToItemClickListener(CustomerListAdapter.ListViewItemClickListener listener)
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
        private CustomerListAdapter.ListViewItemClickListener mListener;

        RelativeLayout relativeLayoutCustomerList;
        CustomTextView textViewCustomerCode;
        CustomTextView textViewCustomerName;
        CustomTextView textViewCustomerNickname;
        CheckBox checkboxSelected;

        @Override
        public void onClick(View view)
        {

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomerListAdapter.Holder holder=new CustomerListAdapter.Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.saleorder_listview_customer_item, null);

        holder.relativeLayoutCustomerList=(RelativeLayout) rowView.findViewById(R.id.relativeLayoutCustomerList);
        holder.textViewCustomerCode=(CustomTextView) rowView.findViewById(R.id.textViewCusCode);
        holder.textViewCustomerName=(CustomTextView) rowView.findViewById(R.id.textViewCusName);
        holder.textViewCustomerNickname=(CustomTextView) rowView.findViewById(R.id.textViewCusNickName);
        holder.checkboxSelected=(CheckBox) rowView.findViewById(R.id.checkboxSelected);

        CustomerObject object = mList.get(position);

        holder.textViewCustomerCode.setText(object.custcode);
        holder.textViewCustomerName.setText(object.name);
        holder.textViewCustomerNickname.setText(object.nickname);
        holder.checkboxSelected.setVisibility(View.GONE);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCustomerItemClick(v, position);
            }
        });

        return rowView;
    }

    public interface ListViewItemClickListener {

        void onCustomerItemClick(View view, int position);

        void listViewDidScrollToEnd();
    }

}
