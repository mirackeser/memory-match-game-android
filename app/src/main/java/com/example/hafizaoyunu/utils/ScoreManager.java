package com.example.hafizaoyunu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.hafizaoyunu.model.HighScore;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Skor yönetimi - Hem yerel (SharedPreferences) hem de Firebase Firestore kullanır
 */
public class ScoreManager {

    private static final String TAG = "ScoreManager";
    private static final String PREF_NAME = "hafiza_oyunu_scores";
    private static final String KEY_SCORES = "scores_list";
    private static final String COLLECTION_SCORES = "scores";
    private static final int MAX_SCORES = 20;

    private static ScoreManager instance;
    private SharedPreferences prefs;
    private Gson gson;
    private FirebaseFirestore db;

    private ScoreManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized ScoreManager getInstance(Context context) {
        if (instance == null) {
            instance = new ScoreManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Yeni skor kaydeder (hem yerel hem Firebase'e)
     */
    public void saveScore(HighScore score) {
        List<HighScore> scores = getAllScores();

        String scoreId = String.valueOf(System.currentTimeMillis());
        score.setId(scoreId);
        scores.add(score);

        Collections.sort(scores, (a, b) -> Integer.compare(b.getScore(), a.getScore()));

        while (scores.size() > MAX_SCORES) {
            scores.remove(scores.size() - 1);
        }

        saveScoreList(scores);

        // Firebase'e kaydet
        saveScoreToFirebase(score);
    }

    /**
     * Firebase Firestore'a skor kaydeder
     */
    private void saveScoreToFirebase(HighScore score) {
        try {
            Log.d(TAG, "Firebase'e kayıt başlıyor...");
            Log.d(TAG, "DB null mu: " + (db == null));

            Map<String, Object> scoreData = new HashMap<>();
            scoreData.put("id", score.getId());
            scoreData.put("playerName", score.getPlayerName());
            scoreData.put("score", score.getScore());
            scoreData.put("difficulty", score.getDifficulty());
            scoreData.put("moves", score.getMoves());
            scoreData.put("timeMillis", score.getTimeMillis());
            scoreData.put("timestamp", System.currentTimeMillis());
            scoreData.put("theme", score.getTheme());

            Log.d(TAG, "Veri hazırlandı: " + scoreData.toString());

            db.collection(COLLECTION_SCORES)
                    .document(score.getId())
                    .set(scoreData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "BAŞARILI: Skor Firebase'e kaydedildi!");
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "HATA: " + e.getMessage());
                    });

            Log.d(TAG, "Firebase isteği gönderildi");

        } catch (Exception e) {
            Log.e(TAG, "EXCEPTION: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<HighScore> getAllScores() {
        String json = prefs.getString(KEY_SCORES, null);

        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            Type listType = new TypeToken<ArrayList<HighScore>>(){}.getType();
            List<HighScore> scores = gson.fromJson(json, listType);
            return scores != null ? scores : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<HighScore> getTopScores(int limit) {
        List<HighScore> allScores = getAllScores();

        if (allScores.size() <= limit) {
            return allScores;
        }

        return allScores.subList(0, limit);
    }

    public List<HighScore> getScoresByDifficulty(String difficulty) {
        List<HighScore> allScores = getAllScores();
        List<HighScore> filtered = new ArrayList<>();

        for (HighScore score : allScores) {
            if (score.getDifficulty() != null && score.getDifficulty().equals(difficulty)) {
                filtered.add(score);
            }
        }

        return filtered;
    }

    public List<HighScore> getScoresByPlayer(String playerName) {
        List<HighScore> allScores = getAllScores();
        List<HighScore> filtered = new ArrayList<>();

        for (HighScore score : allScores) {
            if (score.getPlayerName() != null && score.getPlayerName().equals(playerName)) {
                filtered.add(score);
            }
        }

        return filtered;
    }

    public HighScore getHighestScore() {
        List<HighScore> scores = getAllScores();
        return scores.isEmpty() ? null : scores.get(0);
    }

    public void clearAllScores() {
        prefs.edit().remove(KEY_SCORES).apply();
    }

    private void saveScoreList(List<HighScore> scores) {
        String json = gson.toJson(scores);
        prefs.edit().putString(KEY_SCORES, json).apply();
    }
}