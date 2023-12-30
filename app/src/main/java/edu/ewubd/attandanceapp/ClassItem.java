package edu.ewubd.attandanceapp;

public class ClassItem {
    private long id;
    private String className;
    private String secName;

    public ClassItem(long id, String className, String secName) {
        this.id = id;
        this.className = className;
        this.secName = secName;
    }

    public long getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public String getSecName() {
        return secName;
    }
}
