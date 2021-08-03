package sis.com.sis.sis_app.ShipToApproval.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.RequiresApi;
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

import static sis.com.sis.sis_app.Views.CustomDialogLoading.customProgress;

public class FragmentAuthLogin extends Fragment {

    @BindView(R.id.et_email) CustomEditText et_email;
    @BindView(R.id.et_password) CustomEditText et_password;
    @BindView(R.id.btnLogin) CustomButton btnLogin;


    AsyncHttpClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.main_fragment_auth_login, container, false);
        ButterKnife.bind(this, view);

        String email = SharedPreferenceHelper.getSharedPreferenceString(getContext(), "user_email", "");
        et_email.setText(email);

        return view;
    }

    @OnClick(R.id.btnLogin)
    public void btnLoginPressed(Button button)
    {
        if (InputHelpers.isEmpty(et_email))
        {
//            GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.authHintFillEmail));
            GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_enter_username));
            return;
        }

        if (InputHelpers.isEmpty(et_password))
        {
            GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_enter_password));
            return;
        }

        if (client == null) client = new AsyncHttpClient(80,443);
        RequestParams rParams = new RequestParams();

        rParams.put("username", et_email.getText().toString());
        rParams.put("password", et_password.getText().toString());

        client.post(Constants.API_HOST + "api_so_app_getbudep?OpenAgent&", rParams, new AsyncHttpResponseHandler() {

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

                Gson gson = new Gson();
                ResponseResult responseResult = new ResponseResult();

                Constants.doLog("LOG RESPONSE RESULT AUTH : " + new String(response));
                Constants.doLog("LOG RESPONSE RESULT AUTH STATUS CODE : " + Constants.API_HOST + "api_so_app_getbudep?OpenAgent&");

                if (isJSONValid(new String(response))){
                    responseResult = gson.fromJson(new String(response),ResponseResult.class);
                }


                if (responseResult.status == 200 || responseResult.status == 503)
                {
                    // set previous code to be able to auto load next time for convenience
                    SharedPreferenceHelper.setSharedPreferenceBoolean(getContext(), "logged_in", true);
                    SharedPreferenceHelper.setSharedPreferenceString(getContext(), "username", et_email.getText().toString());
                    SharedPreferenceHelper.setSharedPreferenceString(getContext(), "password", et_password.getText().toString());

                    Intent myIntent = new Intent(getActivity(), MainActivity.class);
                    getActivity().startActivity(myIntent);
                    getActivity().finish();


                }
                else if (responseResult.status == 501)
                {

                    // show error
                    GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_username_incorrect));
                }
                else if (responseResult.status == 502)
                {

                    // show error
                    GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_password_incorrect));
                }
                else
                {
                    if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), "Cannot do this action, Please contact IS.");
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
                Constants.doLog("LOG RESPONSE RESULT AUTH STATUS CODE : " + e.getLocalizedMessage());
                Constants.doLog("LOG RESPONSE RESULT AUTH STATUS CODE : " + Constants.API_HOST + "api_so_app_getbudep?OpenAgent&");

//                Constants.doLog("LOG RESPONSE RESULT AUTH : " + new String(errorResponse));
                GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_cannot_connect_server));
            }


            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });

    }

}
