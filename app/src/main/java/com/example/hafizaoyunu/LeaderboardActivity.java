package com.example.hafizaoyunu;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hafizaoyunu.adapter.ScoreAdapter;
import com.example.hafizaoyunu.model.HighScore;
import com.example.hafizaoyunu.utils.ScoreManager;
import com.example.hafizaoyunu.utils.SoundManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Skor tablosu ekranı
 * Yerel olarak kaydedilmiş skorları gösterir
 */
public class LeaderboardActivity extends AppCompatActivity {
    
    private ImageButton btnBack;
    private RecyclerView recyclerScores;
    private ProgressBar progressBar;
    private View textEmpty;
    
    private ScoreAdapter scoreAdapter;
    private ScoreManager scoreManager;
    private SoundManager soundManager;
    
    private List<HighScore> scores = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        
        scoreManager = ScoreManager.getInstance(this);
        soundManager = SoundManager.getInstance();
        soundManager.init(this);
        
        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadScores();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        recyclerScores = findViewById(R.id.recyclerScores);
        progressBar = findViewById(R.id.progressBar);
        textEmpty = findViewById(R.id.textEmpty);
    }
    
    private void setupRecyclerView() {
        recyclerScores.setLayoutManager(new LinearLayoutManager(this));
        scoreAdapter = new ScoreAdapter(this, scores);
        recyclerScores.setAdapter(scoreAdapter);
    }
    
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            soundManager.playClickSound();
            finish();
        });
    }
    
    private void loadScores() {
        progressBar.setVisibility(View.VISIBLE);
        textEmpty.setVisibility(View.GONE);
        recyclerScores.setVisibility(View.GONE);
        
        // Yerel skorları al
        List<HighScore> fetchedScores = scoreManager.getTopScores(20);
        
        progressBar.setVisibility(View.GONE);
        
        if (fetchedScores.isEmpty()) {
            textEmpty.setVisibility(View.VISIBLE);
        } else {
            scores.clear();
            scores.addAll(fetchedScores);
            scoreAdapter.updateScores(scores);
            recyclerScores.setVisibility(View.VISIBLE);
        }
    }
}
