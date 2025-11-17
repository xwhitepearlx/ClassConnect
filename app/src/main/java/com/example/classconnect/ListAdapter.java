package com.example.classconnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<NotificationData> {
    public ListAdapter(@NonNull Context context, ArrayList<NotificationData> notificationDataArrayList) {
        super(context, R.layout.notification_item, notificationDataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        NotificationData notificationData = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.notification_item, parent, false);
        }

        TextView listSessionID = view.findViewById(R.id.SessionID);
        TextView listSessionTime = view.findViewById(R.id.sessionStartTime);

        listSessionID.setText(notificationData.getSessionID());
        listSessionTime.setText(notificationData.getScheduleTime());

        return view;
    }
}
