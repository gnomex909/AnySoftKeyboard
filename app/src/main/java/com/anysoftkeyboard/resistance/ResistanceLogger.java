package com.anysoftkeyboard.resistance;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ResistanceLogger {
    private static final String TAG = "ResistanceLogger";
    public boolean regularLogger(Context context, long startTime, long currTime, int resistanceDriver, int activeModel, double primalDecision, double primalFocusTime, double easyDecision, double easyFocusTime, Map<String, Object> appTime) {
        Map<String, Object> calc = new HashMap<>();
        Log.d(TAG, "regularLogger: starts");
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        DateFormat dateFormat2 = new SimpleDateFormat("yyyy.MM.dd");
        DateFormat dateFormat3 = new SimpleDateFormat("HH:mm:ss");
        calc.put("date", dateFormat2.format(new Date(currTime)));
        calc.put("startTime", dateFormat3.format(new Date(startTime)));
        calc.put("endTime", dateFormat3.format(new Date(currTime)));
        calc.put("rDriver", resistanceDriver);
        calc.put("activeModel", activeModel);
        calc.put("primDec", primalDecision);
        calc.put("primFTime", primalFocusTime);
        calc.put("easyDec", easyDecision);
        calc.put("easyFTime", easyFocusTime);
        calc.put("appUsages", appTime);
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connec != null && (
                (connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) ||
                        (connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED))) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            if (mAuth.getCurrentUser() != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();


                db.collection("UserLogs").document(mAuth.getCurrentUser().getEmail())
                        .collection("regularCalculation").document(dateFormat.format(new Date(currTime))).set(calc)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
            }
        }
        return true;
    }
    public boolean regularScreenLogger(Context context, long startTime, long currTime, int resistanceDriver, int activeModel, Map<String, Object> appTime, ArrayList<ScreenEvent> screenEvents, double lastDecision, double decision, long screenTime) {
        Map<String, Object> calc = new HashMap<>();
        Map<String, Integer> screenEventsMap = new HashMap<>();
        DateFormat dateFormat3 = new SimpleDateFormat("HH:mm:ss");
        for(ScreenEvent e : screenEvents){
            screenEventsMap.put(dateFormat3.format(new Date(e.getTimeStamp())), e.isScreenOn());
        }
        Log.d(TAG, "regularLogger: starts");
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        DateFormat dateFormat2 = new SimpleDateFormat("yyyy.MM.dd");
        calc.put("date", dateFormat2.format(new Date(currTime)));
        calc.put("startTime", dateFormat3.format(new Date(startTime)));
        calc.put("endTime", dateFormat3.format(new Date(currTime)));
        calc.put("rDriver", resistanceDriver);
        calc.put("activeModel", activeModel);
        calc.put("decision", decision);
        calc.put("lastDecision", lastDecision);
        calc.put("screenOnTime", screenTime);
        calc.put("appUsages", appTime);
        calc.put("screenEvents", screenEventsMap);
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connec != null && (
                (connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) ||
                        (connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED))) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            if (mAuth.getCurrentUser() != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();


                db.collection("UserLogs").document(mAuth.getCurrentUser().getEmail())
                        .collection("regularCalculation").document(dateFormat.format(new Date(currTime))).set(calc)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
            }
        }
        return true;
    }
    public boolean keyboardLogger(Context context, int logLevel, long tempTime, long resEndTime, int resistanceDriver, long usageTime, int tempPress, List<Double> eventList){
        Log.d(TAG, "keyboardLogger: Starts");
        Log.d(TAG, "keyboardLogger: LogLevel is " + logLevel);
        if(logLevel>1) {
            ConnectivityManager connec = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connec != null && (
                    (connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) ||
                            (connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED))) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                eventList.add(Double.parseDouble((System.currentTimeMillis() - tempTime) + "." + "99"));
                Map<String, Object> events = new HashMap<>();
                double timeBetweenPresses = 0;

                if (tempPress != 0) {
                    timeBetweenPresses = usageTime / tempPress;
                }
                DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                DateFormat dateFormat2 = new SimpleDateFormat("yyyy.MM.dd");
                DateFormat dateFormat3 = new SimpleDateFormat("HH:mm:ss.SSS");
                events.put("date", dateFormat2.format(new Date(tempTime)));
                events.put("startTime", dateFormat3.format(new Date(tempTime)));
                events.put("endTime", dateFormat3.format(new Date(resEndTime)));
                events.put("rDriver", resistanceDriver);
                events.put("usageTime", usageTime);
                events.put("kPressed", tempPress);
                events.put("freq", timeBetweenPresses);
                if(logLevel>1)
                    events.put("eList", eventList);
                Log.d(TAG, "keyboardLogger: " + mAuth);
                Log.d(TAG, "keyboardLogger: " + mAuth.getCurrentUser());
                if(mAuth.getCurrentUser()!= null)
                    db.collection("UserLogs").document(mAuth.getCurrentUser().getEmail())
                            .collection("KeyboardEvents").document(dateFormat.format(Calendar.getInstance().getTime()))
                            .set(events)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });
                ;}
        }
        return true;
    }
}
