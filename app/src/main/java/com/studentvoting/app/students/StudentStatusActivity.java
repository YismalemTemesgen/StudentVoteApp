package com.studentvoting.app.students;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.studentvoting.app.R;
import com.studentvoting.app.database.AppDatabase;
import com.studentvoting.app.database.Candidate;
import com.studentvoting.app.database.Student;
import com.studentvoting.app.utils.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentStatusActivity extends AppCompatActivity {

    private TextView tvStudentName;
    private TextView tvStudentId;
    private TextView tvDepartment;
    private TextView tvYear;
    private TextView tvVoteStatus;
    private TextView tvVotedFor;
    private CardView cardVotedFor;
    private CardView cardNotVoted;
    private View progressBar;

    private SessionManager sessionManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_status);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Voting Status");
        }

        sessionManager = new SessionManager(this);

        tvStudentName = findViewById(R.id.tv_student_name);
        tvStudentId = findViewById(R.id.tv_student_id);
        tvDepartment = findViewById(R.id.tv_department);
        tvYear = findViewById(R.id.tv_year);
        tvVoteStatus = findViewById(R.id.tv_vote_status);
        tvVotedFor = findViewById(R.id.tv_voted_for);
        cardVotedFor = findViewById(R.id.card_voted_for);
        cardNotVoted = findViewById(R.id.card_not_voted);
        progressBar = findViewById(R.id.progress_bar);

        loadStudentStatus();
    }

    private void loadStudentStatus() {
        showLoading(true);
        String studentId = sessionManager.getStudentId();

        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Student student = db.studentDao().getStudentByStudentId(studentId);
            Candidate votedCandidate = null;

            if (student != null && student.isHasVoted() && student.getVotedCandidateId() > 0) {
                votedCandidate = db.candidateDao().getCandidateById(student.getVotedCandidateId());
            }

            final Candidate finalVotedCandidate = votedCandidate;

            mainHandler.post(() -> {
                showLoading(false);
                if (student != null) {
                    displayStatus(student, finalVotedCandidate);
                }
            });
        });
    }

    private void displayStatus(Student student, Candidate votedCandidate) {
        tvStudentName.setText(student.getName());
        tvStudentId.setText("Student ID: " + student.getStudentId());
        tvDepartment.setText("Department: " + student.getDepartment());
        tvYear.setText("Year: " + student.getYear());

        if (student.isHasVoted()) {
            tvVoteStatus.setText("✓ Vote Cast");
            tvVoteStatus.getBackground().setTint(0xFF4CAF50);
            cardVotedFor.setVisibility(View.VISIBLE);
            cardNotVoted.setVisibility(View.GONE);

            if (votedCandidate != null) {
                tvVotedFor.setText("You voted for:\n" + votedCandidate.getName() + "\n" + votedCandidate.getPosition());
            }
        } else {
            tvVoteStatus.setText("✗ Not Voted Yet");
            tvVoteStatus.getBackground().setTint(0xFFFF9800);
            cardVotedFor.setVisibility(View.GONE);
            cardNotVoted.setVisibility(View.VISIBLE);
        }

        // Animate
        View contentCard = findViewById(R.id.card_student_info);
        if (contentCard != null) {
            contentCard.setAlpha(0f);
            contentCard.setTranslationY(40f);
            contentCard.animate().alpha(1f).translationY(0f).setDuration(400).start();
        }
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
