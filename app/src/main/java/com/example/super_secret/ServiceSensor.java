package com.example.super_secret;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;
import androidx.annotation.Nullable;


public class ServiceSensor extends Service {
        private Context ctx;
        private static final String TAG = "Myactivity";
        private int counter = 0;

        public ServiceSensor()
        {

        }
        public ServiceSensor(Context applicationContext)
        {
            super();
        }
        @Nullable
        @Override
        public IBinder onBind(Intent intent)
        {
            return null;
        }
        public void onCreate()
        {
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startID)
        {
            super.onStartCommand(intent, flags, startID);
            startTimer();
            return START_STICKY;
        }
        @Override
        public void onDestroy()
        {
            super.onDestroy();
            Intent broadcastIntent = new Intent(this, ServiceReceiver.class);
            sendBroadcast(broadcastIntent);

        }
        private Timer timer;
        private TimerTask timerTask;

        public void startTimer()
        {
            timer = new Timer();
            initializeTimerTask();
            timer.schedule(timerTask,1000,1000);

        }
        public void initializeTimerTask()
        {
            timerTask = new TimerTask() {
                public void run()
                {
                    Log.i("in Timer","in timer ++++" + (counter++));
                }
            };
        }
}
