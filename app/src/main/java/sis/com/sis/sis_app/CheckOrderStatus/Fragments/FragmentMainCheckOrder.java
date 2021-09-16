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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import sis.com.sis.sis_app.CheckOrderStatus.Activities.MainActivity;
import sis.com.sis.sis_app.CheckOrderStatus.Adapters.SearchSaleOrderListAdapter;
import sis.com.sis.sis_app.CheckOrderStatus.Constants;
import sis.com.sis.sis_app.CheckOrderStatus.Models.CheckStatusObject;
import sis.com.sis.sis_app.CheckOrderStatus.Models.DetailObject;
import sis.com.sis.sis_app.CheckOrderStatus.Models.ResponseResult;
import sis.com.sis.sis_app.Helpers.GeneralHelper;
import sis.com.sis.sis_app.Helpers.SharedPreferenceHelper;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.SaleOrders.Fragments.FragmentMainSaleOrderDetail;
import sis.com.sis.sis_app.SaleOrders.Models.SaleOrderObject;
import sis.com.sis.sis_app.Views.CustomDialogLoading;
import sis.com.sis.sis_app.Views.CustomMessageRelativeLayout;
import sis.com.sis.sis_app.Views.CustomTextView;
import sis.com.sis.sis_app.Views.ScrollDetectableListView;

public class FragmentMainCheckOrder extends Fragment implements SearchSaleOrderListAdapter.ListViewItemClickListener, SearchView.OnQueryTextListener {

    @BindView(R.id.rl_no_information) CustomMessageRelativeLayout rl_no_information;
    @BindView(R.id.recycleView) ScrollDetectableListView scrollDetectableListView;
    @BindView(R.id.textViewSearchResultTitle) CustomTextView textViewSearchResultTitle;
//    @BindView(R.id.relativeLayoutSaleOrderHeader) RelativeLayout relativeLayoutSaleOrderHeader;
//    @BindView(R.id.textViewSearchResult) CustomTextView textViewSearchResult;
    @BindView(R.id.searchView) SearchView searchView;

    SearchSaleOrderListAdapter searchSaleOrderListAdapter;
    List<CheckStatusObject> arrayList;

    AsyncHttpClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.checkorder_fragment_search, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Sale Order");
        setHasOptionsMenu(true);

        arrayList = new ArrayList<CheckStatusObject>();
        searchSaleOrderListAdapter = new SearchSaleOrderListAdapter(getContext(), arrayList);
        searchSaleOrderListAdapter.setListViewItemClickListener(this);
        scrollDetectableListView.setAdapter(searchSaleOrderListAdapter);

        searchView.setOnQueryTextListener(this);

        loadListSaleOrder("");

        return view;
    }

    private void loadListSaleOrder(String searchValue) {
        if (client == null) client = new AsyncHttpClient(80,443);


        String user_code = SharedPreferenceHelper.getSharedPreferenceString(getContext(), sis.com.sis.sis_app.Constants.user_code, "");

        RequestParams rParams = new RequestParams();
        rParams.put("SiS", "denis"); //Constants.SIS_SECRET
        rParams.put("se", "100" + user_code);


        if (searchValue.isEmpty()){
            //        sis.com.sis.sis_app.CheckOrderStatus.Constants.doLog("LOG HISTORY : " + Constants.API_HOST + "MSOOrderListBySales.php?");
            //        sis.com.sis.sis_app.CheckOrderStatus.Constants.doLog("LOG HISTORY : " + rParams);

            client.get(Constants.API_HOST + "MSOOrderListBySales.php?", rParams, new AsyncHttpResponseHandler() {


                @Override
                public void onStart() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (customProgress == null)
                            customProgress = CustomDialogLoading.getInstance();
                        if (isAdded())
                            customProgress.showProgress(getContext(), "Loading", null, getContext().getDrawable(R.drawable.ic_loading), false, false, true);
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    sis.com.sis.sis_app.ShipToApproval.Constants.doLog("LOG HISTORY : " + new String(response));
                    arrayList.clear();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (customProgress != null) customProgress.hideProgress();
                    }
                    Gson gson = new Gson();
                    ResponseResult responseResult = new ResponseResult();

                    if (isJSONValid(new String(response))) {
                        responseResult = gson.fromJson(new String(response), ResponseResult.class);
                    }

                    //sis.com.sis.sis_app.ShipToApproval.Constants.doLog("LOG HISTORY : " + new String(response));

                    if (responseResult.status_code == 200) {

                        if (customProgress != null) customProgress.hideProgress();
                        for (CheckStatusObject item : responseResult.datas) {
                            arrayList.add(item);
                            Constants.doLog("LOG TEST : " + item.docflow);
                        }
                    } else if (responseResult.status_code == 503) {
                        if (isAdded() && rl_no_information != null)
                            rl_no_information.show(getResources().getString(R.string.ship_to_no_history), getResources().getString(R.string.ship_to_no_history_message), getResources().getDrawable(R.drawable.ic_cross));
                    } else {
//                    if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), "Cannot do this action, Please contact IS.");
                    }

                    searchSaleOrderListAdapter.notifyDataSetChanged();

                    if (arrayList.size() == 0) {
                        if (isAdded() && rl_no_information != null)
                            rl_no_information.show(getResources().getString(R.string.ship_to_no_history), getResources().getString(R.string.ship_to_no_history_message), getResources().getDrawable(R.drawable.ic_cross));
                    }
                }


                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (isAdded() && customProgress != null) customProgress.hideProgress();
                    }

                    GeneralHelper.getInstance().showBasicAlert(getContext(), getResources().getString(R.string.message_cannot_connect_server));
                    Intent myIntent = new Intent(getActivity(), sis.com.sis.sis_app.Main.Activities.MainActivity.class);
                    getActivity().startActivity(myIntent);
                    getActivity().finish();
                }


                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
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
        }
        else{
            if (searchValue.length() >= 8) {
            //        sis.com.sis.sis_app.CheckOrderStatus.Constants.doLog("LOG HISTORY : " + Constants.API_HOST + "MSOOrderListBySales.php?");
            //        sis.com.sis.sis_app.CheckOrderStatus.Constants.doLog("LOG HISTORY : " + rParams);
            rParams.put("kw", searchValue);
            client.get(Constants.API_HOST + "MSOOrderListBySales.php?", rParams, new AsyncHttpResponseHandler() {


                @Override
                public void onStart() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (customProgress == null)
                            customProgress = CustomDialogLoading.getInstance();
                        if (isAdded())
                            customProgress.showProgress(getContext(), "Loading", null, getContext().getDrawable(R.drawable.ic_loading), false, false, true);
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    sis.com.sis.sis_app.ShipToApproval.Constants.doLog("LOG HISTORY : " + new String(response));
                    arrayList.clear();
                    textViewSearchResultTitle.setText("ผลการค้นหา \"" + searchValue + "\"");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (customProgress != null) customProgress.hideProgress();
                    }
                    Gson gson = new Gson();
                    ResponseResult responseResult = new ResponseResult();

                    if (isJSONValid(new String(response))) {
                        responseResult = gson.fromJson(new String(response), ResponseResult.class);
                    }

                    //sis.com.sis.sis_app.ShipToApproval.Constants.doLog("LOG HISTORY : " + new String(response));

                    if (responseResult.status_code == 200) {

                            for (CheckStatusObject item : responseResult.datas) {
                                arrayList.add(item);
                                Constants.doLog("LOG TEST : " + item.docflow);
                            }

                            if (rl_no_information != null) rl_no_information.hide();

                    } else if (responseResult.status_code == 503) {
                        if (isAdded() && rl_no_information != null)
                            rl_no_information.show("Not Found", "ไม่พบผลลัพธ์การค้นหา", getResources().getDrawable(R.drawable.ic_cross));
                    } else {
//                    if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), "Cannot do this action, Please contact IS.");
                    }

                    searchSaleOrderListAdapter.notifyDataSetChanged();

                    if (arrayList.size() == 0) {
                        if (isAdded() && rl_no_information != null)
                            rl_no_information.show("Not Found", "ไม่พบผลลัพธ์การค้นหา", getResources().getDrawable(R.drawable.ic_cross));
                    }
                }


                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (isAdded() && customProgress != null) customProgress.hideProgress();
                    }

                    GeneralHelper.getInstance().showBasicAlert(getContext(), getResources().getString(R.string.message_cannot_connect_server));
                    Intent myIntent = new Intent(getActivity(), sis.com.sis.sis_app.Main.Activities.MainActivity.class);
                    getActivity().startActivity(myIntent);
                    getActivity().finish();
                }


                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
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
                    return true;//
                }

            });
            }
            else{
                GeneralHelper.getInstance().showBasicAlert(getContext(),"Please insert keyword more than 8 digits");
            }
        }


