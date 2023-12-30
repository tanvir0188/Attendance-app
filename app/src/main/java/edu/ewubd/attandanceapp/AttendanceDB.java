package edu.ewubd.attandanceapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class AttendanceDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Attendance.db";
    private static final int DATABASE_VERSION = 1;

    public AttendanceDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Class (" +
                "class_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "className TEXT," +
                "secName TEXT);");

        db.execSQL("CREATE TABLE Lecture (" +
                "lecture_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "lecture_no INTEGER," +
                "date TEXT," +
                "className TEXT," +
                "secName TEXT," +
                "FOREIGN KEY (className) REFERENCES Class(className) ON DELETE CASCADE);");

        db.execSQL("CREATE TABLE Attendance (" +
                "at_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "student_id TEXT," +
                "lecture_no INTEGER," +
                "className INTEGER,"+
                "secName TEXT," +
                "date TEXT," +
                "status INTEGER," +
                "FOREIGN KEY (student_id) REFERENCES Student(id)," +
                "FOREIGN KEY (lecture_no) REFERENCES Lecture(lecture_no) ON DELETE CASCADE);");


        db.execSQL("CREATE TABLE Student (" +
                "id TEXT PRIMARY KEY," +
                "name TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Class");
        db.execSQL("DROP TABLE IF EXISTS Lecture");
        db.execSQL("DROP TABLE IF EXISTS Attendance");
        db.execSQL("DROP TABLE IF EXISTS Student");
        onCreate(db);
    }





    //all the insertion operations
    public long insertClass(String className, String secName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();

        cols.put("className", className);
        cols.put("secName", secName);

        long newRowId = db.insert("Class", null, cols);

        db.close();
        return newRowId;
    }


    public long insertLecture(int lectureNo, String date, String className, String secName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("lecture_no", lectureNo);
        values.put("date", date);
        values.put("className", className);
        values.put("secName", secName);

        long lectureId = db.insert("Lecture", null, values);

        db.close();

        return lectureId;
    }

    public long insertAttendance(String studentId, long lectureNo, String className, String secName, String date, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();

        cols.put("student_id", studentId);
        cols.put("lecture_no", lectureNo);
        cols.put("className", className);
        cols.put("secName", secName);
        cols.put("date", date);
        cols.put("status", status);

        long newRowId = db.insert("Attendance", null, cols);

        db.close();
        return newRowId;
    }


    public void insertStudent(String studentId, String studentName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();

        cols.put("id", studentId);
        cols.put("name", studentName);

        db.insert("Student", null, cols);

        db.close();

    }

    // delete operations

    public void deleteClass(long classId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "className=?";
        String[] selectionArgs = {String.valueOf(classId)};
        db.delete("Class", selection, selectionArgs);
        db.close();
    }

    public void deleteStudent(String StudentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "id=?";
        String[] selectionArgs = {String.valueOf(StudentId)};
        db.delete("Student", selection, selectionArgs);
        db.close();
    }

    public void deleteLecture(long lectureId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "lecture_no=?";
        String[] selectionArgs = {String.valueOf(lectureId)};
        db.delete("Lecture", selection, selectionArgs);
        db.close();
    }

    public void deleteAttendance(String studentId, String className, String secName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "student_id=? AND className=? AND secName=?";
        String[] selectionArgs = {studentId, className, secName};
        db.delete("Attendance", selection, selectionArgs);
        db.close();
    }




    // checks whether a student already is added in the attendancesheet in a lecture
    public boolean isAttendanceRecordExists(String studentId, long lectureNo, String className, String secName) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM Attendance WHERE student_id = ? AND lecture_no = ? AND className = ? AND secName = ?";
        String[] selectionArgs = {studentId, String.valueOf(lectureNo), className, secName};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        boolean recordExists = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return recordExists;
    }

    //for AttendanceSheetAdapter
    public String getStudentNameById(String studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"name"};
        String selection = "id=?";
        String[] selectionArgs = {studentId};

        Cursor cursor = db.query("Student", columns, selection, selectionArgs, null, null, null);

        String studentName = "";

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("name");
            studentName = cursor.getString(nameIndex);
        }

        cursor.close();
        db.close();

        return studentName;
    }

public ArrayList<AttendanceItem> getStudentsForAttendance(long lectureNo, String className, String secName) {
    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<AttendanceItem> studentData = new ArrayList<>();

    String query = "SELECT s.id, s.name, a.status " +
            "FROM Student s " +
            "JOIN Attendance a ON s.id = a.student_id " +
            "WHERE a.lecture_no = ? AND a.className = ? AND a.secName = ?";

    Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(lectureNo), className, secName});

    if (cursor != null && cursor.moveToFirst()) {
        do {
            String studentId = cursor.getString(0);
            int status = cursor.getInt(2);
            String date = "";
            long lecture = 1;

            AttendanceItem attendanceItem = new AttendanceItem(studentId, className, secName, date, lecture, status);
            studentData.add(attendanceItem);
        } while (cursor.moveToNext());

        cursor.close();
    }

    db.close();
    return studentData;
}

    public int getPresentCount(long lectureNo, String className, String secName) {

        SQLiteDatabase db = this.getReadableDatabase();
        int presentCount = 0;

        String query = "SELECT COUNT(*) FROM Attendance WHERE lecture_no = ? AND className = ? AND secName = ? AND status = 1";
        String[] selectionArgs = {String.valueOf(lectureNo), className, secName};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            presentCount = cursor.getInt(0);
            cursor.close();
        }

        db.close();
        return presentCount;
    }

    public int getAbsentCount(long lectureNo, String className, String secName) {


        SQLiteDatabase db = this.getReadableDatabase();
        int absentCount = 0;

        String query = "SELECT COUNT(*) FROM Attendance WHERE lecture_no = ? AND className = ? AND secName = ? AND status = 0";
        String[] selectionArgs = {String.valueOf(lectureNo), className, secName};


        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {

            absentCount = cursor.getInt(0);
            cursor.close();
        }

        db.close();
        return absentCount;
    }





    //actually updates the attendance table(only the status), triggered while submitting the attendance for a lecture
    public void takeAttendance(String studentId, long lectureNo, String className, String secName, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);

        String selection = "student_id = ? AND lecture_no = ? AND className = ? AND secName = ?";
        String[] selectionArgs = {studentId, String.valueOf(lectureNo), className, secName};

        db.update("Attendance", values, selection, selectionArgs);
        db.close();
    }






}
