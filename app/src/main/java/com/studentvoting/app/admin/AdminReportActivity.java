package com.studentvoting.app.admin;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminReportActivity extends AppCompatActivity {

    private TextView tvReportDate;
    private TextView tvTotalCandidates;
    private TextView tvTotalStudents;
    private TextView tvVotedStudents;
    private TextView tvTotalVotes;
    private TextView tvTurnoutPercent;
    private TextView tvWinnerName;
    private TextView tvWinnerPosition;
    private TextView tvWinnerDept;
    private TextView tvWinnerVotes;
    private TextView tvWinnerPercent;
    private CardView cardWinner;
    private TextView tvNoWinner;
    private View progressBar;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_report);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Election Report");
        }

        initViews();
        loadReport();
    }

    private void initViews() {
        tvReportDate = findViewById(R.id.tv_report_date);
        tvTotalCandidates = findViewById(R.id.tv_total_candidates);
        tvTotalStudents = findViewById(R.id.tv_total_students);
        tvVotedStudents = findViewById(R.id.tv_voted_students);
        tvTotalVotes = findViewById(R.id.tv_total_votes);
        tvTurnoutPercent = findViewById(R.id.tv_turnout_percent);
        tvWinnerName = findViewById(R.id.tv_winner_name);
        tvWinnerPosition = findViewById(R.id.tv_winner_position);
        tvWinnerDept = findViewById(R.id.tv_winner_dept);
        tvWinnerVotes = findViewById(R.id.tv_winner_votes);
        tvWinnerPercent = findViewById(R.id.tv_winner_percent);
        cardWinner = findViewById(R.id.card_winner);
        tvNoWinner = findViewById(R.id.tv_no_winner);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void loadReport() {
        showLoading(true);

        String date = new SimpleDateFormat("MMMM dd, yyyy HH:mm", Locale.US).format(new Date());
        tvReportDate.setText("Generated: " + date);

        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            int totalCandidates = db.candidateDao().getCandidateCount();
            int totalStudents = db.studentDao().getTotalStudents();
            int votedStudents = db.studentDao().getVotedStudentsCount();
            int totalVotes = db.candidateDao().getTotalVotes();
            Candidate winner = db.candidateDao().getWinner();
            List<Candidate> allCandidates = db.candidateDao().getAllCandidates();

            mainHandler.post(() -> {
                showLoading(false);
                displayReport(totalCandidates, totalStudents, votedStudents, totalVotes, winner, allCandidates);
            });
        });
    }

    private void displayReport(int totalCandidates, int totalStudents, int votedStudents,
                                int totalVotes, Candidate winner, List<Candidate> allCandidates) {
        tvTotalCandidates.setText(String.valueOf(totalCandidates));
        tvTotalStudents.setText(String.valueOf(totalStudents));
        tvVotedStudents.setText(String.valueOf(votedStudents));
        tvTotalVotes.setText(String.valueOf(totalVotes));

        double turnout = totalStudents > 0 ? (votedStudents * 100.0 / totalStudents) : 0;
        tvTurnoutPercent.setText(String.format(Locale.US, "%.1f%%", turnout));

        if (winner != null && winner.getVoteCount() > 0) {
            cardWinner.setVisibility(View.VISIBLE);
            tvNoWinner.setVisibility(View.GONE);

            tvWinnerName.setText(winner.getName());
            tvWinnerPosition.setText(winner.getPosition());
            tvWinnerDept.setText(winner.getDepartment() + " | " + winner.getYear());
            tvWinnerVotes.setText(winner.getVoteCount() + " votes");

            double winnerPct = totalVotes > 0 ? (winner.getVoteCount() * 100.0 / totalVotes) : 0;
            tvWinnerPercent.setText(String.format(Locale.US, "%.1f%% of total votes", winnerPct));

            cardWinner.setAlpha(0f);
            cardWinner.setTranslationY(40f);
            cardWinner.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(300).start();
        } else {
            cardWinner.setVisibility(View.GONE);
            tvNoWinner.setVisibility(View.VISIBLE);
        }

        // Animate stats cards
        View statsCard = findViewById(R.id.card_stats);
        if (statsCard != null) {
            statsCard.setAlpha(0f);
            statsCard.setTranslationY(30f);
            statsCard.animate().alpha(1f).translationY(0f).setDuration(400).setStartDelay(100).start();
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
