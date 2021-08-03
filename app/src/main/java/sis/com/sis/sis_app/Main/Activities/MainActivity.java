package sis.com.sis.sis_app.Main.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import sis.com.sis.sis_app.Main.Fragments.MainFragmentHome;
import sis.com.sis.sis_app.Main.Fragments.MainFragmentSetting;
import sis.com.sis.sis_app.R;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_navigation) BottomNavigationView mNavigationBar;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
//            Manifest.permission.READ_CONTACTS,
//            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_NETWORK_STATE,
//            Manifest.permission.INTERNET,
//            Manifest.permission.FOREGROUND_SERVICE,
//            Manifest.permission.USE_BIOMETRIC,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity_main);
        ButterKnife.bind(this);

        mNavigationBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        replaceFragment(new MainFragmentHome(),false);

        if(!hasPermissions(this, PERMISSIONS))
        {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null)
        {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }
        return true;
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
                case R.id.navigation_home:
                    MainFragmentHome mainFragmentHome = new MainFragmentHome();
                    replaceFragment(mainFragmentHome, false);
                    return true;

                case R.id.navigation_setting:
                    MainFragmentSetting mainFragmentSetting = new MainFragmentSetting();
                    replaceFragment(mainFragmentSetting, false);
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