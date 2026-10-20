package com.studentvoting.app.students;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.studentvoting.app.R;
import com.studentvoting.app.candidates.CandidateAdapter;
import com.studentvoting.app.database.AppDatabase;
import com.studentvoting.app.database.Candidate;
import com.studentvoting.app.database.Student;
import com.studentvoting.app.database.Vote;
import com.studentvoting.app.utils.SessionManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentVoteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CandidateAdapter adapter;
    private TextView tvEmpty;
    private TextView tvAlreadyVoted;
    private TextView tvStudentInfo;
    private CardView cardAlreadyVoted;
    private View progressBar;

    private SessionManager sessionManager;
    private Candidate selectedCandidate;
    private Student currentStudent;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_vote);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Cast Your Vote");
        }

        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recycler_view);
        tvEmpty = findViewById(R.id.tv_empty);
        tvAlreadyVoted = findViewById(R.id.tv_already_voted);
        tvStudentInfo = findViewById(R.id.tv_student_info);
        cardAlreadyVoted = findViewById(R.id.card_already_voted);
        progressBar = findViewById(R.id.progress_bar);

        adapter = new CandidateAdapter(this);
        adapter.setOnCandidateClickListener(candidate -> {
            selectedCandidate = candidate;
            showVoteConfirmDialog(candidate);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        checkStudentAndLoadCandidates();
    }

    private void checkStudentAndLoadCandidates() {
        showLoading(true);
        String studentId = sessionManager.getStudentId();

        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Student student = db.studentDao().getStudentByStudentId(studentId);
            List<Candidate> candidates = db.candidateDao().getAllCandidates();

            mainHandler.post(() -> {
                showLoading(false);
                currentStudent = student;

                if (student != null) {
                    tvStudentInfo.setText("Voting as: " + student.getName() + " (" + student.getStudentId() + ")");

                    if (student.isHasVoted()) {
                        // Already voted
                        cardAlreadyVoted.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.GONE);

                        // Find voted candidate name
                        if (candidates != null) {
                            for (Candidate c : candidates) {
                                if (c.getId() == student.getVotedCandidateId()) {
                                    tvAlreadyVoted.setText("You have already voted for:\n" + c.getName() + "\n(" + c.getPosition() + ")");
                                    break;
                                }
                            }
                        }
                    } else {
                        cardAlreadyVoted.setVisibility(View.GONE);
                        if (candidates == null || candidates.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            tvEmpty.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            adapter.setCandidates(candidates);
                        }
                    }
                }
            });
        });
    }

    private void showVoteConfirmDialog(Candidate candidate) {
        String message = String.format(getString(R.string.vote_confirm_message), candidate.getName());
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.vote_confirm_title))
                .setMessage(message + "\n\nPosition: " + candidate.getPosition() + "\nDepartment: " + candidate.getDepartment())
                .setPositiveButton(getString(R.string.btn_confirm), (dialog, which) -> castVote(candidate))
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .show();
    }

    private void castVote(Candidate candidate) {
        if (currentStudent == null) return;
        if (currentStudent.isHasVoted()) {
            Toast.makeText(this, getString(R.string.already_voted), Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            // Double-check student hasn't voted (race condition protection)
            Student freshStudent = db.studentDao().getStudentByStudentId(currentStudent.getStudentId());
            if (freshStudent != null && freshStudent.isHasVoted()) {
                mainHandler.post(() -> {
                    showLoading(false);
                    Toast.makeText(this, getString(R.string.already_voted), Toast.LENGTH_SHORT).show();
                    checkStudentAndLoadCandidates();
                });
                return;
            }

            // Record vote
            Vote vote = new Vote(currentStudent.getStudentId(), candidate.getId(), System.currentTimeMillis());
            db.voteDao().insert(vote);

            // Update student
            db.studentDao().markStudentAsVoted(currentStudent.getStudentId(), candidate.getId());

            // Increment candidate vote count
            db.candidateDao().incrementVoteCount(candidate.getId());

            mainHandler.post(() -> {
                showLoading(false);
                Toast.makeText(this, getString(R.string.vote_success), Toast.LENGTH_LONG).show();
                checkStudentAndLoadCandidates();
            });
        });
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
