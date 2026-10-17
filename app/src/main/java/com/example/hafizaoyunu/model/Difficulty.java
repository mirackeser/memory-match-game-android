package com.example.hafizaoyunu.model;

/**
 * Oyun zorluk seviyelerini tanımlayan enum
 */
public enum Difficulty {
    EASY(4, "Kolay (4x4)", 8),       // 4x4 = 16 kart = 8 çift
    MEDIUM(6, "Orta (6x6)", 18),     // 6x6 = 36 kart = 18 çift
    HARD(8, "Zor (8x8)", 32);        // 8x8 = 64 kart = 32 çift
    
    private final int gridSize;      // Izgara boyutu
    private final String displayName; // Görüntülenecek isim
    private final int pairs;          // Çift sayısı
    
    Difficulty(int gridSize, String displayName, int pairs) {
        this.gridSize = gridSize;
        this.displayName = displayName;
        this.pairs = pairs;
    }
    
    public int getGridSize() {
        return gridSize;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getPairs() {
        return pairs;
    }
    
    public int getTotalCards() {
        return pairs * 2;
    }
    
    /**
     * Zorluk seviyesine göre bonus çarpanı
     */
    public double getScoreMultiplier() {
        switch (this) {
            case EASY: return 1.0;
            case MEDIUM: return 1.5;
            case HARD: return 2.0;
            default: return 1.0;
        }
    }
}
