package com.example.hafizaoyunu.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.hafizaoyunu.model.Difficulty;
import com.example.hafizaoyunu.model.Theme;

/**
 * Kullanıcı tercihlerini yöneten sınıf
 * SharedPreferences kullanarak verileri kalıcı olarak saklar
 */
public class PreferenceManager {
    
    private static final String PREF_NAME = "hafiza_oyunu_prefs";
    
    // Anahtar sabitleri
    private static final String KEY_SOUND_ENABLED = "sound_enabled";
    private static final String KEY_DIFFICULTY = "difficulty";
    private static final String KEY_THEME = "theme";
    private static final String KEY_PLAYER_NAME = "player_name";
    private static final String KEY_HIGH_SCORE = "high_score";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    
    private static PreferenceManager instance;
    private SharedPreferences prefs;
    
    private PreferenceManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public static synchronized PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context.getApplicationContext());
        }
        return instance;
    }
    
    // Ses ayarları
    public void setSoundEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply();
    }
    
    public boolean isSoundEnabled() {
        return prefs.getBoolean(KEY_SOUND_ENABLED, true);
    }
    
    // Zorluk seviyesi
    public void setDifficulty(Difficulty difficulty) {
        prefs.edit().putString(KEY_DIFFICULTY, difficulty.name()).apply();
    }
    
    public Difficulty getDifficulty() {
        String difficultyName = prefs.getString(KEY_DIFFICULTY, Difficulty.EASY.name());
        try {
            return Difficulty.valueOf(difficultyName);
        } catch (IllegalArgumentException e) {
            return Difficulty.EASY;
        }
    }
    
    // Tema
    public void setTheme(Theme theme) {
        prefs.edit().putString(KEY_THEME, theme.name()).apply();
    }
    
    public Theme getTheme() {
        String themeName = prefs.getString(KEY_THEME, Theme.EMOJI.name());
        try {
            return Theme.valueOf(themeName);
        } catch (IllegalArgumentException e) {
            return Theme.EMOJI;
        }
    }
    
    // Oyuncu adı
    public void setPlayerName(String name) {
        prefs.edit().putString(KEY_PLAYER_NAME, name).apply();
    }
    
    public String getPlayerName() {
        return prefs.getString(KEY_PLAYER_NAME, "");
    }
    
    // Yerel en yüksek skor
    public void setLocalHighScore(int score) {
        int currentHighScore = getLocalHighScore();
        if (score > currentHighScore) {
            prefs.edit().putInt(KEY_HIGH_SCORE, score).apply();
        }
    }
    
    public int getLocalHighScore() {
        return prefs.getInt(KEY_HIGH_SCORE, 0);
    }
    
    // İlk açılış kontrolü
    public boolean isFirstLaunch() {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true);
    }
    
    public void setFirstLaunchDone() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
    }
    
    /**
     * Tüm ayarları sıfırlar
     */
    public void clearAll() {
        prefs.edit().clear().apply();
    }
}
