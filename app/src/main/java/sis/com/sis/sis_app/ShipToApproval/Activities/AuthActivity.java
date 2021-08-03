package sis.com.sis.sis_app.ShipToApproval.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import sis.com.sis.sis_app.ShipToApproval.Fragments.FragmentAuthLogin;
import sis.com.sis.sis_app.R;


public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            getSupportActionBar().hide();
        } catch (Exception e)
        {

        }
        setContentView(R.layout.main_activity_auth);

//        boolean logged_in = SharedPreferenceHelper.getSharedPreferenceBoolean(this, "logged_in", false);
//        if (logged_in)
//        {
//
//            Intent myIntent = new Intent(this, MainActivity.class);
//            startActivity(myIntent);
//            finish();
//        } else
//        {
            if (findViewById(R.id.fragment_container) != null)
            {

                if (savedInstanceState != null)
                {

                } else
                {
                    replaceFragment(new FragmentAuthLogin(),false);
                }
            }
//        }
    }



    public void replaceFragment(Fragment fragment)
    {
        replaceFragment(fragment, true);
    }

    public void replaceFragment(Fragment fragment, boolean addBackstack)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        if (addBackstack) fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }
}
