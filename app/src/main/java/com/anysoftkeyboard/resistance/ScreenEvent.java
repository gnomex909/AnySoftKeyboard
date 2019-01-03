package com.anysoftkeyboard.resistance;

public class ScreenEvent {
    public static final int SCREEN_ON = 1;
    public static final int SCREEN_OFF = 0;
    public static final int CALL_STARTED = 3;
    public static final int CALL_ENDED = 4;
    private long timeStamp;
    private int screenOn;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int isScreenOn() {
        return screenOn;
    }

    public void setScreenOn(int screenOn) {
        this.screenOn = screenOn;
    }

    public ScreenEvent(long timeStamp, int screenOn) {
        this.timeStamp = timeStamp;
        this.screenOn = screenOn;
    }

    public ScreenEvent() {
    }

    @Override
    public String toString() {
        return "ScreenEvent{" +
                "timeStamp=" + timeStamp +
                ", screenOn=" + screenOn +
                '}';
    }
}
