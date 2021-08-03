package sis.com.sis.sis_app.ShipToApproval.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.text.NumberFormat;
import java.util.List;

import sis.com.sis.sis_app.ShipToApproval.Models.ItemObject;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.Views.CustomTextView;
import sis.com.sis.sis_app.Views.CustomTextViewBold;


public class ItemListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ItemObject> mList;
    private static LayoutInflater inflater=null;



    public ItemListAdapter(Context context, List<ItemObject> list)
    {
        mContext=context;
        mList=list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

        CustomTextViewBold tvArticle;
        CustomTextView tvDescription;
        CustomTextView tvQty;
        CustomTextView tvTotalPrice;

        @Override
        public void onClick(View view)
        {

        }
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ItemListAdapter.Holder holder=new ItemListAdapter.Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.shipto_listview_ship_to_detail_item, null);

//        holder.tvArticle=(CustomTextViewBold) rowView.findViewById(R.id.textViewArticle);
        holder.tvDescription=(CustomTextView) rowView.findViewById(R.id.textViewDescription);
        holder.tvQty=(CustomTextView) rowView.findViewById(R.id.textViewQty);
        holder.tvTotalPrice=(CustomTextView) rowView.findViewById(R.id.textViewTotalPrice);

        ItemObject object = mList.get(position);

        String amount = NumberFormat.getInstance().format(object.item_netprice);
//        String total_price = new DecimalFormat("#,###.00").format(object.item_netprice);

        String qty =  String.format("%d",object.item_qty);

//        holder.tvArticle.setText(object.item_part);
        holder.tvDescription.setText(object.item_desc);
        holder.tvQty.setText(qty);
//        holder.tvTotalPrice.setText(amount);

        return rowView;
    }

}
