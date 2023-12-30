package edu.ewubd.attandanceapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class AttendanceSheetAdapter extends ArrayAdapter<AttendanceItem> {
    private final Context context;
    private final ArrayList<AttendanceItem> values;
    private final AttendanceDB dbHelper;

    public AttendanceSheetAdapter(@NonNull Context context, @NonNull ArrayList<AttendanceItem> items, AttendanceDB dbHelper) {
        super(context, -1, items);
        this.context = context;
        this.values = items;
        this.dbHelper = dbHelper;
    }

    public ArrayList<AttendanceItem> getAttendanceList() {
        ArrayList<AttendanceItem> attendanceList = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            AttendanceItem item = getItem(i);
            if (item != null) {
                attendanceList.add(item);
            }
        }
        return attendanceList;
    }

    public void addNewStudent(AttendanceItem student) {
        values.add(student);
    }



    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.attendance_sheet_listview_layout, parent, false);

        TextView studentName = rowView.findViewById(R.id.tvstudName);
        TextView studentId = rowView.findViewById(R.id.tvstudid);
        CheckBox statusCheckBox = rowView.findViewById(R.id.cbStatus);

        AttendanceItem e = values.get(position);

        String studentNameFromDB = dbHelper.getStudentNameById(e.getStudentId());

        studentId.setText(e.getStudentId());
        studentName.setText(studentNameFromDB);

        statusCheckBox.setChecked(e.getStatus() == 1);


        statusCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                e.setStatus(isChecked ? 1 : 0);
            }
        });

        return rowView;
    }

}
