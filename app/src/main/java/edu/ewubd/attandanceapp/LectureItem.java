package edu.ewubd.attandanceapp;

public class LectureItem {
    long lectureNo;
    String date;
    String className, secName;


    public String getSecName() {
        return secName;
    }

    public void setSecName(String secName) {
        this.secName = secName;
    }

    public LectureItem(long lectureNo, String date, String className, String secName) {
        this.lectureNo = lectureNo;
        this.date = date;
        this.className = className;
        this.secName = secName;
    }

    public long getLectureNo() {
        return lectureNo;
    }

    public void setLectureNo(long lectureNo) {
        this.lectureNo = lectureNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
