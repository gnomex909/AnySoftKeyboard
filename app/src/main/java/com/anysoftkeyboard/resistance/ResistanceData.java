package com.anysoftkeyboard.resistance;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResistanceData {
    public ArrayList<AppUsageInfo> getUsageStatistics(Context mContext, long startTime, long currTime) {

        UsageEvents.Event currentEvent;
        List<UsageEvents.Event> allEvents = new ArrayList<>();
        HashMap<String, AppUsageInfo> map = new HashMap<String, AppUsageInfo>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        UsageStatsManager mUsageStatsManager =  (UsageStatsManager)
                mContext.getSystemService(Context.USAGE_STATS_SERVICE);

        assert mUsageStatsManager != null;
        UsageEvents usageEvents = mUsageStatsManager.queryEvents(startTime, currTime);

//capturing all events in a array to compare with next element

        while (usageEvents.hasNextEvent()) {
            currentEvent = new UsageEvents.Event();
            usageEvents.getNextEvent(currentEvent);
            if (currentEvent.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND ||
                    currentEvent.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                allEvents.add(currentEvent);
                String key = currentEvent.getPackageName();
// taking it into a collection to access by package name
                if (map.get(key)==null)
                    map.put(key,new AppUsageInfo(key));
            }
        }

//iterating through the arraylist
        for (int i=0;i<allEvents.size()-1;i++){
            UsageEvents.Event E0=allEvents.get(i);
            UsageEvents.Event E1=allEvents.get(i+1);

//for UsageTime of apps in time range
            if (E0.getEventType()==1 && E1.getEventType()==2
                    && E0.getClassName().equals(E1.getClassName())){
                long diff = E1.getTimeStamp()-E0.getTimeStamp();
                map.get(E0.getPackageName()).timeInForeground+= diff;
            }
        }
//transferred final data into modal class object
//        List<UsageStats> usageStats = mUsageStatsManager.queryUsageStats(INTERVAL_YEARLY,startTime, currTime);
//        for(UsageStats usageStat : usageStats){
//            String name = getAppNameFromPackage(usageStat.getPackageName(), mContext);
//            if(name != null) {
//                Log.d(TAG, "getUsageStatistics: " + name);
//            }
//        }
        return new ArrayList<>(map.values());
    }

    public ArrayList<ScreenEvent> getScreenEvents(Context context, long startTime, long endTime){
        ArrayList<ScreenEvent> screenEvents = ResistanceDatabase.getInstance(context).getScreenEvents(startTime,endTime);
        return screenEvents;
    }
    public String getAppNameFromPackage(String packageName, Context context) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> pkgAppsList = context.getPackageManager()
                .queryIntentActivities(mainIntent, 0);

        for (ResolveInfo app : pkgAppsList) {
            if (app.activityInfo.packageName.equals(packageName)) {
                return app.activityInfo.loadLabel(context.getPackageManager()).toString();
            }
        }
        return null;
    }
}
