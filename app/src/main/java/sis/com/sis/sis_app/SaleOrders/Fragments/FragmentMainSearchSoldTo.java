package sis.com.sis.sis_app.SaleOrders.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    Date date = new Date();

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

        textViewSearchResultTitle.setText("?????????????????? Sold To");
        searchView.setOnQueryTextListener(this);

        loadListCustomer("");

        return view;
    }

    private void loadListCustomer(String searchValue) {

        if (client == null) client = new AsyncHttpClient(80,443);
        client.setTimeout(Constants.DEFAULT_TIMEOUT);

        //Bundle bundle = getArguments();
        //String so_no = String.valueOf(bundle.getSerializable("so_no"));
        String username = SharedPreferenceHelper.getSharedPreferenceString(getContext(), sis.com.sis.sis_app.Constants.username, "");

        RequestParams rParams = new RequestParams();

        rParams.put("SiS", Constants.SIS_SECRET);
        rParams.put("u", username);

        if (searchValue.isEmpty()){

            Constants.doLog("LOG SEARCH DEFAULT : " + arrayListSoldTo.size());

            client.get(Constants.API_HOST + "user/login?", rParams, new AsyncHttpResponseHandler() {

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

                    Constants.doLog("LOG RESPONSE NO SEARCH RESULT : " + statusCode);
                    Constants.doLog("LOG RESPONSE NO SEARCH RESULT : " + new String(response));
                    Gson gson = new Gson();
                    ResponseResult responseNoSearchResult = new ResponseResult();

                    if (isJSONValid(new String(response))){
                        responseNoSearchResult = gson.fromJson(new String(response),ResponseResult.class);

                        Constants.doLog("LOG RESPONSE NO SEARCH RESULT : " + responseNoSearchResult.soldto.size());
                        if (responseNoSearchResult.status_code == 200)
                        {
                            if (!responseNoSearchResult.soldto.isEmpty())
                            {
                                for (CustomerObject item: responseNoSearchResult.soldto)
                                {
                                    arrayListSoldTo.add(item);
                                }
                            }
                        }
                        else if (responseNoSearchResult.status_code == 401)
                        {
                            if (isAdded() && rl_no_information != null) rl_no_information.show("??????????????????????????????????????????????????????","????????????????????????????????????????????????????????? Lotus Notes ???????????????????????????????????????????????????????????????",getResources().getDrawable(R.drawable.ic_cross));
                        }
                        else {
                            if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), getResources().getString(R.string.message_contact_is));
                        }

                        customerListAdapter.notifyDataSetChanged();

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

                    GeneralHelper.getInstance().showBasicAlert(getContext(), getResources().getString(R.string.message_cannot_connect_server) + "\n\nSearch Sold to (On Lotus Notes) : " + DateFormat.format("dd/MM/yyyy HH:mm:ss",date)  + "\nError : " + e.toString());
                }


                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });
        }
        else {
            client.get(Constants.API_HOST + "user/login?", rParams, new AsyncHttpResponseHandler() {

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
                    ResponseResult responseSearchNotesResult = new ResponseResult();

                    if (isJSONValid(new String(response))){
                        responseSearchNotesResult = gson.fromJson(new String(response),ResponseResult.class);

                        if (responseSearchNotesResult.status_code == 200)
                        {
                            Constants.doLog("LOG RESPONSE RESULT : " + responseSearchNotesResult.soldto.size());
                            if (responseSearchNotesResult.soldto.size() != 0) {
                                for (CustomerObject item: responseSearchNotesResult.soldto)
                                {
                                    Constants.doLog("LOG TOLOWERCASE 1 : " + item.nickname.toLowerCase());
                                    Constants.doLog("LOG TOLOWERCASE 2 : " + searchValue.toLowerCase());
                                    Constants.doLog("LOG COMPARESEARCH : " + item.nickname.toLowerCase().contains(searchValue));
                                    if (item.nickname.toLowerCase().contains(searchValue.toLowerCase()) || item.name.toLowerCase().contains(searchValue.toLowerCase()) || item.custcode.toLowerCase().contains(searchValue.toLowerCase()) || item.soldto.toLowerCase().contains(searchValue.toLowerCase())){
                                        arrayListSoldTo.add(item);
                                    }
                                }
                                if (rl_no_information != null) rl_no_information.hide();
                            }
                            else {
                                if (isAdded() && rl_no_information != null) rl_no_information.show("No List.","??????????????? List Customer ??????????????????????????????????????? Lotus Notes",getResources().getDrawable(R.drawable.ic_cross));
                            }
                        }
                        else if (responseSearchNotesResult.status_code == 201)
                        {
                            if (isAdded() && rl_no_information != null) rl_no_information.show("No Result.","?????????????????????????????? '" + searchValue +"' ???????????????????????????????????????????????????, ?????????????????????????????????",getResources().getDrawable(R.drawable.ic_cross));
                        }
                        else {
                            if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), getResources().getString(R.string.message_contact_is));
                        }
                        if (arrayListSoldTo.size() == 0)
                        {
                            if (isAdded() && rl_no_information != null) rl_no_information.show("??????????????????????????????????????????????????????","????????????????????????????????????????????????????????? Lotus Notes ???????????????????????????????????????????????????????????????",getResources().getDrawable(R.drawable.ic_cross));

                            if (searchValue.length() >= 8) {
                                rParams.put("kw", searchValue);
                                client.get(Constants.API_HOST + "customer/search?", rParams, new AsyncHttpResponseHandler() {

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
                                        ResponseResult responseSearchSAPResult = new ResponseResult();

                                        if (isJSONValid(new String(response))){
                                            responseSearchSAPResult = gson.fromJson(new String(response),ResponseResult.class);

                                            if (responseSearchSAPResult.status_code == 200)
                                            {
                                                if (responseSearchSAPResult.soldto.size() != 0) {
                                                    for (CustomerObject item: responseSearchSAPResult.soldto)
                                                    {
                                                        arrayListSoldTo.add(item);
                                                    }
                                                    if (rl_no_information != null) rl_no_information.hide();
                                                }
                                                else {
                                                    if (isAdded() && rl_no_information != null) rl_no_information.show("No List.","??????????????? List Customer ??????????????????????????????????????? Lotus Notes",getResources().getDrawable(R.drawable.ic_cross));
                                                }
                                            }
                                            else if (responseSearchSAPResult.status_code == 201)
                                            {
                                                if (isAdded() && rl_no_information != null) rl_no_information.show("No Result.","?????????????????????????????? '" + searchValue +"' ???????????????????????????????????????????????????, ?????????????????????????????????",getResources().getDrawable(R.drawable.ic_cross));
                                            }
                                            else {
                                                if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), getResources().getString(R.string.message_contact_is));
                                            }
                                            if (arrayListSoldTo.size() == 0)
                                            {
                                                if (isAdded() && rl_no_information != null) rl_no_information.show("??????????????????????????????????????????????????????","????????????????????????????????????????????????????????? Lotus Notes ???????????????????????????????????????????????????????????????",getResources().getDrawable(R.drawable.ic_cross));
                                            }

                                            customerListAdapter.notifyDataSetChanged();

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

                                        GeneralHelper.getInstance().showBasicAlert(getContext(), getResources().getString(R.string.message_cannot_connect_server) + "\n\nSearch Sold to (On SAP) : " + DateFormat.format("dd/MM/yyyy HH:mm:ss",date)  + "\nError : " + e.toString());
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
                                    rl_no_information.show("No Result.", "??????????????????????????????????????????????????? ??????????????????????????????????????????????????????????????????????????? 8 ???????????????????????? (??????????????????????????????????????????????????? Lotus Notes ?????????????????????????????????????????????)", getResources().getDrawable(R.drawable.ic_cross));

                            }
                        }

                        customerListAdapter.notifyDataSetChanged();

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

                    GeneralHelper.getInstance().showBasicAlert(getContext(), getResources().getString(R.string.message_cannot_connect_server) + "\n\nSearch Sold to (On Lotus Notes) : " + DateFormat.format("dd/MM/yyyy HH:mm:ss",date)  + "\nError : " + e.toString());
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
                FragmentMainSaleOrderCreate fragment = new FragmentMainSaleOrderCreate();
                ((MainActivity)getActivity()).replaceFragment(fragment, true);
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

        textViewSearchResultTitle.setText("?????????????????????????????? : \" " + query + " \"");
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
