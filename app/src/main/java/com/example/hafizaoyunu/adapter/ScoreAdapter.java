package com.example.hafizaoyunu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hafizaoyunu.R;
import com.example.hafizaoyunu.model.HighScore;

import java.util.List;

/**
 * Skor tablosu için RecyclerView adaptörü
 */
public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {
    
    private List<HighScore> scores;
    private Context context;
    
    public ScoreAdapter(Context context, List<HighScore> scores) {
        this.context = context;
        this.scores = scores;
    }
    
    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_score, parent, false);
        return new ScoreViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        HighScore score = scores.get(position);
        int rank = position + 1;
        holder.bind(score, rank);
    }
    
    @Override
    public int getItemCount() {
        return scores != null ? scores.size() : 0;
    }
    
    /**
     * Skorları günceller
     */
    public void updateScores(List<HighScore> newScores) {
        this.scores = newScores;
        notifyDataSetChanged();
    }
    
    /**
     * Skor satırı için ViewHolder
     */
    class ScoreViewHolder extends RecyclerView.ViewHolder {
        
        private CardView cardView;
        private TextView textRank;
        private TextView textName;
        private TextView textScore;
        private TextView textMoves;
        private TextView textTime;
        private TextView textDifficulty;
        
        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = itemView.findViewById(R.id.scoreCard);
            textRank = itemView.findViewById(R.id.textRank);
            textName = itemView.findViewById(R.id.textName);
            textScore = itemView.findViewById(R.id.textScore);
            textMoves = itemView.findViewById(R.id.textMoves);
            textTime = itemView.findViewById(R.id.textTime);
            textDifficulty = itemView.findViewById(R.id.textDifficulty);
        }
        
        public void bind(HighScore score, int rank) {
            // Sıralama
            String rankEmoji;
            int rankColor;
            
            switch (rank) {
                case 1:
                    rankEmoji = "🥇";
                    rankColor = R.color.gold;
                    break;
                case 2:
                    rankEmoji = "🥈";
                    rankColor = R.color.silver;
                    break;
                case 3:
                    rankEmoji = "🥉";
                    rankColor = R.color.bronze;
                    break;
                default:
                    rankEmoji = String.valueOf(rank);
                    rankColor = R.color.text_secondary;
            }
            
            textRank.setText(rankEmoji);
            
            // Oyuncu adı
            String playerName = score.getPlayerName();
            if (playerName == null || playerName.isEmpty()) {
                playerName = "Anonim";
            }
            textName.setText(playerName);
            
            // Skor
            textScore.setText(String.valueOf(score.getScore()));
            
            // Hamle
            textMoves.setText(score.getMoves() + " hamle");
            
            // Süre
            textTime.setText(score.getFormattedTime());
            
            // Zorluk
            textDifficulty.setText(score.getDifficulty());
            
            // İlk 3 için özel arka plan
            if (rank <= 3) {
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, rankColor)
                );
                cardView.setAlpha(0.9f);
            } else {
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.card_background)
                );
                cardView.setAlpha(1f);
            }
        }
    }
}