//        CheckStatusObject checkStatusObject = new CheckStatusObject();
//        checkStatusObject.customer_code = "1100000033";
//        checkStatusObject.customer_name = "เค.เอส.วาย. คอมพิวเตอร์ แอนด์ คอมมิวนิเคชั่น";
//        checkStatusObject.reason = "ติด PM Block(Y2)";
//        checkStatusObject.status = "Waiting DO";
//        checkStatusObject.sale_order = "1212562180";
//
//        CheckStatusObject checkStatusObject1 = new CheckStatusObject();
//        checkStatusObject1.customer_code = "1100000033";
//        checkStatusObject1.customer_name = "เค.เอส.วาย. คอมพิวเตอร์ แอนด์ คอมมิวนิเคชั่น";
//        checkStatusObject1.reason = "ติด PM Block(Y2)";
//        checkStatusObject1.status = "Waiting DO";
//        checkStatusObject1.sale_order = "1212562180";
//
//        arrayList.add(checkStatusObject);
//        arrayList.add(checkStatusObject1);
//        searchSaleOrderListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {
        CheckStatusObject checkStatusObjectItem = arrayList.get(position);

        sis.com.sis.sis_app.CheckOrderStatus.Constants.doLog("Item was clicked" + checkStatusObjectItem.sono);

        FragmentMainOrderDetail fragmentMainOrderDetail = new FragmentMainOrderDetail();
        Bundle bundle = new Bundle();
        bundle.putSerializable("sono",checkStatusObjectItem.sono);
        fragmentMainOrderDetail.setArguments(bundle);

        ((MainActivity)getActivity()).replaceFragment(fragmentMainOrderDetail);
    }

    @Override
    public void listViewDidScrollToEnd() {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Constants.doLog("LOG SEARCH FIELD CHANGE : " + query);


        loadListSaleOrder(query);
//        if (arrayList.size() != 0){
//            arrayList.clear();
//        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Constants.doLog("LOG SEARCH FIELD CHANGE : " + newText);
        if (newText.isEmpty()) {
            if (arrayList.size() != 0){
                arrayList.clear();
                loadListSaleOrder("");
            }
            //textViewSearchResultTitle.setText("\" - \"");

            //if (isAdded() && rl_no_information != null) rl_no_information.show("No Content","Please search your sale order number to check status.",getResources().getDrawable(R.drawable.ic_cross));
//            if (rl_no_information != null) rl_no_information.hide();

            textViewSearchResultTitle.setText("รายการ Sales Order");


        }
        return false;
    }

    @Override //back button
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent myIntent = new Intent(getActivity(), sis.com.sis.sis_app.Main.Activities.MainActivity.class);
                getActivity().startActivity(myIntent);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

}
