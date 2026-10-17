package com.example.hafizaoyunu.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Yüksek skor kaydını temsil eden model sınıfı
 * Firebase Firestore ile uyumlu
 */
public class HighScore implements Serializable {
    
    private String id;           // Firestore document ID
    private String playerName;   // Oyuncu adı
    private int score;           // Skor
    private int moves;           // Hamle sayısı
    private long timeMillis;     // Tamamlama süresi (milisaniye)
    private String difficulty;   // Zorluk seviyesi
    private String theme;        // Tema
    private long timestamp;      // Kayıt zamanı
    
    // Boş constructor (Firestore için gerekli)
    public HighScore() {
    }
    
    public HighScore(String playerName, int score, int moves, long timeMillis, 
                     String difficulty, String theme) {
        this.playerName = playerName;
        this.score = score;
        this.moves = moves;
        this.timeMillis = timeMillis;
        this.difficulty = difficulty;
        this.theme = theme;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getter ve Setter metodları
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public int getMoves() {
        return moves;
    }
    
    public void setMoves(int moves) {
        this.moves = moves;
    }
    
    public long getTimeMillis() {
        return timeMillis;
    }
    
    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }
    
    public String getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    
    public String getTheme() {
        return theme;
    }
    
    public void setTheme(String theme) {
        this.theme = theme;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Formatlanmış süre döndürür (MM:SS)
     */
    public String getFormattedTime() {
        long totalSeconds = timeMillis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    /**
     * Firestore için Map'e dönüştürür
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("playerName", playerName);
        map.put("score", score);
        map.put("moves", moves);
        map.put("timeMillis", timeMillis);
        map.put("difficulty", difficulty);
        map.put("theme", theme);
        map.put("timestamp", timestamp);
        return map;
    }
    
    /**
     * Firestore Map'inden HighScore oluşturur
     */
    public static HighScore fromMap(String id, Map<String, Object> map) {
        HighScore highScore = new HighScore();
        highScore.setId(id);
        highScore.setPlayerName((String) map.get("playerName"));
        highScore.setScore(((Long) map.get("score")).intValue());
        highScore.setMoves(((Long) map.get("moves")).intValue());
        highScore.setTimeMillis((Long) map.get("timeMillis"));
        highScore.setDifficulty((String) map.get("difficulty"));
        highScore.setTheme((String) map.get("theme"));
        highScore.setTimestamp((Long) map.get("timestamp"));
        return highScore;
    }
}
