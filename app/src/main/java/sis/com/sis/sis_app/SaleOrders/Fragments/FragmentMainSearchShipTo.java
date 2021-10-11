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
import sis.com.sis.sis_app.SaleOrders.Adapters.ShipToListAdapter;
import sis.com.sis.sis_app.SaleOrders.Constants;
import sis.com.sis.sis_app.SaleOrders.Models.CustomerObject;
import sis.com.sis.sis_app.SaleOrders.Models.ResponseResult;
import sis.com.sis.sis_app.Views.CustomDialogLoading;
import sis.com.sis.sis_app.Views.CustomMessageRelativeLayout;
import sis.com.sis.sis_app.Views.CustomTextView;
import sis.com.sis.sis_app.Views.ScrollDetectableListView;

import static sis.com.sis.sis_app.Views.CustomDialogLoading.customProgress;

public class FragmentMainSearchShipTo extends Fragment implements ShipToListAdapter.ListViewItemClickListener {

    @BindView(R.id.rl_no_information) CustomMessageRelativeLayout rl_no_information;
    @BindView(R.id.textViewSearchResultTitle) CustomTextView textViewSearchResultTitle;
    @BindView(R.id.recycleView) ScrollDetectableListView listViewShipTo;
    @BindView(R.id.expandableRecycleView) ExpandableListView expandableRecycleView;

    @BindView(R.id.searchView) SearchView searchView;

    ShipToListAdapter shipToListAdapter;
    List<CustomerObject> arrayListShipTo;

    AsyncHttpClient client;

    String shipToCode;
    String shipToName;

    boolean checkedQuery = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.saleorder_fragment_search, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("List Ship To");
        setHasOptionsMenu(true);

        arrayListShipTo = new ArrayList<CustomerObject>();
        shipToListAdapter = new ShipToListAdapter(getContext(), arrayListShipTo);
        shipToListAdapter.setListViewShipToItemClickListener(this);
        listViewShipTo.setAdapter(shipToListAdapter);
        listViewShipTo.setVisibility(View.VISIBLE);
        expandableRecycleView.setVisibility(View.GONE);

        textViewSearchResultTitle.setText("รายการ Ship To");
        searchView.setVisibility(View.GONE);
        //searchView.setOnQueryTextListener(this);

        loadListShipTo();

        return view;
    }

    private void loadListShipTo()
    {
        String soldTo_code = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SOLD_TO_CODE, "");

        if (client == null) client = new AsyncHttpClient(80,443);

        //Bundle bundle = getArguments();
        //String so_no = String.valueOf(bundle.getSerializable("so_no"));
        String username = SharedPreferenceHelper.getSharedPreferenceString(getContext(), sis.com.sis.sis_app.Constants.username, "");

        RequestParams rParams = new RequestParams();

        rParams.put("SiS", Constants.SIS_SECRET);
        rParams.put("u", username);

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
                Constants.doLog("LOG1 : " + arrayListShipTo.size());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    customProgress.hideProgress();
                }

                Constants.doLog("LOG RESPONSE RESULT1 : " + statusCode);
                Constants.doLog("LOG RESPONSE RESULT1 : " + new String(response));
                Gson gson = new Gson();
                ResponseResult responseResult = new ResponseResult();

                if (isJSONValid(new String(response))){
                    responseResult = gson.fromJson(new String(response),ResponseResult.class);
                }

                if (responseResult.status_code == 200)
                {

                    for (CustomerObject item: responseResult.shipto)
                    {
                        if (soldTo_code.equals(item.soldto)){
                            arrayListShipTo.add(item);
                        }
                    }
                    if (arrayListShipTo.isEmpty()) {
                        for (CustomerObject item: responseResult.soldto)
                        {
                            if (soldTo_code.equals(item.soldto)){
                                arrayListShipTo.add(item);
                            }
                        }
                    }

                    if (arrayListShipTo.size() == 0) {

                        rParams.put("kw", soldTo_code);
                        client.get(Constants.API_HOST + "MSOCustSearch.php?", rParams, new AsyncHttpResponseHandler() {

                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onStart() {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        if (customProgress == null) customProgress = CustomDialogLoading.getInstance();
//                        customProgress.showProgress(getContext(), "Loading", null, getContext().getDrawable(R.drawable.ic_loading), false, false, true);
//                    }
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] response)
                            {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        customProgress.hideProgress();
//                    }

                                Constants.doLog("LOG RESPONSE RESULT2 : " + statusCode);
                                Constants.doLog("LOG RESPONSE RESULT2 : " + new String(response));
                                Gson gson = new Gson();
                                ResponseResult responseResult = new ResponseResult();

                                if (isJSONValid(new String(response))){
                                    responseResult = gson.fromJson(new String(response),ResponseResult.class);
                                }

                                if (responseResult.status_code == 200)
                                {
//                        Constants.doLog("LOG2 : " + responseResult.shipto.size());

                                    for (CustomerObject item: responseResult.shipto)
                                    {
                                        if (soldTo_code.equals(item.soldto)){
                                            arrayListShipTo.add(item);
                                        }
                                    }
                                    if (arrayListShipTo.isEmpty()) {
                                        for (CustomerObject item: responseResult.soldto)
                                        {
                                            if (soldTo_code.equals(item.soldto)){
                                                arrayListShipTo.add(item);
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), "Cannot do this action, Please contact IS.");
                                }

                                shipToListAdapter.notifyDataSetChanged();

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

                    Constants.doLog("LOG2 : " + arrayListShipTo.size());
                }
                else
                {
                    if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), "Cannot do this action, Please contact IS.");
                }

                shipToListAdapter.notifyDataSetChanged();

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
    public void onShipToItemClick(View view, int position) {
        CustomerObject customerObject = arrayListShipTo.get(position);
//
//        for (int i = 0; i < arrayListShipTo.size() ; i++) {
//            listViewShipTo.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.background_edittext_white));
//        }

        view.setBackground(getResources().getDrawable(R.drawable.background_select));

        shipToCode = customerObject.custcode;
        shipToName = customerObject.name;

        Constants.doLog("Item was clicked" + position);

        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SHIP_TO_CODE, shipToCode);
        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SHIP_TO_NAME, shipToName);
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
//                SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SHIP_TO_CODE, shipToCode);
//                SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SHIP_TO_NAME, shipToName);
//                FragmentMainSaleOrderCreate fragment = new FragmentMainSaleOrderCreate();
//                ((MainActivity)getActivity()).replaceFragment(fragment, true);
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

}
