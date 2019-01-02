package com.anysoftkeyboard.resistance;

import android.content.Context;
import android.util.Log;

import com.anysoftkeyboard.addons.AddOnsFactory;
import com.anysoftkeyboard.theme.KeyboardTheme;
import com.menny.android.anysoftkeyboard.AnyApplication;

import java.util.Random;


/**
 * Created by Gnomex on 29.10.2017.
 */

public class ThemeChanger {
    private static final String TAG="ThemeChanger";
    private static final String theme_id = "8774f99e-fb4a-49fa-b8d0-4083f762250a";
    private static final String light_theme_id ="1f593f8f-0457-4ba1-9d26-11cc69ac24f4";
    public ThemeChanger(){

    }
    public void simpleThemeChanger(Context context){
        Log.d(TAG, "simpleThemeChanger: starts");
        AddOnsFactory<KeyboardTheme> mFactory = AnyApplication.getKeyboardThemeFactory(context);
        String[] id = {"8774f99e-fb4a-49fa-b8d0-4083f762250a0","8774f99e-fb4a-49fa-b8d0-4083f762250a1","8774f99e-fb4a-49fa-b8d0-4083f762250a2"};
        Random r = new Random();
        int rand = r.nextInt(3);
        Log.d(TAG, "simpleThemeChanger: rand is " + rand);
        mFactory.setAddOnEnabled(id[rand], true);
    }
    public void specificThemeChanger(Context context, int ressistanceDriver, boolean lightTheme){
        Log.d(TAG, "specificThemeChanger: starts: " + ressistanceDriver);
        AddOnsFactory<KeyboardTheme> mFactory = AnyApplication.getKeyboardThemeFactory(context);
        String theme;
        if(lightTheme){
            theme = light_theme_id;
        }
        else {
            theme = theme_id;
        }
        theme += ressistanceDriver;
        Log.d(TAG, "specificThemeChanger: Theme is " + theme);

        mFactory.setAddOnEnabled(theme, true);
    }
}
