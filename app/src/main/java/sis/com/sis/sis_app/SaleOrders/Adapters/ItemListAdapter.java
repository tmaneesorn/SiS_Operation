package sis.com.sis.sis_app.SaleOrders.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import java.text.DecimalFormat;
import java.util.List;

import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.SaleOrders.Constants;
import sis.com.sis.sis_app.SaleOrders.Models.ArticleKITObject;
import sis.com.sis.sis_app.SaleOrders.Models.ArticleObject;
import sis.com.sis.sis_app.Views.CustomEditText;
import sis.com.sis.sis_app.Views.CustomTextView;
import sis.com.sis.sis_app.Views.CustomTextViewBold;

public class ItemListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ArticleObject> mList;
    private static LayoutInflater inflater=null;

    private ListViewItemClickListener mListener;

    public ItemListAdapter(Context context, List<ArticleObject> list)
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

    public class Holder implements View.OnClickListener
    {
        private ItemListAdapter.ListViewItemClickListener mListener;

        CustomTextView textViewPartNo;
        CustomTextViewBold textViewPartDesc;
        CustomEditText editTextQty;
        CustomTextView editTextPrice;
        CustomTextView textViewAvStock;
        CustomTextView textViewTotalPrice;
        CustomEditText editTextLoc;
        CustomTextView textViewKIT;
        CustomTextViewBold textViewKITTitle;

        @Override
        public void onClick(View view)
        {

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemListAdapter.Holder holder=new ItemListAdapter.Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.saleorder_listview_article_item, null);

        holder.textViewPartNo=(CustomTextView) rowView.findViewById(R.id.textViewPartNo);
        holder.textViewPartDesc=(CustomTextViewBold) rowView.findViewById(R.id.textViewPartDesc);
        holder.editTextQty=(CustomEditText) rowView.findViewById(R.id.editTextQty);
        holder.editTextPrice=(CustomTextView) rowView.findViewById(R.id.editTextPrice);
        holder.textViewAvStock=(CustomTextView) rowView.findViewById(R.id.textViewAvStock);
        holder.textViewTotalPrice=(CustomTextView) rowView.findViewById(R.id.textViewTotalPrice);
        holder.textViewKIT=(CustomTextView) rowView.findViewById(R.id.textViewKIT);
        holder.textViewKITTitle=(CustomTextViewBold) rowView.findViewById(R.id.textViewKITTitle);
        holder.editTextLoc=(CustomEditText) rowView.findViewById(R.id.editTextLoc);
        rowView.setTag(holder);

        ArticleObject object = mList.get(position);
        String str = "";

        if (object.kititem != null){
            for (ArticleKITObject item: object.kititem) {
                str = str + "- " + item.sku + "\t\t\t\t\t\tQty " + item.qty + "\n  " + item.name + "\n";
                Constants.doLog("ItemListAdapter OBJECT : " + str);
            }
        }
        else {
            holder.textViewKIT.setVisibility(View.GONE);
            holder.textViewKITTitle.setVisibility(View.GONE);
        }

        Constants.doLog("ItemListAdapter OBJECT : " + object.sku.substring(0,3));
        holder.textViewPartNo.setText(object.sku);
        if (object.sku.substring(0,3).equals("KIT")) {
            Constants.doLog("ItemListAdapter OBJECT : " + object.sku.substring(0,3));
            holder.textViewPartDesc.setText("??????????????????????????????????????????????????????????????????");
        }
        else {
            holder.textViewPartDesc.setText(object.name);
        }
        holder.textViewAvStock.setText(object.stock);
        holder.textViewKIT.setText(str.trim());
        if (object.price == null){
            holder.editTextPrice.setText(object.unitprice);
            holder.textViewTotalPrice.setText(new DecimalFormat("#,###.00").format(Double.parseDouble(object.unitprice) - Double.parseDouble(object.discount)));
        }
        else {
            holder.editTextPrice.setText(object.price);
            holder.textViewTotalPrice.setText(new DecimalFormat("#,###.00").format(Double.parseDouble(object.price.replace(",","")) - Double.parseDouble(object.discount.replace(",",""))));
        }
        if (object.qty == null){
            holder.editTextQty.setText("1");
        }
        else {
            holder.editTextQty.setText(object.qty);
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
