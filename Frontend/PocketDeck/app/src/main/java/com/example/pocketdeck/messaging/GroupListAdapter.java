package com.example.pocketdeck.messaging;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketdeck.R;

import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupHolder> {

    private List<MessageGroup> groups;
    private GroupsListActivity groupManager;

    public static class GroupHolder extends RecyclerView.ViewHolder {
        public Button groupLink;

        public GroupHolder(View view) {
            super(view);
            this.groupLink = view.findViewById(R.id.groupMessagingLink);
        }
    }

    public GroupListAdapter(List<MessageGroup> groups, GroupsListActivity groupActivity) {
        this.groups = groups;
        this.groupManager = groupActivity;
    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messaging_group_box, parent, false);
        return new GroupHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupHolder holder, int position) {
        MessageGroup selectGroup = groups.get(position);

        /* Set text */
        holder.groupLink.setText(selectGroup.getGroupName());
        /* Set button onClick*/
        holder.groupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupManager.openGroupMessages(selectGroup.getGroupId(), selectGroup.getGroupName());
            }
        });
    }

    @Override
    public int getItemCount() { return groups.size(); }
}
