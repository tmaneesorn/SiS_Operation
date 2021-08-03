package sis.com.sis.sis_app.SaleOrders.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

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
import sis.com.sis.sis_app.Helpers.GeneralHelper;
import sis.com.sis.sis_app.Helpers.SharedPreferenceHelper;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.SaleOrders.Activities.MainActivity;
import sis.com.sis.sis_app.SaleOrders.Adapters.CustomerListAdapter;
import sis.com.sis.sis_app.SaleOrders.Constants;
import sis.com.sis.sis_app.SaleOrders.Models.CustomerObject;
import sis.com.sis.sis_app.SaleOrders.Models.ResponseResult;
import sis.com.sis.sis_app.Views.CustomDialogLoading;
import sis.com.sis.sis_app.Views.CustomMessageRelativeLayout;
import sis.com.sis.sis_app.Views.CustomTextView;
import sis.com.sis.sis_app.Views.ScrollDetectableListView;

import static sis.com.sis.sis_app.Views.CustomDialogLoading.customProgress;

public class FragmentMainSearchSoldTo extends Fragment implements CustomerListAdapter.ListViewItemClickListener, SearchView.OnQueryTextListener {

    @BindView(R.id.rl_no_information) CustomMessageRelativeLayout rl_no_information;
    @BindView(R.id.textViewSearchResultTitle) CustomTextView textViewSearchResultTitle;
    @BindView(R.id.recycleView) ScrollDetectableListView listViewSoldTo;
    @BindView(R.id.expandableRecycleView) ExpandableListView expandableRecycleView;

    @BindView(R.id.searchView) SearchView searchView;

    CustomerListAdapter customerListAdapter;
    List<CustomerObject> arrayListSoldTo;

    AsyncHttpClient client;

    String soldToCode;
    String soldToName;

    boolean checkedSelected = false;
    boolean checkedQuery = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.saleorder_fragment_search, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("List Sold To");
        setHasOptionsMenu(true);

        arrayListSoldTo = new ArrayList<CustomerObject>();
        customerListAdapter = new CustomerListAdapter(getContext(), arrayListSoldTo);
        customerListAdapter.setListViewSoldToItemClickListener(this);
        listViewSoldTo.setAdapter(customerListAdapter);
        listViewSoldTo.setVisibility(View.VISIBLE);
        expandableRecycleView.setVisibility(View.GONE);

        textViewSearchResultTitle.setText("รายการ Sold To");
        searchView.setOnQueryTextListener(this);

        loadListCustomer("");

