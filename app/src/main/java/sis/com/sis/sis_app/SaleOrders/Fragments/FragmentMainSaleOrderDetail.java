package sis.com.sis.sis_app.SaleOrders.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.fragment.app.Fragment;

import com.baoyz.actionsheet.ActionSheet;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import sis.com.sis.sis_app.Helpers.GeneralHelper;
import sis.com.sis.sis_app.Helpers.SharedPreferenceHelper;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.SaleOrders.Activities.MainActivity;
import sis.com.sis.sis_app.SaleOrders.Adapters.ItemAllListAdapter;
import sis.com.sis.sis_app.SaleOrders.Adapters.ItemListAdapter;
import sis.com.sis.sis_app.SaleOrders.Constants;
import sis.com.sis.sis_app.SaleOrders.Models.ArticleObject;
import sis.com.sis.sis_app.SaleOrders.Models.SaleOrderObject;
import sis.com.sis.sis_app.Views.CustomButton;
import sis.com.sis.sis_app.Views.CustomEditText;
import sis.com.sis.sis_app.Views.CustomTextView;
import sis.com.sis.sis_app.Views.ScrollDetectableListView;

import static sis.com.sis.sis_app.Views.CustomDialogLoading.customProgress;

public class FragmentMainSaleOrderDetail extends Fragment implements ActionSheet.ActionSheetListener, ItemAllListAdapter.ListViewItemClickListener {

    @BindView(R.id.recycleViewItems) ScrollDetectableListView recycleViewItems;
    @BindView(R.id.textViewOrderNo) CustomTextView textViewOrderNo;
    @BindView(R.id.textViewDate) CustomTextView textViewDate;
    @BindView(R.id.textViewCustomer) CustomTextView textViewCustomer;
    @BindView(R.id.textViewShipTo) CustomTextView textViewShipTo;
    @BindView(R.id.textViewTotalPrice) CustomTextView textViewTotalPrice;
    @BindView(R.id.textViewDiscount) CustomTextView textViewDiscount;
    @BindView(R.id.textViewVAT) CustomTextView textViewVAT;
    @BindView(R.id.textViewNetTotalPrice) CustomTextView textViewNetTotalPrice;
    @BindView(R.id.textViewPayment) CustomTextView textViewPayment;

    @BindView(R.id.btnConfirm) CustomButton btnConfirm;

//    @BindView(R.id.relativeLayoutRecycleView) RelativeLayout relativeLayoutRecycleView;
//
//    @BindView(R.id.radioGroupPaymentTerm) RadioGroup radioGroupPaymentTerm;
//    @BindView(R.id.radioButtonCreditTerm) RadioButton radioButtonCreditTerm;
//    @BindView(R.id.radioButtonCashTransfer) RadioButton radioButtonCashTransfer;
//    @BindView(R.id.checkboxPI) CheckBox checkboxPI;
//    @BindView(R.id.radioGroupCashTransfer) RadioGroup radioGroupCashTransfer;
//    @BindView(R.id.radioButtonUOB) RadioButton radioButtonUOB;
//    @BindView(R.id.radioButtonOther) RadioButton radioButtonOther;
//    @BindView(R.id.editTextPaymentTermUOB) CustomEditText editTextPaymentTermUOB;
//    @BindView(R.id.imageViewUploadSlip) ImageView imageViewUploadSlip;

    ItemAllListAdapter itemListAdapter;
    List<ArticleObject> arrayListItems;
    SaleOrderObject saleOrderObjectItem;

    List<String> allItemSelected = new CopyOnWriteArrayList<String>();

    AsyncHttpClient client;

    String frontJson = "{ \"sale_orders\": [";
    String backJson = "]}";
    String fullJson = "";


    String username = "";
    String soldToCode = "";
    String soldToName = "";
    String shipToCode = "";
    String shipToName = "";
    String saleOrderItem = "";
    int totalQty = 0;
    double AllTotalPrice = 0;

    RequestParams rParamsCreateOrder = new RequestParams();

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.saleorder_fragment_saleorder_detail, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Sale Order Detail");
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        saleOrderObjectItem = (SaleOrderObject) bundle.getSerializable("saleOrderObjectItem");

