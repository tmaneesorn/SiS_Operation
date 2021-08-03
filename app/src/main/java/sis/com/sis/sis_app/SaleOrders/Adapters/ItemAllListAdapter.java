package sis.com.sis.sis_app.SaleOrders.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import java.text.DecimalFormat;
import java.util.List;

import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.SaleOrders.Constants;
import sis.com.sis.sis_app.SaleOrders.Models.ArticleObject;
import sis.com.sis.sis_app.Views.CustomEditText;
import sis.com.sis.sis_app.Views.CustomTextView;
import sis.com.sis.sis_app.Views.CustomTextViewBold;

public class ItemAllListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ArticleObject> mList;
    private static LayoutInflater inflater=null;

    private ListViewItemClickListener mListener;

    public ItemAllListAdapter(Context context, List<ArticleObject> list)
    {
        mContext=context;
        mList=list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setListViewItemClickListener(ItemAllListAdapter.ListViewItemClickListener listener)
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
        private ItemAllListAdapter.ListViewItemClickListener mListener;

        CustomTextViewBold textViewPartNo;
        CustomTextView textViewPartDesc;
        CustomTextView textViewQty;
        CustomTextView textViewPrice;
        CustomTextView textViewTotalPrice;
        LinearLayout linearLayoutItemList;

        @Override
        public void onClick(View view)
        {

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemAllListAdapter.Holder holder=new ItemAllListAdapter.Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.saleorder_listview_item, null);

        holder.linearLayoutItemList=(LinearLayout) rowView.findViewById(R.id.linearLayoutItemList);
        holder.textViewPartNo=(CustomTextViewBold) rowView.findViewById(R.id.textViewPartNo);
        holder.textViewPartDesc=(CustomTextView) rowView.findViewById(R.id.textViewPartDesc);
        holder.textViewQty=(CustomTextView) rowView.findViewById(R.id.textViewQty);
        holder.textViewPrice=(CustomTextView) rowView.findViewById(R.id.textViewPrice);
        holder.textViewTotalPrice=(CustomTextView) rowView.findViewById(R.id.textViewTotalPrice);
        rowView.setTag(holder);

        ArticleObject object = mList.get(position);
        Constants.doLog("LOG RESPONSE RESULT : " + object.price);

        holder.textViewPartNo.setText(object.sku);
        holder.textViewPartDesc.setText(object.name);
        holder.textViewQty.setText(object.qty);
        holder.textViewPrice.setText(object.price + " THB");

        int totalQty = Integer.parseInt(object.qty);
        double AllTotalPrice = Double.parseDouble(object.price.replace(",",""));
        if (object.discount == null || object.discount.equals("null")){
            object.discount = "0";
        }

        holder.textViewTotalPrice.setText(new DecimalFormat("#,###.00").format((AllTotalPrice - Double.parseDouble(object.discount.replace(",",""))) * totalQty * Constants.INCLUDE_VAT) + " THB");

        if (mList.size() == 1)
        {
            holder.linearLayoutItemList.setBackground(mContext.getDrawable(R.drawable.background_more_singleitem));
        } else if (mList.size() == 2)
        {
            if (position == 0)
            {
                holder.linearLayoutItemList.setBackground(mContext.getDrawable(R.drawable.background_more_multi_item_top));
            } else
            {
                holder.linearLayoutItemList.setBackground(mContext.getDrawable(R.drawable.background_more_multi_item_bottom));
            }
        } else
        {
            if (position == 0)
            {
                holder.linearLayoutItemList.setBackground(mContext.getDrawable(R.drawable.background_more_multi_item_top));
            } else if (position < mList.size()-1)
            {
                holder.linearLayoutItemList.setBackground(mContext.getDrawable(R.drawable.background_more_multi_item_mid));
            } else
            {
                holder.linearLayoutItemList.setBackground(mContext.getDrawable(R.drawable.background_more_multi_item_bottom));
            }
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
