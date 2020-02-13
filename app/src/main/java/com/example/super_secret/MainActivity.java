package com.example.super_secret;

//https://medium.com/mindorks/multiple-runtime-permissions-in-android-without-any-third-party-libraries-53ccf7550d0
//https://github.com/JaimePerezS/GPSLocation


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //List of all permission needed for the app
    String[] appPermissions = {Manifest.permission.RECEIVE_SMS, Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int PERMISSIONS_REQUEST_CODE = 1240;

    private LocationManager mLocationManagerGPS;
    private LocationListener mLocationListenerGPS;
    private LocationManager mLocationManagerNetwork;
    private LocationListener mLocationListenerNetwork;



    //private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    //    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
    private String messages;


    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");

    //myRef.setValue("Hello, World!");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkAndRequestPermission()) {
            getPositionNetwork();
        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        toolbar = (Toolbar) findViewById(R.id.myToolbar);

        setSupportActionBar(toolbar);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        // if sms permission is not granted
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED)
//        {
//            // if the permission is not granted then check if the user has denied the permission
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS))
//            {
//                 // Do nothing as user has denied
//            }
//            else
//            {
//                // a popup will appear
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
//            }
//        }

    }

    // OnCreate
    //after getting the result of permission request

    public boolean checkAndRequestPermission() {
        //Check which permission are granted
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String perm : appPermissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(perm);
            }
        }

        //Ask for non-granted permissions
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSIONS_REQUEST_CODE);
            return false;
        }
        // App has all permission. Proceed ahead
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            HashMap<String, Integer> permissionResults = new HashMap<>();
            int deniedCount = 0;

            //Gather permission grant results
            for (int i = 0; i < grantResults.length; i++) {
                //Add only permissions which are denied
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResults.put(permissions[i], grantResults[i]);
                    deniedCount++;
                }
            }

            // Check if all permission are granted
            if (deniedCount == 0) {
                //Proceed ahead with the app
                //getPositionGPS();
                getPositionNetwork();
            }
            //At least one or all permission are denied
            else {
                for (Map.Entry<String, Integer> entry : permissionResults.entrySet()) {
                    String permName = entry.getKey();
                    int permResult = entry.getValue();

                    // permission is denied (this is the first time when "never ask again" is not checked)
                    // so will prompt for permission request, explaining the reason behide the permission request
                    //showShowRequestPermissionRationale will return true
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permName)) {
                        //Show dialog of explanation
                        showDialog("", "This app needs some permission to work properly", "Yes, Allow permissions", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        dialogInterface.dismiss();
                                        checkAndRequestPermission();
                                    }

                                },
                                "No, Exit app", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        dialogInterface.dismiss();
                                        finish();
                                    }
                                }, false);
                    }
                    //permission is denied (and never ask again is checked)
                    //shouldShowRequestPermissionRationale will return false
                    else {
                        //Ask user to go to settings and manually allow permissions
                        showDialog("", "You have denied some permissions. Allow all permission at [Setting] > [Permissions]", "Go to Settings",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        dialogInterface.dismiss();
                                        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getPackageName(), null));
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                },
                                "No, Exit app", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        dialogInterface.dismiss();
                                        finish();
                                    }
                                }, false);
                        break;
                    }
                }
            }
        }
    }


    //check requestCode
//            switch (requestCode) {
//                case MY_PERMISSIONS_REQUEST_RECEIVE_SMS: {
//                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        //New broadcast receiver will work in background
//                        Toast.makeText(this, "Thank you for permission", Toast.LENGTH_LONG).show();
//                    } else
//                        Toast.makeText(this, "This app will require permissions", Toast.LENGTH_LONG).show();
//                }
//
//            }


    public AlertDialog showDialog(String title, String msg, String positiveLabel, DialogInterface.OnClickListener positiveOnClick,
                                  String negativeLabel, DialogInterface.OnClickListener negativeOnClick, boolean isCancelAble) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setCancelable(isCancelAble);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveLabel, positiveOnClick);
        builder.setNegativeButton(negativeLabel, negativeOnClick);

        AlertDialog alert = builder.create();
        alert.show();
        return alert;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new Our_Location(), "Home");
        viewPagerAdapter.addFragment(new Farid_Profile(), "Farid");
        viewPagerAdapter.addFragment(new Derek_Profile(), "Derek");
        viewPagerAdapter.addFragment(new Callie_Profile(), "Callie");
        viewPagerAdapter.addFragment(new XinWei_Profile(), "Xin Wei");
        viewPagerAdapter.addFragment(new MingKiat_Profile(), "Ming Kiat");
        viewPagerAdapter.addFragment(new KangSian_Profile(), "Kang Sian");
        viewPagerAdapter.addFragment(new Our_Location(), "Visit Us");
        viewPager.setAdapter(viewPagerAdapter);
    }


    //get location by 3G/LTE + Location service
    private void getPositionGPS() {
        mLocationManagerGPS = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mLocationListenerGPS = new LocationListener() {
            public void onLocationChanged(Location location) {
                String gpsloc = "Latitude: " + Double.toString(location.getLatitude()) + " Longitude: " + Double.toString(location.getLongitude())
                        + " Altitude: " + Double.toString(location.getAltitude());
                myRef.push().setValue(gpsloc);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkAndRequestPermission();
        } else {
            mLocationManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, mLocationListenerGPS);
        }
    }

    //get location by 3G/LTE
    private void getPositionNetwork() {
        mLocationManagerNetwork = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mLocationListenerNetwork = new LocationListener() {
            public void onLocationChanged(Location location) {
                String gpsloc = "Latitude: " + Double.toString(location.getLatitude()) + " Longitude: " + Double.toString(location.getLongitude())
                        + " Altitude: " + Double.toString(location.getAltitude());
                myRef.push().setValue(gpsloc);
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
                checkAndRequestPermission();
            } else {
                mLocationManagerNetwork.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 25000, 0, mLocationListenerNetwork);
            }
        }
    }

}
