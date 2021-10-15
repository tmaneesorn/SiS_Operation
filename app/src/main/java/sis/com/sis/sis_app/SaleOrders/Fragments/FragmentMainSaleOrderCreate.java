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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.baoyz.actionsheet.ActionSheet;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import sis.com.sis.sis_app.Helpers.GeneralHelper;
import sis.com.sis.sis_app.Helpers.PicassoTrustAll;
import sis.com.sis.sis_app.Helpers.SharedPreferenceHelper;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.SaleOrders.Activities.MainActivity;
import sis.com.sis.sis_app.SaleOrders.Adapters.ItemListAdapter;
import sis.com.sis.sis_app.SaleOrders.Constants;
import sis.com.sis.sis_app.SaleOrders.Models.ArticleObject;
import sis.com.sis.sis_app.SaleOrders.Models.ResponseResult;
import sis.com.sis.sis_app.ShipToApproval.Fragments.FragmentMainShipToApprove;
import sis.com.sis.sis_app.Views.CustomButton;
import sis.com.sis.sis_app.Views.CustomDialogLoading;
import sis.com.sis.sis_app.Views.CustomEditText;
import sis.com.sis.sis_app.Views.CustomTextView;
import sis.com.sis.sis_app.Views.ScrollDetectableListView;

import static sis.com.sis.sis_app.Views.CustomDialogLoading.customProgress;

public class FragmentMainSaleOrderCreate extends Fragment implements ActionSheet.ActionSheetListener,ItemListAdapter.ListViewItemClickListener {

    @BindView(R.id.imageButtonSearchSoldTo) ImageButton imageButtonSearchSoldTo;
    @BindView(R.id.imageButtonSearchShipTo) ImageButton imageButtonSearchShipTo;
    @BindView(R.id.imageButtonAddItem) ImageButton imageButtonAddItem;
    @BindView(R.id.imageButtonCheckStock) ImageButton imageButtonCheckStock;
    @BindView(R.id.imageButtonDelete) ImageButton imageButtonDelete;
    @BindView(R.id.btnCreateSaleOrder) CustomButton btnCreateSaleOrder;

    @BindView(R.id.editTextSoldToTitle) CustomEditText editTextSoldToTitle;
    @BindView(R.id.relativeLayoutSoldTo) RelativeLayout relativeLayoutSoldTo;
    @BindView(R.id.textViewCustomerNameTitle) CustomTextView textViewCustomerNameTitle;
    @BindView(R.id.editTextShipToTitle) CustomEditText editTextShipToTitle;
    @BindView(R.id.relativeLayoutShipTo) RelativeLayout relativeLayoutShipTo;
    @BindView(R.id.textViewShipNameTitle) CustomTextView textViewShipNameTitle;

    @BindView(R.id.editTextMsg) CustomEditText editTextMsg;

    @BindView(R.id.recycleViewItems) ScrollDetectableListView recycleViewItems;
    @BindView(R.id.textViewNoItems) CustomTextView textViewNoItems;
    @BindView(R.id.linearLayoutRecycleView) LinearLayout linearLayoutRecycleView;
    @BindView(R.id.textViewTotalQty) CustomTextView textViewTotalQty;
    @BindView(R.id.textViewTotalPrice) CustomTextView textViewTotalPrice;
//    @BindView(R.id.relativeLayoutRecycleView) RelativeLayout relativeLayoutRecycleView;

    @BindView(R.id.radioGroupPaymentTerm) RadioGroup radioGroupPaymentTerm;
    @BindView(R.id.radioButtonCreditTerm) RadioButton radioButtonCreditTerm;
    @BindView(R.id.radioButtonCashTransfer) RadioButton radioButtonCashTransfer;
    @BindView(R.id.checkboxPI) CheckBox checkboxPI;
    @BindView(R.id.radioGroupCashTransfer) RadioGroup radioGroupCashTransfer;
    @BindView(R.id.radioButtonUOB) RadioButton radioButtonUOB;
    @BindView(R.id.radioButtonOther) RadioButton radioButtonOther;
    @BindView(R.id.editTextPaymentTermUOB) CustomEditText editTextPaymentTermUOB;
    @BindView(R.id.imageViewUploadSlip) ImageView imageViewUploadSlip;

    ItemListAdapter itemListAdapter;
    List<ArticleObject> arrayListItems;
    List<ArticleObject> arrayListItemsLocal = new ArrayList<ArticleObject>();

    List<String> allItemSelected = new CopyOnWriteArrayList<String>();

    AsyncHttpClient client;

    String frontJson = "{ \"sale_orders\": [";
    String backJson = "}]";
    String fullJson = "";

    String frontItemJson = "\"items\": [";
    String fullItemJson = "";

    String username = "";
    String soldToCode = "";
    String soldToName = "";
    String shipToCode = "";
    String shipToName = "";
    String saleOrderItem = "";
    String payment_term = "";
    String bank = "";
    int totalQty = 0;
    double AllTotalPrice = 0;
//    double VAT = 0;
//    double Discount = 0;
//    double NetTotalPrice = 0;

