package sis.com.sis.sis_app.Main.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
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
import sis.com.sis.sis_app.Constants;
import sis.com.sis.sis_app.Helpers.GeneralHelper;
import sis.com.sis.sis_app.Helpers.SharedPreferenceHelper;
import sis.com.sis.sis_app.Main.Adapters.AppListAdapter;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.SaleOrders.Activities.MainActivity;
import sis.com.sis.sis_app.Main.Activities.AuthActivity;
import sis.com.sis.sis_app.Main.Models.AppObject;
import sis.com.sis.sis_app.Main.Models.ResponseResult;
import sis.com.sis.sis_app.Views.CustomDialogLoading;
import sis.com.sis.sis_app.Views.CustomMessageRelativeLayout;

import static sis.com.sis.sis_app.Views.CustomDialogLoading.customProgress;

public class MainFragmentHome extends Fragment implements AppListAdapter.ListViewItemClickListener {

    @BindView(R.id.searchView) SearchView searchView;
    @BindView(R.id.recycleViewAllApplication) GridView mListView;
    @BindView(R.id.rl_no_information) CustomMessageRelativeLayout rl_no_information;
    AppListAdapter appListAdapter;
    List<AppObject> arrayList;
    AsyncHttpClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.main_fragment_main_home, container, false);
        ButterKnife.bind(this, view);


        ((sis.com.sis.sis_app.Main.Activities.MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((sis.com.sis.sis_app.Main.Activities.MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);

        arrayList = new ArrayList<AppObject>();
        appListAdapter = new AppListAdapter(getContext(), arrayList);
        appListAdapter.setListViewItemClickListener(this);
        mListView.setAdapter(appListAdapter);
        loadAllApplication();

        return view;
    }

    private void loadAllApplication() {

        if (client == null) client = new AsyncHttpClient(80,443);

        String username = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.email, "");
        String password = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.password, "");

        RequestParams rParams = new RequestParams();
        rParams.put("username", username);
        rParams.put("password", password);



        client.post(sis.com.sis.sis_app.ShipToApproval.Constants.API_HOST + "public_login?OpenAgent&", rParams, new AsyncHttpResponseHandler() {

            //            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                Constants.doLog("LOG SHIPTO : " + new String(response));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (isAdded() && customProgress != null) customProgress.hideProgress();
                }

                Gson gson = new Gson();
                ResponseResult responseResult = new ResponseResult();

                if (isJSONValid(new String(response))){
                    responseResult = gson.fromJson(new String(response),ResponseResult.class);
                }

                Constants.doLog("LOG SHIPTO : " + responseResult.status);

                if (responseResult.status == 200)
                {
                    for (AppObject item: responseResult.access_app)
                    {
                        arrayList.add(item);
                    }
                }
                else if (responseResult.status == 503)
                {
                    if (isAdded() && rl_no_information != null) rl_no_information.show("No Application","You don't have access to use any application.",getResources().getDrawable(R.drawable.ic_cross));
                }
                else
                {
                    if (isAdded() && rl_no_information != null) rl_no_information.show("No Application","You don't have access to use any application.",getResources().getDrawable(R.drawable.ic_cross));
                }

                appListAdapter.notifyDataSetChanged();

//                if (arrayList.size() == 0)
//                {
//                    if (isAdded() && rl_no_information != null) rl_no_information.show(getResources().getString(R.string.ship_to_no_order),getResources().getString(R.string.ship_to_no_order_message),getResources().getDrawable(R.drawable.ic_cross));
//                }

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

    @Override
    public void onItemClick(View view, int position) {
        Constants.doLog("Item was clicked");
        AppObject object = arrayList.get(position);

        loadDocumentItem(object.app_name);
    }

    private void loadDocumentItem(String appName) {

        Intent myIntent = null;

        Constants.doLog("Item was clicked" + appName);
//        switch (id){
//
//        }
        if (appName.equals("SaleOrderMobile")) {
            myIntent = new Intent(getActivity(), MainActivity.class);
            //getActivity().finish();

//            FragmentShipTo fragmentShipTo = new FragmentShipTo();
//            fragmentShipTo.startActivity();
        }
        else if (appName.equals("ShiptoApproval")) {
            myIntent = new Intent(getActivity(), sis.com.sis.sis_app.ShipToApproval.Activities.MainActivity.class);
        }
        else if (appName.equals("CheckOrderStatus")) {
            myIntent = new Intent(getActivity(), sis.com.sis.sis_app.CheckOrderStatus.Activities.MainActivity.class);
        }
        getActivity().startActivity(myIntent);
//        Intent myIntent = new Intent(getActivity(), ShipToMainActivity.class);
//        getActivity().startActivity(myIntent);
//        getActivity().finish();

//        FragmentShipTo fragmentShipTo = new FragmentShipTo();
//        fragmentShipTo.startActivity();
    }
}
