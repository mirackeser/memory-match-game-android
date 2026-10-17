package com.example.hafizaoyunu.model;

import java.io.Serializable;

/**
 * Oyundaki bir kartı temsil eden model sınıfı
 * Serializable: Oryantasyon değişimlerinde state korumak için
 */
public class Card implements Serializable {
    
    private int id;           // Kartın benzersiz kimliği
    private String content;   // Kartın içeriği (emoji, sembol vb.)
    private int pairId;       // Eşleşen çift için kullanılan kimlik
    private boolean isFlipped;   // Kart çevrilmiş mi?
    private boolean isMatched;   // Kart eşleşmiş mi?
    
    public Card(int id, String content, int pairId) {
        this.id = id;
        this.content = content;
        this.pairId = pairId;
        this.isFlipped = false;
        this.isMatched = false;
    }
    
    // Getter ve Setter metodları
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public int getPairId() {
        return pairId;
    }
    
    public void setPairId(int pairId) {
        this.pairId = pairId;
    }
    
    public boolean isFlipped() {
        return isFlipped;
    }
    
    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
    }
    
    public boolean isMatched() {
        return isMatched;
    }
    
    public void setMatched(boolean matched) {
        isMatched = matched;
    }
    
    /**
     * İki kartın eşleşip eşleşmediğini kontrol eder
     */
    public boolean matches(Card other) {
        return this.pairId == other.pairId && this.id != other.id;
    }
    
    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", pairId=" + pairId +
                ", isFlipped=" + isFlipped +
                ", isMatched=" + isMatched +
                '}';
    }
}
