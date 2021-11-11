package sis.com.sis.sis_app.CheckOrderStatus.Fragments;

import static sis.com.sis.sis_app.Views.CustomDialogLoading.customProgress;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import sis.com.sis.sis_app.CheckOrderStatus.Activities.MainActivity;
import sis.com.sis.sis_app.CheckOrderStatus.Adapters.ItemListAdapter;
import sis.com.sis.sis_app.CheckOrderStatus.Constants;
import sis.com.sis.sis_app.CheckOrderStatus.Models.CheckStatusObject;
import sis.com.sis.sis_app.CheckOrderStatus.Models.ItemObject;
import sis.com.sis.sis_app.CheckOrderStatus.Models.ResponseResult;
import sis.com.sis.sis_app.Helpers.GeneralHelper;
import sis.com.sis.sis_app.Helpers.SharedPreferenceHelper;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.Views.CustomDialogLoading;
import sis.com.sis.sis_app.Views.CustomMessageRelativeLayout;
import sis.com.sis.sis_app.Views.ScrollDetectableListView;

public class FragmentMainOrderDetailItem extends Fragment implements ItemListAdapter.ListViewItemClickListener {

    @BindView(R.id.rl_no_information)
    CustomMessageRelativeLayout rl_no_information;
//    @BindView(R.id.textViewSearchResultTitle) CustomTextView textViewSearchResultTitle;
    @BindView(R.id.recycleViewItems)
    ScrollDetectableListView scrollDetectableListView;
//    @BindView(R.id.expandableRecycleView) ExpandableListView expandableRecycleView;
//    @BindView(R.id.searchView) SearchView searchView;
    String sono;
    ItemListAdapter itemListAdapter;
    List<ItemObject> arrayListItem;

    AsyncHttpClient client;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.checkorder_fragment_main_order_detail_item, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        sono = (String) bundle.getSerializable("sono");

        String Title = "Sale Order" + " " + sono;

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(Title);

        arrayListItem = new ArrayList<ItemObject>();
        itemListAdapter = new ItemListAdapter(getContext(), arrayListItem);
        scrollDetectableListView.setAdapter(itemListAdapter);

        loadListItem();

//        textViewSearchResultTitle.setText("รายการสินค้า");
//      searchView.setOnQueryTextListener(this);

//      loadListArticle("");

        return view;
    }
    private void loadListItem() {
        if (client == null) client = new AsyncHttpClient(80,443);

        String user_code = SharedPreferenceHelper.getSharedPreferenceString(getContext(), sis.com.sis.sis_app.Constants.user_code, "");

        Bundle bundle = getArguments();
        sono = (String) bundle.getSerializable("sono");

        RequestParams rParams = new RequestParams();
        rParams.put("SiS", Constants.SIS_SECRET); //Constants.SIS_SECRET
        rParams.put("se", "100" + user_code);
        rParams.put("kw", sono);
        rParams.put("st", "d");
        client.get(Constants.API_HOST + "MSOOrderListBySales.php?", rParams, new AsyncHttpResponseHandler() {

            @Override
            public void onStart()
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (customProgress == null) customProgress = CustomDialogLoading.getInstance();
                    if (isAdded())
                        customProgress.showProgress(getContext(), "Loading", null, getContext().getDrawable(R.drawable.ic_loading), false, false, true);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                sis.com.sis.sis_app.CheckOrderStatus.Constants.doLog("LOG HISTORY : " + new String(responseBody));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (isAdded() && customProgress != null) customProgress.hideProgress();
                }
                Gson gson = new Gson();
                ResponseResult responseResult = new ResponseResult();

                if (isJSONValid(new String(responseBody))) {
                    responseResult = gson.fromJson(new String(responseBody), ResponseResult.class);

                    sis.com.sis.sis_app.ShipToApproval.Constants.doLog("LOG HISTORY : " + new String(responseBody));

                    if (responseResult.status_code == 200)
                    {
                        for (CheckStatusObject item : responseResult.datas)
                        {
                            for (ItemObject itemObject : item.items)
                            {
                                arrayListItem.add(itemObject);
                                Constants.doLog("LOG TEST : " + itemObject.article);
                            }
                        }
                    }
                    else if (responseResult.status_code == 503)
                    {
                        if (isAdded() && rl_no_information != null) rl_no_information.show("No Item","No item in this Order",getResources().getDrawable(R.drawable.ic_cross));
                    }
                    else
                    {
                        if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), getResources().getString(R.string.message_contact_is));
                    }

                    itemListAdapter.notifyDataSetChanged();

                    if (arrayListItem.size() == 0) {
                        if (isAdded() && rl_no_information != null)
                          rl_no_information.show("No Item","No item in this Order",getResources().getDrawable(R.drawable.ic_cross));
                    }
                }
                else if (new String(responseBody).equals("Not Authorized or Invalid version!")){
                    GeneralHelper.getInstance().showUpdateAlert(getContext(),getResources().getString(R.string.message_update_version));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (isAdded() && customProgress != null) customProgress.hideProgress();
                }

                GeneralHelper.getInstance().showBasicAlert(getContext(), getResources().getString(R.string.message_cannot_connect_server));
                rl_no_information.show("เกิดข้อผิดพลาด", "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้ ณ ขณะนี้ กรุณาลองใหม่อีกครั้ง", getResources().getDrawable(R.drawable.ic_cross));
                //Intent myIntent = new Intent(getActivity(), sis.com.sis.sis_app.Main.Activities.MainActivity.class);
                //getActivity().startActivity(myIntent);
                //getActivity().finish();
            }

            public boolean isJSONValid(String test) {
                try {
                    new JSONObject(test);
                } catch (JSONException ex) {
                    // edited, to include @Arthur's comment
                    // e.g. in case JSONArray is valid as well...
                    try {
                        new JSONArray(test);
                    } catch (JSONException ex1) {
                        return false;
                    }
                }
                return true;
            }

        });



