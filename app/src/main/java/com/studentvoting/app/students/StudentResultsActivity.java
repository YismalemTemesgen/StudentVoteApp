package com.studentvoting.app.students;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.studentvoting.app.R;
import com.studentvoting.app.database.AppDatabase;
import com.studentvoting.app.database.Candidate;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentResultsActivity extends AppCompatActivity {

    private LinearLayout resultsContainer;
    private TextView tvTotalVotes;
    private TextView tvNoVotes;
    private CardView cardWinner;
    private TextView tvWinnerName;
    private TextView tvWinnerPosition;
    private TextView tvWinnerVotes;
    private View progressBar;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static final int[] BAR_COLORS = {
            0xFF3F51B5, 0xFF0288D1, 0xFF00897B, 0xFF43A047,
            0xFFE91E63, 0xFF8E24AA, 0xFFFF6F00, 0xFF6D4C41
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_results);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Election Results");
        }

        resultsContainer = findViewById(R.id.results_container);
        tvTotalVotes = findViewById(R.id.tv_total_votes);
        tvNoVotes = findViewById(R.id.tv_no_votes);
        cardWinner = findViewById(R.id.card_winner);
        tvWinnerName = findViewById(R.id.tv_winner_name);
        tvWinnerPosition = findViewById(R.id.tv_winner_position);
        tvWinnerVotes = findViewById(R.id.tv_winner_votes);
        progressBar = findViewById(R.id.progress_bar);

        loadResults();
    }

    private void loadResults() {
        showLoading(true);
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<Candidate> candidates = db.candidateDao().getAllCandidates();
            int totalVotes = db.candidateDao().getTotalVotes();
            Candidate winner = db.candidateDao().getWinner();

            mainHandler.post(() -> {
                showLoading(false);
                displayResults(candidates, totalVotes, winner);
            });
        });
    }

    private void displayResults(List<Candidate> candidates, int totalVotes, Candidate winner) {
        resultsContainer.removeAllViews();

        if (totalVotes == 0) {
            tvNoVotes.setVisibility(View.VISIBLE);
            cardWinner.setVisibility(View.GONE);
            tvTotalVotes.setText("Total Votes: 0");
            return;
        }

        tvNoVotes.setVisibility(View.GONE);
        tvTotalVotes.setText("Total Votes Cast: " + totalVotes);

        // Show winner card
        if (winner != null && winner.getVoteCount() > 0) {
            cardWinner.setVisibility(View.VISIBLE);
            tvWinnerName.setText(winner.getName());
            tvWinnerPosition.setText(winner.getPosition());
            double winnerPct = totalVotes > 0 ? (winner.getVoteCount() * 100.0 / totalVotes) : 0;
            tvWinnerVotes.setText(winner.getVoteCount() + " votes (" + String.format(Locale.US, "%.1f", winnerPct) + "%)");

            cardWinner.setAlpha(0f);
            cardWinner.setScaleX(0.8f);
            cardWinner.setScaleY(0.8f);
            cardWinner.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(500).start();
        } else {
            cardWinner.setVisibility(View.GONE);
        }

        // Build result rows
        if (candidates != null) {
            for (int i = 0; i < candidates.size(); i++) {
                Candidate c = candidates.get(i);
                View row = buildResultRow(c, totalVotes, i, winner != null && c.getId() == winner.getId());
                resultsContainer.addView(row);
            }
        }
    }

    private View buildResultRow(Candidate candidate, int totalVotes, int index, boolean isWinner) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_result_row, resultsContainer, false);

        TextView tvInitial = row.findViewById(R.id.tv_initial);
        TextView tvName = row.findViewById(R.id.tv_name);
        TextView tvPosition = row.findViewById(R.id.tv_position);
        TextView tvVotes = row.findViewById(R.id.tv_votes);
        TextView tvPercent = row.findViewById(R.id.tv_percent);
        ProgressBar progressVotes = row.findViewById(R.id.progress_votes);
        TextView tvWinnerBadge = row.findViewById(R.id.tv_winner_badge);

        String initial = candidate.getPhotoInitial() != null && !candidate.getPhotoInitial().isEmpty()
                ? candidate.getPhotoInitial().substring(0, 1).toUpperCase()
                : candidate.getName().substring(0, 1).toUpperCase();
        tvInitial.setText(initial);
        tvInitial.getBackground().setTint(BAR_COLORS[index % BAR_COLORS.length]);

        tvName.setText(candidate.getName());
        tvPosition.setText(candidate.getPosition());
        tvVotes.setText(candidate.getVoteCount() + " votes");

        double pct = totalVotes > 0 ? (candidate.getVoteCount() * 100.0 / totalVotes) : 0;
        tvPercent.setText(String.format(Locale.US, "%.1f%%", pct));

        progressVotes.setMax(100);
        progressVotes.setProgress((int) pct);
        progressVotes.getProgressDrawable().setTint(BAR_COLORS[index % BAR_COLORS.length]);

        if (isWinner && candidate.getVoteCount() > 0) {
            tvWinnerBadge.setVisibility(View.VISIBLE);
            row.setBackgroundResource(R.drawable.bg_winner_row);
        } else {
            tvWinnerBadge.setVisibility(View.GONE);
        }

        // Animate row
        row.setAlpha(0f);
        row.setTranslationX(-60f);
        row.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(350)
                .setStartDelay(index * 80L + 200)
                .start();

        return row;
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
