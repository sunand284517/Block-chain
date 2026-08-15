package com.manasshaktiiui.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Offline Local Data Storage Manager.
 * Stores user preferences, intervention logs, journal entries, local statistics,
 * and behavioral summaries completely on-device without cloud API dependencies.
 */
public class LocalStorageManager {

    private static final String TAG = "LocalStorageManager";
    private static final String PREF_NAME = "ManasShaktiiLocalStorage";
    private static final String KEY_INTERVENTIONS = "intervention_history";
    private static final String KEY_JOURNAL = "journal_entries";
    private static final String KEY_SETTINGS = "user_settings";

    private final SharedPreferences preferences;

    public LocalStorageManager(Context context) {
        this.preferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public synchronized void saveInterventionLog(String actionName, String message) {
        try {
            JSONArray history = getInterventionHistory();
            JSONObject entry = new JSONObject();
            entry.put("timestamp", System.currentTimeMillis());
            entry.put("action", actionName);
            entry.put("message", message);
            history.put(entry);

            preferences.edit().putString(KEY_INTERVENTIONS, history.toString()).apply();
            Log.d(TAG, "Intervention log saved to local storage.");
        } catch (Exception e) {
            Log.e(TAG, "Error saving intervention log", e);
        }
    }

    public JSONArray getInterventionHistory() {
        String jsonStr = preferences.getString(KEY_INTERVENTIONS, "[]");
        try {
            return new JSONArray(jsonStr);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public synchronized void saveJournalEntry(String title, String content) {
        try {
            JSONArray journal = getJournalEntries();
            JSONObject entry = new JSONObject();
            entry.put("id", System.currentTimeMillis());
            entry.put("timestamp", System.currentTimeMillis());
            entry.put("title", title);
            entry.put("content", content);
            journal.put(entry);

            preferences.edit().putString(KEY_JOURNAL, journal.toString()).apply();
            Log.d(TAG, "Journal entry saved to local storage.");
        } catch (Exception e) {
            Log.e(TAG, "Error saving journal entry", e);
        }
    }

    public JSONArray getJournalEntries() {
        String jsonStr = preferences.getString(KEY_JOURNAL, "[]");
        try {
            return new JSONArray(jsonStr);
        } catch (Exception e) {
            return new JSONArray();
        }
    }
}
