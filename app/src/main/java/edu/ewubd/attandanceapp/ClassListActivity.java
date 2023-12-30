package edu.ewubd.attandanceapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;



import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class ClassListActivity extends AppCompatActivity {

    private Button actionBtn, btnViewStud;
    private ListView classListView;
    private ClassAdapter classAdapter;
    private AttendanceDB dbHelper;
    private ArrayList<ClassItem> classItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        actionBtn = findViewById(R.id.btnAddClass);
        btnViewStud = findViewById(R.id.btnViewStudents);
        classListView = findViewById(R.id.lvClass);


        dbHelper = new AttendanceDB(this);
        classItems = new ArrayList<>();
        loadClassesFromDatabase();

        classAdapter = new ClassAdapter(this, classItems);
        classListView.setAdapter(classAdapter);


        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        btnViewStud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ClassListActivity.this, StudentListActivity.class);
                startActivity(i);
            }
        });

        classListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(position);
                return true;



            }
        });






    }



    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.class_dialog, null);
        builder.setView(view);

        EditText classNameEditText = view.findViewById(R.id.etClass);
        EditText secNameEditText = view.findViewById(R.id.etSec);

        Button addClassButton = view.findViewById(R.id.diBtnAdd);
        Button cancelClassButton = view.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        cancelClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        addClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String className = classNameEditText.getText().toString();
                String secName = secNameEditText.getText().toString();


                if (secName.isEmpty() || className.isEmpty()) {
                    Toast.makeText(ClassListActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                    return;
                } else if (secName.length() > 1) {
                    Toast.makeText(ClassListActivity.this, "Section can only be a letter or digit", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isClassExists(className, secName)) {
                    Toast.makeText(ClassListActivity.this, "Class already exists", Toast.LENGTH_SHORT).show();
                }

                else {
                    long newClassId = dbHelper.insertClass(className, secName);

                    ClassItem newClassItem = new ClassItem(newClassId, className, secName);
                    classItems.add(newClassItem);
                    classAdapter.notifyDataSetChanged();

                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    // Function to check if the class already exists
    private boolean isClassExists(String className, String secName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM Class WHERE className=? AND secName=?";
        String[] selectionArgs = {className, secName};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        boolean exists = cursor != null && cursor.getCount() > 0;

        if (cursor != null) {
            cursor.close();
        }

        db.close();

        return exists;
    }


    private void loadClassesFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM Class";

        Cursor rows = db.rawQuery(query, null);

        if (rows != null) {
            if (rows.getCount() > 0) {

                while (rows.moveToNext()) {
                    long classId = rows.getLong(0);
                    String className = rows.getString(1);
                    String secName = rows.getString(2);



                    ClassItem cs = new ClassItem (classId, className, secName);
                    classItems.add(cs);



                }

            }
            rows.close();
        }
        db.close();
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
                deleteClass(position);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void deleteClass(int position) {
        long classIdToDelete = classItems.get(position).getId();
        dbHelper.deleteClass(classIdToDelete);
        classItems.remove(position);
        classAdapter.notifyDataSetChanged();
    }







}
