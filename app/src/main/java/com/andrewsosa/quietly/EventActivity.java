package com.andrewsosa.quietly;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Calendar;

public class EventActivity extends AppCompatActivity implements View.OnClickListener{

    Event mEvent;
    Toolbar toolbar;
    FloatingActionButton fab;

    TextView tv_Label;
    TextView tv_Filter;
    TextView tv_Days;
    TextView tv_StartTime;
    TextView tv_EndTime;

    TextView tv_SwitchLabel;
    Switch mSwitch;

    public static final int EVENT_RESULT_FAILURE = 101;
    public static final int EVENT_RESULT_SUCCESS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_close_24dp, getTheme()));
        toolbar.setNavigationOnClickListener(this);

        // intent stuff
        Intent incoming = getIntent();
        String objectID = incoming.getStringExtra(Event.CUSTOM_ID);

        try {
            mEvent = Event.getQuery()
                    .whereEqualTo(Event.CUSTOM_ID, objectID)
                    .getFirst();
        } catch(Exception e) {
            Log.e("Sosa", e.getMessage());
            setResult(EVENT_RESULT_FAILURE);
            finish();
            return;
        }

        toolbar.setTitle(mEvent.getLabel());
        tv_Label = (TextView) findViewById(R.id.tv_Label);
        tv_Label.setText(mEvent.getLabel());

        tv_Filter = (TextView) findViewById(R.id.tv_Filter);
        tv_Filter.setText(getStringForEventFilter(mEvent));

        tv_StartTime = (TextView) findViewById(R.id.tv_StartTime);
        tv_StartTime.setText(mEvent.getStringTime(mEvent.getStartHour(), mEvent.getStartMinute()));

        tv_EndTime = (TextView) findViewById(R.id.tv_EndTime);
        tv_EndTime.setText(mEvent.getStringTime(mEvent.getEndHour(), mEvent.getEndMinute()));



        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                PendingIntent spi = Scheduler.makeStartingPendingIntent(EventActivity.this, mEvent);
                Calendar startTime = Calendar.getInstance();
                startTime.add(Calendar.SECOND, 15);
                alarmManager.setExact(AlarmManager.RTC, startTime.getTimeInMillis(), spi);

            }
        });

        tv_SwitchLabel = (TextView) findViewById(R.id.tv_switchLabel);
        tv_SwitchLabel.setText((mEvent.isActive()) ? "On" : "Off" );

        mSwitch = (Switch) findViewById(R.id.switch_active);
        mSwitch.setChecked(mEvent.isActive());

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEvent.setActive(isChecked);

                /*if(isChecked) {
                    Scheduler.setAlarmsForEvent(EventActivity.this, mEvent);
                } else {
                    Scheduler.cancelAlarmForEvent(EventActivity.this, mEvent);
                }*/

                try {
                    tv_SwitchLabel.setText((mEvent.isActive()) ? "On" : "Off");
                } catch (Exception e) {
                    Log.e("onCheckChanged", e.getMessage());
                }
            }
        });



        updateUIColor();

        // Set listeners for
        findViewById(R.id.ll_label).setOnClickListener(this);
        findViewById(R.id.ll_days).setOnClickListener(this);
        findViewById(R.id.ll_start_time).setOnClickListener(this);
        findViewById(R.id.ll_end_time).setOnClickListener(this);
        findViewById(R.id.ll_filter_level).setOnClickListener(this);




    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.ll_label:
                showLabelDialog(); break;
            case R.id.ll_days:
                showDaysDialog(); break;
            case R.id.ll_start_time:
                showStartPicker(); break;
            case R.id.ll_end_time:
                showEndPicker(); break;
            case R.id.ll_filter_level:
                showFilterDialog(); break;
            default:
                setResult(EVENT_RESULT_SUCCESS);
                mEvent.pinInBackground();
                finish();
        }
    }

    public void showLabelDialog() {
        new MaterialDialog.Builder(this)
                .widgetColor(getResources().getColor(R.color.eventAccent, getTheme()))
                .positiveColor(getResources().getColor(R.color.eventAccent, getTheme()))
                .title("Change label")
                .content("Enter a reminder label:")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("Label", mEvent.getLabel(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        mEvent.setLabel(input.toString());
                        tv_Label.setText(input.toString());
                        toolbar.setTitle(input.toString());
                    }
                }).show();

    }

    public void showDaysDialog() {
        new MaterialDialog.Builder(this)
                .title("Days")
                .items(R.array.days)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        /**
                         * If you use alwaysCallMultiChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected check box to actually be selected.
                         * See the limited multi choice dialog example in the sample project for details.
                         **/
                        return true;
                    }
                })
                .positiveText("Choose")
                .show();
    }

    public void showStartPicker() {
        TimePickerFragment timePickerFragment =
                TimePickerFragment.newInstance(new StartTimeReceiver(),
                        mEvent.getStartHour(),
                        mEvent.getStartMinute());
        timePickerFragment.show(getFragmentManager(), "startPicker");
    }

    public void showEndPicker() {
        TimePickerFragment timePickerFragment =
                TimePickerFragment.newInstance(new EndTimeReceiver(),
                        mEvent.getEndHour(),
                        mEvent.getEndMinute());
        timePickerFragment.show(getFragmentManager(), "endPicker");
    }

    public void showFilterDialog() {
        new MaterialDialog.Builder(this)
                .title("Choose filter")
                .items(R.array.filters_front_end)
                .itemsCallbackSingleChoice(mEvent.getFilter(), new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        mEvent.setFilter(which);
                        tv_Filter.setText(getStringForEventFilter(mEvent));
                        updateUIColor();
                        return true;
                    }
                })
                .positiveText("Done")
                .show();
    }

    public String getStringForEventFilter(Event e) {
        return getResources().getStringArray(R.array.filters_front_end)[e.getFilter()];
    }

    public void updateUIColor() {
        int[] colors = getResources().getIntArray(R.array.filter_colors);
        int[] colorsDark = getResources().getIntArray(R.array.filter_colors_dark);
        int[] colorsLight = getResources().getIntArray(R.array.filter_colors_light);
        //fab.setBackgroundColor(colors[mEvent.getFilter()]);
        fab.setBackgroundTintList(new ColorStateList(
                new int[][]{{-android.R.attr.state_checked}},
                new int[]{colors[mEvent.getFilter()]}
        ));

        toolbar.setBackgroundColor(colors[mEvent.getFilter()]);
        getWindow().setStatusBarColor(colorsDark[mEvent.getFilter()]);
        findViewById(R.id.ll_colorstripe).setBackgroundColor(colorsLight[mEvent.getFilter()]);

    }


    public class StartTimeReceiver implements TimePickerReceiver {
        @Override
        public void receiveTime(int hour, int minute) {
            Scheduler.cancelAlarmForEvent(EventActivity.this, mEvent);
            mEvent.setStartTime(hour, minute);
            tv_StartTime.setText(mEvent.getStringTime(mEvent.getStartHour(), mEvent.getStartMinute()));
            Scheduler.setAlarmsForEvent(EventActivity.this, mEvent);
        }
    }

    public class EndTimeReceiver implements TimePickerReceiver {
        @Override
        public void receiveTime(int hour, int minute) {
            Scheduler.cancelAlarmForEvent(EventActivity.this, mEvent);
            mEvent.setEndTime(hour, minute);
            tv_EndTime.setText(mEvent.getStringTime(mEvent.getEndHour(), mEvent.getEndMinute()));
            Scheduler.setAlarmsForEvent(EventActivity.this, mEvent);
        }
    }
}
