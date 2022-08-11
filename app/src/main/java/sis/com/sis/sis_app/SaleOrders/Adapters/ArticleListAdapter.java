package sis.com.sis.sis_app.SaleOrders.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.SaleOrders.Constants;
import sis.com.sis.sis_app.SaleOrders.Models.ArticleKITObject;
import sis.com.sis.sis_app.SaleOrders.Models.ArticleObject;
import sis.com.sis.sis_app.Views.CustomTextView;
import sis.com.sis.sis_app.Views.CustomTextViewBold;

public class ArticleListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<String> categoryListTitle;
    private HashMap<String, List<ArticleObject>> categoryListDetail;
    private List<ArticleObject> mList;
    private static LayoutInflater inflater=null;

    private ListViewItemClickListener mListener;

//    public ArticleListAdapter(Context context, List<ArticleObject> list)
//    {
//        mContext=context;
//        mList=list;
//        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//    }
    public ArticleListAdapter(Context context, List<String> categoryListTitle, HashMap<String, List<ArticleObject>> categoryListDetail)
    {
        mContext=context;
        this.categoryListTitle = categoryListTitle;
        this.categoryListDetail = categoryListDetail;
    }

    public void setListViewItemClickListener(ArticleListAdapter.ListViewItemClickListener listener)
    {
        mListener = listener;
    }

//    @Override
//    public int getCount() {
//        return mList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return position;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ArticleListAdapter.Holder holder;
//        View rowView;
//
//        if (convertView == null) {
//            holder = new ArticleListAdapter.Holder();
//
//            convertView = inflater.inflate(R.layout.saleorder_listview_customer_item, null);
//            holder.textViewCustomerCode=(CustomTextView) convertView.findViewById(R.id.textViewCusCode);
//            holder.textViewCustomerName=(CustomTextView) convertView.findViewById(R.id.textViewCusName);
//            holder.textViewCustomerNickname=(CustomTextView) convertView.findViewById(R.id.textViewCusNickName);
//            holder.textViewCusCodeTitle=(CustomTextViewBold) convertView.findViewById(R.id.textViewCusCodeTitle);
//            holder.textViewCusNameTitle=(CustomTextViewBold) convertView.findViewById(R.id.textViewCusNameTitle);
//            holder.checkboxSelected=(CheckBox) convertView.findViewById(R.id.checkboxSelected);
//
//            rowView = convertView;
//            convertView.setTag(holder);
//
//        } else {
//            holder = (ArticleListAdapter.Holder) convertView.getTag();
//            rowView = convertView;
//        }
//
//        ArticleObject object = mList.get(position);
//
//        holder.textViewCustomerCode.setText(object.sku);
//        holder.textViewCustomerName.setText(object.name);
//        holder.textViewCustomerNickname.setText(object.nickname);
//
//        holder.textViewCusCodeTitle.setText("Part No. : ");
//        holder.textViewCusNameTitle.setText("Description : ");
//        holder.checkboxSelected.setChecked(object.checked);
//
////        holder.checkboxSelected.setTag(Integer.valueOf(position));
////        holder.checkboxSelected.setChecked(mChecked[position]);
////        holder.checkboxSelected.setOnCheckedChangeListener(mListener);
//
//        rowView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                mListener.onItemClick(v, position);
//
//            }
//        });
//
//        return rowView;
//    }

//    public class Holder implements View.OnClickListener {
//        private ArticleListAdapter.ListViewItemClickListener mListener;
//
//        CustomTextView textViewCustomerCode;
//        CustomTextView textViewCustomerName;
//        CustomTextView textViewCustomerNickname;
//        CustomTextViewBold textViewCusCodeTitle;
//        CustomTextViewBold textViewCusNameTitle;
//        CheckBox checkboxSelected;
//
//        @Override
//        public void onClick(View view)
//        {
//
//        }
//    }


    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.categoryListDetail.get(this.categoryListTitle.get(listPosition)).get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//        final String expandedListText = (String) getChild(listPosition, expandedListPosition);

        View rowView;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.saleorder_listview_customer_item, null);

        }
        rowView = convertView;

//        ArticleListAdapter.Holder holder;

//        if (convertView == null) {
//            holder = new ArticleListAdapter.Holder();
//
//            convertView = inflater.inflate(R.layout.saleorder_listview_customer_item, null);
//
//            rowView = convertView;
//            convertView.setTag(holder);
//
//        }
//        else {
//            holder = (ArticleListAdapter.Holder) convertView.getTag();
//            rowView = convertView;
//        }

        ArticleObject object = (ArticleObject) getChild(listPosition, expandedListPosition);

        CustomTextView textViewCustomerCode =(CustomTextView) convertView.findViewById(R.id.textViewCusCode);
        CustomTextView textViewCustomerName=(CustomTextView) convertView.findViewById(R.id.textViewCusName);
        CustomTextView textViewCustomerNickname=(CustomTextView) convertView.findViewById(R.id.textViewCusNickName);
        CustomTextViewBold textViewCusCodeTitle=(CustomTextViewBold) convertView.findViewById(R.id.textViewCusCodeTitle);
        CustomTextViewBold textViewCusNameTitle=(CustomTextViewBold) convertView.findViewById(R.id.textViewCusNameTitle);
        CheckBox checkboxSelected=(CheckBox) convertView.findViewById(R.id.checkboxSelected);
        CustomTextView textViewKIT=(CustomTextView) convertView.findViewById(R.id.textViewKIT);
        CustomTextViewBold textViewKITTitle=(CustomTextViewBold) convertView.findViewById(R.id.textViewKITTitle);

        textViewCustomerCode.setText(object.sku);
        textViewCustomerName.setText(object.name);
        textViewCustomerNickname.setText(object.nickname);

        textViewCusCodeTitle.setText("Part No. : ");
        textViewCusNameTitle.setText("Description : ");
        checkboxSelected.setChecked(object.checked);

        String str = "";

        if (object.sku.substring(0,3).equals("KIT")){
            for (ArticleKITObject item: object.kititem) {
                str = str + "- " + item.sku + "\t\t\t\t\t\tQty " + item.qty + "\n  " + item.name + "\n";
                Constants.doLog("ArticleListAdapter OBJECT : " + str);
            }

            textViewKITTitle.setVisibility(View.VISIBLE);
            textViewKIT.setVisibility(View.VISIBLE);
        }
        else {
            textViewKITTitle.setVisibility(View.GONE);
            textViewKIT.setVisibility(View.GONE);
        }

        textViewKIT.setText(str.trim());
//        rowView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                mListener.onItemClick(v, listPosition);
//
//            }
//        });

        return rowView;

    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.categoryListDetail.get(this.categoryListTitle.get(listPosition)).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.categoryListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.categoryListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.saleorder_listview_category, null);
        }
        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.textViewCategory);

        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    public interface ListViewItemClickListener {

//        void onItemClick(View view, int position);

        void listViewDidScrollToEnd();
    }

}
