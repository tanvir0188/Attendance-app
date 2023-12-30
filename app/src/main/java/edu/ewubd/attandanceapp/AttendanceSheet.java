package edu.ewubd.attandanceapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AttendanceSheet extends AppCompatActivity {

    private TextView etDate;
    private TextView tvStudentId, tvTotalStudents, tvAbsent, tvPresent;
    private Button btnAddStudent, submit, addNewStud;

    private AttendanceDB dbHelper;
    private ArrayList<AttendanceItem> attendanceItems;
    private ListView attendanceListView;
    private AttendanceSheetAdapter attendanceSheetAdapter;
    private StudentDialogAdapter studentDialogAdapter;
    private String className;
    private String secName;
    private long lectureNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_sheet);

        dbHelper = new AttendanceDB(this);
        attendanceListView = findViewById(R.id.lvAttendance);
        attendanceItems = new ArrayList<>();

        Intent i = getIntent();
        String date = i.getStringExtra("date");
        long lecture = i.getLongExtra("lecture", 0);
        className = i.getStringExtra("class");
        secName = i.getStringExtra("sec");
        lectureNo = lecture;


        attendanceItems = dbHelper.getStudentsForAttendance(lectureNo, className, secName);
        attendanceSheetAdapter = new AttendanceSheetAdapter(this, attendanceItems, dbHelper);
        attendanceListView.setAdapter(attendanceSheetAdapter);

        etDate = findViewById(R.id.etDate);

        btnAddStudent = findViewById(R.id.btnAddStudent);
        submit = findViewById(R.id.btnSubmit);

        etDate.setText(date);
        getTotalStudent();

        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAttendanceForm();

            }
        });
        
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAttendance();
                getTotalStudent();
                Intent classListIntent = new Intent(AttendanceSheet.this, ClassListActivity.class);
                startActivity(classListIntent);
                finish();
            }
        });

        attendanceListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(position);
                return true;

            }
        });




    }

//    private class DatabaseTask extends AsyncTask<Void, Void, ArrayList<AttendanceItem>> {
//        @Override
//        protected ArrayList<AttendanceItem> doInBackground(Void... params) {
//
//            return dbHelper.getStudentsForAttendance(lectureNo, className, secName);
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<AttendanceItem> result) {
//            if (result != null) {
//                attendanceItems.clear();
//                attendanceItems.addAll(result);
//                attendanceSheetAdapter.notifyDataSetChanged();
//                getTotalStudent();
//            }
//        }
//    }

    private int getPresent(ArrayList<AttendanceItem> a) {
        int present = 0;
        for (int i = 0; i < a.size(); i++) {
            AttendanceItem item = a.get(i);
            if (item.getStatus() == 1) {
                present++;
            }
        }
        return present;
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
                initiateDelete(position);
                dialog.dismiss();
            }
        });

        dialog.show();
    }



    private void initiateDelete(int position) {
        AttendanceItem attendanceItem = attendanceItems.get(position);

        dbHelper.deleteAttendance(attendanceItem.getStudentId(),attendanceItem.getClassName(), attendanceItem.getSecName());

        attendanceItems.remove(position);
        attendanceSheetAdapter.notifyDataSetChanged();

        getTotalStudent();

        Toast.makeText(this, "Attendance record deleted successfully", Toast.LENGTH_SHORT).show();
    }





    private void getTotalStudent()
    {
        tvTotalStudents = findViewById(R.id.tvTotalStudent);
        tvAbsent = findViewById(R.id.tvAbsent);
        tvPresent = findViewById(R.id.tvPresent);

        int totalStudents = attendanceSheetAdapter.getCount();
        int present = getPresent(attendanceItems);
        int absent = totalStudents - present;

        tvTotalStudents.setText("Total Students: " + totalStudents);
        tvPresent.setText("Present: " + present);
        tvAbsent.setText("Absent: " + absent);

    }
    private void updateAttendance() {


        attendanceItems = attendanceSheetAdapter.getAttendanceList();

        ArrayList<AttendanceItem> updatedAttendanceList = attendanceItems;

        for (AttendanceItem attendanceItem : updatedAttendanceList) {
            int status;
            if (attendanceItem.getStatus() == 1) {
                status = 1;
            } else {
                status = 0;
            }
            dbHelper.takeAttendance(attendanceItem.getStudentId(), lectureNo, className, secName, status);
        }

        attendanceSheetAdapter.notifyDataSetChanged();

        Toast.makeText(AttendanceSheet.this, "Attendance updated successfully", Toast.LENGTH_SHORT).show();
    }




    private void showStudentListDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.studentid_listview_dialogue, null);
        builder.setView(view);


        ArrayList<Student> studentList = new ArrayList<>();

        ListView studentListView = view.findViewById(R.id.lvStudentList);


        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM Student";
        Cursor rows = db.rawQuery(query, null);

        if (rows != null) {
            if (rows.getCount() > 0) {
                while (rows.moveToNext()) {
                    String id = rows.getString(0);
                    String name = rows.getString(1);

                    Student stud = new Student(id, name);
                    studentList.add(stud);
                }
            }

            rows.close();
        }

        db.close();
        AlertDialog studentListDialog = builder.create();

        studentDialogAdapter = new StudentDialogAdapter(this, studentList, new StudentDialogAdapter.OnStudentItemClickListener() {
                    @Override
                    public void onStudentItemClick(Student student) {

                        tvStudentId.setText(student.getId());
                        studentListDialog.dismiss();
                    }
                });
        studentListView.setAdapter(studentDialogAdapter);


        studentListDialog.show();

    }

    private void showAttendanceForm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.attendance_form_dialogue, null);
        builder.setView(view);

        Intent i = getIntent();
        String date = i.getStringExtra("date");
        long lecture = i.getLongExtra("lecture", 0);

        TextView tvLecture = view.findViewById(R.id.tvLecture);
        tvStudentId = view.findViewById(R.id.tvStudentId);
        Button chooseStudent = view.findViewById(R.id.btnChooseStudentId);
        addNewStud = view.findViewById(R.id.btnAddStud);

        tvLecture.setText(String.valueOf(lecture));

        AlertDialog attendanceFormDialog = builder.create();

        chooseStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here we call the method to show the studentListView dialog
                showStudentListDialogue();
            }
        });

        addNewStud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String studId = tvStudentId.getText().toString();

                if (!studId.isEmpty()) {
                    Intent i = getIntent();
                    String className = i.getStringExtra("class");
                    String secName = i.getStringExtra("sec");
                    String date = i.getStringExtra("date");
                    long lecture = i.getLongExtra("lecture", 0);
                    int status = 0;

                    if (!dbHelper.isAttendanceRecordExists(studId, lecture, className, secName)) {
                        long newRowId = dbHelper.insertAttendance(studId, lecture, className, secName, date, status);

                        if (newRowId != -1) {
                            attendanceSheetAdapter.addNewStudent(new AttendanceItem(studId, className, secName, date, lecture, status));
                            attendanceSheetAdapter.notifyDataSetChanged();

                            getTotalStudent();
                            Toast.makeText(AttendanceSheet.this, "Student attendance added successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(AttendanceSheet.this, "Attendance record for this student already exists", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(AttendanceSheet.this, "Please choose a student first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        attendanceFormDialog.show();
    }




}