    RequestParams rParamsCreateOrder = new RequestParams();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.saleorder_fragment_saleorder_create, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.main_title_sale_order);
        setHasOptionsMenu(true);

        username = SharedPreferenceHelper.getSharedPreferenceString(getContext(), sis.com.sis.sis_app.Constants.username, "");
        soldToCode = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SOLD_TO_CODE, "");
        soldToName = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SOLD_TO_NAME, "");
        shipToCode = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SHIP_TO_CODE, "");
        shipToName = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SHIP_TO_NAME, "");
        saleOrderItem = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SALE_ORDER_ITEMS, "");
        payment_term = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.PAYMENT_TERM, "");
        bank = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.BANK, "");

        arrayListItems = new ArrayList<ArticleObject>();
        itemListAdapter = new ItemListAdapter(getContext(), arrayListItems);
        itemListAdapter.setListViewItemClickListener(this);
        recycleViewItems.setAdapter(itemListAdapter);

        if (!soldToCode.equals("")){
            editTextSoldToTitle.setText(soldToCode);
            editTextShipToTitle.setText(soldToCode);
            editTextSoldToTitle.setEnabled(false);
            relativeLayoutSoldTo.setVisibility(View.VISIBLE);
            relativeLayoutShipTo.setVisibility(View.VISIBLE);
            textViewCustomerNameTitle.setText(soldToName);
            textViewShipNameTitle.setText(soldToName);
        }
        if (!shipToCode.equals("")) {
            editTextShipToTitle.setText(shipToCode);
            editTextShipToTitle.setEnabled(false);
            relativeLayoutShipTo.setVisibility(View.VISIBLE);
            textViewShipNameTitle.setText(shipToName);
        }
        else {
            shipToCode = soldToCode;
            shipToName = soldToName;
        }

        if (payment_term.equals("T001")) {
            radioButtonCashTransfer.setChecked(true);
            if (payment_term.equals("UOB")){
                radioButtonUOB.setChecked(true);
            }
            else {
                radioButtonOther.setChecked(true);
            }
        }
        else if (payment_term.equals("N005"))  {
            radioButtonCreditTerm.setChecked(true);
        }

        if (!(saleOrderItem.length() == 0)){
            linearLayoutRecycleView.setVisibility(View.VISIBLE);
            textViewNoItems.setVisibility(View.GONE);
            loadItems();
        }

        return view;
    }


    private void loadItems() {

        String saleOrderItem = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SALE_ORDER_ITEMS, "");

        String[] allItems = saleOrderItem.split("\\|");
        if (client == null) client = new AsyncHttpClient(80,443);

        //Bundle bundle = getArguments();
        //String so_no = String.valueOf(bundle.getSerializable("so_no"));
        String username = SharedPreferenceHelper.getSharedPreferenceString(getContext(), sis.com.sis.sis_app.Constants.username, "");
        String soldToCode = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SOLD_TO_CODE, "");
        String paymentTerm = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.PAYMENT_TERM, "");

        Constants.doLog("LOG arrayListSoldTo SIZE : " + arrayListItems.size());

        RequestParams rParams = new RequestParams();

        rParams.put("SiS", Constants.SIS_SECRET);
        rParams.put("u", username);
        rParams.put("kw", saleOrderItem);
        rParams.put("sdt", soldToCode);
        rParams.put("pmt", paymentTerm);
        rParams.put("bps", 1);


        Constants.doLog("LOG RESPONSE RESULT : " + rParams);

        client.get(Constants.API_HOST + "MSOSkuSearch.php?", rParams, new AsyncHttpResponseHandler() {

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

                Constants.doLog("LOG RESPONSE RESULT1 : " + statusCode);
                Constants.doLog("LOG RESPONSE RESULT1 : " + new String(response));
                Gson gson = new Gson();
                ResponseResult responseResult = new ResponseResult();

                if (isJSONValid(new String(response))){
                    responseResult = gson.fromJson(new String(response),ResponseResult.class);
                }

                if (responseResult.status_code == 200)
                {
                    ResponseResult finalResponseResult = responseResult;
                    client.get(Constants.API_HOST + "MSOLogin.php?", rParams, new AsyncHttpResponseHandler() {

                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response)
                        {
                            Gson gson = new Gson();
                            ResponseResult responseResultLocal = new ResponseResult();

                            if (isJSONValid(new String(response))){
                                responseResultLocal = gson.fromJson(new String(response),ResponseResult.class);
                            }

                            if (responseResultLocal.status_code == 200)
                            {
                                if (responseResultLocal.articles != null){
                                    for (ArticleObject item: responseResultLocal.articles)
                                    {
                                        Constants.doLog("LOG RESPONSE ADD : " + item);
                                        arrayListItemsLocal.add(item);
                                    }
                                }
                            }

                            for (ArticleObject item: finalResponseResult.articles)
                            {

                                for (ArticleObject itemLocal : arrayListItemsLocal) {
                                    String[] price4customer = itemLocal.price4customer.split(",");
                                    for (String customer : price4customer) {
                                        if (customer.equals(soldToCode)) {
                                            if (itemLocal.sku.equals(item.sku)){
                                                item.price = itemLocal.unitprice;
                                                break;
                                            }
                                        }
                                    }
                                }

                                for (String s : allItems) {
                                    Constants.doLog("LOG soldToCode RESULT1 : " + s);
                                    if (s.equals(item.sku)){
                                        Integer qty = SharedPreferenceHelper.getSharedPreferenceInt(getContext(), item.sku, 1);

                                        Constants.doLog("LOG soldToCode RESULT1 : " + qty);
                                        item.qty = String.valueOf(qty);
                                        arrayListItems.add(item);
                                        break;
                                    }
                                }
                            }

                            itemListAdapter.notifyDataSetChanged();

                            int totalHeight = 0;

                            for (int i = 0; i < itemListAdapter.getCount(); i++) {
                                View mView = itemListAdapter.getView(i, null, recycleViewItems);

                                mView.measure(
                                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),

                                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                                totalHeight += mView.getMeasuredHeight();
                                Constants.doLog("LOG RESPONSE RESULT HEIGHT ("+ i + ") : " + String.valueOf(totalHeight));
                            }

                            ViewGroup.LayoutParams params = recycleViewItems.getLayoutParams();

                            if (itemListAdapter.getCount() < 2) {
                                totalHeight += (100 + itemListAdapter.getCount() * 30);
                            }
                            else {
                                totalHeight += (200 + itemListAdapter.getCount() * 30);
                            }
                            Constants.doLog("LOG RESPONSE RESULT HEIGHT (LAST) : " + String.valueOf(totalHeight));
                            params.height = totalHeight;

                            recycleViewItems.setLayoutParams(params);
                            recycleViewItems.requestLayout();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                if (isAdded() && customProgress != null) customProgress.hideProgress();
                            }
                            GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_cannot_connect_server));

                        }

                        @Override
                        public void onRetry(int retryNo) {
                            // called when request is retried
                        }
                    });

                }
                else if (responseResult.status_code == 201) {
                    textViewNoItems.setVisibility(View.VISIBLE);
                    linearLayoutRecycleView.setVisibility(View.GONE);itemListAdapter.notifyDataSetChanged();

                    int totalHeight = 0;

                    for (int i = 0; i < itemListAdapter.getCount(); i++) {
                        View mView = itemListAdapter.getView(i, null, recycleViewItems);

                        mView.measure(
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),

                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                        totalHeight += mView.getMeasuredHeight();
                        Constants.doLog("LOG RESPONSE RESULT HEIGHT ("+ i + ") : " + String.valueOf(totalHeight));
                    }

                    ViewGroup.LayoutParams params = recycleViewItems.getLayoutParams();

                    if (itemListAdapter.getCount() < 2) {
                        totalHeight += (100 + itemListAdapter.getCount() * 30);
                    }
                    else {
                        totalHeight += (200 + itemListAdapter.getCount() * 30);
                    }
                    Constants.doLog("LOG RESPONSE RESULT HEIGHT (LAST) : " + String.valueOf(totalHeight));
                    params.height = totalHeight;

                    recycleViewItems.setLayoutParams(params);
                    recycleViewItems.requestLayout();
                }
                else if (responseResult.status_code == 505) {
                    textViewNoItems.setVisibility(View.VISIBLE);
                    linearLayoutRecycleView.setVisibility(View.GONE);
                    itemListAdapter.notifyDataSetChanged();

                    int totalHeight = 0;

                    for (int i = 0; i < itemListAdapter.getCount(); i++) {
                        View mView = itemListAdapter.getView(i, null, recycleViewItems);

                        mView.measure(
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),

                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                        totalHeight += mView.getMeasuredHeight();
                        Constants.doLog("LOG RESPONSE RESULT HEIGHT ("+ i + ") : " + String.valueOf(totalHeight));
                    }

                    ViewGroup.LayoutParams params = recycleViewItems.getLayoutParams();

                    if (itemListAdapter.getCount() < 2) {
                        totalHeight += (100 + itemListAdapter.getCount() * 30);
                    }
                    else {
                        totalHeight += (200 + itemListAdapter.getCount() * 30);
                    }
                    Constants.doLog("LOG RESPONSE RESULT HEIGHT (LAST) : " + String.valueOf(totalHeight));
                    params.height = totalHeight;

                    recycleViewItems.setLayoutParams(params);
                    recycleViewItems.requestLayout();
                }
                else
                {
                    if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), "Cannot do this action, Please contact IS.");
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (isAdded() && customProgress != null) customProgress.hideProgress();
                }
                GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_cannot_connect_server));