        if (saleOrderObjectItem.sap_so.equals("-") || saleOrderObjectItem.sap_so.isEmpty()){
            textViewOrderNo.setText(saleOrderObjectItem.mobile_so);
        }
        else {
            textViewOrderNo.setText(saleOrderObjectItem.sap_so);
        }

        textViewDate.setText(saleOrderObjectItem.date);
        textViewCustomer.setText(saleOrderObjectItem.customer_name);
        textViewShipTo.setText(saleOrderObjectItem.shipto_name);
        textViewTotalPrice.setText(saleOrderObjectItem.total_price + " THB");
//        textViewDiscount.setText(new DecimalFormat("#,###.00").format(Double.parseDouble(saleOrderObjectItem.total_price.replace(",",""))*0.005) + " THB");
        textViewVAT.setText(new DecimalFormat("#,###.00").format((Double.parseDouble(saleOrderObjectItem.total_price.replace(",","")) * Constants.VAT)) + " THB");
        textViewNetTotalPrice.setText(saleOrderObjectItem.net_total_price + " THB");

        if (saleOrderObjectItem.payment.equals("T001")){
            textViewPayment.setText("Cash Transfer");
            textViewPayment.setTextColor(R.color.colorGreen);
        }
        else {
            textViewPayment.setText("Credit Term");
            textViewPayment.setTextColor(R.color.colorYellow);
        }


        btnConfirm.setVisibility(View.GONE);


        username = SharedPreferenceHelper.getSharedPreferenceString(getContext(), sis.com.sis.sis_app.Constants.username, "");
        soldToCode = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SOLD_TO_CODE, "");
        soldToName = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SOLD_TO_NAME, "");
        shipToCode = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SHIP_TO_CODE, "");
        shipToName = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SHIP_TO_NAME, "");
        saleOrderItem = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SALE_ORDER_ITEMS, "");

        arrayListItems = new ArrayList<ArticleObject>();
        itemListAdapter = new ItemAllListAdapter(getContext(), arrayListItems);
        itemListAdapter.setListViewItemClickListener(this);
        recycleViewItems.setAdapter(itemListAdapter);
        loadItems();


        return view;
    }


    private void loadItems() {

//        String saleOrderItem = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SALE_ORDER_ITEMS, "");

//        if (client == null) client = new AsyncHttpClient(80,443);

        //Bundle bundle = getArguments();
        //String so_no = String.valueOf(bundle.getSerializable("so_no"));
//        String username = SharedPreferenceHelper.getSharedPreferenceString(getContext(), sis.com.sis.sis_app.Constants.username, "");
//
//        RequestParams rParams = new RequestParams();
//
//        rParams.put("SiS", Constants.SIS_SECRET);
//        rParams.put("u", username);
//        rParams.put("kw", saleOrderItem);


        Constants.doLog("LOG RESPONSE RESULT : " + saleOrderObjectItem.items.size());

        for (ArticleObject item : saleOrderObjectItem.items) {

            Constants.doLog("LOG RESPONSE RESULT : " + item.price);
            arrayListItems.add(item);
        }

        itemListAdapter.notifyDataSetChanged();

        int totalHeight = 0;

        for (int i = 0; i < itemListAdapter.getCount(); i++) {
            View mView = itemListAdapter.getView(i, null, recycleViewItems);

            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),

                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();
            Constants.doLog("LOG RESPONSE RESULT HEIGHT (" + i + ") : " + String.valueOf(totalHeight));
        }

        ViewGroup.LayoutParams params = recycleViewItems.getLayoutParams();

        if (itemListAdapter.getCount() < 4) {
            totalHeight += (50 + itemListAdapter.getCount() * 10);
        }
        else {
            totalHeight += (50 + itemListAdapter.getCount() * 15);
        }
        Constants.doLog("LOG RESPONSE RESULT HEIGHT (LAST) : " + String.valueOf(totalHeight));
        params.height = totalHeight;

        recycleViewItems.setLayoutParams(params);
        recycleViewItems.requestLayout();

