package sis.com.sis.sis_app.Main.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.amirarcane.lockscreen.activity.EnterPinActivity;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sis.com.sis.sis_app.Constants;
import sis.com.sis.sis_app.Helpers.GeneralHelper;
import sis.com.sis.sis_app.Helpers.SharedPreferenceHelper;
import sis.com.sis.sis_app.Main.Activities.AuthActivity;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.SaleOrders.Activities.MainActivity;
import sis.com.sis.sis_app.Views.CustomButtonBold;
import sis.com.sis.sis_app.Views.CustomTextViewBold;

import static com.amirarcane.lockscreen.activity.EnterPinActivity.EXTRA_SET_PIN;

public class MainFragmentSetting extends Fragment {

    @BindView(R.id.textViewSetPinTitle) CustomTextViewBold textViewSetPinTitle;
    @BindView(R.id.relativeLayoutSetPin) RelativeLayout relativeLayoutSetPin;
    @BindView(R.id.customButtonLogOut) CustomButtonBold customButtonLogOut;

    private boolean mSetPin;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.main_fragment_main_setting, container, false);
        ButterKnife.bind(this, view);

        ((sis.com.sis.sis_app.Main.Activities.MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((sis.com.sis.sis_app.Main.Activities.MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.main_title_setting);

        textViewSetPinTitle.setText("Change PIN");
//        getActivity().finish();
//        arrayList = new ArrayList<ShipToObject>();
//        shipToListAdapter = new ShipToListAdapter(getContext(), arrayList);
//        shipToListAdapter.setListViewItemClickListener(this);
//        mListView.setAdapter(shipToListAdapter);
//        loadListShipTo();

        return view;
    }

    @OnClick(R.id.relativeLayoutSetPin)
    public void relativeLayoutSetPinPressed(RelativeLayout relativeLayout) {
        Intent myIntent = EnterPinActivity.getIntent(getActivity(), true);
        mSetPin = myIntent.getBooleanExtra(EXTRA_SET_PIN, false);

        Constants.doLog("Item was clicked" + mSetPin);
        getActivity().startActivity(myIntent);

        GeneralHelper.getInstance().showBasicAlert(getContext(),"Change your pin successfully.");
    }


    @OnClick(R.id.customButtonLogOut)
    public void customButtonLogOutPressed(CustomButtonBold button) {
        new AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        SharedPreferenceHelper.clear(getContext()); // clear
                        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.username, "");
                        SharedPreferenceHelper.setSharedPreferenceString(getContext(), Constants.password, "");
                        SharedPreferenceHelper.setSharedPreferenceBoolean(getContext(), Constants.login, false);

                        Intent myIntent = new Intent(getContext(), AuthActivity.class);
                        startActivity(myIntent);
                        getActivity().finish();
                    }})
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

}
