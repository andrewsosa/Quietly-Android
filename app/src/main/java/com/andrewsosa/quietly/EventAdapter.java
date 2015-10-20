package com.andrewsosa.quietly;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
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
    private ArrayList<Event> mDataset;
    private MainActivity activity;

    // Constructor for setting up the dataset
    public EventAdapter(MainActivity c, ArrayList<Event> myDataset) {
        activity = c;
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

    public ArrayList<Event> getmDataset() {
        return mDataset;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final Event event = mDataset.get(position);

        holder.titleText.setText(event.getLabel());
        holder.mSwitch.setChecked(event.isActive());
        String s = event.getStringTime(event.getStartHour(), event.getStartMinute())
                + " \u2013 " + event.getStringTime(event.getEndHour(), event.getEndMinute());
        holder.subtitleText.setText(s);
        holder.filterText.setText(event.getStringFilter());
        if(event.isActive()) {
            holder.tile.setActivated(true);
        } else {
            holder.tile.setActivated(false);
        }

        holder.tile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivityForEvent(event, position, holder.mImageButton);
            }
        });

        holder.tile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new MaterialDialog.Builder(activity)
                        .positiveColor(activity.getResources().getColor(R.color.accent, activity.getTheme()))
                        .negativeColor(activity.getResources().getColor(R.color.accent, activity.getTheme()))
                        .content("Delete " + event.getLabel() + "?")
                        .positiveText("Delete")
                        .negativeText("Cancel")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                Scheduler.cancelAlarmForEvent(activity, event);

                                try {
                                    mDataset.remove(position);
                                    notifyItemRemoved(position);
                                } catch (Exception e) {
                                    Log.e("on remove", e.getMessage());
                                }



                                event.unpinInBackground();
                                event.deleteInBackground();
                            }
                        })
                        .show();


                return true;
            }
        });

        TypedArray backgrounds = activity.getResources().obtainTypedArray(R.array.icon_backgrounds);
        holder.mImageButton.setBackgroundResource(backgrounds.getResourceId(event.getFilter(), -1));
        TypedArray foregrounds = activity.getResources().obtainTypedArray(R.array.icon_foregrounds_vectors);
        holder.mImageButton.setImageResource(foregrounds.getResourceId(event.getFilter(), -1));

        backgrounds.recycle();
        foregrounds.recycle();


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

        holder.mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isActive = holder.tile.isActivated();
                isActive = !isActive;
                event.setActive(isActive);

                if(isActive) {
                    Scheduler.setAlarmsForEvent(activity, event);
                } else {
                    Scheduler.cancelAlarmForEvent(activity, event);
                }

                try {
                    notifyItemChanged(position);
                } catch (Exception e) {
                    Log.e("Image Button Click", e.getMessage());
                }
            }
        });
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
        public TextView filterText;
        public Switch mSwitch;
        public ImageButton mImageButton;

        // Constructor
        public ViewHolder(View v) {
            super(v);
            tile = v;
            titleText = (TextView) v.findViewById(R.id.eventName);
            subtitleText = (TextView) v.findViewById(R.id.subtitleText);
            filterText = (TextView) v.findViewById(R.id.tv_filterLevel);
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