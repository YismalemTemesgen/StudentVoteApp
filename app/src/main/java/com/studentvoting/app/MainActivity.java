package com.studentvoting.app;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.studentvoting.app.database.AppDatabase;
import com.studentvoting.app.database.Candidate;
import com.studentvoting.app.database.Student;
import com.studentvoting.app.utils.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2500;

    private ImageView ivLogo;
    private TextView tvAppName;
    private TextView tvTagline;
    private View progressBar;

    private SessionManager sessionManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        ivLogo = findViewById(R.id.iv_logo);
        tvAppName = findViewById(R.id.tv_app_name);
        tvTagline = findViewById(R.id.tv_tagline);
        progressBar = findViewById(R.id.progress_bar);

        // Start animations
        startSplashAnimations();

        // Initialize DB in background if needed
        if (!sessionManager.isDbInitialized()) {
            initializeSampleData();
        } else {
            scheduleNavigation();
        }
    }

    private void startSplashAnimations() {
        // Logo: scale + fade in
        ivLogo.setAlpha(0f);
        ivLogo.setScaleX(0.3f);
        ivLogo.setScaleY(0.3f);

        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(ivLogo, "alpha", 0f, 1f);
        ObjectAnimator logoScaleX = ObjectAnimator.ofFloat(ivLogo, "scaleX", 0.3f, 1f);
        ObjectAnimator logoScaleY = ObjectAnimator.ofFloat(ivLogo, "scaleY", 0.3f, 1f);

        AnimatorSet logoSet = new AnimatorSet();
        logoSet.playTogether(logoAlpha, logoScaleX, logoScaleY);
        logoSet.setDuration(700);
        logoSet.setInterpolator(new OvershootInterpolator(1.2f));
        logoSet.start();

        // App name: slide up + fade in
        tvAppName.setAlpha(0f);
        tvAppName.setTranslationY(60f);
        tvAppName.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay(400)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Tagline: slide up + fade in
        tvTagline.setAlpha(0f);
        tvTagline.setTranslationY(40f);
        tvTagline.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay(600)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Progress bar: fade in
        progressBar.setAlpha(0f);
        progressBar.animate()
                .alpha(1f)
                .setDuration(400)
                .setStartDelay(900)
                .start();
    }

    private void initializeSampleData() {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            // Insert sample candidates
            if (db.candidateDao().getCandidateCount() == 0) {
                db.candidateDao().insert(new Candidate(
                        "Alice Johnson", "President", "Computer Science", "3rd Year",
                        "I will champion digital innovation, improve campus Wi-Fi, and create more tech labs for all students.",
                        0, "A"));
                db.candidateDao().insert(new Candidate(
                        "Bob Martinez", "Vice President", "Engineering", "4th Year",
                        "My goal is to bridge the gap between faculty and students, ensuring every voice is heard and acted upon.",
                        0, "B"));
                db.candidateDao().insert(new Candidate(
                        "Carol Williams", "Secretary", "Business Administration", "2nd Year",
                        "I will maintain transparent records, organize engaging events, and keep students informed about all activities.",
                        0, "C"));
                db.candidateDao().insert(new Candidate(
                        "David Chen", "Treasurer", "Finance", "3rd Year",
                        "I will ensure responsible management of student funds, with full transparency and regular financial reports.",
                        0, "D"));
                db.candidateDao().insert(new Candidate(
                        "Emma Davis", "Social Secretary", "Arts & Humanities", "2nd Year",
                        "I will bring vibrant cultural events, foster inclusivity, and make campus life exciting for every student.",
                        0, "E"));
            }

            // Insert sample students
            if (db.studentDao().getStudentCount() == 0) {
                db.studentDao().insert(new Student("STU001", "James Wilson", "Computer Science", "1st Year", false, 0));
                db.studentDao().insert(new Student("STU002", "Sarah Brown", "Engineering", "2nd Year", false, 0));
                db.studentDao().insert(new Student("STU003", "Michael Lee", "Business Administration", "3rd Year", false, 0));
                db.studentDao().insert(new Student("STU004", "Jessica Taylor", "Finance", "1st Year", false, 0));
                db.studentDao().insert(new Student("STU005", "Daniel Anderson", "Arts & Humanities", "4th Year", false, 0));
                db.studentDao().insert(new Student("STU006", "Ashley Thomas", "Computer Science", "2nd Year", false, 0));
                db.studentDao().insert(new Student("STU007", "Christopher Jackson", "Engineering", "3rd Year", false, 0));
                db.studentDao().insert(new Student("STU008", "Amanda White", "Business Administration", "1st Year", false, 0));
                db.studentDao().insert(new Student("STU009", "Matthew Harris", "Finance", "2nd Year", false, 0));
                db.studentDao().insert(new Student("STU010", "Stephanie Martin", "Arts & Humanities", "3rd Year", false, 0));
            }

            sessionManager.setDbInitialized();

            mainHandler.postDelayed(this::navigateNext, SPLASH_DELAY);
        });
    }

    private void scheduleNavigation() {
        mainHandler.postDelayed(this::navigateNext, SPLASH_DELAY);
    }

    private void navigateNext() {
        Intent intent;
        if (sessionManager.isLoggedIn()) {
            intent = new Intent(this, MainMenuActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
