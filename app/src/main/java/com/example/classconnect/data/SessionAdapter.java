package com.example.classconnect.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.classconnect.R;
import com.example.classconnect.Session;

import java.util.List;

public class SessionAdapter extends ArrayAdapter<Session> {

    public SessionAdapter(Context context, List<Session> sessions) {
        super(context, 0, sessions);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.session_item, parent, false);
        }

        Session session = getItem(position);

        if (session != null) {
            TextView tvSessionID = convertView.findViewById(R.id.SessionID);
            TextView tvDate = convertView.findViewById(R.id.sessionStartDate);
            TextView tvTime = convertView.findViewById(R.id.sessionStartTime);
            TextView tvLocation = convertView.findViewById(R.id.tvSessionLocation);
            TextView tvDuration = convertView.findViewById(R.id.tvSessionDuration);
            TextView tvParticipants = convertView.findViewById(R.id.tvSessionParticipants);

            tvSessionID.setText(String.valueOf(session.getId()));
            tvDate.setText(session.getDate());
            tvTime.setText(session.getStartTime());
            tvLocation.setText(session.getLocation());
            tvDuration.setText(session.getDuration() + " mins");
            tvParticipants.setText(String.valueOf(session.getMaxParticipant()));
        }

        return convertView;
    }
}