package com.studentvoting.app.candidates;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class CandidateUpdateActivity extends AppCompatActivity {

    private TextInputLayout tilSearchId;
    private TextInputEditText etSearchId;
    private Button btnSearch;
    private CardView cardUpdateForm;

    private TextInputLayout tilName;
    private TextInputLayout tilPosition;
    private TextInputLayout tilDepartment;
    private TextInputLayout tilYear;
    private TextInputLayout tilManifesto;
    private TextInputEditText etName;
    private TextInputEditText etPosition;
    private TextInputEditText etDepartment;
    private TextInputEditText etYear;
    private TextInputEditText etManifesto;
    private Button btnUpdate;
    private View progressBar;

    private Candidate currentCandidate;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_update);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Update Candidate");
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        tilSearchId = findViewById(R.id.til_search_id);
        etSearchId = findViewById(R.id.et_search_id);
        btnSearch = findViewById(R.id.btn_search);
        cardUpdateForm = findViewById(R.id.card_update_form);

        tilName = findViewById(R.id.til_name);
        tilPosition = findViewById(R.id.til_position);
        tilDepartment = findViewById(R.id.til_department);
        tilYear = findViewById(R.id.til_year);
        tilManifesto = findViewById(R.id.til_manifesto);
        etName = findViewById(R.id.et_name);
        etPosition = findViewById(R.id.et_position);
        etDepartment = findViewById(R.id.et_department);
        etYear = findViewById(R.id.et_year);
        etManifesto = findViewById(R.id.et_manifesto);
        btnUpdate = findViewById(R.id.btn_update);
        progressBar = findViewById(R.id.progress_bar);

        cardUpdateForm.setVisibility(View.GONE);
    }

    private void setupClickListeners() {
        btnSearch.setOnClickListener(v -> {
            String idStr = etSearchId.getText() != null ? etSearchId.getText().toString().trim() : "";
            tilSearchId.setError(null);

            if (TextUtils.isEmpty(idStr)) {
                tilSearchId.setError(getString(R.string.field_required));
                return;
            }

            try {
                int id = Integer.parseInt(idStr);
                searchCandidate(id);
            } catch (NumberFormatException e) {
                tilSearchId.setError("Please enter a valid numeric ID");
            }
        });

        btnUpdate.setOnClickListener(v -> updateCandidate());
    }

    private void searchCandidate(int id) {
        showLoading(true);
        cardUpdateForm.setVisibility(View.GONE);

        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Candidate candidate = db.candidateDao().getCandidateById(id);

            mainHandler.post(() -> {
                showLoading(false);
                if (candidate != null) {
                    currentCandidate = candidate;
                    populateForm(candidate);
                } else {
                    tilSearchId.setError(getString(R.string.candidate_not_found));
                }
            });
        });
    }

    private void populateForm(Candidate candidate) {
        etName.setText(candidate.getName());
        etPosition.setText(candidate.getPosition());
        etDepartment.setText(candidate.getDepartment());
        etYear.setText(candidate.getYear());
        etManifesto.setText(candidate.getManifesto());

        cardUpdateForm.setVisibility(View.VISIBLE);
        cardUpdateForm.setAlpha(0f);
        cardUpdateForm.setTranslationY(40f);
        cardUpdateForm.animate().alpha(1f).translationY(0f).setDuration(400).start();
    }

    private void updateCandidate() {
        if (currentCandidate == null) return;

        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String position = etPosition.getText() != null ? etPosition.getText().toString().trim() : "";
        String department = etDepartment.getText() != null ? etDepartment.getText().toString().trim() : "";
        String year = etYear.getText() != null ? etYear.getText().toString().trim() : "";
        String manifesto = etManifesto.getText() != null ? etManifesto.getText().toString().trim() : "";

        // Validate
        boolean valid = true;
        if (TextUtils.isEmpty(name)) { tilName.setError(getString(R.string.field_required)); valid = false; }
        else tilName.setError(null);
        if (TextUtils.isEmpty(position)) { tilPosition.setError(getString(R.string.field_required)); valid = false; }
        else tilPosition.setError(null);
        if (TextUtils.isEmpty(department)) { tilDepartment.setError(getString(R.string.field_required)); valid = false; }
        else tilDepartment.setError(null);
        if (TextUtils.isEmpty(year)) { tilYear.setError(getString(R.string.field_required)); valid = false; }
        else tilYear.setError(null);
        if (!valid) return;

        showLoading(true);

        currentCandidate.setName(name);
        currentCandidate.setPosition(position);
        currentCandidate.setDepartment(department);
        currentCandidate.setYear(year);
        currentCandidate.setManifesto(manifesto);
        currentCandidate.setPhotoInitial(name.substring(0, 1).toUpperCase());

        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            db.candidateDao().update(currentCandidate);

            mainHandler.post(() -> {
                showLoading(false);
                Toast.makeText(this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                cardUpdateForm.setVisibility(View.GONE);
                etSearchId.setText("");
                currentCandidate = null;
            });
        });
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
