package sis.com.sis.sis_app.SaleOrders.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import sis.com.sis.sis_app.Helpers.GeneralHelper;
import sis.com.sis.sis_app.Helpers.SharedPreferenceHelper;
import sis.com.sis.sis_app.Main.Activities.AuthActivity;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.SaleOrders.Activities.MainActivity;
import sis.com.sis.sis_app.SaleOrders.Adapters.SaleOrderListAdapter;
import sis.com.sis.sis_app.SaleOrders.Constants;
import sis.com.sis.sis_app.SaleOrders.Models.ArticleObject;
import sis.com.sis.sis_app.SaleOrders.Models.CheckStatusObject;
import sis.com.sis.sis_app.SaleOrders.Models.ResponseResult;
import sis.com.sis.sis_app.SaleOrders.Models.SaleOrderObject;
import sis.com.sis.sis_app.Views.CustomDialogLoading;
import sis.com.sis.sis_app.Views.CustomMessageRelativeLayout;
import sis.com.sis.sis_app.Views.ScrollDetectableListView;

import static sis.com.sis.sis_app.Views.CustomDialogLoading.customProgress;

public class FragmentMainSaleOrder extends Fragment implements SaleOrderListAdapter.ListViewItemClickListener {

    @BindView(R.id.rl_no_information) CustomMessageRelativeLayout rl_no_info_view;
    @BindView(R.id.recycleViewShipToApprove) ScrollDetectableListView mListView;
    @BindView(R.id.btnCreateSaleOrder) ImageButton btnCreateSaleOrder;
    SaleOrderListAdapter saleOrderListAdapter;
    List<SaleOrderObject> arrayList;
    List<SaleOrderObject> arrayListSaleOrder = new ArrayList<SaleOrderObject>();
    AsyncHttpClient client;

    boolean checkUpdate = false;

    String frontJson = "{ \"sale_orders\": [";
    String backJson = "}]";
    String fullJson = "";

    String frontItemJson = "\"items\": [";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.saleorder_fragment_main_list_saleorder, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.main_title_sale_order);
        setHasOptionsMenu(true);

        arrayList = new ArrayList<SaleOrderObject>();

        saleOrderListAdapter = new SaleOrderListAdapter(getContext(), arrayList);
        saleOrderListAdapter.setListViewItemClickListener(this);
        mListView.setAdapter(saleOrderListAdapter);
        loadListSaleOrder();

        return view;
    }

    private void loadListSaleOrder() {

        if (client == null) client = new AsyncHttpClient(80,443);

        String username = SharedPreferenceHelper.getSharedPreferenceString(getContext(), sis.com.sis.sis_app.Constants.username, "");
        String listCheckStatusOrder = "";
        String json_list = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SALE_ORDER_LISTS, "");
//        String json_list = "{ \"sale_orders\": [{\"customer_name\": \"เอสไอเอส ดิสทริบิวชั่น(ประเทศไทย)\", \"customer_code\": \"1100005532\", \"shipto_name\": \"เอสไอเอส ดิสทริบิวชั่น(ประเทศไทย)\", \"shipto_code\": \"1100005532\", \"mobile_so\": \"pornchai_202104231054\", \"sap_so\": \"1210316032\", \"sap_do\": \"-\",\"sap_inv\": \"-\", \"total_price\": \"11,100.00\", \"net_total_price\": \"11,817.62\", \"date\": \"23/04/2021\", \"status\": \"226.2\", \"payment\": \"credit\", \"items\": [{\"sku\": \"APC-AP7920\", \"name\": \"APC Rack PDU, Switched, 1U, 10A, 208/230\", \"qty\": \"111\", \"price\": \"100\"}]}, {\"customer_name\": \"วันโอวัน โกลเบิ้ล\", \"customer_code\": \"1100003759\", \"shipto_name\": \"วันโอวัน โกลเบิ้ล\", \"shipto_code\": \"9900000686\", \"mobile_so\": \"pornchai_202104231205\", \"sap_so\": \"-\", \"sap_do\": \"-\",\"sap_inv\": \"-\", \"total_price\": \".00\", \"net_total_price\": \".00\", \"date\": \"23/04/2021\", \"status\": \"-\", \"payment\": \"T001\", \"items\": [{\"sku\": \"APC-AP7553-RACK\", \"name\": \"Rack PDU, 0U, 32A, (20)C13 & (4)C19\", \"qty\": \"1\", \"price\": \"0.00\"}, {\"sku\": \"APC-AP7920\", \"name\": \"APC Rack PDU, Switched, 1U, 10A, 208/230\", \"qty\": \"1\", \"price\": \"0.00\"}, {\"sku\": \"WIK-6943279409843B\", \"name\": \"WIKO ROBBY 5.5inch HD QC1.3 16+1GB 8+5MP GD\", \"qty\": \"1\", \"price\": \"0.00\"}]}]}";
//        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SALE_ORDER_LISTS, json_list);
        //String password = SharedPreferenceHelper.getSharedPreferenceString(getContext(), "password", "");

        RequestParams rParams = new RequestParams();
        rParams.put("SiS", Constants.SIS_SECRET);
        rParams.put("u", username);
        //rParams.put("password", password);

        Constants.doLog("LOG SHIPTO : " + rParams);

        Gson gson = new Gson();
