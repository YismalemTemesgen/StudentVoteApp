package com.studentvoting.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "StudentVotingSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_TYPE = "userType";
    private static final String KEY_STUDENT_ID = "studentId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_DB_INITIALIZED = "dbInitialized";

    public static final String USER_TYPE_ADMIN = "admin";
    public static final String USER_TYPE_STUDENT = "student";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Create admin login session
     */
    public void createAdminSession() {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_TYPE, USER_TYPE_ADMIN);
        editor.putString(KEY_STUDENT_ID, "");
        editor.putString(KEY_USER_NAME, "Administrator");
        editor.apply();
    }

    /**
     * Create student login session
     */
    public void createStudentSession(String studentId, String studentName) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_TYPE, USER_TYPE_STUDENT);
        editor.putString(KEY_STUDENT_ID, studentId);
        editor.putString(KEY_USER_NAME, studentName);
        editor.apply();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Check if current user is admin
     */
    public boolean isAdmin() {
        return USER_TYPE_ADMIN.equals(prefs.getString(KEY_USER_TYPE, ""));
    }

    /**
     * Check if current user is student
     */
    public boolean isStudent() {
        return USER_TYPE_STUDENT.equals(prefs.getString(KEY_USER_TYPE, ""));
    }

    /**
     * Get current user type
     */
    public String getUserType() {
        return prefs.getString(KEY_USER_TYPE, "");
    }

    /**
     * Get logged-in student ID
     */
    public String getStudentId() {
        return prefs.getString(KEY_STUDENT_ID, "");
    }

    /**
     * Get logged-in user name
     */
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "");
    }

    /**
     * Clear session (logout)
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }

    /**
     * Check if database has been initialized with sample data
     */
    public boolean isDbInitialized() {
        return prefs.getBoolean(KEY_DB_INITIALIZED, false);
    }

    /**
     * Mark database as initialized
     */
    public void setDbInitialized() {
        editor.putBoolean(KEY_DB_INITIALIZED, true);
        editor.apply();
    }
}
