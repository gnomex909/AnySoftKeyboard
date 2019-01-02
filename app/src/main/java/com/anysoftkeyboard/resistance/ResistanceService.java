package com.anysoftkeyboard.resistance;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.anysoftkeyboard.AnySoftKeyboard;

public class ResistanceService extends Service {
    private static final String TAG = "ResistanceService";
    private final String CHANNEL_ID = "2027";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerScreenReceiver();
        startInForeground();
    }
    private void startInForeground(){
        Log.d(TAG, "startInForeground: Started");
        Intent notificationIntent = new Intent(this, AnySoftKeyboard.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Resistance Keyboard")
                .setContentText("Screen usage registration service is running")
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"Resistance Keyboard", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Screen usage registration service is running");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(2027, notification);
    }

    private void registerScreenReceiver(){
        Log.d(TAG, "registerScreenReceiver: Started");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: Started");
                ScreenEvent screenEvent;
                if ("android.intent.action.SCREEN_ON".equals(intent.getAction())) {
                    screenEvent = new ScreenEvent(System.currentTimeMillis(), 1);
                    Log.d(TAG, "onReceiveService: Screen on");

                }else{
                    screenEvent = new ScreenEvent(System.currentTimeMillis(), 0);
                    Log.d(TAG, "onReceiveService: Screen off");
                }
                ResistanceDatabase resistanceDatabase = ResistanceDatabase.getInstance(context.getApplicationContext());
                resistanceDatabase.addScreenEvent(screenEvent);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(broadcastReceiver, filter);
    }
}
