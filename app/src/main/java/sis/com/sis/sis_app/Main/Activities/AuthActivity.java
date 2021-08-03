package sis.com.sis.sis_app.Main.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.amirarcane.lockscreen.activity.EnterPinActivity;

import sis.com.sis.sis_app.Constants;
import sis.com.sis.sis_app.Helpers.SharedPreferenceHelper;
import sis.com.sis.sis_app.R;
import sis.com.sis.sis_app.Main.Fragments.FragmentAuthLogin;


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

        boolean logged_in = SharedPreferenceHelper.getSharedPreferenceBoolean(this, Constants.login, false);
        if (logged_in)
        {
            Intent myIntentMainApp = new Intent(this, MainActivity.class);
            startActivity(myIntentMainApp);
            finish();
            Intent myIntentEnterPin = new Intent(this, EnterPinActivity.class);
            startActivity(myIntentEnterPin);
            finish();
        }
        else
        {
            if (findViewById(R.id.fragment_container) != null)
            {

                if (savedInstanceState != null)
                {

                }
                else
                {
                    replaceFragment(new FragmentAuthLogin(),false);
                }
            }
        }
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
