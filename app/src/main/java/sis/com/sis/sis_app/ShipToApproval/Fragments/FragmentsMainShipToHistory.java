package sis.com.sis.sis_app.ShipToApproval.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.RequiresApi;
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
import sis.com.sis.sis_app.ShipToApproval.Activities.AuthActivity;
import sis.com.sis.sis_app.ShipToApproval.Activities.MainActivity;
import sis.com.sis.sis_app.ShipToApproval.Adapters.ShipToHistoryListAdapter;
import sis.com.sis.sis_app.ShipToApproval.Constants;
import sis.com.sis.sis_app.Helpers.GeneralHelper;
import sis.com.sis.sis_app.Helpers.SharedPreferenceHelper;
import sis.com.sis.sis_app.ShipToApproval.Models.ResponseResult;
import sis.com.sis.sis_app.ShipToApproval.Models.ShipToObject;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.Views.CustomDialogLoading;
import sis.com.sis.sis_app.Views.CustomMessageRelativeLayout;

import static sis.com.sis.sis_app.Views.CustomDialogLoading.customProgress;

public class FragmentsMainShipToHistory extends Fragment implements ShipToHistoryListAdapter.ListViewItemClickListener {

    @BindView(R.id.recycleViewShipToApprove) GridView mListView;
    @BindView(R.id.rl_no_information) CustomMessageRelativeLayout rl_no_info_view;
    ShipToHistoryListAdapter shipToHistoryListAdapter;
    List<ShipToObject> arrayList;
    AsyncHttpClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.shipto_fragment_main_shipto, container, false);
        ButterKnife.bind(this, view);

        ((sis.com.sis.sis_app.ShipToApproval.Activities.MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((sis.com.sis.sis_app.ShipToApproval.Activities.MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.main_title_history);

        arrayList = new ArrayList<ShipToObject>();
        shipToHistoryListAdapter = new ShipToHistoryListAdapter(getContext(), arrayList);
        shipToHistoryListAdapter.setListViewItemClickListener(this);
        mListView.setAdapter(shipToHistoryListAdapter);

        loadDocuments();

        return view;
    }

    private void loadDocuments() {

        if (client == null) client = new AsyncHttpClient(80,443);


        String username = SharedPreferenceHelper.getSharedPreferenceString(getContext(), sis.com.sis.sis_app.Constants.email, "");

        RequestParams rParams = new RequestParams();
        rParams.put("username", username);


        Constants.doLog("LOG HISTORY : " + Constants.API_HOST + "api_so_app_gethistory?OpenAgent&");
        Constants.doLog("LOG HISTORY : " + rParams);

        client.post(Constants.API_HOST + "api_so_app_gethistory?OpenAgent&", rParams, new AsyncHttpResponseHandler() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
            public void onSuccess(int statusCode, Header[] headers, byte[] response)
            {
                Constants.doLog("LOG HISTORY : " + new String(response));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (isAdded() && customProgress != null) customProgress.hideProgress();
                }
                Gson gson = new Gson();
                ResponseResult responseResult = new ResponseResult();

                if (isJSONValid(new String(response))){
                    responseResult = gson.fromJson(new String(response),ResponseResult.class);
                }

                Constants.doLog("LOG HISTORY : " + new String(response));

                if (responseResult.status == 200)
                {
                    for (ShipToObject item: responseResult.so_list)
                    {
                        arrayList.add(item);
                    }
                }
                else if (responseResult.status == 503)
                {
                    if (isAdded() && rl_no_info_view != null) rl_no_info_view.show(getResources().getString(R.string.ship_to_no_history),getResources().getString(R.string.ship_to_no_history_message),getResources().getDrawable(R.drawable.ic_cross));
                }
                else
                {
//                    if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), "Cannot do this action, Please contact IS.");
                }

                shipToHistoryListAdapter.notifyDataSetChanged();

                if (arrayList.size() == 0 )
                {
                    if (isAdded() && rl_no_info_view != null) rl_no_info_view.show(getResources().getString(R.string.ship_to_no_history),getResources().getString(R.string.ship_to_no_history_message),getResources().getDrawable(R.drawable.ic_cross));
                }
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

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
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
        ShipToObject shipToObjectItem = arrayList.get(position);

        Constants.doLog("HISTORY : SO_NO " + shipToObjectItem.so_no);

        FragmentMainShipToHistoryDetail fragment = new FragmentMainShipToHistoryDetail();
        Bundle bundle = new Bundle();
        bundle.putSerializable("shipToObjectItem",shipToObjectItem);
        bundle.putSerializable("so_no",shipToObjectItem.so_no);
        bundle.putSerializable("history",1);
        fragment.setArguments(bundle);
        ((MainActivity)getActivity()).replaceFragment(fragment, true);
    }


    @Override
    public void listViewDidScrollToEnd()
    {
        Constants.doLog("listViewDidScrollToEnd");
        loadDocuments();
    }
}
