package com.studentvoting.app.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "votes")
public class Vote {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String studentId;
    private int candidateId;
    private long timestamp;

    public Vote() {}

    public Vote(String studentId, int candidateId, long timestamp) {
        this.studentId = studentId;
        this.candidateId = candidateId;
        this.timestamp = timestamp;
    }

    // Getters
    public int getId() { return id; }
    public String getStudentId() { return studentId; }
    public int getCandidateId() { return candidateId; }
    public long getTimestamp() { return timestamp; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setCandidateId(int candidateId) { this.candidateId = candidateId; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
