package com.studentvoting.app.candidates;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.studentvoting.app.R;
import com.studentvoting.app.database.AppDatabase;
import com.studentvoting.app.database.Candidate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CandidateDetailActivity extends AppCompatActivity {

    private TextInputLayout tilCandidateId;
    private TextInputEditText etCandidateId;
    private Button btnSearch;
    private CardView cardDetail;
    private TextView tvInitial;
    private TextView tvName;
    private TextView tvPosition;
    private TextView tvDepartment;
    private TextView tvYear;
    private TextView tvManifesto;
    private TextView tvVoteCount;
    private TextView tvCandidateId;
    private View progressBar;
    private View searchSection;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static final int[] AVATAR_COLORS = {
            0xFF3F51B5, 0xFF0288D1, 0xFF00897B, 0xFF43A047,
            0xFFE91E63, 0xFF8E24AA, 0xFFFF6F00, 0xFF6D4C41
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("View Candidate");
        }

        initViews();
        setupClickListeners();

        // Check if candidate_id was passed directly
        int candidateId = getIntent().getIntExtra("candidate_id", -1);
        if (candidateId != -1) {
            searchSection.setVisibility(View.GONE);
            loadCandidateById(candidateId);
        }
    }

    private void initViews() {
        tilCandidateId = findViewById(R.id.til_candidate_id);
        etCandidateId = findViewById(R.id.et_candidate_id);
        btnSearch = findViewById(R.id.btn_search);
        cardDetail = findViewById(R.id.card_detail);
        tvInitial = findViewById(R.id.tv_initial);
        tvName = findViewById(R.id.tv_name);
        tvPosition = findViewById(R.id.tv_position);
        tvDepartment = findViewById(R.id.tv_department);
        tvYear = findViewById(R.id.tv_year);
        tvManifesto = findViewById(R.id.tv_manifesto);
        tvVoteCount = findViewById(R.id.tv_vote_count);
        tvCandidateId = findViewById(R.id.tv_candidate_id);
        progressBar = findViewById(R.id.progress_bar);
        searchSection = findViewById(R.id.search_section);

        cardDetail.setVisibility(View.GONE);
    }

    private void setupClickListeners() {
        btnSearch.setOnClickListener(v -> {
            String idStr = etCandidateId.getText() != null ? etCandidateId.getText().toString().trim() : "";
            tilCandidateId.setError(null);

            if (TextUtils.isEmpty(idStr)) {
                tilCandidateId.setError(getString(R.string.field_required));
                return;
            }

            try {
                int id = Integer.parseInt(idStr);
                loadCandidateById(id);
            } catch (NumberFormatException e) {
                tilCandidateId.setError("Please enter a valid numeric ID");
            }
        });
    }

    private void loadCandidateById(int id) {
        showLoading(true);
        cardDetail.setVisibility(View.GONE);

        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Candidate candidate = db.candidateDao().getCandidateById(id);

            mainHandler.post(() -> {
                showLoading(false);
                if (candidate != null) {
                    displayCandidate(candidate);
                } else {
                    if (tilCandidateId != null) {
                        tilCandidateId.setError(getString(R.string.candidate_not_found));
                    }
                    cardDetail.setVisibility(View.GONE);
                }
            });
        });
    }

    private void displayCandidate(Candidate candidate) {
        String initial = candidate.getPhotoInitial() != null && !candidate.getPhotoInitial().isEmpty()
                ? candidate.getPhotoInitial().substring(0, 1).toUpperCase()
                : candidate.getName().substring(0, 1).toUpperCase();
        tvInitial.setText(initial);

        int colorIndex = (candidate.getId() - 1) % AVATAR_COLORS.length;
        tvInitial.getBackground().setTint(AVATAR_COLORS[colorIndex]);

        tvName.setText(candidate.getName());
        tvPosition.setText(candidate.getPosition());
        tvDepartment.setText(candidate.getDepartment());
        tvYear.setText(candidate.getYear());
        tvManifesto.setText(candidate.getManifesto());
        tvVoteCount.setText(candidate.getVoteCount() + " votes");
        tvCandidateId.setText("Candidate ID: " + candidate.getId());

        cardDetail.setVisibility(View.VISIBLE);
        cardDetail.setAlpha(0f);
        cardDetail.setTranslationY(40f);
        cardDetail.animate().alpha(1f).translationY(0f).setDuration(400).start();
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
