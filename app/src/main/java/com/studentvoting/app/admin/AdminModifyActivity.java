package com.studentvoting.app.admin;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.studentvoting.app.R;
import com.studentvoting.app.candidates.CandidateAdapter;
import com.studentvoting.app.database.AppDatabase;
import com.studentvoting.app.database.Candidate;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminModifyActivity extends AppCompatActivity {

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
    private Button btnAdd;

    private RecyclerView recyclerView;
    private CandidateAdapter adapter;
    private TextView tvEmpty;
    private View progressBar;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_modify);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Modify Candidates");
        }

        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadCandidates();
    }

    private void initViews() {
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
        btnAdd = findViewById(R.id.btn_add);
        recyclerView = findViewById(R.id.recycler_view);
        tvEmpty = findViewById(R.id.tv_empty);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupRecyclerView() {
        adapter = new CandidateAdapter(this);
        adapter.setShowDeleteButton(true, candidate -> showDeleteDialog(candidate));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnAdd.setOnClickListener(v -> addCandidate());
    }

    private void addCandidate() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String position = etPosition.getText() != null ? etPosition.getText().toString().trim() : "";
        String department = etDepartment.getText() != null ? etDepartment.getText().toString().trim() : "";
        String year = etYear.getText() != null ? etYear.getText().toString().trim() : "";
        String manifesto = etManifesto.getText() != null ? etManifesto.getText().toString().trim() : "";

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

        String initial = name.substring(0, 1).toUpperCase();
        Candidate newCandidate = new Candidate(name, position, department, year, manifesto, 0, initial);

        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            db.candidateDao().insert(newCandidate);

            mainHandler.post(() -> {
                showLoading(false);
                Toast.makeText(this, getString(R.string.candidate_added), Toast.LENGTH_SHORT).show();
                clearForm();
                loadCandidates();
            });
        });
    }

    private void showDeleteDialog(Candidate candidate) {
        String message = String.format(getString(R.string.delete_confirm_message), candidate.getName());
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_confirm_title))
                .setMessage(message)
                .setPositiveButton("Delete", (dialog, which) -> deleteCandidate(candidate))
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .show();
    }

    private void deleteCandidate(Candidate candidate) {
        showLoading(true);
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            db.candidateDao().delete(candidate);

            mainHandler.post(() -> {
                showLoading(false);
                Toast.makeText(this, getString(R.string.candidate_deleted), Toast.LENGTH_SHORT).show();
                loadCandidates();
            });
        });
    }

    private void loadCandidates() {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<Candidate> candidates = db.candidateDao().getAllCandidates();

            mainHandler.post(() -> {
                if (candidates == null || candidates.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.setCandidates(candidates);
                }
            });
        });
    }

    private void clearForm() {
        etName.setText("");
        etPosition.setText("");
        etDepartment.setText("");
        etYear.setText("");
        etManifesto.setText("");
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
