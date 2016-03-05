package com.andrewsosa.quietly;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    EventAdapter mAdapter;
    AlarmReceiver alarmReceiver;

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
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .sizeResId(R.dimen.divider)
                .marginResId(R.dimen.rightmargin, R.dimen.rightmargin)
                .build());

        mAdapter = new EventAdapter(this, new ArrayList<Event>());
        recyclerView.setAdapter(mAdapter);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                /*TimePickerFragment timePicker = new TimePickerFragment();
                timePicker.assignMethod(new TimePickerReceiver() {
                    @Override
                    public void receiveTime(int hour, int minute) {
                        mAdapter.addElement(onNewEvent(new Event("Label", hour, minute)));
                    }
                });
                timePicker.show(getFragmentManager(), "timePicker");*/
                chooseFilter();

            }
        });

        ParseQuery<Event> query = Event.getQuery();
        query.fromLocalDatastore();
        query.orderByAscending("comparableTime");
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> list, ParseException e) {
                if (e == null) {
                    mAdapter.replaceDataset(list);
                } else {
                    Log.e("onCreate query find", e.getMessage());
                }
            }
        });

        /* Handle Preferences on first run */
        SharedPreferences sp = getSharedPreferences(Quietly.SHARED_PRFERENCES, MODE_PRIVATE);
        if(!sp.contains(Quietly.DEFAULT_FILTER)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(Quietly.DEFAULT_FILTER, 0);
            editor.apply();
        }

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

        if(id == R.id.action_settings) {
            Intent i = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivityForResult(i, 999);
        }

        return super.onOptionsItemSelected(item);
    }

    public void chooseFilter() {
        new MaterialDialog.Builder(this)
            .title("Choose filter")
            .items(R.array.filters_front_end)
            .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                @Override
                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                    mAdapter.addElement(onNewEvent(new Event(which)));
                    return true;
                }
            })
            .positiveText("Done")
            .show();
    }

    /*
     *
     *  CALLBACK METHODS
     *
     */

    public static final int EVENT_REQUEST_CODE = 100;


    public void startActivityForEvent(Event e, int position, View icon) {
        Intent outgoing = new Intent(this, EventActivity.class);
        outgoing.putExtra(Event.CUSTOM_ID, e.getCustomId());
        Log.d("sosa", "Event ID: " + e.getCustomId());

        //ActivityOptions options = ActivityOptions
        //        .makeSceneTransitionAnimation(this, icon, "shared");

        startActivityForResult(outgoing, position);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        NotificationManager n = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        if(!n.isNotificationPolicyAccessGranted()) {
            startActivity(new Intent(this, DispatchActivity.class));
            finish();
            return;
        }

        if(resultCode == EventActivity.EVENT_RESULT_SUCCESS) {


            Collections.sort(mAdapter.getmDataset(), new Comparator<Event>() {
                @Override
                public int compare(Event lhs, Event rhs) {
                    return lhs.getComparableTime() - rhs.getComparableTime();
                }
            });
            mAdapter.notifyDataSetChanged();


        }
    }

    public Event onNewEvent(Event e) {
        e.pinInBackground();
        Scheduler.setAlarmsForEvent(this, e);
        return e;
    }

    public Event onRemoveEvent(Event e) {

        return e;
    }

}