//                    Intent myIntent = new Intent(getActivity(), MainActivity.class);
//                    getActivity().startActivity(myIntent);
//                    getActivity().finish();
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

    @OnCheckedChanged(R.id.radioButtonCreditTerm)
    public void radioButtonCreditTermChange(RadioButton radioButton)
    {
        if (!radioButtonCreditTerm.isChecked()){
            SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.PAYMENT_TERM, "T001");
            radioButtonUOB.setEnabled(true);
            radioButtonOther.setEnabled(true);
        }
        else {
            SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.PAYMENT_TERM, "N005");
            radioButtonUOB.setEnabled(false);
            radioButtonOther.setEnabled(false);
            editTextPaymentTermUOB.setEnabled(false);
        }
    }

    @OnCheckedChanged(R.id.radioButtonCashTransfer)
    public void radioButtonCashTransferChange(RadioButton radioButton)
    {
        if (radioButtonCashTransfer.isChecked()){
            SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.PAYMENT_TERM, "T001");
            radioButtonUOB.setEnabled(true);
            radioButtonOther.setEnabled(true);
        }
        else {
            SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.PAYMENT_TERM, "N005");
            radioButtonUOB.setEnabled(false);
            radioButtonOther.setEnabled(false);
            editTextPaymentTermUOB.setEnabled(false);
        }
    }

    @OnCheckedChanged(R.id.radioButtonUOB)
    public void radioButtonUOBChange(RadioButton radioButton)
    {
        Date date = new Date();
        if (radioButtonUOB.isChecked()) {
            SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.BANK, "UOB");
            editTextPaymentTermUOB.setEnabled(true);
            editTextPaymentTermUOB.setText("ยูโอบี ยอด " + new DecimalFormat("#,###.00").format(AllTotalPrice) + " บาท วันที่ " + DateFormat.format("dd/MM/yyyy",   date));
        }
        else {
            SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.BANK, "OTHER");
            editTextPaymentTermUOB.setEnabled(false);
        }
    }

    @OnClick(R.id.imageButtonSearchSoldTo)
    public void imageButtonSearchSoldTo(ImageButton button)
    {
        FragmentMainSearchSoldTo fragment = new FragmentMainSearchSoldTo();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("shipToObjectItem",saleOrderObjectItem);
//        bundle.putSerializable("so_no",saleOrderObjectItem.so_no);
//        fragment.setArguments(bundle);
        ((MainActivity)getActivity()).replaceFragment(fragment, true);

    }

    @OnClick(R.id.imageButtonSearchShipTo)
    public void imageButtonSearchShipTo(ImageButton button)
    {
        String soldTo_code = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SOLD_TO_CODE, "");
        if (soldTo_code.isEmpty()){
            GeneralHelper.getInstance().showBasicAlert(getContext(), "Sold To (Customer) dose not selected. Please select before choose Ship To.");
        }
        else {
            FragmentMainSearchShipTo fragment = new FragmentMainSearchShipTo();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("shipToObjectItem",saleOrderObjectItem);
//        bundle.putSerializable("so_no",saleOrderObjectItem.so_no);
//        fragment.setArguments(bundle);
            ((MainActivity)getActivity()).replaceFragment(fragment, true);
        }
    }

    @OnClick(R.id.imageButtonAddItem)
    public void imageButtonAddItem(ImageButton button)
    {
        String soldTo_code = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SOLD_TO_CODE, "");
        if (soldTo_code.isEmpty()){
            GeneralHelper.getInstance().showBasicAlert(getContext(), "Sold To (Customer) dose not selected. Please select before add items.");
        }
        else {
            if (allItemSelected.size() != 0) {
                for (String item: allItemSelected) {
                    for (int i = 0; i < arrayListItems.size(); i++) {
                        if (item.equals(arrayListItems.get(i).sku)) {
                            if (arrayListItems.get(i).qty == null){
                                SharedPreferenceHelper.setSharedPreferenceInt(getContext(), arrayListItems.get(i).sku, 1);
                            }
                            else {
                                SharedPreferenceHelper.setSharedPreferenceInt(getContext(), arrayListItems.get(i).sku, Integer.parseInt(arrayListItems.get(i).qty));
                            }
                        }
                    }
                }
            }

            FragmentMainSearchArticle fragment = new FragmentMainSearchArticle();

//        Bundle bundle = new Bundle();
//        bundle.putSerializable("shipToObjectItem",saleOrderObjectItem);
//        bundle.putSerializable("so_no",saleOrderObjectItem.so_no);
//        fragment.setArguments(bundle);
            ((MainActivity)getActivity()).replaceFragment(fragment, true);
        }

    }

    @OnClick(R.id.imageButtonCheckStock)
    public void imageButtonCheckStock(ImageButton button)
    {
//        totalQty = 0;
//        totalPrice = 0;
//        if (allItemSelected.size() != 0) {
//            for (String item: allItemSelected) {
//                for (int i = 0; i < arrayListItems.size(); i++) {
//                    if (item.equals(arrayListItems.get(i).sku)) {
//                        totalQty += Integer.parseInt(arrayListItems.get(i).qty.toString());
//                        totalPrice += Double.parseDouble(arrayListItems.get(i).price.toString()) * Integer.parseInt(arrayListItems.get(i).qty.toString());
//                    }
//                }
//            }
//            textViewTotalPrice.setText(String.valueOf(totalPrice));
//            textViewTotalQty.setText(String.valueOf(totalQty));
//        }
//        else {
//            GeneralHelper.getInstance().showBasicAlert(getContext(),"No article selected in your list.");
//        }

//        FragmentMainSearchArticle fragment = new FragmentMainSearchArticle();

//        Bundle bundle = new Bundle();
//        bundle.putSerializable("shipToObjectItem",saleOrderObjectItem);
//        bundle.putSerializable("so_no",saleOrderObjectItem.so_no);
//        fragment.setArguments(bundle);
//        ((MainActivity)getActivity()).replaceFragment(fragment, true);

    }

    @OnClick(R.id.imageButtonDelete)
    public void imageButtonDeleteItem(ImageButton button)
    {
        String itemList = new String();

        if (allItemSelected.size() != 0){

            String saleOrderItems = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SALE_ORDER_ITEMS, "");
            String[] allItems = saleOrderItems.split("\\|");

            int count = 0;
            for (String item: allItems) {
                boolean checkDelete = true;
                for (String s: allItemSelected) {

                    if (s.equals(item)){
                        checkDelete = false;
                    }
                }
                if (checkDelete) {
                    if (count != allItems.length-1){
                        itemList = itemList + item + "|";
                    }
                    else {
                        itemList = itemList + item;
                    }
                }
                count++;
            }

            Constants.doLog("LOG ALL ITEMS (LAST1) : '" + itemList + "'" );
            arrayListItems.clear();
            allItemSelected.clear();

            SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SALE_ORDER_ITEMS, itemList);
            loadItems();
        }
        else {
            GeneralHelper.getInstance().showBasicAlert(getContext(), "Don't have any article are selected. Please selected before delete.");
        }
