package sis.com.sis.sis_app.CheckOrderStatus.Fragments;

import static sis.com.sis.sis_app.Views.CustomDialogLoading.customProgress;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import sis.com.sis.sis_app.CheckOrderStatus.Activities.MainActivity;
import sis.com.sis.sis_app.CheckOrderStatus.Constants;
import sis.com.sis.sis_app.CheckOrderStatus.Models.ItemObject;
import sis.com.sis.sis_app.CheckOrderStatus.Models.ResponseResult;
import sis.com.sis.sis_app.Helpers.GeneralHelper;
import sis.com.sis.sis_app.Helpers.SharedPreferenceHelper;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.Views.CustomDialogLoading;
import sis.com.sis.sis_app.Views.CustomMessageRelativeLayout;
import sis.com.sis.sis_app.Views.CustomTextView;

public class FragmentMainOrderDetailStatus extends Fragment  {

    @BindView(R.id.rl_no_information)
    CustomMessageRelativeLayout rl_no_information;
    @BindView(R.id.textViewCustomerCodeName) CustomTextView textViewCustomerCodeName;
    @BindView(R.id.textViewShipToCodeName) CustomTextView textViewShipToCodeName;
    @BindView(R.id.textViewPONo) CustomTextView textViewPONo;
    //@BindView(R.id.textViewPODateTime) CustomTextView textViewPODateTime;
    @BindView(R.id.textViewSoNo) CustomTextView textViewSoNo;
    @BindView(R.id.textViewSoDateTime) CustomTextView textViewSoDateTime;
    @BindView(R.id.textViewDoNo) CustomTextView textViewDoNo;
    @BindView(R.id.textViewDoDateTime) CustomTextView textViewDoDateTime;
    @BindView(R.id.textViewPkNo) CustomTextView textViewPkNo;
    @BindView(R.id.textViewPkDateTime) CustomTextView textViewPkDateTime;
    @BindView(R.id.textViewPGINo) CustomTextView textViewPGINo;
    @BindView(R.id.textViewPGIDateTime) CustomTextView textViewPGIDateTime;
    @BindView(R.id.textViewInvNo) CustomTextView textViewInvNo;
    @BindView(R.id.textViewInvDateTime) CustomTextView textViewInvDateTime;
    @BindView(R.id.linearDetail) LinearLayout linearDetail;
    @BindView(R.id.linearStatus) LinearLayout linearStatus;
    @BindView(R.id.TopicDetail)  CustomTextView TopicDetail;
    @BindView(R.id.textTopicStatus) CustomTextView textTopicStatus;
    @BindView(R.id.scrollview)
    ScrollView scrollview;

   // SaleOrderListAdapter saleOrderListAdapter;
   // List<SaleOrderObject> arrayList;
   // List<SaleOrderObject> arrayListSaleOrder = new ArrayList<SaleOrderObject>();
    AsyncHttpClient client;

    String sono;
    boolean checkUpdate = false;

    String frontJson = "{ \"sale_orders\": [";
    String backJson = "}]";
    String fullJson = "";

    String frontItemJson = "\"items\": [";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.checkorder_fragment_main_order_detail_status, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        sono = (String) bundle.getSerializable("sono");

        String Title = "Sale Order" + " " + sono;

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(Title);
        setHasOptionsMenu(true);

//        arrayList = new ArrayList<SaleOrderObject>();

  //      saleOrderListAdapter = new SaleOrderListAdapter(getContext(), arrayList);
    //    saleOrderListAdapter.setListViewItemClickListener(this);
        //mListView.setAdapter(saleOrderListAdapter);
        loadListSaleOrder();

        return view;
    }

    private void loadListSaleOrder() {

        if (client == null) client = new AsyncHttpClient(80,443);

        String user_code = SharedPreferenceHelper.getSharedPreferenceString(getContext(), sis.com.sis.sis_app.Constants.user_code, "");
        String listCheckStatusOrder = "";
        String json_list = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SALE_ORDER_LISTS, "");

        Bundle bundle = getArguments();
        sono = (String) bundle.getSerializable("sono");

        RequestParams rParams = new RequestParams();
        rParams.put("SiS", Constants.SIS_SECRET); //Constants.SIS_SECRET
        rParams.put("se", "100" + user_code);
        rParams.put("kw", sono);
        //rParams.put("password", password);

        Constants.doLog("LOG SHIPTO : " + rParams);

        Gson gson = new Gson();
