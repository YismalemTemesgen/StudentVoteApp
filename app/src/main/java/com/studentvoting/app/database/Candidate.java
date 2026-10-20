package com.studentvoting.app.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "candidates")
public class Candidate {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String position;
    private String department;
    private String year;
    private String manifesto;
    private int voteCount;
    private String photoInitial;

    public Candidate() {}

    public Candidate(String name, String position, String department, String year,
                     String manifesto, int voteCount, String photoInitial) {
        this.name = name;
        this.position = position;
        this.department = department;
        this.year = year;
        this.manifesto = manifesto;
        this.voteCount = voteCount;
        this.photoInitial = photoInitial;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPosition() { return position; }
    public String getDepartment() { return department; }
    public String getYear() { return year; }
    public String getManifesto() { return manifesto; }
    public int getVoteCount() { return voteCount; }
    public String getPhotoInitial() { return photoInitial; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPosition(String position) { this.position = position; }
    public void setDepartment(String department) { this.department = department; }
    public void setYear(String year) { this.year = year; }
    public void setManifesto(String manifesto) { this.manifesto = manifesto; }
    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }
    public void setPhotoInitial(String photoInitial) { this.photoInitial = photoInitial; }
}
