package sis.com.sis.sis_app.ShipToApproval.Fragments;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import sis.com.sis.sis_app.ShipToApproval.Activities.AuthActivity;
import sis.com.sis.sis_app.ShipToApproval.Activities.MainActivity;
import sis.com.sis.sis_app.ShipToApproval.Constants;
import sis.com.sis.sis_app.Helpers.GeneralHelper;
import sis.com.sis.sis_app.Helpers.SharedPreferenceHelper;
import sis.com.sis.sis_app.ShipToApproval.Models.ResponseResult;
import sis.com.sis.sis_app.ShipToApproval.Models.ShipToObject;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.Views.CustomDialogLoading;
import sis.com.sis.sis_app.Views.CustomTabs.Custom2TabView;

import static sis.com.sis.sis_app.Views.CustomDialogLoading.customProgress;

public class FragmentMainShipToDetail extends Fragment implements Custom2TabView.NewTabDialogListener {

    @BindView(R.id.tabView) Custom2TabView tabView;
    ShipToObject shipToObjectItem;
    AsyncHttpClient client;
    String so_no;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.shipto_fragment_main_shipto_detail, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        shipToObjectItem = (ShipToObject) bundle.getSerializable("shipToObjectItem");
        String history = String.valueOf(bundle.getSerializable("history"));
        so_no = String.valueOf(bundle.getSerializable("so_no"));

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.main_title_sale_order_detail);
        setHasOptionsMenu(true);


        tabView.setTab1Text(getString(R.string.main_tab_detail));
        tabView.setTab2Text(getString(R.string.main_tab_item));
        tabView.setTabListener(this);

        loadShipToDetail();

        return view;
    }

    @Override
    public void onTabChanged(int position)
    {
//        arrayList.clear();
        client.cancelAllRequests(true);
//        mListView.isLoading = false;


        loadShipToDetail();
    }

    @Override
    public void onDestroy() {
//        super.onDestroy();
//        if (client != null) client.cancelAllRequests(true);
//        mListView.isLoading = false;
        super.onDestroy();
    }

    @Override
    public void onPause() {
//        super.onPause();
//        if (client != null) client.cancelAllRequests(true);
//        mListView.isLoading = false;
        super.onPause();
    }

    public void loadShipToDetail()
    {

        if (client == null) client = new AsyncHttpClient(80,443);

        String username = SharedPreferenceHelper.getSharedPreferenceString(getContext(), sis.com.sis.sis_app.Constants.email, "");
        RequestParams rParams = new RequestParams();
        rParams.put("so_no", so_no);

        if (tabView.getSelectedTab() == 0)
        {
            client.post(Constants.API_HOST + "api_so_app_getsodetail?OpenAgent&", rParams, new AsyncHttpResponseHandler() {

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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        customProgress.hideProgress();
                    }

                    Constants.doLog("LOG ORDER DETAIL more than 1 : " + new String(response));

                    Gson gson = new Gson();
                    ResponseResult responseResult = new ResponseResult();

                    if (isJSONValid(new String(response))){
                        responseResult = gson.fromJson(new String(response),ResponseResult.class);
                    }

                    Constants.doLog("LOG ORDER DETAIL : " + responseResult);


                    if (responseResult.status == 200)
                    {

                        FragmentMainShipToDetailOrder fragment = new FragmentMainShipToDetailOrder();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("orderDetail", responseResult.order_detail);
                        bundle.putSerializable("headerData", responseResult.header_data);
                        bundle.putSerializable("customerData", responseResult.customer_data);
                        bundle.putSerializable("shipToData", responseResult.shipto_data);
                        bundle.putSerializable("approvedData", responseResult.approved_data);
                        bundle.putSerializable("attachData", responseResult.att_shipto_data);
                        bundle.putSerializable("notesData", responseResult.notes_txt);
                        bundle.putSerializable("notesAttach", (Serializable) responseResult.notes_path);
                        bundle.putSerializable("POAttach", (Serializable) responseResult.po_path);
                        bundle.putSerializable("IDCardAttach", (Serializable) responseResult.idcard_path);
                        bundle.putSerializable("MapAttach", (Serializable) responseResult.map_path);
                        bundle.putSerializable("docOneTime", responseResult.doc_onetime);
                        bundle.putSerializable("so_no", so_no);
                        bundle.putSerializable("username", username);
                        fragment.setArguments(bundle);


                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.relativeLayoutContentView, fragment);
                        fragmentTransaction.commit();
                    }
                    else
                    {
                        if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), "Cannot do this action, Please contact IS.");
                    }


                }


                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e)
                {
//                mListView.isLoading = false;
//                if (isAdded() && rl_no_info_view != null) rl_no_info_view.hide();
//                if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(),e.getLocalizedMessage());
                    GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_cannot_connect_server));
                    Intent myIntent = new Intent(getActivity(), AuthActivity.class);
                    getActivity().startActivity(myIntent);
                    getActivity().finish();
                }


                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });
        }
        if (tabView.getSelectedTab() == 1)
        {
            FragmentMainShipToDetailItem fragment = new FragmentMainShipToDetailItem();

            Bundle bundle = new Bundle();
            bundle.putSerializable("so_no",shipToObjectItem.so_no);
            fragment.setArguments(bundle);

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.relativeLayoutContentView, fragment);
            fragmentTransaction.commit();

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

        //inflater.inflate(R.menu.toolbar_filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
