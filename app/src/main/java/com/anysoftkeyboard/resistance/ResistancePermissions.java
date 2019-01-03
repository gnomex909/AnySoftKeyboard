package com.anysoftkeyboard.resistance;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.menny.android.anysoftkeyboard.R;

public class ResistancePermissions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resistance_permissions);

        if (getApplicationContext().checkCallingOrSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted, therefore prompt the user to grant permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE},
                    0);
        }

        if (getApplicationContext().checkCallingOrSelfPermission(android.Manifest.permission.PROCESS_OUTGOING_CALLS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted, therefore prompt the user to grant permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.PROCESS_OUTGOING_CALLS},
                    0);
        }
        finish();
    }
}
