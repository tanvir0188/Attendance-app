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
public class ClassAdapter extends ArrayAdapter<ClassItem>{
    private final Context context;
    private final ArrayList<ClassItem> values;

    public ClassAdapter(@NonNull Context context, @NonNull ArrayList<ClassItem> items) {
        super(context, -1, items);
        this.context = context;
        this.values = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.class_name_listview_layout, parent, false);

        TextView className = rowView.findViewById(R.id.tvClassName);
        TextView secName = rowView.findViewById(R.id.tvSecName);
        TextView takeAttendance = rowView.findViewById(R.id.btnTkAttendance);

        ClassItem e = values.get(position);
        className.setText(e.getClassName());
        secName.setText("Section: " + e.getSecName());
        takeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassItem selectedClass = values.get(position);
                Intent intent = new Intent(context, AttendanceListActivity.class);
                intent.putExtra("className", selectedClass.getClassName());
                intent.putExtra("secName", selectedClass.getSecName());
                context.startActivity(intent);
            }
        });
        return rowView;
    }



}
