package com.anysoftkeyboard.resistance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.anysoftkeyboard.AnySoftKeyboard.FOCUS_MODEL;
import static com.anysoftkeyboard.AnySoftKeyboard.KEYBOARD_USAGE_TIME;
import static com.anysoftkeyboard.AnySoftKeyboard.RESISTANCE_DRIVER;
import static com.anysoftkeyboard.resistance.ScreenEvent.CALL_ENDED;
import static com.anysoftkeyboard.resistance.ScreenEvent.CALL_STARTED;
import static com.anysoftkeyboard.resistance.ScreenEvent.SCREEN_OFF;
import static com.anysoftkeyboard.resistance.ScreenEvent.SCREEN_ON;

//import com.github.tony19.timber.loggly.LogglyTree;
//import timber.log.Timber;

/**
 * Created by Gnomex on 01.03.2018.
 */

public class ResistanceMaths {
    private static final String TAG = "ResistanceMaths";
    public static final long period = 1000*60*30;
    public static final String LAST_CALCULATION ="LastCalculationTime";
    private static final String PREVIOUS_DECISION = "PreviousResistanceDecision";
    private int amountOfModels = 2;
    private long treshold = 1000*10;
    private double alpha = 0.8;

    public int getAmountOfModels() {
        return amountOfModels;
    }

    public double calculateFocusTime(Context context, ArrayList<AppUsageInfo> appUsageInfos, long startTime, long currTime){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //ResistanceMaths resistanceMaths = new ResistanceMaths();

            long keyboardUsageTime = sharedPreferences.getLong(KEYBOARD_USAGE_TIME, 0);
            int resistanceDriver = sharedPreferences.getInt(RESISTANCE_DRIVER, 0);
            StringBuffer message = new StringBuffer("");
            double calculatedKeyboardTime = 0;
            for (AppUsageInfo app : appUsageInfos) {
                if (app.getAppName() != null) {
                    message.append("<<" + app.getAppName() + " | " + app.timeInForeground + " ms>> ");
                    calculatedKeyboardTime += app.getKeyboardFocus() * app.timeInForeground;
                }
            }

            double primalFocusTime = primalFocusModel(appUsageInfos, keyboardUsageTime);
            double primalDecision = primalFocusTime / period;

            double easyFocusTime = easyFocusModel(appUsageInfos, keyboardUsageTime, calculatedKeyboardTime);
            double easyDecision = easyFocusTime / period;

            int activeModel = sharedPreferences.getInt(FOCUS_MODEL, 0);
            Map<String, Object> appTime = new HashMap<>();
            for (AppUsageInfo app : appUsageInfos) {
                if (app.getAppName() != null) {
                    appTime.put(app.getAppName(), app.timeInForeground);
                }
            }




            boolean logStatus = new ResistanceLogger().regularLogger(context,startTime, currTime, resistanceDriver,activeModel, primalDecision, primalFocusTime, easyDecision, easyFocusTime, appTime);
            Log.d(TAG, "calculateFocusTime: ResistanceDriver: " + resistanceDriver + "| ActiveModel: " + activeModel + " | AppUsages: " + message + "<<Keyboard | " + keyboardUsageTime + " ms>>");
            Log.d(TAG, "calculateFocusTime: PrimalDecision: " + primalDecision + "| PrimalFocusTime: " + primalFocusTime + " ms");
            Log.d(TAG, "calculateFocusTime:EasyDecision: " + primalDecision + "| EasyFocusTime: " + primalFocusTime + " ms\"");


            double decision;
            switch (activeModel) {
                case 0:
                    decision = 0.00;
                    break;
                case 1:
                    decision = easyDecision;
                    break;
                case 2:
                    decision = primalDecision;
                    break;
                default:
                    decision = 0.00;
                    break;
            }

            Log.d(TAG, "onReceive: resistanceDriver equals " + resistanceDriver);
            Log.d(TAG, "calculateFocusTime: decision equals to " + decision);
            sharedPreferences.edit().putLong(KEYBOARD_USAGE_TIME, 0).apply();
            return decision;
    };

