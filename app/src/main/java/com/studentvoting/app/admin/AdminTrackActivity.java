package com.studentvoting.app.admin;

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

import com.studentvoting.app.R;
import com.studentvoting.app.database.AppDatabase;
import com.studentvoting.app.database.Candidate;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminTrackActivity extends AppCompatActivity {

    private LinearLayout trackContainer;
    private TextView tvTotalVotes;
    private TextView tvTurnout;
    private TextView tvNoVotes;
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
        setContentView(R.layout.activity_admin_track);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Track Votes");
        }

        trackContainer = findViewById(R.id.track_container);
        tvTotalVotes = findViewById(R.id.tv_total_votes);
        tvTurnout = findViewById(R.id.tv_turnout);
        tvNoVotes = findViewById(R.id.tv_no_votes);
        progressBar = findViewById(R.id.progress_bar);

        loadTrackData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTrackData();
    }

    private void loadTrackData() {
        showLoading(true);
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<Candidate> candidates = db.candidateDao().getAllCandidates();
            int totalVotes = db.candidateDao().getTotalVotes();
            int totalStudents = db.studentDao().getTotalStudents();
            int votedStudents = db.studentDao().getVotedStudentsCount();

            mainHandler.post(() -> {
                showLoading(false);
                displayTrackData(candidates, totalVotes, totalStudents, votedStudents);
            });
        });
    }

    private void displayTrackData(List<Candidate> candidates, int totalVotes, int totalStudents, int votedStudents) {
        trackContainer.removeAllViews();

        tvTotalVotes.setText("Total Votes: " + totalVotes);

        double turnout = totalStudents > 0 ? (votedStudents * 100.0 / totalStudents) : 0;
        tvTurnout.setText(String.format(Locale.US, "Voter Turnout: %.1f%% (%d/%d)", turnout, votedStudents, totalStudents));

        if (totalVotes == 0) {
            tvNoVotes.setVisibility(View.VISIBLE);
        } else {
            tvNoVotes.setVisibility(View.GONE);
        }

        if (candidates != null) {
            for (int i = 0; i < candidates.size(); i++) {
                Candidate c = candidates.get(i);
                View row = buildTrackRow(c, totalVotes, i);
                trackContainer.addView(row);
            }
        }
    }

    private View buildTrackRow(Candidate candidate, int totalVotes, int index) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_track_row, trackContainer, false);

        TextView tvInitial = row.findViewById(R.id.tv_initial);
        TextView tvName = row.findViewById(R.id.tv_name);
        TextView tvPosition = row.findViewById(R.id.tv_position);
        TextView tvVotes = row.findViewById(R.id.tv_votes);
        TextView tvPercent = row.findViewById(R.id.tv_percent);
        ProgressBar progressVotes = row.findViewById(R.id.progress_votes);

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

        // Animate
        row.setAlpha(0f);
        row.setTranslationX(-60f);
        row.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(350)
                .setStartDelay(index * 80L + 100)
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