//        ResponseResult responseResult = new ResponseResult();

        Constants.doLog("LOG SALE_ORDER_LISTS : " + new String(json_list));
        ResponseResult saleOrderResult = new ResponseResult();

        saleOrderResult = gson.fromJson(new String(json_list), ResponseResult.class);
        if (saleOrderResult != null) {
            int currOrder = 1;
            for (SaleOrderObject item: saleOrderResult.sale_orders) {

                try {
                    Date createdDate = new SimpleDateFormat("dd/MM/yyyy").parse(item.date);
                    if (dateDifference(createdDate) >= 2){
                        checkUpdate = true;
                    }
                    else {

                        if (currOrder != saleOrderResult.sale_orders.size()){
                            listCheckStatusOrder = listCheckStatusOrder + item.mobile_so + "|";

                        }
                        else {
                            listCheckStatusOrder = listCheckStatusOrder + item.mobile_so;
                        }
                        arrayListSaleOrder.add(item);

                        Constants.doLog("LOG SHIPTO : " + arrayListSaleOrder.size());
                        Constants.doLog("LOG SHIPTO : " + listCheckStatusOrder);

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                currOrder++;
            }
        }

        rParams.put("mobodr", listCheckStatusOrder);
        client.get(Constants.API_HOST + "MSOOrderCheck.php?", rParams, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response)
            {
                Constants.doLog("LOG SHIPTO : " + new String(response));
                Constants.doLog("LOG SHIPTO : " + checkUpdate);

                Gson gson = new Gson();
                ResponseResult responseResultCheckStatus = new ResponseResult();

                if (isJSONValid(new String(response))){
                    responseResultCheckStatus = gson.fromJson(new String(response),ResponseResult.class);
                }

                Constants.doLog("LOG SHIPTO : " + responseResultCheckStatus.status_code);
                Constants.doLog("LOG SHIPTO : " + responseResultCheckStatus.data);

                ResponseResult saleOrderResult = new ResponseResult();
                saleOrderResult = gson.fromJson(new String(json_list), ResponseResult.class);

                if (responseResultCheckStatus.status_code == 200)
                {
                    for (SaleOrderObject item: saleOrderResult.sale_orders)
                    {

                        Constants.doLog("LOG SHIPTO : " + item.items);
                        for (CheckStatusObject listItem: responseResultCheckStatus.data)
                        {
                            if (listItem.order.equals(item.mobile_so)) {
                                String fullItemJson = "";
                                for (ArticleObject article: item.items) {
                                    if (fullItemJson.equals("")){
                                        fullItemJson = fullItemJson + "{\"sku\": \"" + article.sku +"\", \"name\": \"" + article.name.replace("\"","inch") + "\", \"qty\": \"" + article.qty + "\", \"price\": \"" + article.price + "\", \"discount\": \"" + article.discount + "\"}";
                                    }
                                    else {
                                        fullItemJson = fullItemJson + ", {\"sku\": \"" + article.sku +"\", \"name\": \"" + article.name.replace("\"","inch") + "\", \"qty\": \"" + article.qty + "\", \"price\": \"" + article.price + "\", \"discount\": \"" + article.discount + "\"}";
                                    }
                                }
                                fullItemJson = frontItemJson + fullItemJson + "]";

                                if (listItem.SAP_Order == null) {
                                    item.sap_so = "-";
                                    item.status = listItem.code;
                                }
                                else if (listItem.SAP_Order.equals("") || listItem.SAP_Order.equals(" ")){
                                    item.sap_so = "-";
                                    item.status = listItem.code;
                                }
                                else {
                                    item.sap_so = listItem.SAP_Order;
                                    item.status = listItem.code;
                                }
                                if (listItem.SAP_Delivery == null) {
                                    item.sap_do = "-";
                                }
                                else if (listItem.SAP_Delivery.equals("") || listItem.SAP_Delivery.equals(" ")){
                                    item.sap_do = "-";
                                }
                                else {
                                    item.sap_do = listItem.SAP_Delivery;
                                    item.status = listItem.code;
                                }
                                if (listItem.SAP_Invoice == null) {
                                    item.sap_inv = "-";
                                }
                                else if (listItem.SAP_Invoice.equals("") || listItem.SAP_Invoice.equals(" ")){
                                    item.sap_inv = "-";
                                }
                                else {
                                    item.sap_inv = listItem.SAP_Invoice;
                                    item.status = listItem.code;
                                }

                                if (fullJson.equals("")) {
                                    fullJson = frontJson + "{\"customer_name\": \"" + item.customer_name +"\", \"customer_code\": \"" + item.customer_code + "\", \"shipto_name\": \"" + item.shipto_name + "\", \"shipto_code\": \"" + item.shipto_code + "\", \"mobile_so\": \"" + item.mobile_so + "\", \"sap_so\": \"" + item.sap_so + "\", \"sap_do\": \"" + item.sap_do + "\",\"sap_inv\": \"" + item.sap_inv + "\", \"total_price\": \"" + item.total_price + "\", \"net_total_price\": \"" + item.net_total_price + "\", \"date\": \"" + item.date + "\", \"status\": \"" + item.status + "\", \"payment\": \"" + item.payment + "\", " + fullItemJson + backJson + "}";
                                }
                                else {
                                    StringBuilder builder = new StringBuilder(fullJson);
                                    builder.replace(builder.length()-2, builder.length(), "");
                                    fullJson = builder.toString();
                                    fullJson = fullJson + ", {\"customer_name\": \"" + item.customer_name +"\", \"customer_code\": \"" + item.customer_code + "\", \"shipto_name\": \"" + item.shipto_name + "\", \"shipto_code\": \"" + item.shipto_code + "\", \"mobile_so\": \"" + item.mobile_so + "\", \"sap_so\": \"" + item.sap_so + "\", \"sap_do\": \"" + item.sap_do + "\",\"sap_inv\": \"" + item.sap_inv + "\", \"total_price\": \"" + item.total_price + "\", \"net_total_price\": \"" + item.net_total_price + "\", \"date\": \"" + item.date + "\", \"status\": \"" + item.status + "\", \"payment\": \"" + item.payment + "\", " + fullItemJson + backJson + "}";
                                }

                                arrayList.add(item);
                            }
                        }
                    }
                    if (getContext() != null){
                        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SALE_ORDER_LISTS, fullJson);
                    }
                    Constants.doLog("LOG NEW LOCAL JSON : " + fullJson);
                    fullJson = "";
                    saleOrderListAdapter.notifyDataSetChanged();
                    Constants.doLog("Item was clicked3 " + arrayList.size());
                }
                if (arrayList.size() == 0)
                {
                    if (isAdded() && rl_no_info_view != null) rl_no_info_view.show(getResources().getString(R.string.ship_to_no_order),"No Sale Order created since yesterday.",getResources().getDrawable(R.drawable.ic_cross));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e)
            {

            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

        });



//            arrayList.add(item);
//        Constants.doLog("Item was clicked3 " + arrayListCheckStatusObjects.size());
//

//        Constants.doLog("Item was clicked " + checkUpdate);
//        Constants.doLog("Item was clicked2 " + code);
//        if (checkUpdate) {
//            for (SaleOrderObject item : saleOrderResult.sale_orders) {
//                fullJson = frontJson + "{\"customer_name\": \"" + item.customer_name +"\", \"customer_code\": \"" + item.customer_code + "\", \"shipto_name\": \"" + item.shipto_name + "\", \"shipto_code\": \"" + item.shipto_code + "\", \"mobile_so\": \"" + item.mobile_so + "\", \"sap_so\": \"-\", \"sap_do\": \"-\",\"sap_inv\": \"-\", \"total_price\": \"" + item.total_price + "\", \"net_total_price\": \""+ item.net_total_price + "\", \"date\": \"" + item.date + "\", \"status\": \"-\"}" + backJson;
//                Constants.doLog("Item was clicked " + fullJson);
//                SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SALE_ORDER_LISTS, fullJson);
//            }
//        }



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
    public void onItemClick(View view, int position) {
        SaleOrderObject saleOrderObjectItem = arrayList.get(position);

        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SOLD_TO_CODE, "");
        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SOLD_TO_NAME, "");
        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SHIP_TO_CODE, "");
        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SHIP_TO_NAME, "");
        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SALE_ORDER_ITEMS, "");
        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.PAYMENT_TERM, "");

        Constants.doLog("Item was clicked" + saleOrderObjectItem.mobile_so);

        FragmentMainSaleOrderDetail fragment = new FragmentMainSaleOrderDetail();
        Bundle bundle = new Bundle();
        bundle.putSerializable("saleOrderObjectItem",saleOrderObjectItem);
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

    @OnClick(R.id.btnCreateSaleOrder)
    public void btnCreateSaleOrder(ImageButton button)
    {
        FragmentMainSaleOrderCreate fragment = new FragmentMainSaleOrderCreate();
        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SOLD_TO_CODE, "");
        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SOLD_TO_NAME, "");
        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SHIP_TO_CODE, "");
        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SHIP_TO_NAME, "");
        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SALE_ORDER_ITEMS, "");
        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.PAYMENT_TERM, "");
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("shipToObjectItem",saleOrderObjectItem);
//        bundle.putSerializable("so_no",saleOrderObjectItem.so_no);
//        fragment.setArguments(bundle);
        ((MainActivity)getActivity()).replaceFragment(fragment, true);

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
