package com.example.myapplication;

public class Form {
    private String branch;
    private String semester;
    private String title;
    private String fileUrl;
    private String uploadDate;
    private  String username;

    public Form() {
        // Default constructor required for Firebase
    }

    public Form(String branch, String semester, String title, String fileUrl,String uploadDate) {
        this.branch = branch;
        this.semester = semester;
        this.title = title;
        this.fileUrl = fileUrl;
        this.uploadDate=uploadDate;
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
    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

