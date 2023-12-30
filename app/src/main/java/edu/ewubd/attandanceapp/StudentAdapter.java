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

public class StudentAdapter extends ArrayAdapter<Student> {
    private final Context context;
    private final ArrayList<Student> values;
    public StudentAdapter(@NonNull Context context, @NonNull ArrayList<Student> items) {
        super(context, -1, items);
        this.context = context;
        this.values = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.studint_info_listview, parent, false);

        TextView studentName = rowView.findViewById(R.id.tvName);
        TextView studentId = rowView.findViewById(R.id.tvId);


        Student e = values.get(position);

        studentName.setText(e.getName());
        studentId.setText(e.getId());

        return rowView;
    }

}