//        FragmentMainSearchArticle fragment = new FragmentMainSearchArticle();

//        Bundle bundle = new Bundle();
//        bundle.putSerializable("shipToObjectItem",saleOrderObjectItem);
//        bundle.putSerializable("so_no",saleOrderObjectItem.so_no);
//        fragment.setArguments(bundle);
//        ((MainActivity)getActivity()).replaceFragment(fragment, true);

    }

    @OnClick(R.id.imageViewUploadSlip)
    public void imageViewUploadSlipPressed(ImageView layout)
    {
        if (radioButtonOther.isChecked()) {
            ActionSheet.createBuilder(getContext(), getActivity().getSupportFragmentManager())
                    .setCancelButtonTitle("Cancel")
                    .setOtherButtonTitles("Photo Library", "Camera")
                    .setCancelableOnTouchOutside(true)
                    .setListener(this).show();
        }
    }

    @OnClick(R.id.btnCreateSaleOrder)
    public void btnCreateSaleOrderPressed(CustomButton button)
    {
        Date date = new Date();
        String payment;
        rParamsCreateOrder.setForceMultipartEntityContentType(true);
        rParamsCreateOrder.put("SiS", Constants.SIS_SECRET);
        rParamsCreateOrder.put("u", username);
        rParamsCreateOrder.put("sdt", soldToCode);
        if (shipToCode.equals("")) {
            shipToCode = soldToCode;
            shipToName = soldToName;
        }
        rParamsCreateOrder.put("sht", shipToCode);
        rParamsCreateOrder.put("derb", "Z2");
        if (editTextMsg.getText().toString().equals("")){
            rParamsCreateOrder.put("tmsg", "-");
        }
        else {
            rParamsCreateOrder.put("tmsg", editTextMsg.getText().toString());
        }
        rParamsCreateOrder.put("apiodr", username + "_" + DateFormat.format("yyyy",   date) + DateFormat.format("MM",   date) + DateFormat.format("dd",   date) + DateFormat.format("HH",   date) + DateFormat.format("mm",   date));
        rParamsCreateOrder.put("icount", allItemSelected.size());
        rParamsCreateOrder.put("chksum", totalQty);

        if (checkboxPI.isChecked()){
            rParamsCreateOrder.put("reqpi", "X");
//            Constants.doLog("LOG VALUE CREATE SALE ORDER reqpi : X");
        }
        else {
            rParamsCreateOrder.put("reqpi", "");
        }
        if (radioButtonCashTransfer.isChecked()) {
            payment = "T001";
            rParamsCreateOrder.put("pmt", "T001");
//            Constants.doLog("LOG VALUE CREATE SALE ORDER pmt : T001");

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
        }
        else if (radioButtonCreditTerm.isChecked())  {
            rParamsCreateOrder.put("pmt", "");
            payment = "credit";
        }
        else {
            GeneralHelper.getInstance().showBasicAlert(getContext(), "Please select Payment Term");
            return;
        }

        if (username.isEmpty())
        {
            GeneralHelper.getInstance().showBasicAlert(getContext(), "Username");
            return;
        }
        if (soldToCode.isEmpty())
        {
            GeneralHelper.getInstance().showBasicAlert(getContext(),"Sold To is empty, please insert.");
            return;
        }
        if (shipToCode.isEmpty())
        {
            GeneralHelper.getInstance().showBasicAlert(getContext(),"Ship To is empty, please insert.");
            return;
        }
        if (arrayListItems.size() == 0)
        {
            GeneralHelper.getInstance().showBasicAlert(getContext(),"No Items and Quantity, please click calculate order before submit and create Sale Order.");
            return;
        }
        if (totalQty == 0)
        {
            GeneralHelper.getInstance().showBasicAlert(getContext(),"No Items and Quantity, please click calculate order before submit and create Sale Order.");
            return;
        }

        if (allItemSelected.size() != 0) {
            for (String item: allItemSelected) {
                for (int i = 0; i < arrayListItems.size(); i++) {
                    if (item.equals(arrayListItems.get(i).sku)) {
                        if (fullItemJson.equals("")){
                            fullItemJson = fullItemJson + "{\"sku\": \"" + arrayListItems.get(i).sku +"\", \"name\": \"" + arrayListItems.get(i).name.replace("\"","inch") + "\", \"qty\": \"" + arrayListItems.get(i).qty + "\", \"price\": \"" + arrayListItems.get(i).price + "\", \"discount\": \"" + arrayListItems.get(i).discount + "\"}";
                        }
                        else {
                            fullItemJson = fullItemJson + ", {\"sku\": \"" + arrayListItems.get(i).sku +"\", \"name\": \"" + arrayListItems.get(i).name.replace("\"","inch") + "\", \"qty\": \"" + arrayListItems.get(i).qty + "\", \"price\": \"" + arrayListItems.get(i).price + "\", \"discount\": \"" + arrayListItems.get(i).discount + "\"}";
                        }

                        Constants.doLog("LOG RESPONSE DISCOUNT : " + arrayListItems.get(i).discount);
                        Constants.doLog("LOG RESPONSE fullItemJson : " + fullItemJson);

                        rParamsCreateOrder.put("isku_"+ (i+1), arrayListItems.get(i).sku);
                        rParamsCreateOrder.put("iqty_"+ (i+1), arrayListItems.get(i).qty);
                        rParamsCreateOrder.put("ipri_"+ (i+1), arrayListItems.get(i).price);
                    }
                }
            }
            fullItemJson = frontItemJson + fullItemJson + "]";
        }
        else {
            GeneralHelper.getInstance().showBasicAlert(getContext(),"No article selected in your list.");
        }


        client.post(Constants.API_HOST + "MSOOrderCreate.php", rParamsCreateOrder, new AsyncHttpResponseHandler() {

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

                Constants.doLog("LOG RESPONSE RESULT3 : " + statusCode);
                Constants.doLog("LOG RESPONSE RESULT3 : " + new String(response));
                Gson gson = new Gson();
                ResponseResult responseResult = new ResponseResult();

                if (isJSONValid(new String(response))){
                    responseResult = gson.fromJson(new String(response),ResponseResult.class);
                }

                if (responseResult.status_code == 200)
                {
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
//                                Constants.doLog("LOG RESPONSE RESULT4 : " + statusCode);
//                                Constants.doLog("LOG RESPONSE RESULT4 : " + new String(response));
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
//                                        fullJson = frontJson + "{\"customer_name\": \"" + soldToName +"\", \"customer_code\": \"" + soldToCode + "\", \"shipto_name\": \"" + shipToName + "\", \"shipto_code\": \"" + shipToCode + "\", \"mobile_so\": \"" + username + "_" + DateFormat.format("dd",   date) + DateFormat.format("MM",   date) + DateFormat.format("yyyy",   date) + DateFormat.format("HH",   date) + DateFormat.format("mm",   date) + "\", \"sap_so\": \"-\", \"sap_do\": \"-\",\"sap_inv\": \"-\", \"total_price\": \"" + new DecimalFormat("#,###.00").format(AllTotalPrice) + "\", \"net_total_price\": \""+ new DecimalFormat("#,###.00").format(NetTotalPrice) + "\", \"date\": \"" + DateFormat.format("dd/MM/yyyy",   date) + "\", \"status\": \"-\"}" + backJson;
////                                        fullJson = frontJson + "{\"customer_name\": \"" + soldToName +"\", \"customer_code\": \"" + soldToCode + "\", \"shipto_name\": \"" + shipToName + "\", \"shipto_code\": \"" + shipToCode + "\", \"mobile_so\": \"" + username + "_" + DateFormat.format("yyyy",   date) + DateFormat.format("MM",   date) + DateFormat.format("dd",   date) + DateFormat.format("HH",   date) + DateFormat.format("mm",   date) + "\", \"sap_so\": \"-\", \"sap_do\": \"-\",\"sap_inv\": \"-\", \"total_price\": \"" + new DecimalFormat("#,###.00").format(AllTotalPrice) + "\", \"net_total_price\": \""+ new DecimalFormat("#,###.00").format(NetTotalPrice) + "\", \"date\": \"" + DateFormat.format("dd/MM/yyyy",   date) + "\", \"status\": \"-\", \"payment\": \"" + payment + "\", " + fullItemJson + "}" + backJson;
//                                    }
//                                    else {
//                                        fullJson = frontJson + fullJson + "{\"customer_name\": \"" + soldToName +"\", \"customer_code\": \"" + soldToCode + "\", \"shipto_name\": \"" + shipToName + "\", \"shipto_code\": \"" + shipToCode + "\", \"mobile_so\": \"" + username + "_" + DateFormat.format("dd",   date) + DateFormat.format("MM",   date) + DateFormat.format("yyyy",   date) + DateFormat.format("HH",   date) + DateFormat.format("mm",   date) + "\", \"sap_so\": \"-\", \"sap_do\": \"-\",\"sap_inv\": \"-\", \"total_price\": \"" + new DecimalFormat("#,###.00").format(AllTotalPrice) + "\", \"net_total_price\": \""+ new DecimalFormat("#,###.00").format(NetTotalPrice) + "\", \"date\": \"" + DateFormat.format("dd/MM/yyyy",   date) + "\", \"status\": \"-\"}" + backJson;
//
////                                        fullJson = frontJson + fullJson + ", {\"customer_name\": \"" + soldToName +"\", \"customer_code\": \"" + soldToCode + "\", \"shipto_name\": \"" + shipToName + "\", \"shipto_code\": \"" + shipToCode + "\", \"mobile_so\": \"" + username + "_" + DateFormat.format("yyyy",   date) + DateFormat.format("MM",   date) + DateFormat.format("dd",   date) + DateFormat.format("HH",   date) + DateFormat.format("mm",   date) + "\", \"sap_so\": \"-\", \"sap_do\": \"-\",\"sap_inv\": \"-\", \"total_price\": \"" + new DecimalFormat("#,###.00").format(AllTotalPrice) + "\", \"net_total_price\": \""+ new DecimalFormat("#,###.00").format(NetTotalPrice) + "\", \"date\": \"" + DateFormat.format("dd/MM/yyyy",   date) + "\", \"status\": \"-\", \"payment\": \"" + payment + "\", " + fullItemJson + "}" + backJson;
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
//                        Discount = AllTotalPrice * 0.005;
//                        VAT = (AllTotalPrice - Discount) * 0.07;
//                        NetTotalPrice = AllTotalPrice - Discount + VAT;
//
//                        if (fullJson.equals("")) {
//                            fullJson = frontJson + "{\"customer_name\": \"" + soldToName +"\", \"customer_code\": \"" + soldToCode + "\", \"shipto_name\": \"" + shipToName + "\", \"shipto_code\": \"" + shipToCode + "\", \"mobile_so\": \"" + username + "_" + DateFormat.format("dd",   date) + DateFormat.format("MM",   date) + DateFormat.format("yyyy",   date) + DateFormat.format("HH",   date) + DateFormat.format("mm",   date) + "\", \"sap_so\": \"-\", \"sap_do\": \"-\",\"sap_inv\": \"-\", \"total_price\": \"" + new DecimalFormat("#,###.00").format(AllTotalPrice) + "\", \"net_total_price\": \""+ new DecimalFormat("#,###.00").format(NetTotalPrice) + "\", \"date\": \"" + DateFormat.format("dd/MM/yyyy",   date) + "\", \"status\": \"-\"}" + backJson;
////                            fullJson = frontJson + "{\"customer_name\": \"" + soldToName +"\", \"customer_code\": \"" + soldToCode + "\", \"shipto_name\": \"" + shipToName + "\", \"shipto_code\": \"" + shipToCode + "\", \"mobile_so\": \"" + username + "_" + DateFormat.format("yyyy",   date) + DateFormat.format("MM",   date) + DateFormat.format("dd",   date) + DateFormat.format("HH",   date) + DateFormat.format("mm",   date) + "\", \"sap_so\": \"-\", \"sap_do\": \"-\",\"sap_inv\": \"-\", \"total_price\": \"" + new DecimalFormat("#,###.00").format(AllTotalPrice) + "\", \"net_total_price\": \""+ new DecimalFormat("#,###.00").format(NetTotalPrice) + "\", \"date\": \"" + DateFormat.format("dd/MM/yyyy",   date) + "\", \"status\": \"-\", \"payment\": \"" + payment + "\", " + fullItemJson + "}" + backJson;
//                        }
//                        else {
//                            fullJson = frontJson + fullJson + "{\"customer_name\": \"" + soldToName +"\", \"customer_code\": \"" + soldToCode + "\", \"shipto_name\": \"" + shipToName + "\", \"shipto_code\": \"" + shipToCode + "\", \"mobile_so\": \"" + username + "_" + DateFormat.format("dd",   date) + DateFormat.format("MM",   date) + DateFormat.format("yyyy",   date) + DateFormat.format("HH",   date) + DateFormat.format("mm",   date) + "\", \"sap_so\": \"-\", \"sap_do\": \"-\",\"sap_inv\": \"-\", \"total_price\": \"" + new DecimalFormat("#,###.00").format(AllTotalPrice) + "\", \"net_total_price\": \""+ new DecimalFormat("#,###.00").format(NetTotalPrice) + "\", \"date\": \"" + DateFormat.format("dd/MM/yyyy",   date) + "\", \"status\": \"-\"}" + backJson;
////                            fullJson = frontJson + fullJson + ", {\"customer_name\": \"" + soldToName +"\", \"customer_code\": \"" + soldToCode + "\", \"shipto_name\": \"" + shipToName + "\", \"shipto_code\": \"" + shipToCode + "\", \"mobile_so\": \"" + username + "_" + DateFormat.format("yyyy",   date) + DateFormat.format("MM",   date) + DateFormat.format("dd",   date) + DateFormat.format("HH",   date) + DateFormat.format("mm",   date) + "\", \"sap_so\": \"-\", \"sap_do\": \"-\",\"sap_inv\": \"-\", \"total_price\": \"" + new DecimalFormat("#,###.00").format(AllTotalPrice) + "\", \"net_total_price\": \""+ new DecimalFormat("#,###.00").format(NetTotalPrice) + "\", \"date\": \"" + DateFormat.format("dd/MM/yyyy",   date) + "\", \"status\": \"-\", \"payment\": \"" + payment + "\", " + fullItemJson + "}" + backJson;
//                        }
//                        Constants.doLog("LOG JSON KEEP LOCAL : " + fullJson);
//                        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SALE_ORDER_LISTS, fullJson);

//                        FragmentMainSaleOrderDetail fragment = new FragmentMainSaleOrderDetail();
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("saleOrder",rParamsCreateOrder);
//                        fragment.setArguments(bundle);
//                        ((MainActivity)getActivity()).replaceFragment(fragment, true);

                        fullJson = SharedPreferenceHelper.getSharedPreferenceString(getContext(),Constants.SALE_ORDER_LISTS,"");

                        Constants.doLog("LOG JSON KEEP LOCAL : " + fullJson);
                        fullJson = fullJson.replace(frontJson,"");
                        Constants.doLog("LOG JSON KEEP LOCAL : " + fullJson);

//                        Discount = AllTotalPrice * 0.005;
//                        VAT = (AllTotalPrice - Discount) * 0.07;
//                        NetTotalPrice = AllTotalPrice - Discount + VAT;

                        if (fullJson.equals("")) {
        //            fullJson = frontJson + "{\"customer_name\": \"" + soldToName +"\", \"customer_code\": \"" + soldToCode + "\", \"shipto_name\": \"" + shipToName + "\", \"shipto_code\": \"" + shipToCode + "\", \"mobile_so\": \"" + username + "_" + DateFormat.format("dd",   date) + DateFormat.format("MM",   date) + DateFormat.format("yyyy",   date) + DateFormat.format("HH",   date) + DateFormat.format("mm",   date) + "\", \"sap_so\": \"-\", \"sap_do\": \"-\",\"sap_inv\": \"-\", \"total_price\": \"" + new DecimalFormat("#,###.00").format(AllTotalPrice) + "\", \"net_total_price\": \""+ new DecimalFormat("#,###.00").format(NetTotalPrice) + "\", \"date\": \"" + DateFormat.format("dd/MM/yyyy",   date) + "\", \"status\": \"-\"}" + backJson;
                            fullJson = frontJson + "{\"customer_name\": \"" + soldToName +"\", \"customer_code\": \"" + soldToCode + "\", \"shipto_name\": \"" + shipToName + "\", \"shipto_code\": \"" + shipToCode + "\", \"mobile_so\": \"" + username + "_" + DateFormat.format("yyyy",   date) + DateFormat.format("MM",   date) + DateFormat.format("dd",   date) + DateFormat.format("HH",   date) + DateFormat.format("mm",   date) + "\", \"sap_so\": \"-\", \"sap_do\": \"-\",\"sap_inv\": \"-\", \"total_price\": \"" + new DecimalFormat("#,###.00").format(AllTotalPrice / Constants.INCLUDE_VAT) + "\", \"net_total_price\": \""+ new DecimalFormat("#,###.00").format(AllTotalPrice) + "\", \"date\": \"" + DateFormat.format("dd/MM/yyyy",   date) + "\", \"status\": \"-\", \"payment\": \"" + payment + "\", " + fullItemJson + backJson + "}";

                            Constants.doLog("LOG JSON KEEP LOCAL EMPTY : " + fullJson);
                        }
                        else {
                            StringBuilder builder = new StringBuilder(fullJson);
                            builder.replace(builder.length()-2, builder.length(), "");
                            fullJson = builder.toString();
                            Constants.doLog("LOG JSON KEEP LOCAL NOT EMPTY: " + fullJson);
        //            fullJson = frontJson + fullJson + ", {\"customer_name\": \"" + soldToName +"\", \"customer_code\": \"" + soldToCode + "\", \"shipto_name\": \"" + shipToName + "\", \"shipto_code\": \"" + shipToCode + "\", \"mobile_so\": \"" + username + "_" + DateFormat.format("dd",   date) + DateFormat.format("MM",   date) + DateFormat.format("yyyy",   date) + DateFormat.format("HH",   date) + DateFormat.format("mm",   date) + "\", \"sap_so\": \"-\", \"sap_do\": \"-\",\"sap_inv\": \"-\", \"total_price\": \"" + new DecimalFormat("#,###.00").format(AllTotalPrice) + "\", \"net_total_price\": \""+ new DecimalFormat("#,###.00").format(NetTotalPrice) + "\", \"date\": \"" + DateFormat.format("dd/MM/yyyy",   date) + "\", \"status\": \"-\"}" + backJson;
                            fullJson = frontJson + fullJson + ", {\"customer_name\": \"" + soldToName +"\", \"customer_code\": \"" + soldToCode + "\", \"shipto_name\": \"" + shipToName + "\", \"shipto_code\": \"" + shipToCode + "\", \"mobile_so\": \"" + username + "_" + DateFormat.format("yyyy",   date) + DateFormat.format("MM",   date) + DateFormat.format("dd",   date) + DateFormat.format("HH",   date) + DateFormat.format("mm",   date) + "\", \"sap_so\": \"-\", \"sap_do\": \"-\",\"sap_inv\": \"-\", \"total_price\": \"" + new DecimalFormat("#,###.00").format(AllTotalPrice / Constants.INCLUDE_VAT) + "\", \"net_total_price\": \""+ new DecimalFormat("#,###.00").format(AllTotalPrice) + "\", \"date\": \"" + DateFormat.format("dd/MM/yyyy",   date) + "\", \"status\": \"-\", \"payment\": \"" + payment + "\", " + fullItemJson + backJson + "}";
                        }
                        Constants.doLog("LOG JSON KEEP LOCAL : " + fullJson);
                        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.SALE_ORDER_LISTS, fullJson);

                        if (allItemSelected.size() != 0) {
                            for (String item: allItemSelected) {
                                SharedPreferenceHelper.setSharedPreferenceInt(getContext(), item, 1);
                            }
                        }

                        String saleOrderItem = SharedPreferenceHelper.getSharedPreferenceString(getContext(), Constants.SALE_ORDER_ITEMS, "");

                        String[] allItems = saleOrderItem.split("\\|");
                        for (String s : allItems) {
                            Constants.doLog("LOG RESET QTY : " + s);
                            SharedPreferenceHelper.setSharedPreferenceInt(getContext(), s, 1);
                        }

                        if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), "Create Sale Order successfully.");


//                        GeneralHelper.getInstance().showBasicAlert(getContext(),responseResult.status_msg);
                        FragmentMainSaleOrder fragment = new FragmentMainSaleOrder();
                        ((MainActivity)getActivity()).replaceFragment(fragment, false);
//                        Intent myIntent = new Intent(getActivity(), MainActivity.class);
//                        getActivity().startActivity(myIntent);
//                        getActivity().finish();
//                    }
                }
                else if (responseResult.status_code == 401) {
                    if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(),responseResult.status_msg);
                }
                else if (responseResult.status_code == 501) {
                    if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(),responseResult.status_msg);
                }
                else
                {
                    if (isAdded()) GeneralHelper.getInstance().showBasicAlert(getContext(), "Cannot do this action, Please contact IS.");
                }

                itemListAdapter.notifyDataSetChanged();

                int totalHeight = 0;

                for (int i = 0; i < itemListAdapter.getCount(); i++) {
                    View mView = itemListAdapter.getView(i, null, recycleViewItems);

                    mView.measure(
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),

                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                    totalHeight += mView.getMeasuredHeight();
                    Constants.doLog("LOG RESPONSE RESULT HEIGHT ("+ i + ") : " + String.valueOf(totalHeight));
                }

                ViewGroup.LayoutParams params = recycleViewItems.getLayoutParams();

                if (itemListAdapter.getCount() < 2) {
                    totalHeight += (100 + itemListAdapter.getCount() * 30);
                }
                else {
                    totalHeight += (200 + itemListAdapter.getCount() * 30);
                }
                Constants.doLog("LOG RESPONSE RESULT HEIGHT (LAST) : " + String.valueOf(totalHeight));
                params.height = totalHeight;

                recycleViewItems.setLayoutParams(params);
                recycleViewItems.requestLayout();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (isAdded() && customProgress != null) customProgress.hideProgress();
                }

                GeneralHelper.getInstance().showBasicAlert(getContext(),getResources().getString(R.string.message_cannot_connect_server));
