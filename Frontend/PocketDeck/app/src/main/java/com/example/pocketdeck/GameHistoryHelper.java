package com.example.pocketdeck;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class GameHistoryHelper extends RecyclerView.Adapter<GameHistoryHelper.HistoryViewHolder> {
    // This list holds every row the RecyclerView shows
    // Each string is one game history
    private ArrayList<String> historyList;

    public GameHistoryHelper(ArrayList<String> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_history_recycler, parent, false);

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        String currentHistory = historyList.get(position);
        holder.historyText.setText(currentHistory);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView historyText;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            historyText = itemView.findViewById(R.id.historyText);
        }
    }
}