//
//        client.get(Constants.API_HOST + "MSOSkuSearch.php?", rParams, new AsyncHttpResponseHandler() {
//
//                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                @Override
//                public void onStart() {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        if (customProgress == null) customProgress = CustomDialogLoading.getInstance();
//                        customProgress.showProgress(getContext(), "Loading", null, getContext().getDrawable(R.drawable.ic_loading), false, false, true);
//                    }
//                }
//
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, byte[] response)
//                {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        customProgress.hideProgress();
//                    }
//
//                    Constants.doLog("LOG RESPONSE RESULT : " + statusCode);
//                    Constants.doLog("LOG RESPONSE RESULT : " + new String(response));
//                    Gson gson = new Gson();
//                    ResponseResult responseResult = new ResponseResult();
//
//                    if (isJSONValid(new String(response))){
//                        responseResult = gson.fromJson(new String(response),ResponseResult.class);
//                    }
//
//                    if (responseResult.status_code == 200)
//                    {
//                        String[] allItems = saleOrderItem.split("\\|");
//
//                        for (ArticleObject item: responseResult.articles)
//                        {
//                            for (String s : allItems) {
//                                if (s.equals(item.sku)){
//                                    arrayListItems.add(item);
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                    else if (responseResult.status_code == 201) {
//                        textViewNoItems.setVisibility(View.VISIBLE);
//                        linearLayoutRecycleView.setVisibility(View.GONE);
//                    }
//                    else if (responseResult.status_code == 505) {
//                        textViewNoItems.setVisibility(View.VISIBLE);
//                        linearLayoutRecycleView.setVisibility(View.GONE);
//                    }
//                    else
//                    {
//                        if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), "Cannot do this action, Please contact IS.");
//                    }
//
//                    itemListAdapter.notifyDataSetChanged();
//
//                    int totalHeight = 0;
//
//                    for (int i = 0; i < itemListAdapter.getCount(); i++) {
//                        View mView = itemListAdapter.getView(i, null, recycleViewItems);
//
//                        mView.measure(
//                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//
//                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//
//                        totalHeight += mView.getMeasuredHeight();
//                        Constants.doLog("LOG RESPONSE RESULT HEIGHT ("+ i + ") : " + String.valueOf(totalHeight));
//                    }
//
//                    ViewGroup.LayoutParams params = recycleViewItems.getLayoutParams();
//
//                    if (itemListAdapter.getCount() < 4) {
//                        totalHeight += (100 + itemListAdapter.getCount() * 10);
//                    }
//                    else {
//                        totalHeight += (100 + itemListAdapter.getCount() * 15);
//                    }
//                    Constants.doLog("LOG RESPONSE RESULT HEIGHT (LAST) : " + String.valueOf(totalHeight));
//                    params.height = totalHeight;
//
//                    recycleViewItems.setLayoutParams(params);
//                    recycleViewItems.requestLayout();
//
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e)
//                {
////                    GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_cannot_connect_server));
////                    Intent myIntent = new Intent(getActivity(), MainActivity.class);
////                    getActivity().startActivity(myIntent);
////                    getActivity().finish();
//                }
//
//
//                @Override
//                public void onRetry(int retryNo) {
//                    // called when request is retried
//                }
//            });
    }

//    public boolean isJSONValid(String test) {
//        try {
//            new JSONObject(test);
//        } catch (JSONException ex) {
//            // edited, to include @Arthur's comment
//            // e.g. in case JSONArray is valid as well...
//            try {
//                new JSONArray(test);
//            } catch (JSONException ex1) {
//                return false;
//            }
//        }
//        return true;
//    }

