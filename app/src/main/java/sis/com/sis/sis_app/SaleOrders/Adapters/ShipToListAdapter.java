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

public class ShipToListAdapter extends BaseAdapter {

    private Context mContext;
    private List<CustomerObject> mList;
    private static LayoutInflater inflater=null;

    private ShipToListAdapter.ListViewItemClickListener mListener;

    public ShipToListAdapter(Context context, List<CustomerObject> list)
    {
        mContext=context;
        mList=list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setListViewShipToItemClickListener(ShipToListAdapter.ListViewItemClickListener listener)
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
        private ShipToListAdapter.ListViewItemClickListener mListener;

        CustomTextView textViewCustomerCode;
        CustomTextView textViewCustomerName;
        CustomTextView textViewShipToAddress;
        RelativeLayout relativeLayoutCustomerNickname;
        RelativeLayout relativeLayoutListKit;
        CheckBox checkboxSelected;

        RelativeLayout relativeLayoutAddress;
        View viewHeader;

        @Override
        public void onClick(View view)
        {

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShipToListAdapter.Holder holder=new ShipToListAdapter.Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.saleorder_listview_customer_item, null);

        holder.textViewCustomerCode=(CustomTextView) rowView.findViewById(R.id.textViewCusCode);
        holder.textViewCustomerName=(CustomTextView) rowView.findViewById(R.id.textViewCusName);
        holder.textViewShipToAddress=(CustomTextView) rowView.findViewById(R.id.textViewShipToAddress);
        holder.relativeLayoutCustomerNickname=(RelativeLayout) rowView.findViewById(R.id.relativeLayoutCustomerNickname);
        holder.relativeLayoutListKit=(RelativeLayout) rowView.findViewById(R.id.relativeLayoutListKit);
        holder.relativeLayoutAddress=(RelativeLayout) rowView.findViewById(R.id.relativeLayoutAddress);
        holder.viewHeader=(View) rowView.findViewById(R.id.viewHeader);
        holder.checkboxSelected=(CheckBox) rowView.findViewById(R.id.checkboxSelected);

        CustomerObject object = mList.get(position);

        holder.textViewCustomerCode.setText(object.custcode);
        holder.textViewCustomerName.setText(object.name);
        holder.textViewShipToAddress.setText(object.address);
        holder.relativeLayoutCustomerNickname.setVisibility(View.GONE);
        holder.checkboxSelected.setVisibility(View.GONE);
        holder.relativeLayoutListKit.setVisibility(View.GONE);
        holder.relativeLayoutAddress.setVisibility(View.VISIBLE);
        holder.viewHeader.setVisibility(View.VISIBLE);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onShipToItemClick(v, position);
            }
        });

        return rowView;
    }

    public interface ListViewItemClickListener {

        void onShipToItemClick(View view, int position);

        void listViewDidScrollToEnd();
    }

}
