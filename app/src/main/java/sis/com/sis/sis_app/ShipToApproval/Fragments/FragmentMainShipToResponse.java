package sis.com.sis.sis_app.ShipToApproval.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import sis.com.sis.sis_app.ShipToApproval.Activities.AuthActivity;
import sis.com.sis.sis_app.ShipToApproval.Activities.MainActivity;
import sis.com.sis.sis_app.ShipToApproval.Constants;
import sis.com.sis.sis_app.Helpers.GeneralHelper;
import sis.com.sis.sis_app.Helpers.InputHelpers;
import sis.com.sis.sis_app.Helpers.SharedPreferenceHelper;
import sis.com.sis.sis_app.ShipToApproval.Models.ResponseResult;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.Views.CustomButton;
import sis.com.sis.sis_app.Views.CustomDialogLoading;
import sis.com.sis.sis_app.Views.CustomEditText;
import sis.com.sis.sis_app.Views.CustomTextView;

import static sis.com.sis.sis_app.Views.CustomDialogLoading.customProgress;

public class FragmentMainShipToResponse extends Fragment {

    @BindView(R.id.customButtonSave) CustomButton customButtonSave;
    @BindView(R.id.customButtonCancel) CustomButton customButtonCancel;
    @BindView(R.id.textViewResponse) CustomEditText textViewResponse;
    @BindView(R.id.textViewTitleAction) CustomTextView textViewTitleAction;

    String username;
    String so_no;
    String action;
    String responseField;

    AsyncHttpClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.shipto_fragment_main_response, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        username = String.valueOf(bundle.getSerializable("username"));
        so_no = String.valueOf(bundle.getSerializable("so_no"));
        action = String.valueOf(bundle.getSerializable("action"));

        responseField = SharedPreferenceHelper.getSharedPreferenceString(getContext(), "textViewResponse", "");
        textViewResponse.setText(responseField);

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (action.equals("app")){
            textViewTitleAction.setText(R.string.main_button_approve);
        }
        else if (action.equals("response")){
            textViewTitleAction.setText(R.string.main_button_response);
        }
        else {
            textViewTitleAction.setText(R.string.main_button_not_approve);
        }

        return view;
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

    @OnClick(R.id.customButtonSave)
    public void btnSave(CustomButton button) {


//        String username = SharedPreferenceHelper.getSharedPreferenceString(getContext(), "username", "");
        responseField = textViewResponse.getText().toString().replace("&", "และ");

        RequestParams rParams = new RequestParams();
        rParams.put("username", username);
        rParams.put("so_no", so_no);
        rParams.put("response", responseField);
        rParams.put("action", action);

        Constants.doLog("LOG PARAMETER RESPONSE TEXT : " + rParams.toString());

        if (InputHelpers.isEmpty(textViewResponse))
        {
            GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_response_empty));
            return;
        }

        if (client == null) client = new AsyncHttpClient(true, 80, 443);

        client.post(Constants.API_HOST + "api_so_app_sendaction", rParams, new AsyncHttpResponseHandler() {

            @Override
            public void onStart()
            {
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

                Constants.doLog("LOG ORDER DETAIL : " + new String(response));

                Gson gson = new Gson();
                ResponseResult responseResult = new ResponseResult();

                if (isJSONValid(new String(response))){
                    responseResult = gson.fromJson(new String(response),ResponseResult.class);
                }

                Constants.doLog("LOG ORDER DETAIL : " + responseResult.status);
                Constants.doLog("LOG ORDER DETAIL ACTION : " + action);

                if (responseResult.status == 200)
                {
                    if (action.equals("app")) {
                        if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_approve_success));

                        Constants.doLog("CHECKED & :" + responseField);
                        FragmentMainShipToApprove fragment = new FragmentMainShipToApprove();
                        ((MainActivity)getActivity()).replaceFragment(fragment, false);
                    }
                    else if (action.equals("reject")) {
                        if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_reject_success));

                        Constants.doLog("CHECKED & :" + responseField);
                        FragmentMainShipToApprove fragment = new FragmentMainShipToApprove();
                        ((MainActivity)getActivity()).replaceFragment(fragment, false);
                    }
                    else {
                        if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_response_success));

                        Constants.doLog("CHECKED & :" + responseField);
                        FragmentMainShipToApprove fragment = new FragmentMainShipToApprove();
                        ((MainActivity)getActivity()).replaceFragment(fragment, false);

                    }
                }
                else
                {
                    // show error
                    if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), "This Sale Order has some problem, Please contact IS.");
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
                    if (customProgress != null) customProgress.hideProgress();
                }

                GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_cannot_connect_server));
                Intent myIntent = new Intent(getActivity(), AuthActivity.class);
                getActivity().startActivity(myIntent);
                getActivity().finish();
            }


            @Override
            public void onRetry(int retryNo)
            {
                // called when request is retried
            }
        });

    }

    @OnClick(R.id.customButtonCancel)
    public void btnCancel(CustomButton button) {

//        Toast.makeText(getActivity(), getResources().getString(R.string.action), Toast.LENGTH_LONG).show();
//        Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }


}
