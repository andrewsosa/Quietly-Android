package com.andrewsosa.quietly;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


public class EventActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener,
                                                                Toolbar.OnMenuItemClickListener{


    // Event Details
    static Event event;
    int position;

    // Views
    EditText editText;
    static TextView timeDisplay;

    // Temp data
    static int tempHour = 0;
    static int tempMinute = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent intent = getIntent();
        event = (Event) intent.getSerializableExtra("Event");
        position = intent.getIntExtra("Index", 0);


        // Toolbar craziness
        Toolbar toolbar = (Toolbar) findViewById(R.id.eventActivityToolbar);
        toolbar.setTitle("Edit Event");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_clear_white_24dp));
        toolbar.inflateMenu(R.menu.menu_event);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Display the name
        editText = (EditText) findViewById(R.id.labelText);
        if(event != null) editText.setText(event.getLabel());

        // Display event time
        /* TextView textView = (TextView) findViewById(R.id.startTimeText);
        if(event != null) textView.setText(event.getStringTime()); */

        // Spinner setup
        /*Spinner modeSpinner = (Spinner) findViewById(R.id.modeSpinner);
        ArrayAdapter<CharSequence> modeAdapter = ArrayAdapter.createFromResource(this,
                R.array.mode_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        modeSpinner.setAdapter(modeAdapter); */

        // Fade exit transition
        //getWindow().setEnterTransition(new Slide());
        //getWindow().setExitTransition(new Slide());
        getWindow().setAllowEnterTransitionOverlap(true);
        getWindow().setAllowReturnTransitionOverlap(true);


        // Time picker handler
        timeDisplay = (TextView) findViewById(R.id.timeDisplay);
        timeDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }


    public boolean onMenuItemClick(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();

            EventHolder.getEvent(position).setLabel(editText.getText().toString());
            EventHolder.getEvent(position).setTime(tempHour, tempMinute);
            Dashboard.mAdapter.notifyDataSetChanged();
            finish();
            return true;
        }

        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    // Inner class for time picker dialog
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            // Write new time to view and to the event
            event.setTime(hourOfDay, minute);
            tempHour = hourOfDay;
            tempMinute = minute;
            timeDisplay.setText(event.getStringTime());

        }
    }
}
