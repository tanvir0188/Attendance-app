package edu.ewubd.attandanceapp;

public class AttendanceItem {
    private String studentId, secName, date, className;
    private  long   lecture;
    private  int status;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public AttendanceItem(String studentId, String className, String secName, String date, long lecture, int status) {
        this.studentId = studentId;
        this.secName = secName;
        this.className = className;
        this.date = date;
        this.lecture = lecture;
        this.status = status;
    }


    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSecName() {
        return secName;
    }

    public void setSecName(String secName) {
        this.secName = secName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getLecture() {
        return lecture;
    }

    public void setLecture(long lecture) {
        this.lecture = lecture;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