//                    Intent myIntent = new Intent(getActivity(), MainActivity.class);
//                    getActivity().startActivity(myIntent);
//                    getActivity().finish();
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
            PicassoTrustAll.getInstance(getContext()).load(new File(image.getPath())).into(imageViewUploadSlip);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(View view, int position) {
        ArticleObject articleObject = arrayListItems.get(position);

        CheckBox cb = (CheckBox) view.findViewById(R.id.checkboxSelected);

        CustomEditText qty = (CustomEditText) view.findViewById(R.id.editTextQty);
        CustomTextView price = (CustomTextView) view.findViewById(R.id.editTextPrice);
        CustomTextView totalPrice = (CustomTextView) view.findViewById(R.id.textViewTotalPrice);

        boolean listSaleOrderChecked = true;
        if (cb.isChecked()){
            if (allItemSelected.size() != 0) {
                for (String item: allItemSelected) {
                    if (item.equals(articleObject.sku)){
                        cb.setChecked(!cb.isChecked());
                        allItemSelected.remove(item);

                        totalQty = Integer.parseInt(textViewTotalQty.getText().toString()) - Integer.parseInt(arrayListItems.get(position).qty);
                        AllTotalPrice = Double.parseDouble(textViewTotalPrice.getText().toString().replace(",","")) - (Double.parseDouble(arrayListItems.get(position).price.replace(",","")) - Double.parseDouble(arrayListItems.get(position).discount.replace(",",""))) * Integer.parseInt(arrayListItems.get(position).qty) * Constants.INCLUDE_VAT;
                    }
                }
            }
            else {
                GeneralHelper.getInstance().showBasicAlert(getContext(),"No article selected in your list.");
            }
        }
        else {
            if (allItemSelected != null && allItemSelected.size() != 0) {
                for (String item: allItemSelected) {
                    if (item.equals(articleObject.sku)){
                        cb.setChecked(!cb.isChecked());
                        listSaleOrderChecked = false;
                        GeneralHelper.getInstance().showBasicAlert(getContext(),"This article already add into your list.");
                    }
                }
                if (listSaleOrderChecked) {
                    cb.setChecked(!cb.isChecked());
                    allItemSelected.add(articleObject.sku);
                    arrayListItems.get(position).qty = qty.getText().toString();
                    arrayListItems.get(position).price = price.getText().toString();

                    if (!qty.getText().toString().equals("") && !price.getText().toString().equals("")){
                        totalPrice.setText(new DecimalFormat("#,###.00").format((Integer.parseInt(qty.getText().toString()) * (Double.parseDouble(price.getText().toString().replace(",","")) - Double.parseDouble(arrayListItems.get(position).discount.replace(",",""))) * Constants.INCLUDE_VAT)));

                        totalQty = Integer.parseInt(textViewTotalQty.getText().toString()) + Integer.parseInt(arrayListItems.get(position).qty);
                        AllTotalPrice = Double.parseDouble(textViewTotalPrice.getText().toString().replace(",","")) + (Double.parseDouble(arrayListItems.get(position).price.replace(",","")) - Double.parseDouble(arrayListItems.get(position).discount.replace(",",""))) * Integer.parseInt(arrayListItems.get(position).qty) * Constants.INCLUDE_VAT;
                    }
                }
            }
            else {
                cb.setChecked(!cb.isChecked());
                allItemSelected.add(articleObject.sku);
                arrayListItems.get(position).qty = qty.getText().toString();
                arrayListItems.get(position).price = price.getText().toString();

                if (!qty.getText().toString().equals("") && !price.getText().toString().equals("")){
                    totalPrice.setText(new DecimalFormat("#,###.00").format((Integer.parseInt(qty.getText().toString()) * (Double.parseDouble(price.getText().toString().replace(",","")) - Double.parseDouble(arrayListItems.get(position).discount.replace(",",""))) * Constants.INCLUDE_VAT)));

                    totalQty = Integer.parseInt(textViewTotalQty.getText().toString()) + Integer.parseInt(arrayListItems.get(position).qty);
                    AllTotalPrice = Double.parseDouble(textViewTotalPrice.getText().toString().replace(",","")) + (Double.parseDouble(arrayListItems.get(position).price.replace(",","")) - Double.parseDouble(arrayListItems.get(position).discount.replace(",",""))) * Integer.parseInt(arrayListItems.get(position).qty) * Constants.INCLUDE_VAT;
                }
            }
        }

        textViewTotalPrice.setText(new DecimalFormat("#,###.00").format(AllTotalPrice));
        textViewTotalQty.setText(String.valueOf(totalQty));
    }

    @Override
    public void listViewDidScrollToEnd() {

    }
}
