package com.studentvoting.app.database;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "students",
        indices = {@Index(value = "studentId", unique = true)})
public class Student {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String studentId;
    private String name;
    private String department;
    private String year;
    private boolean hasVoted;
    private int votedCandidateId;

    public Student() {}

    public Student(String studentId, String name, String department, String year,
                   boolean hasVoted, int votedCandidateId) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.year = year;
        this.hasVoted = hasVoted;
        this.votedCandidateId = votedCandidateId;
    }

    // Getters
    public int getId() { return id; }
    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getYear() { return year; }
    public boolean isHasVoted() { return hasVoted; }
    public int getVotedCandidateId() { return votedCandidateId; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setName(String name) { this.name = name; }
    public void setDepartment(String department) { this.department = department; }
    public void setYear(String year) { this.year = year; }
    public void setHasVoted(boolean hasVoted) { this.hasVoted = hasVoted; }
    public void setVotedCandidateId(int votedCandidateId) { this.votedCandidateId = votedCandidateId; }
}
