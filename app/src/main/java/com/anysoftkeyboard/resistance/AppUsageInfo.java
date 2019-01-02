package com.anysoftkeyboard.resistance;

/**
 * Created by Gnomex on 01.03.2018.
 */

public class AppUsageInfo {
    private String appName, packageName;
    long timeInForeground;
    private double userFocus;
    private double keyboardFocus;

    AppUsageInfo(String pName) {
        this.packageName=pName;
        userFocus = 0;
    }

    @Override
    public String toString() {
        return "AppUsageInfo{" +
                "appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", timeInForeground=" + timeInForeground +
                ", userFocus=" + userFocus +
                ", keyboardFocus=" + keyboardFocus +
                '}';
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setUserFocus(double userFocus) {
        this.userFocus = userFocus;
    }

    public String getPackageName() {
        return packageName;
    }

    public double getUserFocus() {
        return userFocus;
    }

    public double getKeyboardFocus() {
        return keyboardFocus;
    }

    public void setKeyboardFocus(double keyboardFocus) {
        this.keyboardFocus = keyboardFocus;
    }
}