    private double primalFocusModel(ArrayList<AppUsageInfo> appUsageInfos, long keyboardUsageTime){

        double focusTime = 0;

        for(AppUsageInfo app :appUsageInfos) {
            Log.d(TAG, "primalFocusModel: " + app.toString());
            focusTime += (app.timeInForeground - (app.timeInForeground*app.getKeyboardFocus())) * app.getUserFocus();
        }
        Log.d(TAG, "calculateFocusTime: App Focus equals " +focusTime);
        focusTime = focusTime +keyboardUsageTime;
        Log.d(TAG, "primalFocusModel: Focus Time equals to " + focusTime);

        return focusTime;
    };

    private double easyFocusModel(ArrayList<AppUsageInfo> appUsageInfos, long keyboardUsageTime, double calculatedKeyboardTime){
        double focusTime = 0;
        double corrector = keyboardUsageTime/calculatedKeyboardTime;
        for(AppUsageInfo app :appUsageInfos) {
            Log.d(TAG, "calculateFocusTime: " + app.toString());
            focusTime += (app.timeInForeground - (app.timeInForeground*app.getKeyboardFocus()*corrector)) * app.getUserFocus();
        }
        Log.d(TAG, "calculateFocusTime: App Focus equals " +focusTime);
        focusTime += keyboardUsageTime;
        Log.d(TAG, "easyFocusModel: Focus Time equals to " + focusTime);

        return  focusTime;
    }

    public double screenEventMaths(Context context, ArrayList<ScreenEvent> screenEvents, ArrayList<AppUsageInfo> appUsageInfos, long start, long end){
        boolean screenOpened = true;
        long starTime = start;
        long endTime = end;
        long screenOnTimer = 0;
        for(int i = 0; i < screenEvents.size(); i++){
            ScreenEvent e = screenEvents.get(i);

            switch (e.isScreenOn()){
                case SCREEN_ON:
                    starTime = System.currentTimeMillis();
                    screenOpened = true;
                    break;
                case SCREEN_OFF:
                    if(screenOpened){
                        endTime = System.currentTimeMillis();
                        long onTime = endTime = starTime;
                        if(onTime <= treshold){
                            onTime = treshold;
                        }
                        screenOnTimer += onTime;
                        screenOpened = false;
                    }
                    break;
                case CALL_STARTED:
                    screenOpened = false;
                    break;
                case CALL_ENDED:
                    screenOpened = false;
                    break;
                default:
                    break;
            }
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        double current = (screenOnTimer/(end-start));
        if(current>1){
            current = 1;
        }
        double lastDecision = Double.parseDouble(sharedPreferences.getString(PREVIOUS_DECISION, 0+""));
        double decision = (lastDecision*alpha) + ((1- alpha)* current);
        sharedPreferences.edit().putString(PREVIOUS_DECISION, decision+"").apply();

        //Część przygotowujące dane do logów i czyszcząca klawiaturę
        Map<String, Object> appTime = new HashMap<>();
        for (AppUsageInfo app : appUsageInfos) {
            if (app.getAppName() != null) {
                appTime.put(app.getAppName(), app.timeInForeground);
            }
        }

        int resistanceDriver = sharedPreferences.getInt(RESISTANCE_DRIVER, 0);
        int activeModel = sharedPreferences.getInt(FOCUS_MODEL, 0);
        boolean logStatus = new ResistanceLogger().regularScreenLogger(context,start, end, resistanceDriver,activeModel, appTime, screenEvents, lastDecision, decision, screenOnTimer);
        sharedPreferences.edit().putLong(KEYBOARD_USAGE_TIME, 0).apply();
        if(activeModel == 0) {
            return 0;
        }else{
            return decision;
        }
    }
}
