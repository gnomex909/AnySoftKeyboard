package com.anysoftkeyboard.resistance;

import android.content.Context;

import java.util.Date;

public class ResistancePhoneReceiver extends ResistanceCallDetector {
    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {

    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {

    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {

    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {

    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {

    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {

    }

    @Override
    public void onCallStateChanged(Context context, int state, String number) {
        super.onCallStateChanged(context, state, number);
    }
}
