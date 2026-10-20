package com.studentvoting.app.admin;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
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

public class AdminAnnounceActivity extends AppCompatActivity {

    private CardView cardAnnouncement;
    private TextView tvCongrats;
    private TextView tvWinnerLabel;
    private TextView tvWinnerInitial;
    private TextView tvWinnerName;
    private TextView tvWinnerPosition;
    private TextView tvWinnerDept;
    private TextView tvWinnerVotes;
    private TextView tvNoWinner;
    private View progressBar;
    private View confettiContainer;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_announce);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Announce Winner");
        }

        initViews();
        loadWinner();
    }

    private void initViews() {
        cardAnnouncement = findViewById(R.id.card_announcement);
        tvCongrats = findViewById(R.id.tv_congrats);
        tvWinnerLabel = findViewById(R.id.tv_winner_label);
        tvWinnerInitial = findViewById(R.id.tv_winner_initial);
        tvWinnerName = findViewById(R.id.tv_winner_name);
        tvWinnerPosition = findViewById(R.id.tv_winner_position);
        tvWinnerDept = findViewById(R.id.tv_winner_dept);
        tvWinnerVotes = findViewById(R.id.tv_winner_votes);
        tvNoWinner = findViewById(R.id.tv_no_winner);
        progressBar = findViewById(R.id.progress_bar);
        confettiContainer = findViewById(R.id.confetti_container);
    }

    private void loadWinner() {
        showLoading(true);
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Candidate winner = db.candidateDao().getWinner();
            int totalVotes = db.candidateDao().getTotalVotes();

            mainHandler.post(() -> {
                showLoading(false);
                if (winner != null && winner.getVoteCount() > 0) {
                    displayWinner(winner, totalVotes);
                } else {
                    showNoWinner();
                }
            });
        });
    }

    private void displayWinner(Candidate winner, int totalVotes) {
        cardAnnouncement.setVisibility(View.VISIBLE);
        tvNoWinner.setVisibility(View.GONE);

        String initial = winner.getPhotoInitial() != null && !winner.getPhotoInitial().isEmpty()
                ? winner.getPhotoInitial().substring(0, 1).toUpperCase()
                : winner.getName().substring(0, 1).toUpperCase();
        tvWinnerInitial.setText(initial);
        tvWinnerName.setText(winner.getName());
        tvWinnerPosition.setText(winner.getPosition());
        tvWinnerDept.setText(winner.getDepartment() + " | " + winner.getYear());

        double pct = totalVotes > 0 ? (winner.getVoteCount() * 100.0 / totalVotes) : 0;
        tvWinnerVotes.setText(winner.getVoteCount() + " votes  ·  " + String.format(Locale.US, "%.1f%%", pct));

        // Dramatic entrance animation
        cardAnnouncement.setAlpha(0f);
        cardAnnouncement.setScaleX(0.5f);
        cardAnnouncement.setScaleY(0.5f);

        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(cardAnnouncement, "alpha", 0f, 1f);
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(cardAnnouncement, "scaleX", 0.5f, 1f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(cardAnnouncement, "scaleY", 0.5f, 1f);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnim, scaleXAnim, scaleYAnim);
        set.setDuration(700);
        set.setInterpolator(new OvershootInterpolator(1.5f));
        set.start();

        // Animate confetti dots
        if (confettiContainer != null) {
            confettiContainer.setVisibility(View.VISIBLE);
            animateConfetti();
        }

        // Pulse animation on winner name
        mainHandler.postDelayed(() -> {
            ObjectAnimator pulse = ObjectAnimator.ofFloat(tvWinnerName, "scaleX", 1f, 1.05f, 1f);
            pulse.setDuration(600);
            pulse.setRepeatCount(3);
            pulse.start();
        }, 800);
    }

    private void animateConfetti() {
        // Animate each confetti dot with a staggered bounce
        if (confettiContainer instanceof android.widget.LinearLayout) {
            android.widget.LinearLayout container = (android.widget.LinearLayout) confettiContainer;
            for (int i = 0; i < container.getChildCount(); i++) {
                View dot = container.getChildAt(i);
                dot.setAlpha(0f);
                dot.setTranslationY(-40f);
                dot.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(400)
                        .setStartDelay(i * 60L + 500)
                        .start();
            }
        }
    }

    private void showNoWinner() {
        cardAnnouncement.setVisibility(View.GONE);
        tvNoWinner.setVisibility(View.VISIBLE);
        tvNoWinner.setAlpha(0f);
        tvNoWinner.animate().alpha(1f).setDuration(400).start();
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
