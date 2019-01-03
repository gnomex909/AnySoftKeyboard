package com.anysoftkeyboard.resistance;

import android.content.Context;
import android.util.Log;

import java.util.Date;

import static com.anysoftkeyboard.resistance.ScreenEvent.CALL_ENDED;
import static com.anysoftkeyboard.resistance.ScreenEvent.CALL_STARTED;

public class ResistancePhoneReceiver extends ResistanceCallDetector {
    private static final String TAG = "ResistancePhoneReceiver";
    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
        Log.d(TAG, "onIncomingCallReceived: Incoming call detected gnmx");
        ResistanceDatabase.getInstance(ctx).addScreenEvent(new ScreenEvent(System.currentTimeMillis(),CALL_STARTED));
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
        Log.d(TAG, "onIncomingCallAnswered: gnmx");
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d(TAG, "onIncomingCallEnded: gnmx");
        ResistanceDatabase.getInstance(ctx).addScreenEvent(new ScreenEvent(System.currentTimeMillis(),CALL_ENDED));
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.d(TAG, "onOutgoingCallStarted: gnmx");
        ResistanceDatabase.getInstance(ctx).addScreenEvent(new ScreenEvent(System.currentTimeMillis(),CALL_STARTED));
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d(TAG, "onOutgoingCallEnded: gnmx");
        ResistanceDatabase.getInstance(ctx).addScreenEvent(new ScreenEvent(System.currentTimeMillis(),CALL_ENDED));
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Log.d(TAG, "onMissedCall: gnmx");
        ResistanceDatabase.getInstance(ctx).addScreenEvent(new ScreenEvent(System.currentTimeMillis(),CALL_ENDED));
    }

    @Override
    public void onCallStateChanged(Context context, int state, String number) {
        super.onCallStateChanged(context, state, number);
    }
}
