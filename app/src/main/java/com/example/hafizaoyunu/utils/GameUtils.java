package com.example.hafizaoyunu.utils;

import com.example.hafizaoyunu.model.Card;
import com.example.hafizaoyunu.model.Difficulty;
import com.example.hafizaoyunu.model.Theme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Oyun mantığı için yardımcı fonksiyonlar
 */
public class GameUtils {
    
    /**
     * Yeni oyun için kart listesi oluşturur
     * @param difficulty Zorluk seviyesi
     * @param theme Kart teması
     * @return Karıştırılmış kart listesi
     */
    public static List<Card> generateCards(Difficulty difficulty, Theme theme) {
        List<Card> cards = new ArrayList<>();
        int pairCount = difficulty.getPairs();
        
        // Temadan rastgele içerikler al
        List<String> contents = new ArrayList<>(theme.getContents());
        Collections.shuffle(contents);
        
        // Gerekli sayıda çift oluştur
        int cardId = 0;
        for (int pairId = 0; pairId < pairCount; pairId++) {
            String content = contents.get(pairId % contents.size());
            
            // Her içerikten 2 kart oluştur
            cards.add(new Card(cardId++, content, pairId));
            cards.add(new Card(cardId++, content, pairId));
        }
        
        // Kartları karıştır
        Collections.shuffle(cards);
        
        return cards;
    }
    
    /**
     * Skor hesaplar
     * @param matchedPairs Eşleşen çift sayısı
     * @param moves Hamle sayısı
     * @param timeMillis Geçen süre (milisaniye)
     * @param difficulty Zorluk seviyesi
     * @return Hesaplanan skor
     */
    public static int calculateScore(int matchedPairs, int moves, long timeMillis, Difficulty difficulty) {
        // Temel puan: Her çift için 100 puan
        int baseScore = matchedPairs * 100;
        
        // Zaman bonusu: Ne kadar hızlı o kadar çok bonus
        int timeSeconds = (int) (timeMillis / 1000);
        int timeBonus = Math.max(0, 500 - timeSeconds * 2);
        
        // Hamle bonusu: Az hamle = daha çok puan
        int minMoves = matchedPairs; // En az mükemmel eşleşme
        int moveBonus = Math.max(0, (minMoves * 3 - moves) * 10);
        
        // Toplam puan
        int totalScore = baseScore + timeBonus + moveBonus;
        
        // Zorluk çarpanı uygula
        totalScore = (int) (totalScore * difficulty.getScoreMultiplier());
        
        return Math.max(0, totalScore);
    }
    
    /**
     * Yıldız sayısını hesaplar (1-3 yıldız)
     * @param moves Hamle sayısı
     * @param totalPairs Toplam çift sayısı
     * @return Yıldız sayısı (1-3)
     */
    public static int calculateStars(int moves, int totalPairs) {
        int perfectMoves = totalPairs; // Mükemmel oyun için gereken hamle
        int ratio = moves / perfectMoves;
        
        if (ratio <= 1.5) {
            return 3; // Mükemmel - 3 yıldız
        } else if (ratio <= 2.5) {
            return 2; // İyi - 2 yıldız
        } else {
            return 1; // Ortalama - 1 yıldız
        }
    }
    
    /**
     * Süreyi formatlar (MM:SS)
     */
    public static String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    /**
     * Kart çiftinin tamamlanıp tamamlanmadığını kontrol eder
     */
    public static boolean isGameComplete(List<Card> cards) {
        for (Card card : cards) {
            if (!card.isMatched()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * İki kartın eşleşip eşleşmediğini kontrol eder
     */
    public static boolean checkMatch(Card card1, Card card2) {
        if (card1 == null || card2 == null) {
            return false;
        }
        return card1.getPairId() == card2.getPairId() && card1.getId() != card2.getId();
    }
}
