package com.andrewsosa.quietly;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

public class DispatchActivity extends AppCompatActivity {

    final int MY_PERMISSIONS_REQUEST_NOTIFICATION_POLICY = 100;

    SharedPreferences sp;
    NotificationManager n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch);


        n = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        sp = getSharedPreferences("quietly", MODE_PRIVATE);


        if(n.isNotificationPolicyAccessGranted()) {

            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;

        } else {


            if(!sp.contains("firsttime")) {
                showPermissionDialog();
                SharedPreferences.Editor e = sp.edit();
                e.putBoolean("firsttime", false);
                e.apply();
            }

            findViewById(R.id.permissions).setVisibility(View.VISIBLE);

        }

        findViewById(R.id.permissions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPermissionDialog();
            }
        });

    }


    /*
     *
     *  PERMISSION HANDLERS
     *
     */

    public void showPermissionDialog() {

        new MaterialDialog.Builder(this)
                .title("Allow Notification filter permission?")
                .content("Gib permisshuns pls?")
                .positiveText("Allow")
                .negativeText("Deny")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        Toast.makeText(DispatchActivity.this, "This app will not work without Notification " +
                                "filter permissions.", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        Intent i = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                        startActivityForResult(i, 999);

                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(n.isNotificationPolicyAccessGranted()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
