package com.studentvoting.app.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.studentvoting.app.R;

public class AdminMenuActivity extends AppCompatActivity {

    private CardView cardTrack;
    private CardView cardReport;
    private CardView cardModify;
    private CardView cardAnnounce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Election Admin");
        }

        cardTrack = findViewById(R.id.card_track);
        cardReport = findViewById(R.id.card_report);
        cardModify = findViewById(R.id.card_modify);
        cardAnnounce = findViewById(R.id.card_announce);

        cardTrack.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminTrackActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        cardReport.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminReportActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        cardModify.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminModifyActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        cardAnnounce.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminAnnounceActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        animateCards();
    }

    private void animateCards() {
        View[] cards = {cardTrack, cardReport, cardModify, cardAnnounce};
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
