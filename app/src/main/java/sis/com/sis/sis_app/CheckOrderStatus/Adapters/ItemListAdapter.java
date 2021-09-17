package sis.com.sis.sis_app.CheckOrderStatus.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import sis.com.sis.sis_app.CheckOrderStatus.Constants;
import sis.com.sis.sis_app.CheckOrderStatus.Models.ItemObject;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.Views.CustomTextView;
import sis.com.sis.sis_app.Views.CustomTextViewBold;

public class ItemListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ItemObject> mList;
    private static LayoutInflater inflater=null;

    private ListViewItemClickListener mListener;

    public ItemListAdapter(Context context, List<ItemObject> list)
    {
        mContext=context;
        mList=list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setListViewItemClickListener(ItemListAdapter.ListViewItemClickListener listener)
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
        private ItemListAdapter.ListViewItemClickListener mListener;

        CustomTextViewBold textViewArticle;
        CustomTextView textViewDescription;
        CustomTextView textQtyItem;
        CustomTextView textPriceItem;
        CustomTextView textViewReason;
        LinearLayout linearLayoutReasonBlock;


        @Override
        public void onClick(View view)
        {

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemListAdapter.Holder holder = new ItemListAdapter.Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.checkorder_listview_order_item, null);

        holder.textViewArticle=(CustomTextViewBold) rowView.findViewById(R.id.textViewArticle);
        holder.textViewDescription=(CustomTextView) rowView.findViewById(R.id.textViewDescription);
        holder.textQtyItem=(CustomTextView) rowView.findViewById(R.id.textQtyItem);
        holder.textPriceItem=(CustomTextView) rowView.findViewById(R.id.textPriceItem);
        holder.textViewReason=(CustomTextView) rowView.findViewById(R.id.textViewReason);
        holder.linearLayoutReasonBlock=(LinearLayout) rowView.findViewById(R.id.linearLayoutReasonBlock);

        ItemObject object = mList.get(position);

        Constants.doLog("LOG TEST RESULT " + object.totalprice);
        String Qty =  String.format("%s",object.qty);
        String amount = new DecimalFormat("#,##0.00").format(Double.valueOf((String) object.totalprice));

        //String total_price = new DecimalFormat("#,###.00").format(String.format("%s",object.totalprice));

        holder.textViewArticle.setText(object.article);
        holder.textViewDescription.setText(object.desc);
        holder.textQtyItem.setText(Qty);
        holder.textPriceItem.setText(amount);
        if (object.block.equals("")) {
            holder.linearLayoutReasonBlock.setVisibility(View.GONE);
        }else {
            holder.textViewReason.setText(object.block);
        }




//        holder.checkboxSelected.setTag(Integer.valueOf(position));
//        holder.checkboxSelected.setChecked(mChecked[position]);
//        holder.checkboxSelected.setOnCheckedChangeListener(mListener);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //mListener.onItemClick(v, position);

            }
        });

        return rowView;
    }



    public interface ListViewItemClickListener {

        void onItemClick(View view, int position);

        void listViewDidScrollToEnd();
    }

}
