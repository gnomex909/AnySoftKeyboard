package com.anysoftkeyboard.resistance;

public class ResistanceChanger {
    public int levelChanger(double decision, int resistanceDriver){

        if (decision > 0.33) {
            if (resistanceDriver != 4) {
                resistanceDriver++;
//                    sharedPreferences.edit().putBoolean(LEVEL_CHANGED, true).apply();
            }

        } else {
            if (resistanceDriver != 0) {
                resistanceDriver--;
//                    sharedPreferences.edit().putBoolean(LEVEL_CHANGED, true).apply();
            }
        }
        if (decision > 0.50) {
            if (resistanceDriver != 4)
                resistanceDriver++;


        }


        return  resistanceDriver;
    }
}
