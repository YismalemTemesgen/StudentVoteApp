package com.studentvoting.app.candidates;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.studentvoting.app.R;

public class CandidatesMenuActivity extends AppCompatActivity {

    private CardView cardList;
    private CardView cardViewById;
    private CardView cardUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidates_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Candidates");
        }

        cardList = findViewById(R.id.card_candidate_list);
        cardViewById = findViewById(R.id.card_view_by_id);
        cardUpdate = findViewById(R.id.card_update_candidate);

        cardList.setOnClickListener(v -> {
            startActivity(new Intent(this, CandidateListActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        cardViewById.setOnClickListener(v -> {
            startActivity(new Intent(this, CandidateDetailActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        cardUpdate.setOnClickListener(v -> {
            startActivity(new Intent(this, CandidateUpdateActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        animateCards();
    }

    private void animateCards() {
        View[] cards = {cardList, cardViewById, cardUpdate};
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] != null) {
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
