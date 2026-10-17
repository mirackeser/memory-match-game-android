package com.example.hafizaoyunu;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hafizaoyunu.utils.PreferenceManager;
import com.example.hafizaoyunu.utils.SoundManager;

/**
 * Ayarlar ekranı
 */
public class SettingsActivity extends AppCompatActivity {
    
    private ImageButton btnBack;
    private Switch switchSound;
    
    private PreferenceManager prefManager;
    private SoundManager soundManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        prefManager = PreferenceManager.getInstance(this);
        soundManager = SoundManager.getInstance();
        
        initViews();
        loadSettings();
        setupListeners();
    }
    
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        switchSound = findViewById(R.id.switchSound);
    }
    
    private void loadSettings() {
        switchSound.setChecked(prefManager.isSoundEnabled());
    }
    
    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefManager.setSoundEnabled(isChecked);
            soundManager.setSoundEnabled(isChecked);
            if (isChecked) {
                soundManager.playClickSound();
            }
        });
    }
}
