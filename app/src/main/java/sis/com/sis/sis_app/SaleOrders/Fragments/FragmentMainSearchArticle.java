package sis.com.sis.sis_app.SaleOrders.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import sis.com.sis.sis_app.Helpers.GeneralHelper;
import sis.com.sis.sis_app.Helpers.SharedPreferenceHelper;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.SaleOrders.Activities.MainActivity;
import sis.com.sis.sis_app.SaleOrders.Adapters.ArticleListAdapter;
import sis.com.sis.sis_app.SaleOrders.Adapters.CustomerListAdapter;
import sis.com.sis.sis_app.SaleOrders.Adapters.SearchArticleListAdapter;
import sis.com.sis.sis_app.SaleOrders.Constants;
import sis.com.sis.sis_app.SaleOrders.Models.ArticleObject;
import sis.com.sis.sis_app.SaleOrders.Models.CustomerObject;
import sis.com.sis.sis_app.SaleOrders.Models.ResponseResult;
import sis.com.sis.sis_app.Views.CustomDialogLoading;
import sis.com.sis.sis_app.Views.CustomMessageRelativeLayout;
import sis.com.sis.sis_app.Views.CustomTextView;
import sis.com.sis.sis_app.Views.ScrollDetectableListView;

import static sis.com.sis.sis_app.Views.CustomDialogLoading.customProgress;

public class FragmentMainSearchArticle extends Fragment implements ArticleListAdapter.ListViewItemClickListener, SearchArticleListAdapter.ListViewItemClickListener,SearchView.OnQueryTextListener {

    @BindView(R.id.rl_no_information) CustomMessageRelativeLayout rl_no_information;
    @BindView(R.id.textViewSearchResultTitle) CustomTextView textViewSearchResultTitle;
    @BindView(R.id.recycleView) ScrollDetectableListView listViewArticle;
    @BindView(R.id.expandableRecycleView) ExpandableListView expandableRecycleView;
    @BindView(R.id.searchView) SearchView searchView;

    ArticleListAdapter articleListAdapter;
    SearchArticleListAdapter searchArticleListAdapter;
    List<ArticleObject> arrayListArticle;
    List<String> allItemSelected = new CopyOnWriteArrayList<String>();



    List<String> categoryListTitle = new ArrayList<String>();
    HashMap<String, List<ArticleObject>> categoryListDetail = new HashMap<String, List<ArticleObject>>();
    AsyncHttpClient client;

    String category = "";
    int position = -1;

    boolean checkedQuery = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.saleorder_fragment_search, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("List Item");

        arrayListArticle = new ArrayList<ArticleObject>();
        searchArticleListAdapter = new SearchArticleListAdapter(getContext(), arrayListArticle);
        searchArticleListAdapter.setListViewItemClickListener(this);
        listViewArticle.setAdapter(searchArticleListAdapter);

        textViewSearchResultTitle.setText("รายการสินค้า");
        searchView.setOnQueryTextListener(this);

        loadListArticle("");

