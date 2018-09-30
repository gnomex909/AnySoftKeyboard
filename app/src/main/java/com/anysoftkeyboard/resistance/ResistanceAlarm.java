package com.anysoftkeyboard.resistance;

import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import static com.anysoftkeyboard.AnySoftKeyboard.LEVEL_CHANGED;
import static com.anysoftkeyboard.AnySoftKeyboard.RESISTANCE_DRIVER;
import static com.anysoftkeyboard.AnySoftKeyboard.THEME_SELECTED;
import static com.anysoftkeyboard.resistance.ResistanceMaths.LAST_CALCULATION;
import static com.anysoftkeyboard.resistance.ResistanceMaths.period;

/**
 * Created by Gnomex on 22.02.2018.
 */

public class ResistanceAlarm extends BroadcastReceiver {
    private static final String TAG = "ResistanceAlarm";
    public static final String RESISTANCE_ALARM = "com.anysoftkeyboard.alarms,RESISTANCE_ALARM";

    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Checking if user granted Application permissions to actually work on UsageStats Data.
        Log.d(TAG, "onReceive: started");
        boolean granted = false;
        AppOpsManager appOps = (AppOpsManager) context
                .getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), context.getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (context.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }


        if(granted) {
            //Safety check to avoid trying to get data for future time
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            long startTime = sharedPreferences.getLong(LAST_CALCULATION, (System.currentTimeMillis() - period));
            long currTime = startTime + period;
            Log.d(TAG, "calculateFocusTime: Starting");
            Log.d(TAG, "calculateFocusTime: Current time is " + currTime);
            Log.d(TAG, "calculateFocusTime: System time is " + System.currentTimeMillis());
            if (System.currentTimeMillis() >= (currTime - 2000)) {

                // Data gathering
                Log.d(TAG, "calculateFocusTime: Starting calculation");
                sharedPreferences.edit().putLong(LAST_CALCULATION, currTime).apply();
                ResistanceData resistanceData = new ResistanceData();
                ArrayList<AppUsageInfo> appUsageInfos = resistanceData.getUsageStatistics(context, startTime, currTime);
                for (AppUsageInfo app : appUsageInfos) {
                    app.setAppName(resistanceData.getAppNameFromPackage(app.getPackageName(), context));
                }
                ResistanceDatabase resistanceDatabase = new ResistanceDatabase();
                appUsageInfos = resistanceDatabase.getUserFocus(context, appUsageInfos);
                //Doing maths
                Log.d(TAG, "onReceive: Permission granted");
                mContext = context;
                ResistanceMaths resistanceMaths = new ResistanceMaths();
                boolean lightTheme = sharedPreferences.getBoolean(THEME_SELECTED, false);
                double decision = resistanceMaths.calculateFocusTime(context, appUsageInfos, startTime, currTime);
                int resistanceDriver = new ResistanceChanger().levelChanger(decision, sharedPreferences.getInt(RESISTANCE_DRIVER,0));
                sharedPreferences.edit().putInt(RESISTANCE_DRIVER, resistanceDriver).apply();

                sharedPreferences.edit().putBoolean(LEVEL_CHANGED, true).apply();
                Log.d(TAG, "onReceive: ending ");
                //  }
            }
        }
    }

    public void setAlarm(Context context) {
        Log.d(TAG, "setAlarm: started");

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, ResistanceAlarm.class);
            intent.setAction(ResistanceAlarm.RESISTANCE_ALARM);

  //          boolean isWorking = (PendingIntent.getBroadcast(context, 18051994, intent, PendingIntent.FLAG_NO_CREATE) != null);

//           if(!isWorking) {
               Log.d(TAG, "setAlarm: Setting up once again");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 18051994, intent, 0);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                long lastCalculation = sharedPreferences.getLong(LAST_CALCULATION, 0);
                Log.d(TAG, "setAlarm: last calculation is " + lastCalculation);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                Log.d(TAG, "setAlarm:  time in millis is " + calendar.getTimeInMillis());

                if ((calendar.getTimeInMillis() - lastCalculation) > period) {
                    Log.d(TAG, "setAlarm: Setting up now");
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 1000, AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
                }else{
                    Log.d(TAG, "setAlarm: setting up in 30 minutes");
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, lastCalculation + period, AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
             }
    }

    public void cancelAlarm(Context context) {
        Log.d(TAG, "cancelAlarm: started");
        Intent intent = new Intent(context, ResistanceAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

}