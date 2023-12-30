package edu.ewubd.attandanceapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AttendanceListActivity extends AppCompatActivity {

    private Button addLecture, makeAttendance;
    private TextView tvclassName;
    private ListView lectureListView;
    private LectureAdapter lectureAdapter;
    private AttendanceDB dbHelper;
    private ArrayList<LectureItem> lectureItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_list);

        Intent i = getIntent();
        String className = i.getStringExtra("className");
        String secName = i.getStringExtra("secName");


        tvclassName = findViewById(R.id.tvClassName);
        tvclassName.setText("class: "+ className + ", Sec: " + secName);




        addLecture = findViewById(R.id.btnAddLecture);

        lectureListView = findViewById(R.id.lvLectures);

        dbHelper = new AttendanceDB(this);
        lectureItems = new ArrayList<>();
        loadLectures(className, secName);

        lectureAdapter = new LectureAdapter(this, lectureItems);
        lectureListView.setAdapter(lectureAdapter);


        addLecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLectureDialogue();
            }


        });



        lectureListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(position);
                return true;

            }
        });
    }


    private void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.delete_dialog, null);
        builder.setView(view);

        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnDelete = view.findViewById(R.id.btnDelete);

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLecture(position);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void deleteLecture(int position) {
        long lecIdToDelete = lectureItems.get(position).getLectureNo();
        dbHelper.deleteLecture(lecIdToDelete);
        lectureItems.remove(position);
        lectureAdapter.notifyDataSetChanged();
    }

    private void showLectureDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.lecture_add_dialogue, null);
        builder.setView(view);

        EditText lectureNoEditText = view.findViewById(R.id.etLectureId);
        EditText dateEditText = view.findViewById(R.id.etDate);
        Button addLectureButton = view.findViewById(R.id.diBtnAdd);
        Button cancelLectureButton = view.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        cancelLectureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final Calendar calendar = Calendar.getInstance();

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AttendanceListActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                                dateEditText.setText(sdf.format(calendar.getTime()));
                            }
                        },
                        year, month, dayOfMonth);

                datePickerDialog.show();
            }
        });

        addLectureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lectureNoStr = lectureNoEditText.getText().toString();
                String date = dateEditText.getText().toString();

                if (lectureNoStr.isEmpty() || date.isEmpty()) {
                    Toast.makeText(AttendanceListActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = getIntent();
                String className = i.getStringExtra("className");
                String secName = i.getStringExtra("secName");


                int lectureNo = Integer.parseInt(lectureNoStr);


                if (isLectureNoExists(className, lectureNo)) {
                    Toast.makeText(AttendanceListActivity.this, "Lecture number already exists", Toast.LENGTH_SHORT).show();
                }
                else {
                    long newLectureId = dbHelper.insertLecture(lectureNo, date, className, secName);

                    LectureItem newLectureItem = new LectureItem(lectureNo, date, className, secName);
                    lectureItems.add(newLectureItem);

                    lectureAdapter.notifyDataSetChanged();

                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private boolean isLectureNoExists(String className, int lectureNo) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM Lecture WHERE className=? AND lecture_no=?";
        String[] selectionArgs = {className, String.valueOf(lectureNo)};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        boolean exists = cursor != null && cursor.getCount() > 0;

        if (cursor != null) {
            cursor.close();
        }

        db.close();

        return exists;
    }



    private void loadLectures(String className, String secName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM Lecture WHERE className=?";
        String[] selectionArgs = {className};
        Cursor rows = db.rawQuery(query, selectionArgs);

        if (rows != null) {
            if (rows.getCount() > 0) {
                while (rows.moveToNext()) {
                    long lectureNo = rows.getLong(1);
                    String date = rows.getString(2);

                    LectureItem lectureItem = new LectureItem(lectureNo, date, className, secName);
                    lectureItems.add(lectureItem);
                }
            }
            rows.close();
        }

        db.close();

        lectureListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
// String item = (String) parent.getItemAtPosition(position);
                System.out.println(position);
                Intent i = new Intent(AttendanceListActivity.this, AttendanceSheet.class);
                i.putExtra("date", lectureItems.get(position).date);
                i.putExtra("lecture", lectureItems.get(position).lectureNo);
                i.putExtra("sec",lectureItems.get(position).secName);
                i.putExtra("class", lectureItems.get(position).className);
                startActivity(i);


            }
        });


    }


}

