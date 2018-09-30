package com.anysoftkeyboard.resistance;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Gnomex on 01.03.2018.
 */

public class ResistanceDatabase {
    private static final String TAG = "ResistanceDatabase";
    public void createAppDatabase(Context context){
        Log.d(TAG, "createAppDatabase: starts");
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("AppDatabase.db", Context.MODE_PRIVATE,null);
        String sql = "DROP TABLE IF EXISTS appList";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS appList(appName TEXT, userFocus DOUBLE, keyboardFocus DOUBLE);";
        Log.d(TAG, "createAppDatabase: SQL sentence: " + sql);
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('YouTube', 0.90, 0.10);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Kalendarz', 0.70, 0.30);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Calendar', 0.70, 0.30);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Email', 0.90, 0.40);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Sklep Play', 0.50, 0.15);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Play Store', 0.50, 0.15);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Wiadomości', 0.90, 0.75);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Messages', 0.85, 0.75);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Instagram', 0.65, 0.20);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Gmail', 0.90, 0.40);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Mapy', 0.15, 0.05);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Maps', 0.15, 0.05);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Audioteka', 0.05, 0.05);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Messenger', 0.90, 0.75);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Ustawienia', 0.95, 0.00);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Settings', 0.95, 0.00);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Whatsapp', 0.90, 0.66);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Chrome', 0.80, 0.15);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Dyktafon', 0.05, 0.05);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Recorder', 0.05, 0.05);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Internet', 0.80, 0.15);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Discord', 0.90, 0.66);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Poweramp', 0.05, 0.05);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('MX Player', 0.90, 0.05);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Amazon Kindle', 0.90, 0.05);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO appList VALUES('Adblock Browser', 0.80, 0.15);";
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
    }
    public ArrayList<AppUsageInfo> getUserFocus(Context context, ArrayList<AppUsageInfo> appUsageInfos){
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("AppDatabase.db", Context.MODE_PRIVATE,null);

        Cursor query = sqLiteDatabase.rawQuery("SELECT * FROM appList;", null);
        if(query.moveToFirst()){
            for(AppUsageInfo app : appUsageInfos){
                if(app.getAppName()!= null) {
                    boolean nameFound = false;
                    do {
                        String sqlName = query.getString(0);
                        if (app.getAppName().equalsIgnoreCase(sqlName)) {
                            app.setUserFocus(query.getDouble(1));
                            app.setKeyboardFocus(query.getDouble(2));
                            nameFound = true;
                            break;
                        }
                    } while (query.moveToNext());

                    if (!nameFound)
                        app.setUserFocus(0.66);
                        app.setKeyboardFocus(0.33);

                    query.moveToFirst();
                }
            }
        }
        query.close();
        sqLiteDatabase.close();
        return appUsageInfos;
    }
}
