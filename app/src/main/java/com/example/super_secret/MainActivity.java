package com.example.super_secret;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

//import android.os.Bundle;
//import android.app.Activity;
//import android.telephony.SmsManager;
//import android.view.Menu;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.*;
//import android.view.View.OnClickListener;
//import android.view.*;
//
//import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        toolbar =  (Toolbar) findViewById(R.id.myToolbar);

        setSupportActionBar(toolbar);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        // if sms permission is not granted
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED)
        {
            // if the permission is not granted then check if the user has denied the permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS))
            {
                 // Do nothing as user has denied
            }
            else
            {
                // a popup will appear
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            }
        }
    }// OnCreate
    //after getting the result of permission request

    @Override
    public void onRequestPermissionsResult (int requestCode, String permissions[], int[] grantResults)
    {
        //check requestCode
        switch(requestCode)
        {
            case MY_PERMISSIONS_REQUEST_RECEIVE_SMS:
            {
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    //New broadcast receiver will work in background
                    Toast.makeText(this, "Thank you for permission", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(this,"This app will require permissions", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new Farid_Profile(),"Farid");
        viewPagerAdapter.addFragment(new Derek_Profile(),"Derek");
        viewPagerAdapter.addFragment(new Callie_Profile(),"Callie");
        viewPagerAdapter.addFragment(new XinWei_Profile(),"Xin Wei");
        viewPagerAdapter.addFragment(new MingKiat_Profile(),"Ming Kiat");
        viewPagerAdapter.addFragment(new KangSian_Profile(),"Kang Sian");

        viewPager.setAdapter(viewPagerAdapter);
    }

}