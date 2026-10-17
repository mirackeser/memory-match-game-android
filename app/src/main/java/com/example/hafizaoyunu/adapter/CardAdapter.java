package com.example.hafizaoyunu.adapter;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hafizaoyunu.R;
import com.example.hafizaoyunu.model.Card;

import java.util.List;

/**
 * Kart ızgarası için RecyclerView adaptörü
 * Kart çevirme animasyonları ve tıklama olaylarını yönetir
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    
    private List<Card> cards;
    private Context context;
    private OnCardClickListener listener;
    private boolean isClickEnabled = true;
    
    public interface OnCardClickListener {
        void onCardClick(int position, Card card);
    }
    
    public CardAdapter(Context context, List<Card> cards, OnCardClickListener listener) {
        this.context = context;
        this.cards = cards;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cards.get(position);
        holder.bind(card, position);
    }
    
    @Override
    public int getItemCount() {
        return cards != null ? cards.size() : 0;
    }
    
    /**
     * Kartları günceller
     */
    public void updateCards(List<Card> newCards) {
        this.cards = newCards;
        notifyDataSetChanged();
    }
    
    /**
     * Tıklama durumunu ayarlar
     */
    public void setClickEnabled(boolean enabled) {
        this.isClickEnabled = enabled;
    }
    
    /**
     * Belirli bir kartı günceller
     */
    public void updateCard(int position) {
        notifyItemChanged(position);
    }
    
    /**
     * Kart görünümünü yöneten ViewHolder sınıfı
     */
    class CardViewHolder extends RecyclerView.ViewHolder {
        
        private FrameLayout cardContainer;
        private CardView cardFront;
        private CardView cardBack;
        private TextView textFront;
        private TextView textBack;
        
        private boolean isFrontVisible = false;
        
        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardContainer = itemView.findViewById(R.id.cardContainer);
            cardFront = itemView.findViewById(R.id.cardFront);
            cardBack = itemView.findViewById(R.id.cardBack);
            textFront = itemView.findViewById(R.id.textFront);
            textBack = itemView.findViewById(R.id.textBack);
            
            // Kamera mesafesi ayarla (3D efekt için)
            float scale = context.getResources().getDisplayMetrics().density;
            cardFront.setCameraDistance(8000 * scale);
            cardBack.setCameraDistance(8000 * scale);
        }
        
        public void bind(Card card, int position) {
            // İçeriği ayarla
            textFront.setText(card.getContent());
            textBack.setText("?");
            
            // Eşleşmiş kart
            if (card.isMatched()) {
                showFront();
                cardFront.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.matched_card)
                );
                cardFront.setAlpha(0.8f);
                cardContainer.setClickable(false);
            }
            // Çevrilmiş kart
            else if (card.isFlipped()) {
                showFront();
                cardFront.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.card_front)
                );
                cardFront.setAlpha(1f);
            }
            // Kapalı kart
            else {
                showBack();
                cardFront.setAlpha(1f);
            }
            
            // Tıklama olayı
            cardContainer.setOnClickListener(v -> {
                if (isClickEnabled && listener != null && !card.isFlipped() && !card.isMatched()) {
                    listener.onCardClick(position, card);
                }
            });
        }
        
        /**
         * Kartı animasyonlu olarak çevirir
         */
        public void flipCard(boolean toFront, Runnable onComplete) {
            if (toFront && !isFrontVisible) {
                // Arka -> Ön
                AnimatorSet flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(
                    context, R.animator.card_flip_out
                );
                AnimatorSet flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(
                    context, R.animator.card_flip_in
                );
                
                flipOut.setTarget(cardBack);
                flipIn.setTarget(cardFront);
                
                flipOut.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        cardBack.setVisibility(View.INVISIBLE);
                        cardFront.setVisibility(View.VISIBLE);
                    }
                });
                
                flipIn.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isFrontVisible = true;
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }
                });
                
                flipOut.start();
                flipIn.start();
                
            } else if (!toFront && isFrontVisible) {
                // Ön -> Arka
                AnimatorSet flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(
                    context, R.animator.card_flip_out
                );
                AnimatorSet flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(
                    context, R.animator.card_flip_in
                );
                
                flipOut.setTarget(cardFront);
                flipIn.setTarget(cardBack);
                
                flipOut.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        cardFront.setVisibility(View.INVISIBLE);
                        cardBack.setVisibility(View.VISIBLE);
                    }
                });
                
                flipIn.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isFrontVisible = false;
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }
                });
                
                flipOut.start();
                flipIn.start();
            }
        }
        
        /**
         * Ön yüzü gösterir (animasyonsuz)
         */
        private void showFront() {
            cardFront.setVisibility(View.VISIBLE);
            cardBack.setVisibility(View.INVISIBLE);
            cardFront.setRotationY(0);
            cardBack.setRotationY(0);
            isFrontVisible = true;
        }
        
        /**
         * Arka yüzü gösterir (animasyonsuz)
         */
        private void showBack() {
            cardFront.setVisibility(View.INVISIBLE);
            cardBack.setVisibility(View.VISIBLE);
            cardFront.setRotationY(0);
            cardBack.setRotationY(0);
            isFrontVisible = false;
        }
    }
}
