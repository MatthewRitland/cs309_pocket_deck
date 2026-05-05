package com.example.pocketdeck;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.java_websocket.handshake.ServerHandshake;

import java.util.List;

public class RequestsListAdapter extends RecyclerView.Adapter<RequestsListAdapter.RequestHolder> implements WebsocketListener{

    List<RequestObject> requests;

    public RequestsListAdapter(List<RequestObject> requests) {
        this.requests = requests;
    }

    @NonNull
    @Override
    public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_box, parent, false);
        return new RequestHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestHolder holder, int position) {
        RequestObject request = requests.get(position);
        String requestName = request.getLobby().getGameModeName() + " invite from " + request.getRequesterName();
        holder.setRequest(request.getRequesterName(), requestName);
        holder.requestId = request.getLobby().getLobbyId();
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) { }

    @Override
    public void onWebSocketMessage(String message) {
        // Important one
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) { }

    @Override
    public void onWebSocketError(Exception ex) { }

    class RequestHolder extends RecyclerView.ViewHolder {

        public TextView requestLabel;
        public Button acceptInvite, rejectInvite;
        public long requestId;
        public String requesterName;

        public RequestHolder(@NonNull View itemView) {
            super(itemView);
            requestLabel = itemView.findViewById(R.id.friendName);
            TextView statusLabel = itemView.findViewById(R.id.friendStatus);
            statusLabel.setText("ACTIVE INVITE");
            //userIcon = itemView.findViewById(R.id.friendPicture);

            acceptInvite = itemView.findViewById(R.id.friendMessage);
            acceptInvite.setText("Accept");
            rejectInvite = itemView.findViewById(R.id.friendSecondaryButton);
            rejectInvite.setText("Reject");

            ImageButton menuButton = itemView.findViewById(R.id.friendMenuButton);
            menuButton.setVisibility(View.INVISIBLE);
        }

        public void setRequest(String requesterName, String requestLabel) {
            this.requesterName = requesterName;
            this.requestLabel.setText(requestLabel);

            Requester activeRequester = Requester.getInstance(itemView.getContext());

            acceptInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activeRequester.actionRequest(requesterName, true);
                    Intent i = new Intent(itemView.getContext(), LobbyScreen.class);
                    i.putExtra("id", requestId);
                    itemView.getContext().startActivity(i);
                }
            });
            rejectInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activeRequester.actionRequest(requesterName, false);
                }
            });
        }
    }
}
