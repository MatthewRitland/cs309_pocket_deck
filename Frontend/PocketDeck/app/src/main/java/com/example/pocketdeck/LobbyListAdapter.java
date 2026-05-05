package com.example.pocketdeck;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LobbyListAdapter extends RecyclerView.Adapter<LobbyListAdapter.LobbyUserHolder> {

    List<LobbyScreen.LobbyUser> listUsers;

    public LobbyListAdapter(List<LobbyScreen.LobbyUser> users) {
        this.listUsers = users;
    }

    @NonNull
    @Override
    public LobbyUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lobby_user, parent, false);
        return new LobbyUserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LobbyUserHolder holder, int position) {
        LobbyScreen.LobbyUser user = listUsers.get(position);
        String usernameFinal = user.getUsername();
        if (user.isHost()) usernameFinal += " - HOST";
        holder.usernameText.setText(usernameFinal);
        holder.statusDisplay.setText(user.isReady()? "READY":"NOT READY");
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    class LobbyUserHolder extends RecyclerView.ViewHolder {
        public TextView usernameText;
        public TextView statusDisplay;
        public Button removeButton;
        public LobbyUserHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.lobbyuser_username);
            statusDisplay = itemView.findViewById(R.id.lobbyuser_status);
        }
    }
}
