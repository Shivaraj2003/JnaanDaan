package com.example.myapplication;

public class notes {
    private String branch;
    private String semester;
    private String notesTitle;
    private String subject;

    public notes() {
        // Default constructor required for Firebase
    }

    public notes(String branch, String semester, String notesTitle, String subject) {
        this.branch = branch;
        this.semester = semester;
        this.notesTitle = notesTitle;
        this.subject = subject;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getNotesTitle() {
        return notesTitle;
    }

    public void setNotesTitle(String notesTitle) {
        this.notesTitle = notesTitle;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
