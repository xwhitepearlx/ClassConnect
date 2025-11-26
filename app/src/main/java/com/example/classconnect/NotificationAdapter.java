package com.example.classconnect.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.classconnect.NotificationData;
import com.example.classconnect.R;

import java.util.List;

public class NotificationAdapter extends ArrayAdapter<NotificationData> {

    public NotificationAdapter(Context context, List<NotificationData> notifications) {
        super(context, 0, notifications);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.notification_item, parent, false);
        }

        NotificationData notification = getItem(position);

        if (notification != null) {
            TextView tvSessionID = convertView.findViewById(R.id.SessionID);
            TextView tvScheduleTime = convertView.findViewById(R.id.sessionStartTime);
            TextView tvDescription = convertView.findViewById(R.id.notificationDescription);

            tvSessionID.setText("Session #" + notification.getSessionID());
            tvScheduleTime.setText(notification.getTimestamp());

            // Trim message if too long
            String message = notification.getMessage();
            if (message.length() > 80) {
                message = message.substring(0, 77) + "...";
            }
            tvDescription.setText(message);
        }

        return convertView;
    }
}