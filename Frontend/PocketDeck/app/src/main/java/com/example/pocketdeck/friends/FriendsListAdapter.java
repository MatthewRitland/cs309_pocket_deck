package com.example.pocketdeck.friends;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketdeck.R;

import java.util.List;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendsHolder> {

    private List<Friend> friends;
    private boolean isRequests;
    private FriendsScreen screenRef;

    public FriendsListAdapter(List<Friend> friends, boolean isRequests, FriendsScreen screen) {
        this.friends = friends;
        this.isRequests = isRequests;
        this.screenRef = screen;
    }

    @NonNull
    @Override
    public FriendsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_box, parent, false);
        return new FriendsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsHolder holder, int position) {
        Friend holderFriend = friends.get(position);
        holder.usernameLabel.setText(holderFriend.getFriendName());
        holder.statusLabel.setText(holderFriend.getFriendStatus());
        if (isRequests) {
            holder.actionButton.setText("Accept");
            holder.actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    screenRef.acceptRequest( holderFriend.getFriendshipId() );
                }
            });
        } else {
            holder.actionButton.setText("Message");
            holder.actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: MESSAGING OR ACCEPT
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public static class FriendsHolder extends RecyclerView.ViewHolder {

        public TextView usernameLabel, statusLabel;
        public ImageView userIcon;
        public Button actionButton;

        public FriendsHolder(View view) {
            super(view);
            usernameLabel = view.findViewById(R.id.friendName);
            statusLabel = view.findViewById(R.id.friendStatus);
            userIcon = view.findViewById(R.id.friendPicture);
            actionButton = view.findViewById(R.id.friendMessage);
        }
    }
}
