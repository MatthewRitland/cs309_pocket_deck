package com.example.pocketdeck.friends;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
            holder.secondaryActionButton.setVisibility(View.VISIBLE);
            holder.menuButton.setVisibility(View.INVISIBLE);
            holder.primaryActionButton.setText("Accept");
            holder.primaryActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    screenRef.acceptRequest( holderFriend.getFriendshipId() );
                }
            });
            holder.secondaryActionButton.setText("Reject");
            holder.secondaryActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    screenRef.removeFriend(holderFriend.getFriendshipId());
                }
            });
        } else {
            holder.secondaryActionButton.setVisibility(View.INVISIBLE);
            holder.menuButton.setVisibility(View.VISIBLE);
            holder.primaryActionButton.setText("Message");
            holder.primaryActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: MESSAGING OR ACCEPT
                }
            });
        }
    }


    private void friendMenuButton(long friendId, boolean isRequest) {

    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public static class FriendsHolder extends RecyclerView.ViewHolder {

        public TextView usernameLabel, statusLabel;
        public ImageView userIcon;
        public Button primaryActionButton, secondaryActionButton;
        public ImageButton menuButton;

        public FriendsHolder(View view) {
            super(view);
            usernameLabel = view.findViewById(R.id.friendName);
            statusLabel = view.findViewById(R.id.friendStatus);
            userIcon = view.findViewById(R.id.friendPicture);
            primaryActionButton = view.findViewById(R.id.friendMessage);
            secondaryActionButton = view.findViewById(R.id.friendSecondaryButton);
            menuButton = view.findViewById(R.id.friendMenuButton);
        }
    }
}
