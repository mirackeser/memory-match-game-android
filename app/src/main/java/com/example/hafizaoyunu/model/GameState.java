package com.example.hafizaoyunu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Oyunun mevcut durumunu tutan model sınıfı
 * Serializable: Oryantasyon değişimlerinde state korumak için
 */
public class GameState implements Serializable {
    
    private List<Card> cards;           // Tüm kartlar
    private int moves;                   // Hamle sayısı
    private int matchedPairs;           // Eşleşen çift sayısı
    private int totalPairs;             // Toplam çift sayısı
    private long elapsedTimeMillis;     // Geçen süre (milisaniye)
    private long startTimeMillis;       // Oyun başlangıç zamanı
    private boolean isGameStarted;      // Oyun başladı mı?
    private boolean isGameCompleted;    // Oyun tamamlandı mı?
    private int firstFlippedIndex;      // İlk çevrilen kartın indeksi
    private int secondFlippedIndex;     // İkinci çevrilen kartın indeksi
    private boolean isProcessing;       // İşlem devam ediyor mu? (kart kontrolü)
    private int score;                  // Toplam skor
    private Difficulty difficulty;      // Zorluk seviyesi
    private Theme theme;                // Kart teması
    
    public GameState() {
        this.cards = new ArrayList<>();
        this.moves = 0;
        this.matchedPairs = 0;
        this.totalPairs = 0;
        this.elapsedTimeMillis = 0;
        this.startTimeMillis = 0;
        this.isGameStarted = false;
        this.isGameCompleted = false;
        this.firstFlippedIndex = -1;
        this.secondFlippedIndex = -1;
        this.isProcessing = false;
        this.score = 0;
        this.difficulty = Difficulty.EASY;
        this.theme = Theme.EMOJI;
    }
    
    // Getter ve Setter metodları
    public List<Card> getCards() {
        return cards;
    }
    
    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
    
    public int getMoves() {
        return moves;
    }
    
    public void setMoves(int moves) {
        this.moves = moves;
    }
    
    public void incrementMoves() {
        this.moves++;
    }
    
    public int getMatchedPairs() {
        return matchedPairs;
    }
    
    public void setMatchedPairs(int matchedPairs) {
        this.matchedPairs = matchedPairs;
    }
    
    public void incrementMatchedPairs() {
        this.matchedPairs++;
    }
    
    public int getTotalPairs() {
        return totalPairs;
    }
    
    public void setTotalPairs(int totalPairs) {
        this.totalPairs = totalPairs;
    }
    
    public long getElapsedTimeMillis() {
        return elapsedTimeMillis;
    }
    
    public void setElapsedTimeMillis(long elapsedTimeMillis) {
        this.elapsedTimeMillis = elapsedTimeMillis;
    }
    
    public long getStartTimeMillis() {
        return startTimeMillis;
    }
    
    public void setStartTimeMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }
    
    public boolean isGameStarted() {
        return isGameStarted;
    }
    
    public void setGameStarted(boolean gameStarted) {
        isGameStarted = gameStarted;
    }
    
    public boolean isGameCompleted() {
        return isGameCompleted;
    }
    
    public void setGameCompleted(boolean gameCompleted) {
        isGameCompleted = gameCompleted;
    }
    
    public int getFirstFlippedIndex() {
        return firstFlippedIndex;
    }
    
    public void setFirstFlippedIndex(int firstFlippedIndex) {
        this.firstFlippedIndex = firstFlippedIndex;
    }
    
    public int getSecondFlippedIndex() {
        return secondFlippedIndex;
    }
    
    public void setSecondFlippedIndex(int secondFlippedIndex) {
        this.secondFlippedIndex = secondFlippedIndex;
    }
    
    public boolean isProcessing() {
        return isProcessing;
    }
    
    public void setProcessing(boolean processing) {
        isProcessing = processing;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public void addScore(int points) {
        this.score += points;
    }
    
    public Difficulty getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
    
    public Theme getTheme() {
        return theme;
    }
    
    public void setTheme(Theme theme) {
        this.theme = theme;
    }
    
    /**
     * Seçilen kartları sıfırlar
     */
    public void resetFlippedCards() {
        this.firstFlippedIndex = -1;
        this.secondFlippedIndex = -1;
    }
    
    /**
     * Geçen süreyi formatlar (MM:SS)
     */
    public String getFormattedTime() {
        long totalSeconds = elapsedTimeMillis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    /**
     * Oyunun tamamlanıp tamamlanmadığını kontrol eder
     */
    public boolean checkGameCompleted() {
        return matchedPairs >= totalPairs && totalPairs > 0;
    }
}