//    @OnCheckedChanged(R.id.radioButtonCreditTerm)
//    public void radioButtonCreditTermChange(RadioButton radioButton)
//    {
//        if (!radioButtonCreditTerm.isChecked()){
//            radioButtonUOB.setEnabled(true);
//            radioButtonOther.setEnabled(true);
//        }
//        else {
//            radioButtonUOB.setEnabled(false);
//            radioButtonOther.setEnabled(false);
//            editTextPaymentTermUOB.setEnabled(false);
//        }
//    }
//
//    @OnCheckedChanged(R.id.radioButtonCashTransfer)
//    public void radioButtonCashTransferChange(RadioButton radioButton)
//    {
//        if (radioButtonCashTransfer.isChecked()){
//            radioButtonUOB.setEnabled(true);
//            radioButtonOther.setEnabled(true);
//        }
//        else {
//            radioButtonUOB.setEnabled(false);
//            radioButtonOther.setEnabled(false);
//            editTextPaymentTermUOB.setEnabled(false);
//        }
//    }
//
//    @OnCheckedChanged(R.id.radioButtonUOB)
//    public void radioButtonUOBChange(RadioButton radioButton)
//    {
//        Date date = new Date();
//        if (radioButtonUOB.isChecked()){
//            editTextPaymentTermUOB.setEnabled(true);
//            editTextPaymentTermUOB.setText("ยูโอบี ยอด " + new DecimalFormat("#,###.00").format(AllTotalPrice) + " บาท วันที่ " + DateFormat.format("dd/MM/yyyy",   date));
//        }
//        else {
//            editTextPaymentTermUOB.setEnabled(false);
//        }
//    }
//    @OnClick(R.id.imageViewUploadSlip)
//    public void imageViewUploadSlipPressed(ImageView layout)
//    {
//        if (radioButtonOther.isChecked()) {
//            ActionSheet.createBuilder(getContext(), getActivity().getSupportFragmentManager())
//                    .setCancelButtonTitle("Cancel")
//                    .setOtherButtonTitles("Photo Library", "Camera")
//                    .setCancelableOnTouchOutside(true)
//                    .setListener(this).show();
//        }
//    }
//
//    @OnClick(R.id.btnCreateSaleOrder)
//    public void btnCreateSaleOrderPressed(CustomButton button)
//    {
//        Date date = new Date();
//        rParamsCreateOrder.setForceMultipartEntityContentType(true);
//        rParamsCreateOrder.put("SiS", Constants.SIS_SECRET);
//        rParamsCreateOrder.put("u", username);
//        rParamsCreateOrder.put("sdt", soldToCode);
//        if (shipToCode.equals("")) {
//            shipToCode = soldToCode;
//        }
//        rParamsCreateOrder.put("sht", shipToCode);
//        rParamsCreateOrder.put("derb", "Z2");
//        if (editTextMsg.getText().toString().equals("")){
//            rParamsCreateOrder.put("tmsg", "-");
//        }
//        else {
//            rParamsCreateOrder.put("tmsg", editTextMsg.getText().toString());
//        }
//        rParamsCreateOrder.put("apiodr", username + "_" + DateFormat.format("dd",   date) + DateFormat.format("MM",   date) + DateFormat.format("yyyy",   date) + DateFormat.format("HH",   date) + DateFormat.format("mm",   date));
//        rParamsCreateOrder.put("icount", arrayListItems.size());
//        rParamsCreateOrder.put("chksum", totalQty);
//
//        if (checkboxPI.isChecked()){
//            rParamsCreateOrder.put("reqpi", "X");
////            Constants.doLog("LOG VALUE CREATE SALE ORDER reqpi : X");
//        }
//        else {
//            rParamsCreateOrder.put("reqpi", "-");
//        }
//        if (radioButtonCashTransfer.isChecked()) {
//            rParamsCreateOrder.put("pmt", "T001");
////            Constants.doLog("LOG VALUE CREATE SALE ORDER pmt : T001");
//
//            if (radioButtonUOB.isChecked()){
//                rParamsCreateOrder.put("ivtxt", editTextPaymentTermUOB.getText().toString());
////            Constants.doLog("LOG VALUE CREATE SALE ORDER ivtxt : " + editTextPaymentTermUOB.getText().toString());
//            }
//            else if (radioButtonOther.isChecked()){
//                rParamsCreateOrder.put("ivtxt", "-");
////            Constants.doLog("LOG VALUE CREATE SALE ORDER imageUploadName : " + username + "_" + date.getDate() + date.getMonth() + date.getYear() + date.getHours() + date.getMinutes());
//            }
//            else {
//                GeneralHelper.getInstance().showBasicAlert(getContext(), "Please select Payment Term");
//                return;
//            }
//        }
//        else if (radioButtonCreditTerm.isChecked())  {
//            rParamsCreateOrder.put("pmt", "");
//        }
//        else {
//            GeneralHelper.getInstance().showBasicAlert(getContext(), "Please select Payment Term");
//            return;
//        }
//
//        if (username.isEmpty())
//        {
//            GeneralHelper.getInstance().showBasicAlert(getContext(), "Username");
//            return;
//        }
//        if (soldToCode.isEmpty())
//        {
//            GeneralHelper.getInstance().showBasicAlert(getContext(),"Sold To is empty, please insert.");
//            return;
//        }
//        if (shipToCode.isEmpty())
//        {
//            GeneralHelper.getInstance().showBasicAlert(getContext(),"Ship To is empty, please insert.");
//            return;
//        }
//        if (arrayListItems.size() == 0)
//        {
//            GeneralHelper.getInstance().showBasicAlert(getContext(),"No Items and Quantity, please click calculate order before submit and create Sale Order.");
//            return;
//        }
//        if (totalQty == 0)
//        {
//            GeneralHelper.getInstance().showBasicAlert(getContext(),"No Items and Quantity, please click calculate order before submit and create Sale Order.");
//            return;
//        }
//
//        if (allItemSelected.size() != 0) {
//            for (String item: allItemSelected) {
//                for (int i = 0; i < arrayListItems.size(); i++) {
//                    if (item.equals(arrayListItems.get(i).sku)) {
//                        rParamsCreateOrder.put("isku_"+ (i+1), arrayListItems.get(i).sku);
//                        rParamsCreateOrder.put("iqty_"+ (i+1), arrayListItems.get(i).qty);
//                        rParamsCreateOrder.put("ipri_"+ (i+1), arrayListItems.get(i).price);
//                    }
//                }
//            }
//        }
//        else {
//            GeneralHelper.getInstance().showBasicAlert(getContext(),"No article selected in your list.");
//        }
//        Constants.doLog("LOG ALL rparams : " + rParamsCreateOrder.toString());
//
//        client.post(Constants.API_HOST + "MSOOrderCreate.php", rParamsCreateOrder, new AsyncHttpResponseHandler() {
//
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onStart() {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    if (customProgress == null) customProgress = CustomDialogLoading.getInstance();
//                    customProgress.showProgress(getContext(), "Loading", null, getContext().getDrawable(R.drawable.ic_loading), false, false, true);
//                }
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] response)
//            {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    customProgress.hideProgress();
//                }
//
//                Constants.doLog("LOG RESPONSE RESULT : " + statusCode);
//                Constants.doLog("LOG RESPONSE RESULT : " + new String(response));
//                Gson gson = new Gson();
//                ResponseResult responseResult = new ResponseResult();
//
//                if (isJSONValid(new String(response))){
//                    responseResult = gson.fromJson(new String(response),ResponseResult.class);
//                }
//
//                if (responseResult.status_code == 200)
//                {
//                    if (radioButtonOther.isChecked()){
//                        client.post(Constants.API_HOST + "MSOUpload.php?", rParamsCreateOrder, new AsyncHttpResponseHandler() {
//
//                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                            @Override
//                            public void onStart() {
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                    if (customProgress == null) customProgress = CustomDialogLoading.getInstance();
//                                    customProgress.showProgress(getContext(), "Loading", null, getContext().getDrawable(R.drawable.ic_loading), false, false, true);
//                                }
//                            }
//
//                            @Override
//                            public void onSuccess(int statusCode, Header[] headers, byte[] response)
//                            {
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                    customProgress.hideProgress();
//                                }
//
//                                Constants.doLog("LOG RESPONSE RESULT : " + statusCode);
//                                Constants.doLog("LOG RESPONSE RESULT : " + new String(response));
//                                Gson gson = new Gson();
//                                ResponseResult responseResult = new ResponseResult();
//
//                                if (isJSONValid(new String(response))){
//                                    responseResult = gson.fromJson(new String(response),ResponseResult.class);
//                                }
//
//                                if (responseResult.status_code == 200)
//                                {
//                                    fullJson = SharedPreferenceHelper.getSharedPreferenceString(getContext(),Constants.SALE_ORDER_LISTS,"");
//                                    fullJson = fullJson.replace(frontJson,"");
//                                    fullJson = fullJson.replace(backJson,"");
//
//                                    if (fullJson.equals("")) {
//                                        fullJson = frontJson + "{\"customer_name\": \"" + soldToName +"\", \"customer_code\": \"" + soldToCode + "\", \"mobile_so\": \"" + username + "_" + DateFormat.format("dd",   date) + DateFormat.format("MM",   date) + DateFormat.format("yyyy",   date) + DateFormat.format("HH",   date) + DateFormat.format("mm",   date) + "\", \"sap_so\": \"-\", \"sap_do\": \"-\",\"sap_inv\": \"-\", \"total_price\": \"" + new DecimalFormat("#,###.00").format(AllTotalPrice) + "\", \"date\": \"" + DateFormat.format("dd/MM/yyyy",   date) + "\", \"status\": \"-\"}" + backJson;
//                                    }
//                                    else {
//                                        fullJson = frontJson + fullJson + ", {\"customer_name\": \"" + soldToName +"\", \"customer_code\": \"" + soldToCode + "\", \"mobile_so\": \"" + username + "_" + DateFormat.format("dd",   date) + DateFormat.format("MM",   date) + DateFormat.format("yyyy",   date) + DateFormat.format("HH",   date) + DateFormat.format("mm",   date) + "\", \"sap_so\": \"-\", \"sap_do\": \"-\",\"sap_inv\": \"-\", \"total_price\": \"" + new DecimalFormat("#,###.00").format(AllTotalPrice) + "\", \"date\": \"" + DateFormat.format("dd/MM/yyyy",   date) + "\", \"status\": \"-\"}" + backJson;
//                                    }
//                                    Constants.doLog("LOG JSON KEEP LOCAL : " + fullJson);
//                                    SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SALE_ORDER_LISTS, fullJson);
//                                    if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), "Create Sale Order successfully.");
////                                GeneralHelper.getInstance().showBasicAlert(getContext(),responseResult.status_msg);
//                                    Intent myIntent = new Intent(getActivity(), MainActivity.class);
//                                    getActivity().startActivity(myIntent);
//                                    getActivity().finish();
//                                }
//                                else if (responseResult.status_code == 508) {
//                                    if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), responseResult.status_msg);
//                                }
//                                else
//                                {
//                                    if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), "Cannot do this action, Please contact IS.");
//                                }
//
//                                itemListAdapter.notifyDataSetChanged();
//
//                                int totalHeight = 0;
//
//                                for (int i = 0; i < itemListAdapter.getCount(); i++) {
//                                    View mView = itemListAdapter.getView(i, null, recycleViewItems);
//
//                                    mView.measure(
//                                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//
//                                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//
//                                    totalHeight += mView.getMeasuredHeight();
//                                    Constants.doLog("LOG RESPONSE RESULT HEIGHT ("+ i + ") : " + String.valueOf(totalHeight));
//                                }
//
//                                ViewGroup.LayoutParams params = recycleViewItems.getLayoutParams();
//
//                                if (itemListAdapter.getCount() < 4) {
//                                    totalHeight += (100 + itemListAdapter.getCount() * 10);
//                                }
//                                else {
//                                    totalHeight += (100 + itemListAdapter.getCount() * 15);
//                                }
//                                Constants.doLog("LOG RESPONSE RESULT HEIGHT (LAST) : " + String.valueOf(totalHeight));
//                                params.height = totalHeight;
//
//                                recycleViewItems.setLayoutParams(params);
//                                recycleViewItems.requestLayout();
//
//                            }
//
//                            @Override
//                            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e)
//                            {
////                    GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_cannot_connect_server));
////                    Intent myIntent = new Intent(getActivity(), MainActivity.class);
////                    getActivity().startActivity(myIntent);
////                    getActivity().finish();
//                            }
//
//
//                            @Override
//                            public void onRetry(int retryNo) {
//                                // called when request is retried
//                            }
//                        });
//                    }
//                    else {
//                        fullJson = SharedPreferenceHelper.getSharedPreferenceString(getContext(),Constants.SALE_ORDER_LISTS,"");
//                        fullJson = fullJson.replace(frontJson,"");
//                        fullJson = fullJson.replace(backJson,"");
//
//                        if (fullJson.equals("")) {
//                            fullJson = frontJson + "{\"customer_name\": \"" + soldToName +"\", \"customer_code\": \"" + soldToCode + "\", \"mobile_so\": \"" + username + "_" + DateFormat.format("dd",   date) + DateFormat.format("MM",   date) + DateFormat.format("yyyy",   date) + DateFormat.format("HH",   date) + DateFormat.format("mm",   date) + "\", \"sap_so\": \"-\", \"sap_do\": \"-\",\"sap_inv\": \"-\", \"total_price\": \"" + new DecimalFormat("#,###.00").format(AllTotalPrice) + "\", \"date\": \"" + DateFormat.format("dd/MM/yyyy",   date) + "\", \"status\": \"-\"}" + backJson;
//                        }
//                        else {
//                            fullJson = frontJson + fullJson + ", {\"customer_name\": \"" + soldToName +"\", \"customer_code\": \"" + soldToCode + "\", \"mobile_so\": \"" + username + "_" + DateFormat.format("dd",   date) + DateFormat.format("MM",   date) + DateFormat.format("yyyy",   date) + DateFormat.format("HH",   date) + DateFormat.format("mm",   date) + "\", \"sap_so\": \"-\", \"sap_do\": \"-\",\"sap_inv\": \"-\", \"total_price\": \"" + new DecimalFormat("#,###.00").format(AllTotalPrice) + "\", \"date\": \"" + DateFormat.format("dd/MM/yyyy",   date) + "\", \"status\": \"-\"}" + backJson;
//                        }
//                        Constants.doLog("LOG JSON KEEP LOCAL : " + fullJson);
//                        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SALE_ORDER_LISTS, fullJson);
//                        if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), "Create Sale Order successfully.");
////                                GeneralHelper.getInstance().showBasicAlert(getContext(),responseResult.status_msg);
//                        Intent myIntent = new Intent(getActivity(), MainActivity.class);
//                        getActivity().startActivity(myIntent);
//                        getActivity().finish();
//                    }
//                }
//                else if (responseResult.status_code == 401) {
//                    if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(),responseResult.status_msg);
//                }
//                else if (responseResult.status_code == 501) {
//                    if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(),responseResult.status_msg);
//                }
//                else
//                {
//                    if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), "Cannot do this action, Please contact IS.");
//                }
//
//                itemListAdapter.notifyDataSetChanged();
//
//                int totalHeight = 0;
//
//                for (int i = 0; i < itemListAdapter.getCount(); i++) {
//                    View mView = itemListAdapter.getView(i, null, recycleViewItems);
//
//                    mView.measure(
//                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//
//                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//
//                    totalHeight += mView.getMeasuredHeight();
//                    Constants.doLog("LOG RESPONSE RESULT HEIGHT ("+ i + ") : " + String.valueOf(totalHeight));
//                }
//
//                ViewGroup.LayoutParams params = recycleViewItems.getLayoutParams();
//
//                if (itemListAdapter.getCount() < 4) {
//                    totalHeight += (100 + itemListAdapter.getCount() * 10);
//                }
//                else {
//                    totalHeight += (100 + itemListAdapter.getCount() * 15);
//                }
//                Constants.doLog("LOG RESPONSE RESULT HEIGHT (LAST) : " + String.valueOf(totalHeight));
//                params.height = totalHeight;
//
//                recycleViewItems.setLayoutParams(params);
//                recycleViewItems.requestLayout();
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e)
//            {
////                    GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_cannot_connect_server));
////                    Intent myIntent = new Intent(getActivity(), MainActivity.class);
////                    getActivity().startActivity(myIntent);
////                    getActivity().finish();
//            }
//
//
//            @Override
//            public void onRetry(int retryNo) {
//                // called when request is retried
//            }
//        });
//
//
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentMainSaleOrder fragment = new FragmentMainSaleOrder();
                ((MainActivity)getActivity()).replaceFragment(fragment, true);
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

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index)
    {
        if (index == 0) // 0 means photo library
        {
            ImagePicker.create(this)
                    .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
                    .folderMode(true) // folder mode (false by default)
                    .toolbarFolderTitle("Folder") // folder selection title
                    .toolbarImageTitle("Tap to select") // image selection title
                    .includeVideo(false) // Show video on image picker
                    .single() // single mode
                    .showCamera(true) // show camera or not (true by default)
                    .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                    .enableLog(false) // disabling log
                    .start(); // start image picker activity with request code


        }
        if (index == 1) // 1 means camera
        {
            ImagePicker.cameraOnly().start(this); // start image picker activity with request code
        }
    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data))
        {

            // or get a single image only
            Image image = ImagePicker.getFirstImageOrNull(data);
//            uploadAvatar(image);
            File myFile = new File(image.getPath());
            try
            {
                rParamsCreateOrder.put("userfile", myFile);
            } catch(FileNotFoundException e)
            {
                Constants.doLog("file not found");
                e.printStackTrace();
            }
//            PicassoTrustAll.getInstance(getContext()).load(new File(image.getPath())).into(imageViewUploadSlip);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void listViewDidScrollToEnd() {

    }
}
