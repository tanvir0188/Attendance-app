package edu.ewubd.attandanceapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class StudentListActivity extends AppCompatActivity {

    private Button btnAddStudent;

    private ListView studentListView;

    private StudentAdapter studentAdapter;
    private AttendanceDB dbHelper;
    private ArrayList<Student> students;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        btnAddStudent = findViewById(R.id.btnAddStudent);
        studentListView = findViewById(R.id.lvStudents);

        dbHelper = new AttendanceDB(this);
        students = new ArrayList<>();
        loadStudentFromDatabase();

        studentAdapter = new StudentAdapter(this, students);
        studentListView.setAdapter(studentAdapter);


        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStudentDialog();
            }
        });

        studentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
                deleteStudent(position);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void deleteStudent(int position) {
        String studentIdToDelete = students.get(position).getId();
        dbHelper.deleteStudent(studentIdToDelete);
        students.remove(position);
        studentAdapter.notifyDataSetChanged();
    }


    private void showStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.student_form_dialogue, null);
        builder.setView(view);

        EditText etStudentName = view.findViewById(R.id.etStudentName);
        EditText etStudentId = view.findViewById(R.id.etStudentId);
        Button addStudent = view.findViewById(R.id.diBtnAdd);
        Button cancel = view.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String studentName = etStudentName.getText().toString();
                String studentId = etStudentId.getText().toString();

                if (studentId.isEmpty() || studentName.isEmpty()) {
                    Toast.makeText(StudentListActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                dbHelper.insertStudent(studentId,studentName);

                Student newStudent = new Student(studentId, studentName);
                students.add(newStudent);

                studentAdapter.notifyDataSetChanged();

                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void loadStudentFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM Student";
        Cursor rows = db.rawQuery(query, null);

        if (rows != null) {
            if (rows.getCount() > 0) {
                while (rows.moveToNext()) {
                    String id  = rows.getString(0);
                    String name = rows.getString(1);


                    Student stud = new Student(id, name);
                    students.add(stud);
                }
            }

            rows.close();
        }



        db.close();
    }
}
