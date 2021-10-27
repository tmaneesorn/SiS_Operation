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
import android.widget.RelativeLayout;

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
import sis.com.sis.sis_app.Main.Activities.AuthActivity;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.SaleOrders.Activities.MainActivity;
import sis.com.sis.sis_app.SaleOrders.Adapters.CheckSaleOrderListAdapter;
import sis.com.sis.sis_app.SaleOrders.Constants;
import sis.com.sis.sis_app.SaleOrders.Models.CheckStatusObject;
import sis.com.sis.sis_app.SaleOrders.Models.ResponseResult;
import sis.com.sis.sis_app.SaleOrders.Models.SaleOrderObject;
import sis.com.sis.sis_app.Views.CustomDialogLoading;
import sis.com.sis.sis_app.Views.CustomMessageRelativeLayout;
import sis.com.sis.sis_app.Views.CustomTextView;
import sis.com.sis.sis_app.Views.ScrollDetectableListView;

import static sis.com.sis.sis_app.Views.CustomDialogLoading.customProgress;

public class FragmentMainCheckOrder extends Fragment implements CheckSaleOrderListAdapter.ListViewItemClickListener, SearchView.OnQueryTextListener {

    @BindView(R.id.rl_no_information) CustomMessageRelativeLayout rl_no_information;
    @BindView(R.id.recycleViewSaleOrder) ScrollDetectableListView recycleViewSaleOrder;
    @BindView(R.id.relativeLayoutSaleOrderHeader) RelativeLayout relativeLayoutSaleOrderHeader;
    @BindView(R.id.textViewSearchResult) CustomTextView textViewSearchResult;
    @BindView(R.id.searchView) SearchView searchView;

    CheckSaleOrderListAdapter checkSaleOrderListAdapter;
    List<CheckStatusObject> arrayList;
    AsyncHttpClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.saleorder_fragment_main_check_order_status, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("ตรวจสอบสถานะ Sale Order");
        setHasOptionsMenu(true);

        arrayList = new ArrayList<CheckStatusObject>();
        checkSaleOrderListAdapter = new CheckSaleOrderListAdapter(getContext(), arrayList);
        checkSaleOrderListAdapter.setListViewItemClickListener(this);
        recycleViewSaleOrder.setAdapter(checkSaleOrderListAdapter);

        rl_no_information.show("No Content","Please search your sale order number to check status.",getResources().getDrawable(R.drawable.ic_cross));
        textViewSearchResult.setText("\" - \"");

        searchView.setOnQueryTextListener(this);


        loadListSaleOrder("");

        return view;
    }

    private void loadListSaleOrder(String searchValue) {

        if (client == null) client = new AsyncHttpClient(80,443);
        String username = SharedPreferenceHelper.getSharedPreferenceString(getContext(), sis.com.sis.sis_app.Constants.username, "");

        RequestParams rParams = new RequestParams();

        rParams.put("SiS", Constants.SIS_SECRET);
        rParams.put("u", username);
        rParams.put("mobodr", searchValue);
//        rParams.put("sapodr", "pornchai");

        client.get(Constants.API_HOST + "MSOOrderCheck.php?", rParams, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (customProgress == null) customProgress = CustomDialogLoading.getInstance();
                    if (isAdded())
                        customProgress.showProgress(getContext(), "Loading", null, getContext().getDrawable(R.drawable.ic_loading), false, false, true);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response)
            {
                arrayList.clear();
                Constants.doLog("LOG SHIPTO : " + new String(response));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (isAdded() && customProgress != null) customProgress.hideProgress();
                }

                Gson gson = new Gson();
                ResponseResult responseResult = new ResponseResult();

                if (isJSONValid(new String(response))){
                    responseResult = gson.fromJson(new String(response),ResponseResult.class);

                    Constants.doLog("LOG SHIPTO : " + responseResult.status_code);
                    Constants.doLog("LOG SHIPTO : " + responseResult.data);

                    if (responseResult.status_code == 200)
                    {
                        for (CheckStatusObject item: responseResult.data)
                        {
                            if (item.code.equals("502")) {
                                if (item.order.length() >= 8) {
                                    rParams.remove("mobodr");
                                    rParams.put("sapodr", item.order);
                                    client.get(Constants.API_HOST + "MSOOrderCheck.php?", rParams, new AsyncHttpResponseHandler() {

                                        @Override
                                        public void onStart() {

                                        }

                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                            Gson gson = new Gson();
                                            ResponseResult responseResult = new ResponseResult();

                                            if (isJSONValid(new String(response))) {
                                                responseResult = gson.fromJson(new String(response), ResponseResult.class);
                                            }


                                            if (responseResult.status_code == 200) {
                                                for (CheckStatusObject item : responseResult.data) {
                                                    if (!item.code.equals("502")) {
                                                        arrayList.add(item);
                                                    }
                                                    else {
                                                        if (isAdded() && rl_no_information != null) rl_no_information.show("No Result!","Please search your sale order number to check status.",getResources().getDrawable(R.drawable.ic_cross));

                                                    }
                                                }
                                            }
                                            checkSaleOrderListAdapter.notifyDataSetChanged();

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
                                }
                            }
                            else {
                                arrayList.add(item);
                            }
                        }

                        rl_no_information.hide();

                    }
                    else if (responseResult.status_code == 401)
                    {
                        searchView.setVisibility( View.GONE);
                        relativeLayoutSaleOrderHeader.setVisibility(View.GONE);
                        if (isAdded() && rl_no_information != null) rl_no_information.show("Account Unauthorized","Your account are unauthorized, Please contact IS.",getResources().getDrawable(R.drawable.ic_cross));
                    }
                    else if (responseResult.status_code == 201) {
                        if (isAdded() && rl_no_information != null) rl_no_information.show(getResources().getString(R.string.ship_to_no_order),"Please search your sale order.",getResources().getDrawable(R.drawable.ic_cross));
                    }
                    else
                    {
                        if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), getResources().getString(R.string.message_contact_is));
                    }

                    checkSaleOrderListAdapter.notifyDataSetChanged();
                }
                else if (new String(response).equals("Not Authorized or Invalid version!")){
                    GeneralHelper.getInstance().showUpdateAlert(getContext(),getResources().getString(R.string.message_update_version));
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

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void listViewDidScrollToEnd() {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Constants.doLog("LOG SEARCH FIELD CHANGE : " + query);

        textViewSearchResult.setText("\"" + query + "\"");
        loadListSaleOrder(query);
        if (arrayList.size() != 0){
            arrayList.clear();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Constants.doLog("LOG SEARCH FIELD CHANGE : " + newText);
        if (newText.isEmpty()) {
            if (arrayList.size() != 0){
                arrayList.clear();
            }
            textViewSearchResult.setText("\" - \"");

            if (isAdded() && rl_no_information != null) rl_no_information.show("No Content","Please search your sale order number to check status.",getResources().getDrawable(R.drawable.ic_cross));
        }
        return false;
    }

    @Override
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
