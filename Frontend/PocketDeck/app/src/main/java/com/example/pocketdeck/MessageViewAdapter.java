package com.example.pocketdeck;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageViewAdapter extends RecyclerView.Adapter<MessageViewAdapter.MessageHolder>{

    private List<Message> messageList;

    public MessageViewAdapter(List<Message> messages) {
        this.messageList = messages;
    }

    public static class MessageHolder extends RecyclerView.ViewHolder {
        public TextView usernameText, messageText;

        public MessageHolder(View view) {
            super(view);
            usernameText = view.findViewById(R.id.messagerUsername);
            messageText = view.findViewById(R.id.messageContent);
        }

    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Long line of doom
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_box, parent, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        Message message = messageList.get(position);
        holder.usernameText.setText(message.getUsername());
        holder.messageText.setText(message.getMessage());
    }

    public void addNewMessage(Message newMessage) {
        messageList.add(newMessage);
        notifyItemInserted(messageList.size()-1);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