//        ItemObject itemObject = new ItemObject();
//        itemObject.article = "DEL-SNSE2216HV";
//        itemObject.desc = "Dell(TM) E series E2216HV 22";
//        itemObject.qty = "3";
//        itemObject.totalprice = "12,090.00" ;
//        itemObject.block = "ติด PM Block(Y2)";
//
//        ItemObject itemObject1 = new ItemObject();
//        itemObject1.article = "HPI-L0S69AA";
//        itemObject1.desc = "HP 955XL Yellow Original Ink C";
//        itemObject1.qty = "5";
//        itemObject1.totalprice = "1,212,324,434,270.00" ;
//        itemObject1.block = "ติด PM Block(Y2)";
//
//        arrayListItem.add(itemObject);
//        arrayListItem.add(itemObject1);
//        itemListAdapter.notifyDataSetChanged();
    }
//    private void loadListItem(String searchValue) {
//
//    }




    @Override
    public void onItemClick(View view, int position) {
//        ArticleObject articleObject = arrayListArticle.get(position);
////        Constants.doLog(String.valueOf(articleObject.checked));
////        articleListAdapter.notifyDataSetChanged();
//
//        if (articleObject.checked){ //true = remove
//            if (allItemSelected.size() != 0) {
//                for (String item: allItemSelected) {
//                    if (item.equals(articleObject.sku)){
//                        articleObject.checked = !articleObject.checked;
//                        allItemSelected.remove(item);
//                    }
//                }
//            }
//            else {
//                GeneralHelper.getInstance().showBasicAlert(getContext(),"No article selected in your list.");
//            }
//        }
//        else { //false add
//
//            String saleOrderItems = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SALE_ORDER_ITEMS, "");
//            boolean saleOrderChecked = true;
//            boolean listSaleOrderChecked = true;
//
//            if (!saleOrderItems.equals("")) {
//                String[] allItems = saleOrderItems.split("\\|");
//
//                for (String item: allItems) {
//                    if (item.equals(articleObject.sku)){
//                        articleObject.checked = !articleObject.checked;
//                        saleOrderChecked = false;
//                        GeneralHelper.getInstance().showBasicAlert(getContext(),"This article already add into your list.");
//                    }
//                }
//
//                if (saleOrderChecked){
//                    if (allItemSelected != null && allItemSelected.size() != 0) {
//                        for (String item: allItemSelected) {
//                            if (item.equals(articleObject.sku)){
//                                articleObject.checked = !articleObject.checked;
//                                listSaleOrderChecked = false;
//                                GeneralHelper.getInstance().showBasicAlert(getContext(),"This article already add into your list.");
//                            }
//                        }
//                        if (listSaleOrderChecked) {
//                            articleObject.checked = !articleObject.checked;
//                            allItemSelected.add(articleObject.sku);
//                        }
//                    }
//                    else {
//                        articleObject.checked = !articleObject.checked;
//                        allItemSelected.add(articleObject.sku);
//                    }
//                }
//            }
//            else {
//                if (allItemSelected != null && allItemSelected.size() != 0) {
//                    for (String item: allItemSelected) {
//                        if (item.equals(articleObject.sku)){
//                            articleObject.checked = !articleObject.checked;
//                            listSaleOrderChecked = false;
//                            GeneralHelper.getInstance().showBasicAlert(getContext(),"This article already add into your list.");
//                        }
//                    }
//                    if (listSaleOrderChecked) {
//                        articleObject.checked = !articleObject.checked;
//                        allItemSelected.add(articleObject.sku);
//                    }
//                }
//                else {
//                    articleObject.checked = !articleObject.checked;
//                    allItemSelected.add(articleObject.sku);
//                }
//            }
//        }
//        searchArticleListAdapter.notifyDataSetChanged();
    }



    @Override
    public void listViewDidScrollToEnd() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;



            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
       // inflater.inflate(R.menu.toolbar_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


}
