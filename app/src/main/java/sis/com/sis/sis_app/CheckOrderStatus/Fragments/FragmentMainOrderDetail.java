package sis.com.sis.sis_app.CheckOrderStatus.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import sis.com.sis.sis_app.CheckOrderStatus.Activities.MainActivity;
import sis.com.sis.sis_app.CheckOrderStatus.Constants;
import sis.com.sis.sis_app.CheckOrderStatus.Models.CheckStatusObject;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.ShipToApproval.Models.ShipToObject;
import sis.com.sis.sis_app.Views.CustomTabs.Custom2TabView;

public class FragmentMainOrderDetail extends Fragment implements Custom2TabView.NewTabDialogListener {
    @BindView(R.id.tabView)
    Custom2TabView tabView;
    ShipToObject shipToObjectItem;
    AsyncHttpClient client;
    String sono;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.checkorder_fragment_main_order_detail, container, false);
        ButterKnife.bind(this, view);

//        Bundle bundle = getArguments();
//        sono = (String) bundle.getSerializable("sono");
//        FragmentMainOrderDetailStatus fragmentMainOrderDetailStatus = new FragmentMainOrderDetailStatus();
//
//        bundle.putSerializable("sono",sono);
//        fragmentMainOrderDetailStatus.setArguments(bundle);
//        ((MainActivity)getActivity()).replaceFragment(fragmentMainOrderDetailStatus);



//        shipToObjectItem = (ShipToObject) bundle.getSerializable("shipToObjectItem");
//        String history = String.valueOf(bundle.getSerializable("history"));
//        so_no = String.valueOf(bundle.getSerializable("so_no"));

        ((sis.com.sis.sis_app.CheckOrderStatus.Activities.MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((sis.com.sis.sis_app.CheckOrderStatus.Activities.MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.main_title_sale_order_detail);
        setHasOptionsMenu(true);


        tabView.setTab1Text(getString(R.string.main_tab_detail));
        tabView.setTab2Text(getString(R.string.main_tab_item));
        tabView.setTabListener(this);

        loadOrderDetail();

        return view;
    }

    @Override
    public void onTabChanged(int position)
    {
//        arrayList.clear();
//        client.cancelAllRequests(true);
//        mListView.isLoading = false;


        loadOrderDetail();
    }

    @Override
    public void onDestroy() {
//        super.onDestroy();
//        if (client != null) client.cancelAllRequests(true);
//        mListView.isLoading = false;
        super.onDestroy();
    }

    @Override
    public void onPause() {
//        super.onPause();
//        if (client != null) client.cancelAllRequests(true);
//        mListView.isLoading = false;
        super.onPause();
    }


    private void loadOrderDetail() {
        if (tabView.getSelectedTab() == 0)
        {
            Constants.doLog("LOG TEST CASE1 : " + tabView.getSelectedTab());
            FragmentMainOrderDetailStatus fragmentMainOrderDetailStatus = new FragmentMainOrderDetailStatus();
            Bundle bundle = getArguments();
            sono = (String) bundle.getSerializable("sono");
            bundle.putSerializable("sono",sono);
            fragmentMainOrderDetailStatus.setArguments(bundle);

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.relativeMainOrderDetail, fragmentMainOrderDetailStatus);
            fragmentTransaction.commit();
        }

        if (tabView.getSelectedTab() == 1)
        {
            FragmentMainOrderDetailItem fragmentMainOrderDetailItem = new FragmentMainOrderDetailItem();

            Bundle bundle = getArguments();
            sono = (String) bundle.getSerializable("sono");
            bundle.putSerializable("sono",sono);
            fragmentMainOrderDetailItem.setArguments(bundle);

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.relativeMainOrderDetail, fragmentMainOrderDetailItem);
            fragmentTransaction.commit();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {

        //inflater.inflate(R.menu.toolbar_filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}