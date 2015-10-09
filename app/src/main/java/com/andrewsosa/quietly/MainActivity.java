package com.andrewsosa.quietly;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final int MY_PERMISSIONS_REQUEST_NOTIFICATION_POLICY = 100;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        final EventAdapter adapter = new EventAdapter(this, new ArrayList<Event>());
        recyclerView.setAdapter(adapter);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                TimePickerFragment timePicker = new TimePickerFragment();
                timePicker.assignMethod(new TimePickerReceiver() {
                    @Override
                    public void receiveTime(int hour, int minute) {
                        adapter.addElement(onNewEvent(new Event("Label", hour, minute)));
                    }
                });
                timePicker.show(getFragmentManager(), "timePicker");

            }
        });

        ParseQuery<Event> query = Event.getQuery();
        query.fromLocalDatastore();
        query.orderByAscending("comparableTime");
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> list, ParseException e) {
                if (e == null) {
                    adapter.replaceDataset(list);
                } else {
                    Log.e("onCreate query find", e.getMessage());
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        int filter = -2;

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_filter_all:
                filter = NotificationManager.INTERRUPTION_FILTER_ALL;
                break;
            case R.id.action_filter_priority:
                filter = NotificationManager.INTERRUPTION_FILTER_PRIORITY;
                break;
            case R.id.action_filter_alarms:
                filter = NotificationManager.INTERRUPTION_FILTER_ALARMS;
                break;
            case R.id.action_filter_none:
                filter = NotificationManager.INTERRUPTION_FILTER_NONE;
                break;
        }

        if(filter != -2) {
           handleNotificationChange(filter);
        }

        return super.onOptionsItemSelected(item);
    }

    public void handleNotificationChange(int filter) {
        if(hasPermission(Manifest.permission.ACCESS_NOTIFICATION_POLICY)) {
            try {
                NotificationManager n = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if(n.isNotificationPolicyAccessGranted()) n.setInterruptionFilter(filter);
                else {
                    Log.d("set filter", "Access not granted by Notification Manager");

                    Intent i = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivity(i);
                }
            } catch (Exception e) {
                Log.e("notifications", e.getMessage());
            }
        } else {
            Snackbar.make(fab, "Notification permissions denied.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public boolean hasPermission(String requestedPermission) {

        Log.d("permissions", "requesting permissions");

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,requestedPermission)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d("permissions", "permissions denied?");


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_NOTIFICATION_POLICY)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(fab, "Gib permissuns pls", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                try {
                    return hasPermission(requestedPermission);
                } catch (Exception e) {
                    Log.e("hasPermission", e.getMessage());
                    return false;
                }

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{requestedPermission},
                        MY_PERMISSIONS_REQUEST_NOTIFICATION_POLICY);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

            return false;

        } else {
            Log.d("permissions", "permissions granted?");

            return true;
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_NOTIFICATION_POLICY: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Snackbar.make(fab, "Notification access granted", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                } else {

                    Log.d("on result", "permission denied?");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
        Log.d("butts", "lol butts");

    }

    private Event onNewEvent(Event e) {
        e.pinInBackground();
        return e;
    }

}
