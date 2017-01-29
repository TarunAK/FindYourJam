package com.example.tarunkalikivaya.findyourjam.eventlistattend;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tarunkalikivaya.findyourjam.NavigationActivity;
import com.example.tarunkalikivaya.findyourjam.R;
import com.example.tarunkalikivaya.findyourjam.eventlistscreated.DisplayEventListActivity;

import java.util.List;

/**
 * Created by chrisexn on 1/28/2017.
 */
public class EventListAttendAdapter extends ArrayAdapter<EventAttendObject> {
    public EventListAttendAdapter(Context context, List<EventAttendObject> users) {
        super(context, 0,users);
    }

    class ViewHolder{
        TextView title;
        TextView description;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        EventAttendObject user = getItem(position);
        ViewHolder holder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_element, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.description = (TextView) convertView.findViewById(R.id.description);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        // Lookup view for data population
        holder.title.setText(user.getTitle());
        holder.description.setText(user.getDescription());
        convertView.setTag(R.id.event_id_tag,user.getId());


        // Populate the data into the template view using the data object

        // Return the completed view to render on screen
        return convertView;
    }
}