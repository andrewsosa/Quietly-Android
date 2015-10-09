package com.andrewsosa.quietly;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    // The Dataset
    private static ArrayList<Event> mDataset;
    private Context context;

    // Constructor for setting up the dataset
    public EventAdapter(Context c, ArrayList<Event> myDataset) {
        context = c;
        mDataset = new ArrayList<>(myDataset);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // This is our view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_tile, parent, false);


        // Return the new view
        return new ViewHolder(v);

    }

    // Add new items to the dataset
    public void addElement(Event e) {
        mDataset.add(e);
        notifyItemInserted(mDataset.size()-1);
        //notifyDataSetChanged();
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final Event event = mDataset.get(position);

        holder.titleText.setText(event.getLabel());
        holder.mSwitch.setChecked(event.isActive());
        holder.subtitleText.setText(event.getStringTime());
        if(event.isActive()) {
            holder.tile.setActivated(true);
        } else {
            holder.tile.setActivated(false);
        }

        holder.tile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*new MaterialDialog.Builder(context)
                        .widgetColor(context.getResources().getColor(R.color.accent))
                        .positiveColor(context.getResources().getColor(R.color.accent))
                        .title("Change label")
                        .content("Enter a reminder label:")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("Label", event.getLabel(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                event.setLabel(input.toString());
                                holder.titleText.setText(input.toString());
                            }
                        }).show();*/
                Intent outgoing = new Intent(context, EventActivity.class);
                outgoing.putExtra("extraID", mDataset.get(position).getObjectId());
                context.startActivity(outgoing);
            }
        });

        holder.tile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Event e = mDataset.get(position);

                new MaterialDialog.Builder(context)
                        .positiveColor(context.getResources().getColor(R.color.accent))
                        .negativeColor(context.getResources().getColor(R.color.accent))
                        .content("Delete " + e.getLabel() + "?")
                        .positiveText("Delete")
                        .negativeText("Cancel")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                e.unpinInBackground();
                                mDataset.remove(position);
                                notifyItemRemoved(position);
                            }
                        })
                        .show();


                return true;
            }
        });

        holder.mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePicker = new TimePickerFragment();
                timePicker.assignMethod(new TimePickerReceiver() {
                    @Override
                    public void receiveTime(int hour, int minute) {
                        event.setTime(hour,minute);
                        holder.subtitleText.setText(event.getStringTime());
                    }
                });
                timePicker.show(((Activity)context).getFragmentManager(), "timePicker");
            }
        });

        holder.mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                event.setActive(isChecked);
                try {
                    notifyItemChanged(position);
                } catch (Exception e) {
                    Log.e("onCheckChanged", e.getMessage());
                }
            }
        });

        // Store event for click listening and stuff
        //holder.event = event;

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public void replaceDataset(List<Event> events) {
        mDataset = new ArrayList<>(events);
    }



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Reference for the Event that needs to be opened on the click listener
        //Event event;

        // each data item is just a string in this case
        public View tile;
        public TextView titleText;
        public TextView subtitleText;
        public Switch mSwitch;
        public ImageButton mImageButton;

        // Constructor
        public ViewHolder(View v) {
            super(v);
            tile = v;
            titleText = (TextView) v.findViewById(R.id.eventName);
            subtitleText = (TextView) v.findViewById(R.id.subtitleText);
            mSwitch = (Switch) v.findViewById(R.id.activeSwitch);
            mImageButton = (ImageButton) v.findViewById(R.id.activeIcon);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            // Do nothing for now!

        }
    }

}