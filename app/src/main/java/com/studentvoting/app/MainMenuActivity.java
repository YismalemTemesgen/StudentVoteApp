package com.studentvoting.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.studentvoting.app.admin.AdminMenuActivity;
import com.studentvoting.app.candidates.CandidatesMenuActivity;
import com.studentvoting.app.students.StudentsMenuActivity;
import com.studentvoting.app.utils.SessionManager;

public class MainMenuActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private TextView tvUserType;
    private CardView cardCandidates;
    private CardView cardStudents;
    private CardView cardElection;
    private CardView cardExit;
    private View electionSection;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        sessionManager = new SessionManager(this);

        initViews();
        setupUserInfo();
        setupClickListeners();
        animateCards();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        tvUserType = findViewById(R.id.tv_user_type);
        cardCandidates = findViewById(R.id.card_candidates);
        cardStudents = findViewById(R.id.card_students);
        cardElection = findViewById(R.id.card_election);
        cardExit = findViewById(R.id.card_exit);
        electionSection = findViewById(R.id.election_section);
    }

    private void setupUserInfo() {
        String userName = sessionManager.getUserName();
        if (sessionManager.isAdmin()) {
            tvWelcome.setText("Welcome, Administrator!");
            tvUserType.setText("Admin Panel");
            // Show election card for admin
            if (electionSection != null) {
                electionSection.setVisibility(View.VISIBLE);
            }
        } else {
            tvWelcome.setText("Welcome, " + userName + "!");
            tvUserType.setText("Student Portal");
            // Hide election card for students
            if (electionSection != null) {
                electionSection.setVisibility(View.GONE);
            }
        }
    }

    private void setupClickListeners() {
        cardCandidates.setOnClickListener(v -> {
            startActivity(new Intent(this, CandidatesMenuActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        cardStudents.setOnClickListener(v -> {
            startActivity(new Intent(this, StudentsMenuActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        cardElection.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminMenuActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        cardExit.setOnClickListener(v -> showLogoutDialog());
    }

    private void animateCards() {
        View[] cards = {cardCandidates, cardStudents, cardElection, cardExit};
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] != null) {
                cards[i].setAlpha(0f);
                cards[i].setTranslationY(80f);
                cards[i].animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(400)
                        .setStartDelay(100L * i + 200)
                        .start();
            }
        }

        // Animate header
        View header = findViewById(R.id.header_layout);
        if (header != null) {
            header.setAlpha(0f);
            header.setTranslationY(-40f);
            header.animate().alpha(1f).translationY(0f).setDuration(400).start();
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.logout_confirm_title))
                .setMessage(getString(R.string.logout_confirm_message))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    @Override
    public void onBackPressed() {
        showLogoutDialog();
    }
}
