package sis.com.sis.sis_app.ShipToApproval.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import sis.com.sis.sis_app.ShipToApproval.Activities.AuthActivity;
import sis.com.sis.sis_app.ShipToApproval.Activities.MainActivity;
import sis.com.sis.sis_app.ShipToApproval.Adapters.ItemListAdapter;
import sis.com.sis.sis_app.ShipToApproval.Constants;
import sis.com.sis.sis_app.Helpers.GeneralHelper;
import sis.com.sis.sis_app.ShipToApproval.Models.ItemObject;
import sis.com.sis.sis_app.ShipToApproval.Models.ResponseResult;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.Views.CustomDialogLoading;
import sis.com.sis.sis_app.Views.CustomTextView;
import sis.com.sis.sis_app.Views.ScrollDetectableListView;

import static sis.com.sis.sis_app.Views.CustomDialogLoading.customProgress;

public class FragmentMainShipToDetailItem extends Fragment  {

    @BindView(R.id.recycleViewItems) ScrollDetectableListView mListView;
    @BindView(R.id.imageButtonScrollUp) ImageButton imageButtonScrollUp;
    @BindView(R.id.imageButtonScrollDown) ImageButton imageButtonScrollDown;
    @BindView(R.id.textViewAmount) CustomTextView textViewAmount;
    @BindView(R.id.textViewTotalPrice) CustomTextView textViewTotalPrice;

    ItemListAdapter itemListAdapter;
    List<ItemObject> arrayList;
    AsyncHttpClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.shipto_fragment_main_shipto_detail_item, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.main_title_sale_order_detail);


        arrayList = new ArrayList<ItemObject>();
        itemListAdapter = new ItemListAdapter(getContext(), arrayList);
        mListView.setAdapter(itemListAdapter);
        loadItems();

        return view;
    }

    private void loadItems() {

        if (client == null) client = new AsyncHttpClient(80,443);

        Bundle bundle = getArguments();
        String so_no = String.valueOf(bundle.getSerializable("so_no"));

        Constants.doLog("LOG PARAMETER SALE ORDER DETAIL (ITEMS) : " + so_no);
        RequestParams rParams = new RequestParams();
        rParams.put("so_no", so_no);

        client.post(Constants.API_HOST + "api_so_app_getsoitem?OpenAgent&", rParams, new AsyncHttpResponseHandler() {

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    customProgress.hideProgress();
                }

                Constants.doLog("LOG RESPONSE RESULT : " + new String(response));
                Gson gson = new Gson();
                ResponseResult responseResult = new ResponseResult();

                if (isJSONValid(new String(response))){
                    responseResult = gson.fromJson(new String(response),ResponseResult.class);
                }

                Constants.doLog("LOG RESPONSE RESULT ITEM : " + responseResult.item_list);

                if (responseResult.status == 200)
                {
                    for (ItemObject item: responseResult.item_list)
                    {
                        arrayList.add(item);
                    }

                    String total_netprice = new DecimalFormat("#,###.00").format(responseResult.total_netprice);
                    String total_qty = String.valueOf(responseResult.total_qty);
                    textViewAmount.setText(total_qty);
                    textViewTotalPrice.setText(total_netprice);

                    if (arrayList.size() > 8) {
                        imageButtonScrollUp.setVisibility(View.VISIBLE);
                        imageButtonScrollDown.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), "Cannot do this action, Please contact IS.");
                }

                itemListAdapter.notifyDataSetChanged();

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e)
            {
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

    @OnClick(R.id.imageButtonScrollDown)
    public void btnScrollDownPressed(ImageButton button)
    {
        mListView.smoothScrollToPosition(itemListAdapter.getCount());

    }

    @OnClick(R.id.imageButtonScrollUp)
    public void btnScrollUpPressed(ImageButton button)
    {
        mListView.smoothScrollToPosition(0);

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
