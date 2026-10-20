package com.studentvoting.app.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface VoteDao {

    @Insert
    long insert(Vote vote);

    @Query("SELECT * FROM votes ORDER BY timestamp DESC")
    List<Vote> getAllVotes();

    @Query("SELECT * FROM votes WHERE studentId = :studentId")
    Vote getVoteByStudentId(String studentId);

    @Query("SELECT COUNT(*) FROM votes WHERE candidateId = :candidateId")
    int getVoteCountForCandidate(int candidateId);

    @Query("SELECT COUNT(*) FROM votes")
    int getTotalVotes();

    @Query("DELETE FROM votes")
    void deleteAll();
}
