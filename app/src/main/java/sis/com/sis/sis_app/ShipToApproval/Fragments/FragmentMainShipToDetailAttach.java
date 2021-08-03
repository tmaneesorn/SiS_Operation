package sis.com.sis.sis_app.ShipToApproval.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import sis.com.sis.sis_app.ShipToApproval.Activities.AuthActivity;
import sis.com.sis.sis_app.ShipToApproval.Activities.MainActivity;
import sis.com.sis.sis_app.ShipToApproval.Adapters.ShipToListAdapter;
import sis.com.sis.sis_app.ShipToApproval.Constants;
import sis.com.sis.sis_app.Helpers.GeneralHelper;
import sis.com.sis.sis_app.Helpers.SharedPreferenceHelper;
import sis.com.sis.sis_app.ShipToApproval.Models.ItemObject;
import sis.com.sis.sis_app.ShipToApproval.Models.ResponseResult;
import sis.com.sis.sis_app.ShipToApproval.Models.ShipToObject;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.Views.CustomTextView;
import sis.com.sis.sis_app.Views.PopupBrowser;

public class FragmentMainShipToDetailAttach extends Fragment {

    @BindView(R.id.textViewExpense) CustomTextView textViewExpense;
    @BindView(R.id.textViewCase) CustomTextView textViewCase;
    @BindView(R.id.textViewNotes) CustomTextView textViewNotes;

    AsyncHttpClient client;
    String shipto_po_url = "";
    String shipto_idcard_url = "";
    String shipto_map_url = "";

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.shipto_fragment_main_shipto_detail_attach, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.main_title_sale_order_detail);

        loadAttachFile();

        return view;
    }

    private void loadAttachFile() {

        if (client == null) client = new AsyncHttpClient(80,443);

        Bundle bundle = getArguments();
        String so_no = String.valueOf(bundle.getSerializable("so_no"));

        RequestParams rParams = new RequestParams();
        rParams.put("so_no", so_no);

        client.post(Constants.API_HOST + "api_so_app_getsoattach?OpenAgent&", rParams, new AsyncHttpResponseHandler() {

            @Override
            public void onStart()
            {
//                if (customProgress == null) customProgress = CustomDialogLoading.getInstance();
//                customProgress.showProgress(getContext(), getResources().getString(R.string.loading),null,getContext().getDrawable(R.drawable.ic_loading),false, false, true);
            }



            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response)
            {

                Gson gson = new Gson();
                ResponseResult responseResult = null;
//
                responseResult = gson.fromJson(new String(response),ResponseResult.class);


                Constants.doLog("LOG RESPONSE RESULT : " + new String(response));
//                Constants.doLog("LOG RESPONSE RESULT : " + responseResult.item_list);

                if (responseResult.status == 200)
                {
                    shipto_po_url       = responseResult.att_shipto_data.shipto_po;
                    shipto_idcard_url   = responseResult.att_shipto_data.shipto_idcard;
                    shipto_map_url      = responseResult.att_shipto_data.shipto_map;

                    textViewExpense.setText(responseResult.att_shipto_data.shipto_expense);
                    textViewCase.setText(responseResult.att_shipto_data.shipto_customercase);

                    textViewNotes.setText(responseResult.notes_txt);
//                    if (responseResult.notes_txt == 1){
//                        textViewNotes.setText(R.string.message_cannot_display_content);
//                    }
//                    else {
//                        textViewNotes.setText(R.string.message_no_content);
//                    }

                }
//                itemListAdapter.notifyDataSetChanged();

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