        return view;
    }

    private void loadListArticle(String searchValue) {
        if (arrayListArticle.size() != 0){
            Constants.doLog("LOG arrayListSoldTo SIZE : " + arrayListArticle.size());
            arrayListArticle.clear();
            if (rl_no_information != null) rl_no_information.hide();
        }
        if (client == null) client = new AsyncHttpClient(80,443);

        //Bundle bundle = getArguments();
        //String so_no = String.valueOf(bundle.getSerializable("so_no"));
        String username = SharedPreferenceHelper.getSharedPreferenceString(getContext(), sis.com.sis.sis_app.Constants.username, "");

        RequestParams rParams = new RequestParams();

        rParams.put("SiS", Constants.SIS_SECRET);
        rParams.put("u", username);

        Constants.doLog("LOG PARAMETER SALE ORDER DETAIL (ITEMS) : " + "'" + searchValue + "'");


        if (searchValue.equals("")){
            arrayListArticle.clear();
            Constants.doLog("LOG 1 : " + "'" + searchValue + "'");
            client.get(Constants.API_HOST + "MSOLogin.php?", rParams, new AsyncHttpResponseHandler() {

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onStart() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (customProgress == null)
                            customProgress = CustomDialogLoading.getInstance();
                        customProgress.showProgress(getContext(), "Loading", null, getContext().getDrawable(R.drawable.ic_loading), false, false, true);
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response)
                {
                    if (rl_no_information != null) rl_no_information.hide();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        customProgress.hideProgress();
                    }

                    Constants.doLog("LOG RESPONSE RESULT1 : " + statusCode);
                    Constants.doLog("LOG RESPONSE RESULT1 : " + new String(response));
                    Gson gson = new Gson();
                    ResponseResult responseResult = new ResponseResult();

                    if (isJSONValid(new String(response))){
                        responseResult = gson.fromJson(new String(response),ResponseResult.class);

                        if (responseResult.status_code == 200)
                        {
                            if (responseResult.articles == null) {
                                if (isAdded() && rl_no_information != null) rl_no_information.show("No List.","ไม่พบ List Article ที่กำหนดไว้บน Lotus Notes",getResources().getDrawable(R.drawable.ic_cross));
                            }
                            else if (responseResult.articles.size() != 0) {
                                for (ArticleObject item: responseResult.articles)
                                {
                                    if (category.equalsIgnoreCase("")){
                                        categoryListTitle.add(item.category);
                                        category = item.category;

                                        Constants.doLog("CATEGORY : " + "'" + item.category + "'");
                                    }
                                    else {
                                        if (categoryListTitle.size() != 0){
                                            position = categoryListTitle.indexOf(item.category);
                                            if (position == -1){
                                                categoryListTitle.add(item.category);
                                                category = item.category;

                                                Constants.doLog("CATEGORY1 : " + "'" + item.category + "'");
                                            }
                                        }
                                    }
                                    arrayListArticle.add(item);
                                }

                                Constants.doLog("CATEGORY SIZE : " + "'" + categoryListTitle.get(0) + "'");
                                if (categoryListTitle.size() != 0){
                                    for (int i = 0; i < categoryListTitle.size(); i++) {
                                        List<ArticleObject> arrayListArticleCate = new ArrayList<ArticleObject>();

                                        for (ArticleObject item: responseResult.articles)
                                        {
                                            if (categoryListTitle.get(i).equals(item.category)) {
                                                arrayListArticleCate.add(item);
                                            }

                                        }

                                        categoryListDetail.put(categoryListTitle.get(i), arrayListArticleCate);

                                    }

                                }

                                articleListAdapter = new ArticleListAdapter(getContext(), categoryListTitle, categoryListDetail);
//                            articleListAdapter.setListViewItemClickListener(this);
                                expandableRecycleView.setAdapter(articleListAdapter);
                                expandableRecycleView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                                    @Override
                                    public void onGroupExpand(int groupPosition) {
//                                    Toast.makeText(getContext(),
//                                            categoryListTitle.get(groupPosition) + " List Expanded.",
//                                            Toast.LENGTH_SHORT).show();
                                    }
                                });

                                expandableRecycleView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                                    @Override
                                    public void onGroupCollapse(int groupPosition) {
//                                    Toast.makeText(getContext(),
//                                            categoryListTitle.get(groupPosition) + " List Collapsed.",
//                                            Toast.LENGTH_SHORT).show();

                                    }
                                });

                                expandableRecycleView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                    @Override
                                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                                        ArticleObject articleObject = categoryListDetail.get(categoryListTitle.get(groupPosition)).get(childPosition);

                                        if (articleObject.checked){ //true = remove
                                            if (allItemSelected.size() != 0) {
                                                for (String item: allItemSelected) {
                                                    if (item.equals(articleObject.sku)){
                                                        articleObject.checked = !articleObject.checked;
                                                        allItemSelected.remove(item);
                                                    }
                                                }
                                            }
                                            else {
                                                GeneralHelper.getInstance().showBasicAlert(getContext(),"No article selected in your list.");
                                            }
                                        }
                                        else { //false add

                                            String saleOrderItems = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SALE_ORDER_ITEMS, "");
                                            boolean saleOrderChecked = true;
                                            boolean listSaleOrderChecked = true;

                                            if (!saleOrderItems.equals("")) {
                                                String[] allItems = saleOrderItems.split("\\|");

                                                for (String item: allItems) {
                                                    if (item.equals(articleObject.sku)){
                                                        articleObject.checked = !articleObject.checked;
                                                        saleOrderChecked = false;
                                                        GeneralHelper.getInstance().showBasicAlert(getContext(),"This article already add into your list.");
                                                    }
                                                }

                                                if (saleOrderChecked){
                                                    if (allItemSelected != null && allItemSelected.size() != 0) {
                                                        for (String item: allItemSelected) {
                                                            if (item.equals(articleObject.sku)){
                                                                articleObject.checked = !articleObject.checked;
                                                                listSaleOrderChecked = false;
                                                                GeneralHelper.getInstance().showBasicAlert(getContext(),"This article already add into your list.");
                                                            }
                                                        }
                                                        if (listSaleOrderChecked) {
                                                            articleObject.checked = !articleObject.checked;
                                                            allItemSelected.add(articleObject.sku);
                                                        }
                                                    }
                                                    else {
                                                        articleObject.checked = !articleObject.checked;
                                                        allItemSelected.add(articleObject.sku);
                                                    }
                                                }
                                            }
                                            else {
                                                if (allItemSelected != null && allItemSelected.size() != 0) {
                                                    for (String item: allItemSelected) {
                                                        if (item.equals(articleObject.sku)){
                                                            articleObject.checked = !articleObject.checked;
                                                            listSaleOrderChecked = false;
                                                            GeneralHelper.getInstance().showBasicAlert(getContext(),"This article already add into your list.");
                                                        }
                                                    }
                                                    if (listSaleOrderChecked) {
                                                        articleObject.checked = !articleObject.checked;
                                                        allItemSelected.add(articleObject.sku);
                                                    }
                                                }
                                                else {
                                                    articleObject.checked = !articleObject.checked;
                                                    allItemSelected.add(articleObject.sku);
                                                }
                                            }
                                        }
                                        articleListAdapter.notifyDataSetChanged();
                                        return false;
                                    }
                                });
                            }
                            else {
                                if (isAdded() && rl_no_information != null) rl_no_information.show("No List.","ไม่พบ List Article ที่กำหนดไว้บน Lotus Notes",getResources().getDrawable(R.drawable.ic_cross));
                            }

                        }
                        else
                        {
                            if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), getResources().getString(R.string.message_contact_is));
                        }

                        articleListAdapter.notifyDataSetChanged();

                    }
                    else if (new String(response).equals("Not Authorized or Invalid version!")){
                        GeneralHelper.getInstance().showUpdateAlert(getContext(),getResources().getString(R.string.message_update_version));
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (isAdded() && customProgress != null) customProgress.hideProgress();
                    }

                    GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_cannot_connect_server));
                }


                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });
        }
        else {
            listViewArticle.setVisibility(View.VISIBLE);
            expandableRecycleView.setVisibility(View.GONE);
            customProgress.hideProgress();
            arrayListArticle.clear();
            Constants.doLog("LOG 2 : " + "'" + searchValue + "'");
            client.get(Constants.API_HOST + "MSOLogin.php?", rParams, new AsyncHttpResponseHandler() {

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onStart() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            if (customProgress == null)
//                                customProgress = CustomDialogLoading.getInstance();
//                            customProgress.showProgress(getContext(), "Loading", null, getContext().getDrawable(R.drawable.ic_loading), false, false, true);
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    if (rl_no_information != null) rl_no_information.hide();

                    if (arrayListArticle.size() != 0) {
                        Constants.doLog("LOG arrayListSoldTo SIZE : " + arrayListArticle.size());
                        arrayListArticle.clear();
                        if (rl_no_information != null) rl_no_information.hide();
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        customProgress.hideProgress();
                    }

                    Constants.doLog("LOG RESPONSE RESULT2 : " + statusCode);
                    Constants.doLog("LOG RESPONSE RESULT2 : " + new String(response));
                    Gson gson = new Gson();
                    ResponseResult responseResult = new ResponseResult();

                    if (isJSONValid(new String(response))) {
                        responseResult = gson.fromJson(new String(response), ResponseResult.class);

                        if (responseResult.status_code == 200) {
                            for (ArticleObject item : responseResult.articles) {
                                if (item.nickname.contains(searchValue) || item.name.contains(searchValue) || item.sku.contains(searchValue)){
                                    arrayListArticle.add(item);
                                }
                            }
                        }
                        else {
                            if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), getResources().getString(R.string.message_contact_is));
                        }
                        Constants.doLog("LOG arrayListSoldTo SIZE : " + arrayListArticle.size());

                        if (arrayListArticle.size() == 0) {

                            Constants.doLog("LOG 3 : " + "'" + searchValue + "'");
                            if (searchValue.length() >= 8) {

                                String soldToCode = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SOLD_TO_CODE, "");
                                String paymentTerm = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.PAYMENT_TERM, "");

                                Constants.doLog("LOG arrayListSoldTo SIZE : " + paymentTerm);

                                rParams.put("kw", searchValue);
                                rParams.put("sdt", soldToCode);
                                rParams.put("pmt", paymentTerm);
                                rParams.put("bps", 1);
                                customProgress.hideProgress();
                                arrayListArticle.clear();
                                client.get(Constants.API_HOST + "MSOSkuSearch.php?", rParams, new AsyncHttpResponseHandler() {

                                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                    @Override
                                    public void onStart() {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            if (customProgress == null)
//                                customProgress = CustomDialogLoading.getInstance();
//                            customProgress.showProgress(getContext(), "Loading", null, getContext().getDrawable(R.drawable.ic_loading), false, false, true);
                                        }
                                    }

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                        if (rl_no_information != null) rl_no_information.hide();

                                        if (arrayListArticle.size() != 0) {
                                            Constants.doLog("LOG arrayListSoldTo SIZE : " + arrayListArticle.size());
                                            arrayListArticle.clear();
                                            if (rl_no_information != null) rl_no_information.hide();
                                        }

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            customProgress.hideProgress();
                                        }

                                        Constants.doLog("LOG RESPONSE RESULT2 : " + statusCode);
                                        Constants.doLog("LOG RESPONSE RESULT2 : " + new String(response));
                                        Gson gson = new Gson();
                                        ResponseResult responseResult = new ResponseResult();

                                        if (isJSONValid(new String(response))) {
                                            responseResult = gson.fromJson(new String(response), ResponseResult.class);

                                            if (responseResult.status_code == 200) {
                                                for (ArticleObject item : responseResult.articles) {
                                                    arrayListArticle.add(item);
                                                }
                                            } else if (responseResult.status_code == 201) {
                                                if (isAdded() && rl_no_information != null)
                                                    rl_no_information.show("No Result.", "ผลการค้นหา '" + searchValue + "' ไม่พบรายการในระบบ, ลองอีกครั้ง", getResources().getDrawable(R.drawable.ic_cross));
                                            } else {
                                                if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), getResources().getString(R.string.message_contact_is));
                                            }

                                            articleListAdapter.notifyDataSetChanged();

                                        }
                                        else if (new String(response).equals("Not Authorized or Invalid version!")){
                                            GeneralHelper.getInstance().showUpdateAlert(getContext(),getResources().getString(R.string.message_update_version));
                                        }

                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            if (isAdded() && customProgress != null) customProgress.hideProgress();
                                        }

                                        GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_cannot_connect_server));
                                    }


                                    @Override
                                    public void onRetry(int retryNo) {
                                        // called when request is retried
                                    }
                                });
                            }
                            else {
                                GeneralHelper.getInstance().showBasicAlert(getContext(),"Please insert keyword more than 8 digits");
                                if (isAdded() && rl_no_information != null)
                                    rl_no_information.show("No Result.", "ไม่สามารถค้นหาได้ เนื่องจากใส่คำค้นหาไม่ถึง 8 ตัวอักษร (กรณีไม่มีข้อมูลบน Lotus Notes ตามที่เตรียมไว้)", getResources().getDrawable(R.drawable.ic_cross));

                            }
                        }

                        articleListAdapter.notifyDataSetChanged();

                    }
                    else if (new String(response).equals("Not Authorized or Invalid version!")){
                        GeneralHelper.getInstance().showUpdateAlert(getContext(),getResources().getString(R.string.message_update_version));
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (isAdded() && customProgress != null) customProgress.hideProgress();
                    }

                    GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_cannot_connect_server));
                }


                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });
        }
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



    @Override
    public void onItemClick(View view, int position) {
        ArticleObject articleObject = arrayListArticle.get(position);
//        Constants.doLog(String.valueOf(articleObject.checked));
//        articleListAdapter.notifyDataSetChanged();

        if (articleObject.checked){ //true = remove
            if (allItemSelected.size() != 0) {
                for (String item: allItemSelected) {
                    if (item.equals(articleObject.sku)){
                        articleObject.checked = !articleObject.checked;
                        allItemSelected.remove(item);
                    }
                }
            }
            else {
                GeneralHelper.getInstance().showBasicAlert(getContext(),"No article selected in your list.");
            }
        }
        else { //false add

            String saleOrderItems = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SALE_ORDER_ITEMS, "");
            boolean saleOrderChecked = true;
            boolean listSaleOrderChecked = true;

            if (!saleOrderItems.equals("")) {
                String[] allItems = saleOrderItems.split("\\|");

                for (String item: allItems) {
                    if (item.equals(articleObject.sku)){
                        articleObject.checked = !articleObject.checked;
                        saleOrderChecked = false;
                        GeneralHelper.getInstance().showBasicAlert(getContext(),"This article already add into your list.");
                    }
                }

                if (saleOrderChecked){
                    if (allItemSelected != null && allItemSelected.size() != 0) {
                        for (String item: allItemSelected) {
                            if (item.equals(articleObject.sku)){
                                articleObject.checked = !articleObject.checked;
                                listSaleOrderChecked = false;
                                GeneralHelper.getInstance().showBasicAlert(getContext(),"This article already add into your list.");
                            }
                        }
                        if (listSaleOrderChecked) {
                            articleObject.checked = !articleObject.checked;
                            allItemSelected.add(articleObject.sku);
                        }
                    }
                    else {
                        articleObject.checked = !articleObject.checked;
                        allItemSelected.add(articleObject.sku);
                    }
                }
            }
            else {
                if (allItemSelected != null && allItemSelected.size() != 0) {
                    for (String item: allItemSelected) {
                        if (item.equals(articleObject.sku)){
                            articleObject.checked = !articleObject.checked;
                            listSaleOrderChecked = false;
                            GeneralHelper.getInstance().showBasicAlert(getContext(),"This article already add into your list.");
                        }
                    }
                    if (listSaleOrderChecked) {
                        articleObject.checked = !articleObject.checked;
                        allItemSelected.add(articleObject.sku);
                    }
                }
                else {
                    articleObject.checked = !articleObject.checked;
                    allItemSelected.add(articleObject.sku);
                }
            }
        }
        searchArticleListAdapter.notifyDataSetChanged();
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
            case R.id.menu_add:
                String itemList = new String();
                if (allItemSelected.size() != 0){
                    for (String s: allItemSelected) {
                        if (!s.equals(allItemSelected.get(allItemSelected.size()-1))){
                            itemList = itemList + s + "|";
                        }
                        else {
                            itemList = itemList + s;
                        }
                    }
                    Constants.doLog("LOG ALL ITEMS : " + allItemSelected.get(allItemSelected.size()-1));

                    String saleOrderItems = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SALE_ORDER_ITEMS, "");

                    if (!saleOrderItems.equals("")) {
                        itemList = saleOrderItems + "|" + itemList;
                    }
                    Constants.doLog("LOG ALL ITEMS : " + itemList );
                    SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SALE_ORDER_ITEMS, itemList);

                    FragmentMainSaleOrderCreate fragment = new FragmentMainSaleOrderCreate();
                    ((MainActivity)getActivity()).replaceFragment(fragment, true);
                }
                else {
                    GeneralHelper.getInstance().showBasicAlert(getContext(), "Don't have any article are selected. Please selected before confirm.");
                }


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.toolbar_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Constants.doLog("LOG SEARCH FIELD SUBMIT : " + query);

        checkedQuery = true;

        textViewSearchResultTitle.setText("ผลการค้นหา : \" " + query + " \"");
        if (arrayListArticle.size() != 0){
            arrayListArticle.clear();
        }
        loadListArticle(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }
}
