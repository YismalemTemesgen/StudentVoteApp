package com.studentvoting.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.studentvoting.app.database.AppDatabase;
import com.studentvoting.app.database.Student;
import com.studentvoting.app.utils.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private TabLayout tabLayout;

    // Student login views
    private View studentLoginCard;
    private TextInputLayout tilStudentId;
    private TextInputLayout tilStudentPassword;
    private TextInputEditText etStudentId;
    private TextInputEditText etStudentPassword;
    private Button btnStudentLogin;

    // Admin login views
    private View adminLoginCard;
    private TextInputLayout tilAdminUsername;
    private TextInputLayout tilAdminPassword;
    private TextInputEditText etAdminUsername;
    private TextInputEditText etAdminPassword;
    private Button btnAdminLogin;

    private Button btnExit;
    private View loadingOverlay;

    private SessionManager sessionManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        initViews();
        setupTabs();
        setupClickListeners();
        animateEntrance();
    }

    private void initViews() {
        tabLayout = findViewById(R.id.tab_layout);

        studentLoginCard = findViewById(R.id.card_student_login);
        tilStudentId = findViewById(R.id.til_student_id);
        tilStudentPassword = findViewById(R.id.til_student_password);
        etStudentId = findViewById(R.id.et_student_id);
        etStudentPassword = findViewById(R.id.et_student_password);
        btnStudentLogin = findViewById(R.id.btn_student_login);

        adminLoginCard = findViewById(R.id.card_admin_login);
        tilAdminUsername = findViewById(R.id.til_admin_username);
        tilAdminPassword = findViewById(R.id.til_admin_password);
        etAdminUsername = findViewById(R.id.et_admin_username);
        etAdminPassword = findViewById(R.id.et_admin_password);
        btnAdminLogin = findViewById(R.id.btn_admin_login);

        btnExit = findViewById(R.id.btn_exit);
        loadingOverlay = findViewById(R.id.loading_overlay);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_student));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_admin));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    showStudentLogin();
                } else {
                    showAdminLogin();
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Default: show student login
        showStudentLogin();
    }

    private void showStudentLogin() {
        studentLoginCard.setVisibility(View.VISIBLE);
        adminLoginCard.setVisibility(View.GONE);
        studentLoginCard.animate().alpha(1f).translationY(0f).setDuration(300).start();
    }

    private void showAdminLogin() {
        studentLoginCard.setVisibility(View.GONE);
        adminLoginCard.setVisibility(View.VISIBLE);
        adminLoginCard.animate().alpha(1f).translationY(0f).setDuration(300).start();
    }

    private void setupClickListeners() {
        btnStudentLogin.setOnClickListener(v -> attemptStudentLogin());
        btnAdminLogin.setOnClickListener(v -> attemptAdminLogin());
        btnExit.setOnClickListener(v -> showExitDialog());
    }

    private void animateEntrance() {
        View headerCard = findViewById(R.id.header_card);
        View loginContainer = findViewById(R.id.login_container);

        if (headerCard != null) {
            headerCard.setAlpha(0f);
            headerCard.setTranslationY(-80f);
            headerCard.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(100).start();
        }

        if (loginContainer != null) {
            loginContainer.setAlpha(0f);
            loginContainer.setTranslationY(60f);
            loginContainer.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(300).start();
        }
    }

    private void attemptStudentLogin() {
        String studentId = etStudentId.getText() != null ? etStudentId.getText().toString().trim() : "";
        String password = etStudentPassword.getText() != null ? etStudentPassword.getText().toString().trim() : "";

        // Clear errors
        tilStudentId.setError(null);
        tilStudentPassword.setError(null);

        boolean valid = true;
        if (TextUtils.isEmpty(studentId)) {
            tilStudentId.setError(getString(R.string.field_required));
            valid = false;
        }
        if (TextUtils.isEmpty(password)) {
            tilStudentPassword.setError(getString(R.string.field_required));
            valid = false;
        }
        if (!valid) return;

        showLoading(true);

        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Student student = db.studentDao().getStudentByStudentId(studentId);

            mainHandler.post(() -> {
                showLoading(false);
                if (student != null) {
                    sessionManager.createStudentSession(student.getStudentId(), student.getName());
                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                    navigateToMainMenu();
                } else {
                    tilStudentId.setError(getString(R.string.student_not_found));
                    shakeView(studentLoginCard);
                }
            });
        });
    }

    private void attemptAdminLogin() {
        String username = etAdminUsername.getText() != null ? etAdminUsername.getText().toString().trim() : "";
        String password = etAdminPassword.getText() != null ? etAdminPassword.getText().toString().trim() : "";

        // Clear errors
        tilAdminUsername.setError(null);
        tilAdminPassword.setError(null);

        boolean valid = true;
        if (TextUtils.isEmpty(username)) {
            tilAdminUsername.setError(getString(R.string.field_required));
            valid = false;
        }
        if (TextUtils.isEmpty(password)) {
            tilAdminPassword.setError(getString(R.string.field_required));
            valid = false;
        }
        if (!valid) return;

        showLoading(true);

        mainHandler.postDelayed(() -> {
            showLoading(false);
            if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
                sessionManager.createAdminSession();
                Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                navigateToMainMenu();
            } else {
                tilAdminPassword.setError(getString(R.string.login_failed));
                shakeView(adminLoginCard);
            }
        }, 600);
    }

    private void navigateToMainMenu() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.btn_exit))
                .setMessage("Are you sure you want to exit the app?")
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> finishAffinity())
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void showLoading(boolean show) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void shakeView(View view) {
        android.animation.ObjectAnimator shaker = android.animation.ObjectAnimator.ofFloat(
                view, "translationX", 0f, 20f, -20f, 15f, -15f, 8f, -8f, 0f);
        shaker.setDuration(500);
        shaker.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
