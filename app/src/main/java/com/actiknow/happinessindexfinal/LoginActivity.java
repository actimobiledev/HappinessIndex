package com.actiknow.happinessindexfinal;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import actiknow.com.Fragment.LoginFragment1;
import actiknow.com.utils.Constants;


/**
 * Created by SUDHANSHU SHARMA on 24-02-2016.
 */
public class LoginActivity  extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Bundle bundle = new Bundle();
        bundle.putString("mobilenumber", Constants.mobile_number);
        bundle.putString("companyid", Constants.companyCode);
        bundle.putString("employeeid", Constants.companyID);
        bundle.putInt("number_status", Constants.status);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LoginFragment1 f1 = new LoginFragment1();
        f1.setArguments (bundle);
        fragmentTransaction.add(R.id.fragment_container, f1, "fragment1");
        fragmentTransaction.commit();


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null && item.getItemId() == android.R.id.home) {
            return true;
        }
        return super.onOptionsItemSelected (item);
    }
    @Override
    public void onBackPressed () {
        super.onBackPressed ();
    }
}