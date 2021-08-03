package sis.com.sis.sis_app.ShipToApproval.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import sis.com.sis.sis_app.ShipToApproval.Fragments.FragmentMainShipToApprove;
import sis.com.sis.sis_app.ShipToApproval.Fragments.FragmentsMainShipToHistory;
import sis.com.sis.sis_app.R;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.shipto_navigation) BottomNavigationView mNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.shipto_activity_main);
        ButterKnife.bind(this);

        mNavigationBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        replaceFragment(new FragmentMainShipToApprove(),false);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
//        if (handler == null) handler = new Handler();
//        if (handler != null) handler.removeCallbacks(runnable);
//        if (handler != null) handler.postDelayed(runnable, syncDelay);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (handler != null) handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (handler == null) handler = new Handler();
//        if (handler != null) handler.removeCallbacks(runnable);
//        if (handler != null) handler.postDelayed(runnable, syncDelay);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (handler != null) handler.removeCallbacks(runnable);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_document:
                    FragmentMainShipToApprove fragmentShipTo = new FragmentMainShipToApprove();
                    replaceFragment(fragmentShipTo, false);
                    return true;

                case R.id.navigation_history:
                    FragmentsMainShipToHistory fragmentHistory = new FragmentsMainShipToHistory();
                    replaceFragment(fragmentHistory, false);
                    return true;
            }
            return false;
        }
    };



    public void replaceFragment(Fragment fragment)
    {
        replaceFragment(fragment,true);
    }

    public void replaceFragment(Fragment fragment, boolean addBackstack)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        if (addBackstack)
        {
            fragmentTransaction.addToBackStack(fragment.toString());
        } else
        {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fragmentTransaction.commit();
    }

    public void addFragment(Fragment fragment)
    {
        replaceFragment(fragment,true);
    }

    public void addFragment(Fragment fragment, boolean addBackstack)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        if (addBackstack)
        {
            fragmentTransaction.addToBackStack(fragment.toString());
        } else
        {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fragmentTransaction.commit();
    }
}