package com.anysoftkeyboard.resistance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.anysoftkeyboard.AnySoftKeyboard.AUTH_EMAIL;
import static com.anysoftkeyboard.AnySoftKeyboard.AUTH_PASSWORD;
import static com.anysoftkeyboard.AnySoftKeyboard.DEBUG_MODE;
import static com.anysoftkeyboard.AnySoftKeyboard.FIRST_TIME_USED;
import static com.anysoftkeyboard.AnySoftKeyboard.KEYBOARD_USAGE_TIME;
import static com.anysoftkeyboard.AnySoftKeyboard.KEY_LIMIT;
import static com.anysoftkeyboard.AnySoftKeyboard.LEVEL_CHANGED;
import static com.anysoftkeyboard.AnySoftKeyboard.LOG_LEVEL;
import static com.anysoftkeyboard.AnySoftKeyboard.RESISTANCE_DRIVER;
import static com.anysoftkeyboard.AnySoftKeyboard.THEME_SELECTED;

public class KeyboardModifiers {
    private static final String TAG = "KeyboardModifiers";
    private int debugTrigger;
    private long inactivityTimestamp;
    private long timeOfStart;
    private int keyPresses;
    private long timeOfEnd;
    private long inactivityTime;
    private ArrayList<Double> eventList;
    private long inactivityTreshold = 20 *1000;
    boolean debugMode;

    // uruchamiana przy pierwszym włączeniu, umieścić w sprawdzeniu czy to pierwsze odpalenie
    public void init(Context context, SharedPreferences sharedPreferences){
        ResistanceDatabase resistanceDatabase = ResistanceDatabase.getInstance(context);
        sharedPreferences.edit().putString(FIRST_TIME_USED,"It_was_already_booted_up").apply();
    };
    //Uruchamiane przy każdym głównym włączeniu aplikacji, w celu sprawdzenia czy użytkownik jest zalogowany, a w przeciwnym wypadku - stworzenia użytkownika
    public void logInit(SharedPreferences sharedPreferences){
        Log.d(TAG, "logInit: Starts");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "logInit: " + currentUser);
        if (currentUser == null) {
            Log.d(TAG, "logInit: Got there to setting up new user");
            Random rand = new Random();
            int value = rand.nextInt(100000);
            sharedPreferences.edit().putString(AUTH_EMAIL, "user" + value + "@gmail.com").apply();
            sharedPreferences.edit().putString(AUTH_PASSWORD, "password" + value).apply();
            mAuth.createUserWithEmailAndPassword(sharedPreferences.getString(AUTH_EMAIL, ""), sharedPreferences.getString(AUTH_PASSWORD, "")).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    }
                }
            });
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> nameData = new HashMap<>();
            nameData.put("userName", sharedPreferences.getString(AUTH_EMAIL, ""));
            db.collection("UserLogs").document(sharedPreferences.getString(AUTH_EMAIL, "")).set(nameData, SetOptions.merge());
}
    };
    public boolean keyboardStartup(Context context){
        Log.d(TAG, "keyboardStartup: starting");
        debugTrigger = 0;
        keyPresses = 0;
        inactivityTime = 0;
        timeOfStart = System.currentTimeMillis();
        inactivityTimestamp = System.currentTimeMillis();
        eventList = new ArrayList<>();
        eventList.add(Double.parseDouble((System.currentTimeMillis()-timeOfStart)+"."+11));
        new ResistanceAlarm().setAlarm(context);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean themeChanged = sharedPreferences.getBoolean(LEVEL_CHANGED, false);
        Log.d(TAG, "keyboardStartup: Theme changed equals " + themeChanged);
        if(themeChanged) {
            sharedPreferences.edit().putBoolean(LEVEL_CHANGED, false).apply();
//            sharedPreferences.edit().putBoolean(CHANGED_REACT, true).apply();
            boolean lightTheme = sharedPreferences.getBoolean(THEME_SELECTED, false);
            int rDriver = sharedPreferences.getInt(RESISTANCE_DRIVER, 0);
            new ThemeChanger().specificThemeChanger(context, rDriver, lightTheme);
            return true;
        }
        else{
            return false;
        }
    };
    public boolean keyboardEnding(Context context) {
        Log.d(TAG, "keyboardEnding: Starts");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Log.d(TAG, "keyboardEnding: Key presses equals " + keyPresses);
        if (keyPresses > 0) {
            timeOfEnd = System.currentTimeMillis();
            long keyboardUsageTime = sharedPreferences.getLong(KEYBOARD_USAGE_TIME, 0);
            long usageInstance = timeOfEnd - timeOfStart - inactivityTime;
            keyboardUsageTime += usageInstance;
            sharedPreferences.edit().putLong(KEYBOARD_USAGE_TIME, keyboardUsageTime).apply();
            inactivityTime = 0;
            Log.d(TAG, "keyboardEnding: " + eventList);
            if (eventList != null) {
                int resistanceDriver = sharedPreferences.getInt(RESISTANCE_DRIVER, 0);
                int logLevel = sharedPreferences.getInt(LOG_LEVEL, 2);
                boolean logStatus = new ResistanceLogger().keyboardLogger(context, logLevel, timeOfStart, timeOfEnd, resistanceDriver, usageInstance, keyPresses, eventList);
            }
        }

        debugMode = sharedPreferences.getBoolean(DEBUG_MODE, false);
        if (debugMode) {
            int limit = sharedPreferences.getInt(KEY_LIMIT, 7);
            if (keyPresses < limit && limit == 7) {
                int rDriver = sharedPreferences.getInt(RESISTANCE_DRIVER, 0);
                if (rDriver != 0) {
                    rDriver--;
                    sharedPreferences.edit().putInt(RESISTANCE_DRIVER, rDriver).apply();
                    sharedPreferences.edit().putBoolean(LEVEL_CHANGED, true).apply();
                }
            }
        }
        keyPresses =0;
        return debugMode;
    };
    public void handleKey(Context context, boolean isFunction){
        Log.d(TAG, "handleKey: starts");
        keyPresses++;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        debugMode = sharedPreferences.getBoolean(DEBUG_MODE, false);
        long currentTime = System.currentTimeMillis();
        if(currentTime - inactivityTimestamp > inactivityTreshold){
            inactivityTime += (currentTime - inactivityTimestamp);
        }
        inactivityTimestamp = currentTime;
        if(eventList != null){
            long event = System.currentTimeMillis() - timeOfStart;
            if(isFunction) {
                eventList.add(Double.parseDouble("" + event + "." + 66));
            }
            else{
                eventList.add(Double.parseDouble("" + event + "." + 44));
            }
        }
        if(debugMode){
            debugTrigger++;
            int limit = sharedPreferences.getInt(KEY_LIMIT,7);
            if(debugTrigger>limit){
                int rDriver = sharedPreferences.getInt(RESISTANCE_DRIVER, 0);
                if (rDriver != 4)
                    rDriver++;
                sharedPreferences.edit().putInt(RESISTANCE_DRIVER, rDriver).apply();
                sharedPreferences.edit().putBoolean(LEVEL_CHANGED, true).apply();
            }
        }
    }
    public void handleNonFunctionKey(){

    }
}
