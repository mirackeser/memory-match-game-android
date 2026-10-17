package com.example.hafizaoyunu;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hafizaoyunu.adapter.CardAdapter;
import com.example.hafizaoyunu.model.Card;
import com.example.hafizaoyunu.model.Difficulty;
import com.example.hafizaoyunu.model.GameState;
import com.example.hafizaoyunu.model.HighScore;
import com.example.hafizaoyunu.model.Theme;
import com.example.hafizaoyunu.utils.GameUtils;
import com.example.hafizaoyunu.utils.PreferenceManager;
import com.example.hafizaoyunu.utils.ScoreManager;
import com.example.hafizaoyunu.utils.SoundManager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Oyun ekranı - Kartların gösterildiği ve oyunun oynandığı ana ekran
 *
 * Özellikler:
 * - Kart çevirme animasyonu
 * - Eşleştirme kontrolü
 * - Hamle ve süre takibi
 * - Skor hesaplama
 * - Oryantasyon değişiminde state koruma
 */
public class GameActivity extends AppCompatActivity implements CardAdapter.OnCardClickListener {

    private static final String TAG = "GameActivity";
    private static final String KEY_GAME_STATE = "game_state";
    private static final int FLIP_DELAY = 1000; // Kart kapanma gecikmesi (ms)

    // UI Elemanları
    private RecyclerView recyclerCards;
    private TextView textTime;
    private TextView textMoves;
    private TextView textScore;
    private TextView textMatched;
    private ProgressBar progressBar;
    private ImageButton btnBack;
    private ImageButton btnRestart;

    // Adapter ve Data
    private CardAdapter cardAdapter;
    private GameState gameState;

    // Managers
    private PreferenceManager prefManager;
    private SoundManager soundManager;
    private ScoreManager scoreManager;

    // Timer
    private Timer timer;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Handler oluştur
        handler = new Handler(Looper.getMainLooper());

        // Manager'ları başlat
        prefManager = PreferenceManager.getInstance(this);
        soundManager = SoundManager.getInstance();
        soundManager.init(this);
        soundManager.setSoundEnabled(prefManager.isSoundEnabled());
        scoreManager = ScoreManager.getInstance(this);

        // UI elemanlarını bağla
        initViews();

