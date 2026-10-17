package com.example.hafizaoyunu.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

/**
 * Oyun seslerini yöneten sınıf
 * Ses dosyaları olmadan da çalışır (sessiz mod)
 * 
 * Ses dosyası eklemek için:
 * 1. res/raw klasörü oluşturun
 * 2. Aşağıdaki ses dosyalarını ekleyin:
 *    - flip_sound.mp3
 *    - match_sound.mp3
 *    - mismatch_sound.mp3
 *    - victory_sound.mp3
 *    - click_sound.mp3
 */
public class SoundManager {
    
    private static final String TAG = "SoundManager";
    
    private static SoundManager instance;
    
    private SoundPool soundPool;
    private int flipSoundId = 0;
    private int matchSoundId = 0;
    private int mismatchSoundId = 0;
    private int victorySoundId = 0;
    private int clickSoundId = 0;
    
    private boolean isSoundEnabled = true;
    private boolean isInitialized = false;
    
    private SoundManager() {
        // Private constructor for singleton
    }
    
    public static synchronized SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    /**
     * SoundPool'u başlatır
     */
    public void init(Context context) {
        if (isInitialized) return;
        
        try {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
            
            // Ses dosyalarını yüklemeyi dene
            // Dosyalar yoksa sessiz çalışır
            loadSounds(context);
            
            isInitialized = true;
        } catch (Exception e) {
            Log.e(TAG, "SoundPool başlatılamadı: " + e.getMessage());
        }
    }
    
    /**
     * Ses dosyalarını yükler
     */
    private void loadSounds(Context context) {
        // Her ses dosyasını ayrı ayrı yüklemeyi dene
        // Dosya yoksa 0 kalır ve ses çalmaz
        
        try {
            int resId = context.getResources().getIdentifier("flip_sound", "raw", context.getPackageName());
            if (resId != 0) {
                flipSoundId = soundPool.load(context, resId, 1);
            }
        } catch (Exception e) {
            Log.d(TAG, "flip_sound yüklenemedi");
        }
        
        try {
            int resId = context.getResources().getIdentifier("match_sound", "raw", context.getPackageName());
            if (resId != 0) {
                matchSoundId = soundPool.load(context, resId, 1);
            }
        } catch (Exception e) {
            Log.d(TAG, "match_sound yüklenemedi");
        }
        
        try {
            int resId = context.getResources().getIdentifier("mismatch_sound", "raw", context.getPackageName());
            if (resId != 0) {
                mismatchSoundId = soundPool.load(context, resId, 1);
            }
        } catch (Exception e) {
            Log.d(TAG, "mismatch_sound yüklenemedi");
        }
        
        try {
            int resId = context.getResources().getIdentifier("victory_sound", "raw", context.getPackageName());
            if (resId != 0) {
                victorySoundId = soundPool.load(context, resId, 1);
            }
        } catch (Exception e) {
            Log.d(TAG, "victory_sound yüklenemedi");
        }
        
        try {
            int resId = context.getResources().getIdentifier("click_sound", "raw", context.getPackageName());
            if (resId != 0) {
                clickSoundId = soundPool.load(context, resId, 1);
            }
        } catch (Exception e) {
            Log.d(TAG, "click_sound yüklenemedi");
        }
    }
    
    /**
     * Kart çevirme sesi
     */
    public void playFlipSound() {
        playSound(flipSoundId);
    }
    
    /**
     * Eşleşme sesi
     */
    public void playMatchSound() {
        playSound(matchSoundId);
    }
    
    /**
     * Yanlış eşleşme sesi
     */
    public void playMismatchSound() {
        playSound(mismatchSoundId);
    }
    
    /**
     * Zafer sesi
     */
    public void playVictorySound() {
        playSound(victorySoundId);
    }
    
    /**
     * Tıklama sesi
     */
    public void playClickSound() {
        playSound(clickSoundId);
    }
    
    private void playSound(int soundId) {
        if (isSoundEnabled && soundPool != null && soundId != 0) {
            try {
                soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                // Sessiz devam et
            }
        }
    }
    
    /**
     * Ses durumunu ayarlar
     */
    public void setSoundEnabled(boolean enabled) {
        this.isSoundEnabled = enabled;
    }
    
    /**
     * Ses durumunu döndürür
     */
    public boolean isSoundEnabled() {
        return isSoundEnabled;
    }
    
    /**
     * Kaynakları serbest bırakır
     */
    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        isInitialized = false;
    }
}
