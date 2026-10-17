package com.example.hafizaoyunu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import android.util.Log;

import com.example.hafizaoyunu.model.Difficulty;
import com.example.hafizaoyunu.model.Theme;
import com.example.hafizaoyunu.utils.PreferenceManager;
import com.example.hafizaoyunu.utils.SoundManager;
/**
 * Ana ekran - Oyun ayarları ve başlatma
 * 
 * Bu ekranda kullanıcı:
 * - Zorluk seviyesi seçebilir (4x4, 6x6, 8x8)
 * - Tema seçebilir (Emoji, Hayvanlar, Sayılar vb.)
 * - Ses açıp kapatabilir
 * - Oyunu başlatabilir
 * - Skor tablosunu görebilir
 */
public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "MainActivity";
    
    // UI Elemanları
    private TextView textTitle;
    private TextView textHighScore;
    private Spinner spinnerDifficulty;
    private Spinner spinnerTheme;
    private Switch switchSound;
    private Button btnStartGame;
    private Button btnLeaderboard;
    private TextView textPreview;
    
    // Managers
    private PreferenceManager prefManager;
    private SoundManager soundManager;
    
    // Seçilen değerler
    private Difficulty selectedDifficulty = Difficulty.EASY;
    private Theme selectedTheme = Theme.EMOJI;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Manager'ları başlat
        prefManager = PreferenceManager.getInstance(this);
        soundManager = SoundManager.getInstance();
        soundManager.init(this);

        // UI elemanlarını bağla
        initViews();

        // Ayarları yükle
        loadSettings();

        // Spinner'ları ayarla
        setupSpinners();

        // Click listener'ları ayarla
        setupClickListeners();

        // Animasyonları başlat
        startAnimations();
        setupNotificationSubscription();
    }
        /**
         * Firebase Duyuru kanalına abone olur
         */
        private void setupNotificationSubscription() {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("duyurular")
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            android.util.Log.d(TAG, "Duyuru kanalına başarıyla abone olundu.");
                        } else {
                            android.util.Log.d(TAG, "Abonelik hatası!");
                        }
                    });
        }


    
    /**
     * UI elemanlarını bağlar
     */
    private void initViews() {
        textTitle = findViewById(R.id.textTitle);
        textHighScore = findViewById(R.id.textHighScore);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        spinnerTheme = findViewById(R.id.spinnerTheme);
        switchSound = findViewById(R.id.switchSound);
        btnStartGame = findViewById(R.id.btnStartGame);
        btnLeaderboard = findViewById(R.id.btnLeaderboard);
        textPreview = findViewById(R.id.textPreview);
    }
    
    /**
     * Kaydedilmiş ayarları yükler
     */
    private void loadSettings() {
        selectedDifficulty = prefManager.getDifficulty();
        selectedTheme = prefManager.getTheme();
        boolean soundEnabled = prefManager.isSoundEnabled();
        
        switchSound.setChecked(soundEnabled);
        soundManager.setSoundEnabled(soundEnabled);
        
        // En yüksek skoru göster
        int highScore = prefManager.getLocalHighScore();
        if (highScore > 0) {
            textHighScore.setText("En Yüksek Skor: " + highScore);
            textHighScore.setVisibility(View.VISIBLE);
        } else {
            textHighScore.setVisibility(View.GONE);
        }
        
        updatePreview();
    }
    
    /**
     * Spinner'ları ayarlar
     */
    private void setupSpinners() {
        // Zorluk spinner'ı
        String[] difficultyNames = new String[Difficulty.values().length];
        for (int i = 0; i < Difficulty.values().length; i++) {
            difficultyNames[i] = Difficulty.values()[i].getDisplayName();
        }

        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                difficultyNames
        );
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(difficultyAdapter);
        spinnerDifficulty.setSelection(selectedDifficulty.ordinal());
        
        spinnerDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDifficulty = Difficulty.values()[position];
                prefManager.setDifficulty(selectedDifficulty);
                updatePreview();
                soundManager.playClickSound();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        // Tema spinner'ı
        String[] themeNames = new String[Theme.values().length];
        for (int i = 0; i < Theme.values().length; i++) {
            themeNames[i] = Theme.values()[i].getDisplayName();
        }

        ArrayAdapter<String> themeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                themeNames
        );
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheme.setAdapter(themeAdapter);
        spinnerTheme.setSelection(selectedTheme.ordinal());
        
        spinnerTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTheme = Theme.values()[position];
                prefManager.setTheme(selectedTheme);
                updatePreview();
                soundManager.playClickSound();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    
    /**
     * Click listener'ları ayarlar
     */
    private void setupClickListeners() {
        // Ses switch'i
        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefManager.setSoundEnabled(isChecked);
            soundManager.setSoundEnabled(isChecked);
            if (isChecked) {
                soundManager.playClickSound();
            }
        });
        
        // Oyunu başlat butonu
        btnStartGame.setOnClickListener(v -> {
            soundManager.playClickSound();
            startGame();
        });
        
        // Skor tablosu butonu
        btnLeaderboard.setOnClickListener(v -> {
            soundManager.playClickSound();
            openLeaderboard();
        });
    }
    
    /**
     * Önizlemeyi günceller
     */
    private void updatePreview() {
        StringBuilder preview = new StringBuilder();
        java.util.List<String> contents = new java.util.ArrayList<>(selectedTheme.getContents());
        java.util.Collections.shuffle(contents);
        
        for (int i = 0; i < Math.min(6, contents.size()); i++) {
            preview.append(contents.get(i)).append(" ");
        }
        
        textPreview.setText(preview.toString().trim());
    }
    
    /**
     * Oyunu başlatır
     */
    private void startGame() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("difficulty", selectedDifficulty.name());
        intent.putExtra("theme", selectedTheme.name());
        startActivity(intent);
    }
    
    /**
     * Skor tablosunu açar
     */
    private void openLeaderboard() {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
    }
    
    /**
     * Animasyonları başlatır
     */
    private void startAnimations() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        
        textTitle.startAnimation(fadeIn);
        btnStartGame.startAnimation(slideUp);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Skor güncellenmiş olabilir
        int highScore = prefManager.getLocalHighScore();
        if (highScore > 0) {
            textHighScore.setText("En Yüksek Skor: " + highScore);
            textHighScore.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundManager.release();
    }
}
