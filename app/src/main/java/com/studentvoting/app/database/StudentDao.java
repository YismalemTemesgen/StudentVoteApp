package com.studentvoting.app.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StudentDao {

    @Insert
    long insert(Student student);

    @Update
    void update(Student student);

    @Delete
    void delete(Student student);

    @Query("SELECT * FROM students ORDER BY name ASC")
    List<Student> getAllStudents();

    @Query("SELECT * FROM students WHERE studentId = :studentId")
    Student getStudentByStudentId(String studentId);

    @Query("SELECT * FROM students WHERE id = :id")
    Student getStudentById(int id);

    @Query("SELECT COUNT(*) FROM students")
    int getTotalStudents();

    @Query("SELECT COUNT(*) FROM students WHERE hasVoted = 1")
    int getVotedStudentsCount();

    @Query("UPDATE students SET hasVoted = 1, votedCandidateId = :candidateId WHERE studentId = :studentId")
    void markStudentAsVoted(String studentId, int candidateId);

    @Query("SELECT COUNT(*) FROM students")
    int getStudentCount();

    @Query("DELETE FROM students")
    void deleteAll();
}
