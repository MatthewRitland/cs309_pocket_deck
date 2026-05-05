package com.example.pocketdeck;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsHolder>{

    private List<FriendObject> friends;
    private boolean isRequests;
    private FriendsScreen screenRef;

    public FriendsListAdapter(List<FriendObject> friends, boolean isRequests, FriendsScreen screen) {
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
        FriendObject holderFriend = friends.get(position);
        holder.setHolderView(holderFriend);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
}