        return view;
    }

    private void loadListCustomer(String searchValue) {

        if (client == null) client = new AsyncHttpClient(80,443);

        //Bundle bundle = getArguments();
        //String so_no = String.valueOf(bundle.getSerializable("so_no"));
        String username = SharedPreferenceHelper.getSharedPreferenceString(getContext(), sis.com.sis.sis_app.Constants.username, "");

        RequestParams rParams = new RequestParams();

        rParams.put("SiS", Constants.SIS_SECRET);
        rParams.put("u", username);

        if (searchValue.isEmpty()){

            Constants.doLog("LOG SEARCH DEFAULT : " + arrayListSoldTo.size());

            client.get(Constants.API_HOST + "MSOLogin.php?", rParams, new AsyncHttpResponseHandler() {

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onStart() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (customProgress == null) customProgress = CustomDialogLoading.getInstance();
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

                    Constants.doLog("LOG RESPONSE RESULT : " + statusCode);
                    Constants.doLog("LOG RESPONSE RESULT : " + new String(response));
                    Gson gson = new Gson();
                    ResponseResult responseResult = new ResponseResult();

                    if (isJSONValid(new String(response))){
                        responseResult = gson.fromJson(new String(response),ResponseResult.class);
                    }

                    if (responseResult.status_code == 200)
                    {
                        if (!responseResult.soldto.isEmpty())
                        {
                            for (CustomerObject item: responseResult.soldto)
                            {
                                arrayListSoldTo.add(item);
                            }
                        }
                        else {
                            if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), "Cannot do this action, Please contact IS.");
                        }
                    }
                    else if (responseResult.status_code == 401)
                    {
                        if (isAdded() && rl_no_information != null) rl_no_information.show("ไม่พบรายชื่อลูกค้า","กรุณาเตรียมข้อมูลใน Lotus Notes เพื่อแสดงข้อมูลลูกค้า",getResources().getDrawable(R.drawable.ic_cross));
                    }

                    customerListAdapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e)
                {
                    GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_cannot_connect_server));
                    Intent myIntent = new Intent(getActivity(), MainActivity.class);
                    getActivity().startActivity(myIntent);
                    getActivity().finish();
                }


                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });
        }
        else {
            client.get(Constants.API_HOST + "MSOLogin.php?", rParams, new AsyncHttpResponseHandler() {

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onStart() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Constants.doLog("LOG SEARCH SUBMIT : " + arrayListSoldTo.size());
                        if (customProgress == null) customProgress = CustomDialogLoading.getInstance();
                        customProgress.showProgress(getContext(), "Loading", null, getContext().getDrawable(R.drawable.ic_loading), false, false, true);
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response)
                {
                    if (arrayListSoldTo.size() != 0){
                        Constants.doLog("LOG arrayListSoldTo SIZE : " + arrayListSoldTo.size());
                        arrayListSoldTo.clear();
                        if (rl_no_information != null) rl_no_information.hide();
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        customProgress.hideProgress();
                    }

                    Constants.doLog("LOG RESPONSE RESULT : " + statusCode);
                    Constants.doLog("LOG RESPONSE RESULT : " + new String(response));
                    Gson gson = new Gson();
                    ResponseResult responseResult = new ResponseResult();

                    if (isJSONValid(new String(response))){
                        responseResult = gson.fromJson(new String(response),ResponseResult.class);
                    }

                    if (responseResult.status_code == 200)
                    {
                        if (responseResult.soldto.size() != 0) {
                            for (CustomerObject item: responseResult.soldto)
                            {
                                if (item.nickname.contains(searchValue) || item.name.contains(searchValue) || item.custcode.contains(searchValue) || item.soldto.contains(searchValue)){
                                    arrayListSoldTo.add(item);
                                }
                            }
                            if (rl_no_information != null) rl_no_information.hide();
                        }
                        else {
                            if (isAdded() && rl_no_information != null) rl_no_information.show("No List.","ไม่พบ List Customer ที่กำหนดไว้บน Lotus Notes",getResources().getDrawable(R.drawable.ic_cross));
                        }
                    }
                    else if (responseResult.status_code == 201)
                    {
                        if (isAdded() && rl_no_information != null) rl_no_information.show("No Result.","ผลการค้นหา '" + searchValue +"' ไม่พบรายการในระบบ, ลองอีกครั้ง",getResources().getDrawable(R.drawable.ic_cross));
                    }
                    if (arrayListSoldTo.size() == 0)
                    {
                        if (isAdded() && rl_no_information != null) rl_no_information.show("ไม่พบรายชื่อลูกค้า","กรุณาเตรียมข้อมูลใน Lotus Notes เพื่อแสดงข้อมูลลูกค้า",getResources().getDrawable(R.drawable.ic_cross));

                        if (searchValue.length() >= 8) {
                            rParams.put("kw", searchValue);
                            client.get(Constants.API_HOST + "MSOCustSearch.php?", rParams, new AsyncHttpResponseHandler() {

                                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                @Override
                                public void onStart() {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        Constants.doLog("LOG SEARCH SUBMIT : " + arrayListSoldTo.size());
                                        if (customProgress == null) customProgress = CustomDialogLoading.getInstance();
                                        customProgress.showProgress(getContext(), "Loading", null, getContext().getDrawable(R.drawable.ic_loading), false, false, true);
                                    }
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] response)
                                {
                                    if (arrayListSoldTo.size() != 0){
                                        Constants.doLog("LOG arrayListSoldTo SIZE : " + arrayListSoldTo.size());
                                        arrayListSoldTo.clear();
                                        if (rl_no_information != null) rl_no_information.hide();
                                    }

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        customProgress.hideProgress();
                                    }

                                    Constants.doLog("LOG RESPONSE RESULT : " + statusCode);
                                    Constants.doLog("LOG RESPONSE RESULT : " + new String(response));
                                    Gson gson = new Gson();
                                    ResponseResult responseResult = new ResponseResult();

                                    if (isJSONValid(new String(response))){
                                        responseResult = gson.fromJson(new String(response),ResponseResult.class);
                                    }

                                    if (responseResult.status_code == 200)
                                    {
                                        if (responseResult.soldto.size() != 0) {
                                            for (CustomerObject item: responseResult.soldto)
                                            {
                                                arrayListSoldTo.add(item);
                                            }
                                            if (rl_no_information != null) rl_no_information.hide();
                                        }
                                        else {
                                            if (isAdded() && rl_no_information != null) rl_no_information.show("No List.","ไม่พบ List Customer ที่กำหนดไว้บน Lotus Notes",getResources().getDrawable(R.drawable.ic_cross));
                                        }
                                    }
                                    else if (responseResult.status_code == 201)
                                    {
                                        if (isAdded() && rl_no_information != null) rl_no_information.show("No Result.","ผลการค้นหา '" + searchValue +"' ไม่พบรายการในระบบ, ลองอีกครั้ง",getResources().getDrawable(R.drawable.ic_cross));
                                    }
                                    if (arrayListSoldTo.size() == 0)
                                    {
                                        if (isAdded() && rl_no_information != null) rl_no_information.show("ไม่พบรายชื่อลูกค้า","กรุณาเตรียมข้อมูลใน Lotus Notes เพื่อแสดงข้อมูลลูกค้า",getResources().getDrawable(R.drawable.ic_cross));
                                    }

                                    customerListAdapter.notifyDataSetChanged();

                                }


                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e)
                                {
                                    //                    GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_cannot_connect_server));
                                    //                    Intent myIntent = new Intent(getActivity(), MainActivity.class);
                                    //                    getActivity().startActivity(myIntent);
                                    //                    getActivity().finish();
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

                    customerListAdapter.notifyDataSetChanged();

                }


                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e)
                {
                    //                    GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_cannot_connect_server));
                    //                    Intent myIntent = new Intent(getActivity(), MainActivity.class);
                    //                    getActivity().startActivity(myIntent);
                    //                    getActivity().finish();
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
    public void onCustomerItemClick(View view, int position) {
        CustomerObject customerObject = arrayListSoldTo.get(position);

        view.setBackground(getResources().getDrawable(R.drawable.background_select));

//        bundle.putSerializable(Constants.SOLD_TO ,customerObject);
        soldToCode = customerObject.custcode;
        soldToName = customerObject.name;

        Constants.doLog("Item was clicked" + position);

        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SOLD_TO_CODE, soldToCode);
        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SOLD_TO_NAME, soldToName);
        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SHIP_TO_CODE, "");
        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SHIP_TO_NAME, "");
        FragmentMainSaleOrderCreate fragment = new FragmentMainSaleOrderCreate();
        ((MainActivity)getActivity()).replaceFragment(fragment, true);

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
//            case R.id.menu_add:
//                return false;
//                if (!checkedSelected) {
//                    GeneralHelper.getInstance().showBasicAlert(getContext(), "Sold To(Customer) does not selected. Please selected before confirm.");
//                }
//                else {
//                    SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SOLD_TO_CODE, soldToCode);
//                    SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SOLD_TO_NAME, soldToName);
//                    SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SHIP_TO_CODE, "");
//                    SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SHIP_TO_NAME, "");
//                    FragmentMainSaleOrderCreate fragment = new FragmentMainSaleOrderCreate();
//                    ((MainActivity)getActivity()).replaceFragment(fragment, true);
//                }
//
//            case R.id.menu_search:
//                return false;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
//        inflater.inflate(R.menu.toolbar_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        Constants.doLog("LOG SEARCH FIELD SUBMIT1 : " + query);

        checkedQuery = true;

        textViewSearchResultTitle.setText("ผลการค้นหา : \" " + query + " \"");
        if (arrayListSoldTo.size() != 0){
            arrayListSoldTo.clear();
        }
        loadListCustomer(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        Constants.doLog("LOG SEARCH FIELD CHANGE : " + newText);
//
//        if (newText.isEmpty()) {
//            if (arrayListSoldTo.size() != 0){
//                arrayListSoldTo.clear();
//            }
//            loadListCustomer(newText);
//            checkedQuery = false;
//        }
        return false;
    }
}
