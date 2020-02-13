package com.example.super_secret;
import android.app.job.JobScheduler;
import android.os.Build;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class ServiceReceiver extends BroadcastReceiver {
    private static final String TAG = "Myactivity";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "About to start timer " + context.toString());
        context.startService(new Intent(context, ServiceSensor.class));

    }
}