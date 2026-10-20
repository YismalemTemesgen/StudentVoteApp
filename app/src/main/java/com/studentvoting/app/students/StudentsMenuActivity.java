package com.studentvoting.app.students;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.studentvoting.app.R;
import com.studentvoting.app.utils.SessionManager;

public class StudentsMenuActivity extends AppCompatActivity {

    private CardView cardStudentList;
    private CardView cardVote;
    private CardView cardResults;
    private CardView cardStatus;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Students");
        }

        sessionManager = new SessionManager(this);

        cardStudentList = findViewById(R.id.card_student_list);
        cardVote = findViewById(R.id.card_vote);
        cardResults = findViewById(R.id.card_results);
        cardStatus = findViewById(R.id.card_status);

        // Only show vote card for students
        if (sessionManager.isAdmin()) {
            cardVote.setVisibility(View.GONE);
            cardStatus.setVisibility(View.GONE);
        }

        cardStudentList.setOnClickListener(v -> {
            startActivity(new Intent(this, StudentListActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        cardVote.setOnClickListener(v -> {
            startActivity(new Intent(this, StudentVoteActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        cardResults.setOnClickListener(v -> {
            startActivity(new Intent(this, StudentResultsActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        cardStatus.setOnClickListener(v -> {
            startActivity(new Intent(this, StudentStatusActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        animateCards();
    }

    private void animateCards() {
        View[] cards = {cardStudentList, cardVote, cardResults, cardStatus};
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] != null && cards[i].getVisibility() == View.VISIBLE) {
                cards[i].setAlpha(0f);
                cards[i].setTranslationX(100f);
                cards[i].animate()
                        .alpha(1f)
                        .translationX(0f)
                        .setDuration(350)
                        .setStartDelay(80L * i + 100)
                        .start();
            }
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
}