        // State'i geri yükle veya yeni oyun başlat
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_GAME_STATE)) {
            // Oryantasyon değişimi - state'i geri yükle
            gameState = (GameState) savedInstanceState.getSerializable(KEY_GAME_STATE);
            setupRecyclerView();
            updateUI();

            // Timer'ı yeniden başlat
            if (gameState.isGameStarted() && !gameState.isGameCompleted()) {
                startTimer();
            }
        } else {
            // Yeni oyun
            initNewGame();
        }

        // Click listener'ları ayarla
        setupClickListeners();
    }

    /**
     * UI elemanlarını bağlar
     */
    private void initViews() {
        recyclerCards = findViewById(R.id.recyclerCards);
        textTime = findViewById(R.id.textTime);
        textMoves = findViewById(R.id.textMoves);
        textScore = findViewById(R.id.textScore);
        textMatched = findViewById(R.id.textMatched);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);
        btnRestart = findViewById(R.id.btnRestart);
    }

    /**
     * Yeni oyunu başlatır
     */
    private void initNewGame() {
        // Intent'ten ayarları al
        String difficultyName = getIntent().getStringExtra("difficulty");
        String themeName = getIntent().getStringExtra("theme");

        Difficulty difficulty;
        Theme theme;

        try {
            difficulty = Difficulty.valueOf(difficultyName);
        } catch (Exception e) {
            difficulty = prefManager.getDifficulty();
        }

        try {
            theme = Theme.valueOf(themeName);
        } catch (Exception e) {
            theme = prefManager.getTheme();
        }

        // GameState oluştur
        gameState = new GameState();
        gameState.setDifficulty(difficulty);
        gameState.setTheme(theme);
        gameState.setTotalPairs(difficulty.getPairs());

        // Kartları oluştur
        List<Card> cards = GameUtils.generateCards(difficulty, theme);
        gameState.setCards(cards);

        // RecyclerView'ı ayarla
        setupRecyclerView();

        // UI'ı güncelle
        updateUI();
    }

    /**
     * RecyclerView'ı ayarlar
     */
    private void setupRecyclerView() {
        int gridSize = gameState.getDifficulty().getGridSize();

        // Grid layout manager
        GridLayoutManager layoutManager = new GridLayoutManager(this, gridSize);
        recyclerCards.setLayoutManager(layoutManager);

        // Adapter oluştur
        cardAdapter = new CardAdapter(this, gameState.getCards(), this);
        recyclerCards.setAdapter(cardAdapter);

        // Item decoration (boşluklar)
        int spacing = getResources().getDimensionPixelSize(R.dimen.card_spacing);
        recyclerCards.addItemDecoration(new GridSpacingItemDecoration(gridSize, spacing, true));
    }

    /**
     * Click listener'ları ayarlar
     */
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            soundManager.playClickSound();
            onBackPressed();
        });

        btnRestart.setOnClickListener(v -> {
            soundManager.playClickSound();
            showRestartDialog();
        });
    }

    /**
     * Kart tıklandığında çağrılır
     */
    @Override
    public void onCardClick(int position, Card card) {
        // İşlem devam ediyorsa tıklamayı ignore et
        if (gameState.isProcessing()) return;

        // Oyun bittiyse ignore et
        if (gameState.isGameCompleted()) return;

        // Zaten çevrilmiş veya eşleşmiş kartı ignore et
        if (card.isFlipped() || card.isMatched()) return;

        // Oyunu başlat (ilk tıklama)
        if (!gameState.isGameStarted()) {
            gameState.setGameStarted(true);
            gameState.setStartTimeMillis(System.currentTimeMillis());
            startTimer();
        }

        // Kart çevirme sesi
        soundManager.playFlipSound();

        // Kartı çevir
        card.setFlipped(true);
        cardAdapter.updateCard(position);

        // İlk kart mı yoksa ikinci kart mı?
        if (gameState.getFirstFlippedIndex() == -1) {
            // İlk kart
            gameState.setFirstFlippedIndex(position);
        } else {
            // İkinci kart
            gameState.setSecondFlippedIndex(position);
            gameState.incrementMoves();
            gameState.setProcessing(true);

            // UI'ı güncelle
            updateUI();

            // Eşleşmeyi kontrol et
            checkMatch();
        }
    }

    /**
     * İki kartın eşleşip eşleşmediğini kontrol eder
     */
    private void checkMatch() {
        int firstIndex = gameState.getFirstFlippedIndex();
        int secondIndex = gameState.getSecondFlippedIndex();

        Card firstCard = gameState.getCards().get(firstIndex);
        Card secondCard = gameState.getCards().get(secondIndex);

        handler.postDelayed(() -> {
            if (GameUtils.checkMatch(firstCard, secondCard)) {
                // Eşleşme var!
                onMatch(firstCard, secondCard, firstIndex, secondIndex);
            } else {
                // Eşleşme yok
                onMismatch(firstCard, secondCard, firstIndex, secondIndex);
            }
        }, FLIP_DELAY);
    }

    /**
     * Eşleşme olduğunda çağrılır
     */
    private void onMatch(Card firstCard, Card secondCard, int firstIndex, int secondIndex) {
        // Sesleri çal
        soundManager.playMatchSound();

        // Kartları eşleşmiş olarak işaretle
        firstCard.setMatched(true);
        secondCard.setMatched(true);

        // Eşleşme sayısını artır
        gameState.incrementMatchedPairs();

        // Skor ekle
        int points = 100;
        gameState.addScore(points);

        // Kartları güncelle
        cardAdapter.updateCard(firstIndex);
        cardAdapter.updateCard(secondIndex);

        // State'i sıfırla
        gameState.resetFlippedCards();
        gameState.setProcessing(false);

        // UI'ı güncelle
        updateUI();

        // Oyun bitti mi?
        if (gameState.checkGameCompleted()) {
            onGameComplete();
        }
    }

    /**
     * Eşleşme olmadığında çağrılır
     */
    private void onMismatch(Card firstCard, Card secondCard, int firstIndex, int secondIndex) {
        // Sesleri çal
        soundManager.playMismatchSound();

        // Kartları geri çevir
        firstCard.setFlipped(false);
        secondCard.setFlipped(false);

        // Kartları güncelle
        cardAdapter.updateCard(firstIndex);
        cardAdapter.updateCard(secondIndex);

        // State'i sıfırla
        gameState.resetFlippedCards();
        gameState.setProcessing(false);

        // UI'ı güncelle
        updateUI();
    }

    /**
     * Oyun tamamlandığında çağrılır
     */
    private void onGameComplete() {
        // Timer'ı durdur
        stopTimer();

        // Oyun tamamlandı
        gameState.setGameCompleted(true);

        // Zafer sesi
        soundManager.playVictorySound();

        // Final skoru hesapla
        int finalScore = GameUtils.calculateScore(
                gameState.getMatchedPairs(),
                gameState.getMoves(),
                gameState.getElapsedTimeMillis(),
                gameState.getDifficulty()
        );
        gameState.setScore(finalScore);

        // Yerel skoru güncelle
        prefManager.setLocalHighScore(finalScore);

        // UI'ı güncelle
        updateUI();

        // Zafer dialogunu göster
        showVictoryDialog();
    }

    /**
     * Zafer dialogunu gösterir
     */
    private void showVictoryDialog() {
        int stars = GameUtils.calculateStars(gameState.getMoves(), gameState.getTotalPairs());
        String starsText = "";
        for (int i = 0; i < stars; i++) {
            starsText += "⭐";
        }

        String message = "🎉 Tebrikler!\n\n" +
                starsText + "\n\n" +
                "⏱️ Süre: " + gameState.getFormattedTime() + "\n" +
                "👆 Hamle: " + gameState.getMoves() + "\n" +
                "🏆 Skor: " + gameState.getScore();

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setTitle("Oyun Tamamlandı!");
        builder.setMessage(message);

        // İsim girişi için EditText
        final EditText input = new EditText(this);
        input.setHint("Adınızı girin");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(prefManager.getPlayerName());
        builder.setView(input);

        builder.setPositiveButton("Skoru Kaydet", (dialog, which) -> {
            String playerName = input.getText().toString().trim();
            if (playerName.isEmpty()) {
                playerName = "Anonim";
            }
            prefManager.setPlayerName(playerName);
            saveScore(playerName);
        });

        builder.setNeutralButton("Tekrar Oyna", (dialog, which) -> {
            restartGame();
        });

        builder.setNegativeButton("Ana Menü", (dialog, which) -> {
            finish();
        });

        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Buton renklerini ayarla
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primary));
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.accent));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.text_secondary));
    }

    /**
     * Skoru yerel olarak kaydeder
     */
    private void saveScore(String playerName) {
        HighScore highScore = new HighScore(
                playerName,
                gameState.getScore(),
                gameState.getMoves(),
                gameState.getElapsedTimeMillis(),
                gameState.getDifficulty().getDisplayName(),
                gameState.getTheme().getDisplayName()
        );

        scoreManager.saveScore(highScore);
        Toast.makeText(this, "Skor başarıyla kaydedildi! 🎉", Toast.LENGTH_SHORT).show();
    }

    /**
     * Yeniden başlatma dialogunu gösterir
     */
    private void showRestartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setTitle("Yeni Oyun");
        builder.setMessage("Mevcut oyunu sıfırlamak istediğinize emin misiniz?");

        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restartGame();
            }
        });

        builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Buton renklerini ayarla
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primary));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.error));
    }

    /**
     * Oyunu yeniden başlatır
     */
    private void restartGame() {
        stopTimer();
        initNewGame();
    }

    /**
     * Timer'ı başlatır
     */
    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!gameState.isGameCompleted()) {
                    long elapsed = System.currentTimeMillis() - gameState.getStartTimeMillis();
                    gameState.setElapsedTimeMillis(elapsed);

                    handler.post(() -> updateTimeUI());
                }
            }
        }, 0, 1000);
    }

    /**
     * Timer'ı durdurur
     */
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * UI'ı günceller
     */
    private void updateUI() {
        updateTimeUI();
        textMoves.setText("Hamle: " + gameState.getMoves());
        textScore.setText("Skor: " + gameState.getScore());
        textMatched.setText("Eşleşen: " + gameState.getMatchedPairs() + "/" + gameState.getTotalPairs());

        // Progress bar
        int progress = (int) ((float) gameState.getMatchedPairs() / gameState.getTotalPairs() * 100);
        progressBar.setProgress(progress);
    }

    /**
     * Sadece süre UI'ını günceller
     */
    private void updateTimeUI() {
        textTime.setText("Süre: " + gameState.getFormattedTime());
    }

    /**
     * Oryantasyon değişiminde state'i kaydet
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_GAME_STATE, gameState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // RecyclerView'ı yeniden ayarla (oryantasyona göre)
        setupRecyclerView();
        cardAdapter.updateCards(gameState.getCards());
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Timer geçici olarak durduruluyor, süre state'de kalıyor
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Timer devam ediyorsa yeniden başlat
        if (gameState != null && gameState.isGameStarted() && !gameState.isGameCompleted()) {
            if (timer == null) {
                // Başlangıç zamanını ayarla (geçen süreyi koruyarak)
                long elapsed = gameState.getElapsedTimeMillis();
                gameState.setStartTimeMillis(System.currentTimeMillis() - elapsed);
                startTimer();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    @Override
    public void onBackPressed() {
        if (gameState != null && gameState.isGameStarted() && !gameState.isGameCompleted()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            builder.setTitle("Çıkış");
            builder.setMessage("Oyundan çıkmak istediğinize emin misiniz?\nİlerlemeniz kaybolacak.");

            builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            // Buton renklerini ayarla
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primary));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.error));
        } else {
            super.onBackPressed();
        }
    }
}