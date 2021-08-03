package sis.com.sis.sis_app.ShipToApproval.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import sis.com.sis.sis_app.ShipToApproval.Adapters.ShipToListAdapter;
import sis.com.sis.sis_app.ShipToApproval.Constants;
import sis.com.sis.sis_app.Helpers.GeneralHelper;
import sis.com.sis.sis_app.Helpers.SharedPreferenceHelper;
import sis.com.sis.sis_app.ShipToApproval.Models.ResponseResult;
import sis.com.sis.sis_app.ShipToApproval.Models.ShipToObject;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.Views.CustomDialogLoading;
import sis.com.sis.sis_app.Views.CustomMessageRelativeLayout;

import static sis.com.sis.sis_app.Views.CustomDialogLoading.customProgress;

public class FragmentMainShipToApprove extends Fragment implements ShipToListAdapter.ListViewItemClickListener {

    @BindView(R.id.rl_no_information) CustomMessageRelativeLayout rl_no_info_view;
    @BindView(R.id.recycleViewShipToApprove) GridView mListView;
    ShipToListAdapter shipToListAdapter;
    List<ShipToObject> arrayList;
    AsyncHttpClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.shipto_fragment_main_shipto, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.main_title_shipto);


        arrayList = new ArrayList<ShipToObject>();
        shipToListAdapter = new ShipToListAdapter(getContext(), arrayList);
        shipToListAdapter.setListViewItemClickListener(this);
        mListView.setAdapter(shipToListAdapter);
        loadListShipTo();

        return view;
    }

    private void loadListShipTo() {

        if (client == null) client = new AsyncHttpClient(80,443);

        String username = SharedPreferenceHelper.getSharedPreferenceString(getContext(), sis.com.sis.sis_app.Constants.email, "");
        String password = SharedPreferenceHelper.getSharedPreferenceString(getContext(), sis.com.sis.sis_app.Constants.password, "");

        RequestParams rParams = new RequestParams();
        rParams.put("username", username);
        rParams.put("password", password);

        client.post(Constants.API_HOST + "api_so_app_getbudep?OpenAgent&", rParams, new AsyncHttpResponseHandler() {

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
                    int i = 0;
                    for (ShipToObject item: responseResult.so_list)
                    {
                        if (item.so_special_approle != 0) {
                            arrayList.add(i,item);
                            i++;
                        }
                        else {
                            arrayList.add(item);
                        }
                    }
                }
                else if (responseResult.status == 503)
                {
                    if (isAdded() && rl_no_info_view != null) rl_no_info_view.show(getResources().getString(R.string.ship_to_no_order),getResources().getString(R.string.ship_to_no_order_message),getResources().getDrawable(R.drawable.ic_cross));
                }
                else
                {
                    if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), "Cannot do this action, Please contact IS.");
                }

                shipToListAdapter.notifyDataSetChanged();

                if (arrayList.size() == 0)
                {
                    if (isAdded() && rl_no_info_view != null) rl_no_info_view.show(getResources().getString(R.string.ship_to_no_order),getResources().getString(R.string.ship_to_no_order_message),getResources().getDrawable(R.drawable.ic_cross));
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
    public void onDestroy() {
        super.onDestroy();
        if (client != null) client.cancelAllRequests(true);
//        mListView.isLoading = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (client != null) client.cancelAllRequests(true);
//        mListView.isLoading = false;
    }


    @Override
    public void onItemClick(View view, int position) {
        ShipToObject shipToObjectItem = arrayList.get(position);

        Constants.doLog("Item was clicked" + shipToObjectItem.so_no);

        FragmentMainShipToDetail fragment = new FragmentMainShipToDetail();
        Bundle bundle = new Bundle();
        bundle.putSerializable("shipToObjectItem",shipToObjectItem);
        bundle.putSerializable("so_no",shipToObjectItem.so_no);
        fragment.setArguments(bundle);
        ((MainActivity)getActivity()).replaceFragment(fragment, true);
    }

    @Override
    public void listViewDidScrollToEnd() {
//        loadDocuments();
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


}