//        ResponseResult responseResult = new ResponseResult();

        Constants.doLog("LOG SALE_ORDER_LISTS : " + new String(json_list));
        ResponseResult saleOrderResult = new ResponseResult();

        saleOrderResult = gson.fromJson(new String(json_list), ResponseResult.class);
        client.get(Constants.API_HOST + "MSOOrderListBySales.php?", rParams, new AsyncHttpResponseHandler() {

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
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                sis.com.sis.sis_app.CheckOrderStatus.Constants.doLog("LOG HISTORY : " + new String(responseBody));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (isAdded() && customProgress != null) customProgress.hideProgress();
                }
                Gson gson = new Gson();
                ResponseResult responseResult = new ResponseResult();

                if (isJSONValid(new String(responseBody))) {
                    responseResult = gson.fromJson(new String(responseBody), ResponseResult.class);
                    sis.com.sis.sis_app.CheckOrderStatus.Constants.doLog("LOG HISTORY : " + new String(responseBody));

                    if (responseResult.status_code == 200)
                    {
                        //sis.com.sis.sis_app.CheckOrderStatus.Constants.doLog("LOG HISTORY1 : " + responseResult.datas.get(0).docflow.get("dodate"));
                        String Custcodename = responseResult.datas.get(0).soldto + " / " + responseResult.datas.get(0).soldtoname;
                        String Shiptoname = responseResult.datas.get(0).shipto +" / " + responseResult.datas.get(0).shiptoname + " / " + responseResult.datas.get(0).shiptoaddr;
                        String SoDateTime = responseResult.datas.get(0).sodate + " " + responseResult.datas.get(0).sotime;
                        String DoDateTime = responseResult.datas.get(0).docflow.get("dodate") + " " + responseResult.datas.get(0).docflow.get("dotime");
                        String PickDateTime = responseResult.datas.get(0).docflow.get("pickdate") + " " + responseResult.datas.get(0).docflow.get("pictime");
                        String PGIDateTime = responseResult.datas.get(0).docflow.get("pgidate") + " " + responseResult.datas.get(0).docflow.get("pgitime");
                        String InvDateTime = responseResult.datas.get(0).docflow.get("invdate") + " " + responseResult.datas.get(0).docflow.get("invtime");

                        if (responseResult.datas.get(0).soldto.equals("")){
                            textViewCustomerCodeName.setText("-");
                        }else {
                            textViewCustomerCodeName.setText(Custcodename);
                        }

                        if (responseResult.datas.get(0).shipto.equals("")){
                            textViewShipToCodeName.setText("-");
                        }else {
                            textViewShipToCodeName.setText(Shiptoname);
                        }

                        if (responseResult.datas.get(0).custpono.equals("")){
                            textViewPONo.setText("-");
                            //textViewPODateTime.setText("-");
                        }else {
                            textViewPONo.setText(responseResult.datas.get(0).custpono);
                            //textViewPODateTime.setText(responseResult.datas.get(0).custpodate);
                        }

                        if (responseResult.datas.get(0).sono.equals("")){
                            textViewSoNo.setText("-");
                            textViewSoDateTime.setText("-");
                        }else {
                            textViewSoNo.setText(responseResult.datas.get(0).sono);
                            textViewSoDateTime.setText(SoDateTime);
                        }

                        if (responseResult.datas.get(0).docflow.get("dono").equals("")){
                            textViewDoNo.setText("-");
                            textViewDoDateTime.setText("-");
                        }else {
                            textViewDoNo.setText(responseResult.datas.get(0).docflow.get("dono"));
                            textViewDoDateTime.setText(DoDateTime);
                        }

                        if (responseResult.datas.get(0).docflow.get("pickno").equals("")){
                            textViewPkNo.setText("-");
                            textViewPkDateTime.setText("-");
                        }else {
                            textViewPkNo.setText(responseResult.datas.get(0).docflow.get("pickno"));
                            textViewPkDateTime.setText(PickDateTime);
                        }

                        if (responseResult.datas.get(0).docflow.get("pgino").equals("")){
                            textViewPGINo.setText("-");
                            textViewPGIDateTime.setText("-");
                        }else {
                            textViewPGINo.setText(responseResult.datas.get(0).docflow.get("pgino"));
                            textViewPGIDateTime.setText(PGIDateTime);
                        }

                        if (responseResult.datas.get(0).docflow.get("invno").equals("")){
                            textViewPGINo.setText("-");
                            textViewPGIDateTime.setText("-");
                        }else {
                            textViewInvNo.setText(responseResult.datas.get(0).docflow.get("invno"));
                            textViewInvDateTime.setText(InvDateTime);
                        }

                    }
                    else if (responseResult.status_code == 503)
                    {
                        if (isAdded() && rl_no_information != null) rl_no_information.show("No Item","No item in this Order",getResources().getDrawable(R.drawable.ic_cross));
                    }
                    else
                    {
                        if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), getResources().getString(R.string.message_contact_is));
                    }

                }
                else if (new String(responseBody).equals("Not Authorized or Invalid version!")){
                    GeneralHelper.getInstance().showUpdateAlert(getContext(),getResources().getString(R.string.message_update_version));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (isAdded() && customProgress != null) customProgress.hideProgress();
                }
                linearDetail.setVisibility(View.GONE);
                linearStatus.setVisibility(View.GONE);
                TopicDetail.setVisibility(View.GONE);
                textTopicStatus.setVisibility(View.GONE);
                scrollview.setVisibility(View.GONE);
                GeneralHelper.getInstance().showBasicAlert(getContext(), getResources().getString(R.string.message_cannot_connect_server));
                rl_no_information.show("เกิดข้อผิดพลาด", "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้ ณ ขณะนี้ กรุณาลองใหม่อีกครั้ง", getResources().getDrawable(R.drawable.ic_cross));

                //Intent myIntent = new Intent(getActivity(), sis.com.sis.sis_app.Main.Activities.MainActivity.class);
                //getActivity().startActivity(myIntent);
                //getActivity().finish();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Intent myIntent = new Intent(getActivity(), sis.com.sis.sis_app.Main.Activities.MainActivity.class);
                //getActivity().startActivity(myIntent);
                //getActivity().finish();
                getActivity().onBackPressed();
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

    public int dateDifference(Date startDate) {
        //milliseconds
        Date date = new Date();
        long different =  date.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ date.getTime());
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

        return (int) elapsedDays;
    }

}
