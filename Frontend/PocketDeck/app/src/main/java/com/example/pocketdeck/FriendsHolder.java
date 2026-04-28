package com.example.pocketdeck;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class FriendsHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {

    public TextView usernameLabel, statusLabel;
    public ImageView userIcon;
    public Button primaryButton, secondaryButton;
    public ImageButton menuButton;
    private FriendObject holderFriend;

    public FriendsHolder(View view) {
        super(view);
        usernameLabel = view.findViewById(R.id.friendName);
        statusLabel = view.findViewById(R.id.friendStatus);
        userIcon = view.findViewById(R.id.friendPicture);
        primaryButton = view.findViewById(R.id.friendMessage);
        secondaryButton = view.findViewById(R.id.friendSecondaryButton);
        menuButton = view.findViewById(R.id.friendMenuButton);
    }

    public void setHolderView(FriendObject friend) {
        /* Friend Properties */
        usernameLabel.setText(friend.getFriendName());
        statusLabel.setText(friend.getFriendStatus());

        /* Default button config */
        primaryButton.setVisibility(View.VISIBLE);
        secondaryButton.setVisibility(View.INVISIBLE);

        if (friend.getPending()) {
            menuButton.setVisibility(View.INVISIBLE);
            if( friend.getRequested() ) {
                primaryButton.setVisibility(View.INVISIBLE);
                statusLabel.setText("Request Pending");
            } else {
                secondaryButton.setVisibility(View.VISIBLE);
                /* Accept Button */
                primaryButton.setText("Accept");
                primaryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FriendsUtilities.getInstance().acceptRequest( friend.getFriendshipId(), itemView.getContext());
                    }
                });

                /* Decline Button */
                secondaryButton.setText("Decline");
                secondaryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FriendsUtilities.getInstance().removeFriend(friend.getFriendshipId(), itemView.getContext());
                    }
                });
            }
        }
        else {
            primaryButton.setText("Message");
            primaryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: MESSAGING OR ACCEPT
                }
            });
            menuButton.setVisibility(View.VISIBLE);
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    friendMenuButton(friend.getFriendId(), menuButton);
                }
            });
        }

        holderFriend = friend;
    }

    private void friendMenuButton(long friendId, View anchor) {
        // Popup menu
        PopupMenu menu = new PopupMenu(itemView.getContext(), anchor);
        menu.getMenuInflater().inflate(R.menu.friend_options_menu, menu.getMenu());
        menu.setOnMenuItemClickListener(this);
        menu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.fnav_delete) {
            // request deletion
            FriendsUtilities.getInstance().confirmRemoval(holderFriend, itemView.getContext());
            return true;
        }

        return false;
    }
}
