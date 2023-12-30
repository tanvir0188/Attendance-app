package edu.ewubd.attandanceapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;

import java.util.ArrayList;

public class StudentDialogAdapter extends ArrayAdapter<Student> {
    private final Context context;
    private final ArrayList<Student> values;
    private OnStudentItemClickListener itemClickListener;

    public interface OnStudentItemClickListener {
        void onStudentItemClick(Student student);
    }

    public StudentDialogAdapter(@NonNull Context context, @NonNull ArrayList<Student> items, OnStudentItemClickListener listener) {
        super(context, -1, items);
        this.context = context;
        this.values = items;
        this.itemClickListener = listener;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.student_info_dialogue, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.studentIdTextView = convertView.findViewById(R.id.tvId);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Student student = values.get(position);

        viewHolder.studentIdTextView.setText(student.getId());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onStudentItemClick(student);
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView studentIdTextView;
    }
}
