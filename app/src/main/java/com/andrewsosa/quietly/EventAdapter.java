package com.andrewsosa.quietly;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    // The Dataset
    private static ArrayList<Event> mDataset;
    private static ActionBarActivity activity;

    // Constructor for setting up the dataset
    public EventAdapter(ArrayList<Event> myDataset, ActionBarActivity c) {
        mDataset = new ArrayList<>(myDataset);
        activity = c;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // This is our view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_item, parent, false);


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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final Event event = mDataset.get(position);

        holder.titleText.setText(event.getLabel());
        holder.mSwitch.setChecked(event.isActive());
        holder.subtitleText.setText(event.getStringTime());
        if(event.isActive()) {
            holder.mImageButton.setBackground(activity.getResources().getDrawable(R.drawable.avatar_background_active));
            holder.mImageButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_notifications_white_24dp));
        } else {
            holder.mImageButton.setBackground(activity.getResources().getDrawable(R.drawable.avatar_background));
            holder.mImageButton.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_notifications_grey600_24dp));
        }

        holder.mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.toggleActive();
                notifyItemChanged(position);
            }
        });

        // Store event for click listening and stuff
        holder.event = event;

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }






    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Reference for the Event that needs to be opened on the click listener
        Event event;

        // each data item is just a string in this case
        public TextView titleText;
        public TextView subtitleText;
        public Switch mSwitch;
        public ImageButton mImageButton;

        // Constructor
        public ViewHolder(View v) {
            super(v);
            titleText = (TextView) v.findViewById(R.id.eventName);
            subtitleText = (TextView) v.findViewById(R.id.subtitleText);
            mSwitch = (Switch) v.findViewById(R.id.activeSwitch);
            mImageButton = (ImageButton) v.findViewById(R.id.activeIcon);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            // Launch activity here
            Intent intent = new Intent(activity, EventActivity.class);
            if(event != null) {
                intent.putExtra("Event", event);
                intent.putExtra("Index", EventHolder.indexOf(event));
            }

            //activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
            activity.startActivity(intent);
        }
    }

}