package com.studentvoting.app.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CandidateDao {

    @Insert
    long insert(Candidate candidate);

    @Update
    void update(Candidate candidate);

    @Delete
    void delete(Candidate candidate);

    @Query("SELECT * FROM candidates ORDER BY voteCount DESC")
    List<Candidate> getAllCandidates();

    @Query("SELECT * FROM candidates WHERE id = :id")
    Candidate getCandidateById(int id);

    @Query("SELECT * FROM candidates ORDER BY voteCount DESC LIMIT 1")
    Candidate getWinner();

    @Query("SELECT SUM(voteCount) FROM candidates")
    int getTotalVotes();

    @Query("SELECT COUNT(*) FROM candidates")
    int getCandidateCount();

    @Query("DELETE FROM candidates")
    void deleteAll();

    @Query("UPDATE candidates SET voteCount = voteCount + 1 WHERE id = :candidateId")
    void incrementVoteCount(int candidateId);
}
