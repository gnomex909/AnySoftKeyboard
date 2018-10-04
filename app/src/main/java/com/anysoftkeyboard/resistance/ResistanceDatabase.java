package com.anysoftkeyboard.resistance;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gnomex on 01.03.2018.
 */

public class ResistanceDatabase extends SQLiteOpenHelper {
    private static final String TAG = "ResistanceDatabase";
    private static ResistanceDatabase resistanceDatabase;
    private static final String DATABASE_NAME = "ResistanceDatabase";
    private static final String APP_TABLE = "appList";
    private static final String EVENT_TABLE = "eventList";
    private static final int DATABASE_VERSION = 2;
    public static synchronized ResistanceDatabase getInstance(Context context){
        if(resistanceDatabase == null){
            resistanceDatabase = new ResistanceDatabase(context);
        }
        return resistanceDatabase;
    };

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE IF NOT EXISTS " + APP_TABLE + "(appName TEXT, userFocus DOUBLE, keyboardFocus DOUBLE);";
        Log.d(TAG, "createAppDatabase: SQL sentence: " + sql);
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('YouTube', 0.90, 0.10);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Kalendarz', 0.70, 0.30);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Calendar', 0.70, 0.30);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Email', 0.90, 0.40);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Sklep Play', 0.50, 0.15);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Play Store', 0.50, 0.15);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Wiadomości', 0.90, 0.75);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Messages', 0.85, 0.75);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Instagram', 0.65, 0.20);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Gmail', 0.90, 0.40);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Mapy', 0.15, 0.05);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Maps', 0.15, 0.05);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Audioteka', 0.05, 0.05);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Messenger', 0.90, 0.75);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Ustawienia', 0.95, 0.00);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Settings', 0.95, 0.00);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Whatsapp', 0.90, 0.66);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Chrome', 0.80, 0.15);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Dyktafon', 0.05, 0.05);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Recorder', 0.05, 0.05);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Internet', 0.80, 0.15);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Discord', 0.90, 0.66);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Poweramp', 0.05, 0.05);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('MX Player', 0.90, 0.05);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Amazon Kindle', 0.90, 0.05);";
        sqLiteDatabase.execSQL(sql);
        sql = "INSERT INTO " + APP_TABLE + " VALUES('Adblock Browser', 0.80, 0.15);";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS " + EVENT_TABLE + "(timestamp INTEGER, eventType INTEGER);";
        sqLiteDatabase.execSQL(sql);
    }

    private ResistanceDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "createAppDatabase: starts");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + APP_TABLE);
            onCreate(db);
        }
    }

    public ArrayList<AppUsageInfo> getUserFocus(ArrayList<AppUsageInfo> appUsageInfos){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor query = sqLiteDatabase.rawQuery("SELECT * FROM "+ APP_TABLE +";", null);
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
    public void addScreenEvent(ScreenEvent screenEvent){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL("INSERT INTO " + EVENT_TABLE +" VALUES(" + screenEvent.getTimeStamp()+","+screenEvent.isScreenOn()+");");
    }
    public List<ScreenEvent> getScreenEvents(long startTime, long endTime){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ArrayList<ScreenEvent> screenEvents = new ArrayList<>();
        Cursor query = sqLiteDatabase.query(EVENT_TABLE, null, "timestamp > ? AND timestamp < ?", new String[] {""+startTime,""+endTime}, null, null, null);
        if(query.moveToFirst()){
            do{
                ScreenEvent screenEvent = new ScreenEvent();
                screenEvent.setTimeStamp(query.getLong(0));
                screenEvent.setScreenOn(query.getInt(1));
                screenEvents.add(screenEvent);
            }while(query.moveToNext());
        }
        return screenEvents;
    }
}
