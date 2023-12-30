package edu.ewubd.attandanceapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class LectureAdapter extends ArrayAdapter<LectureItem> {
    private final Context context;
    private final ArrayList<LectureItem> values;

    public LectureAdapter(@NonNull Context context, @NonNull ArrayList<LectureItem> items) {
        super(context, -1, items);
        this.context = context;
        this.values = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.lecture_view_layout, parent, false);

        TextView lectureNo = rowView.findViewById(R.id.tvLecture);
        TextView date = rowView.findViewById(R.id.tvDate);


        TextView presence = rowView.findViewById(R.id.tvPresent);
        TextView absence = rowView.findViewById(R.id.tvAbsent);

        LectureItem e = values.get(position);
        lectureNo.setText(String.valueOf(e.lectureNo));
        date.setText(e.date);
        AttendanceDB dbHelper = new AttendanceDB(context);
        int present = dbHelper.getPresentCount(e.lectureNo, e.className, e.secName);
        int absent = dbHelper.getAbsentCount(e.lectureNo, e.className, e.secName);

        presence.setText( String.valueOf(present));
        absence.setText(String.valueOf(absent));



        return rowView;
    }
}
