package sis.com.sis.sis_app.SaleOrders.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import java.util.HashMap;
import java.util.List;

import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.SaleOrders.Models.ArticleObject;
import sis.com.sis.sis_app.Views.CustomTextView;
import sis.com.sis.sis_app.Views.CustomTextViewBold;

public class SearchArticleListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ArticleObject> mList;
    private static LayoutInflater inflater=null;

    private ListViewItemClickListener mListener;

    public SearchArticleListAdapter(Context context, List<ArticleObject> list)
    {
        mContext=context;
        mList=list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setListViewItemClickListener(SearchArticleListAdapter.ListViewItemClickListener listener)
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
        private SearchArticleListAdapter.ListViewItemClickListener mListener;

        CustomTextView textViewCustomerCode;
        CustomTextView textViewCustomerName;
        CustomTextView textViewCustomerNickname;
        CustomTextViewBold textViewCusCodeTitle;
        CustomTextViewBold textViewCusNameTitle;
        CheckBox checkboxSelected;

        @Override
        public void onClick(View view)
        {

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchArticleListAdapter.Holder holder;
        View rowView;

        if (convertView == null) {
            holder = new SearchArticleListAdapter.Holder();

            convertView = inflater.inflate(R.layout.saleorder_listview_customer_item, null);
            holder.textViewCustomerCode=(CustomTextView) convertView.findViewById(R.id.textViewCusCode);
            holder.textViewCustomerName=(CustomTextView) convertView.findViewById(R.id.textViewCusName);
            holder.textViewCustomerNickname=(CustomTextView) convertView.findViewById(R.id.textViewCusNickName);
            holder.textViewCusCodeTitle=(CustomTextViewBold) convertView.findViewById(R.id.textViewCusCodeTitle);
            holder.textViewCusNameTitle=(CustomTextViewBold) convertView.findViewById(R.id.textViewCusNameTitle);
            holder.checkboxSelected=(CheckBox) convertView.findViewById(R.id.checkboxSelected);

            rowView = convertView;
            convertView.setTag(holder);

        } else {
            holder = (SearchArticleListAdapter.Holder) convertView.getTag();
            rowView = convertView;
        }

        ArticleObject object = mList.get(position);

        holder.textViewCustomerCode.setText(object.sku);
        holder.textViewCustomerName.setText(object.name);
        holder.textViewCustomerNickname.setText(object.nickname);

        holder.textViewCusCodeTitle.setText("Part No. : ");
        holder.textViewCusNameTitle.setText("Description : ");
        holder.checkboxSelected.setChecked(object.checked);

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
