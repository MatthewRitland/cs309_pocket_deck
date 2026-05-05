package com.example.pocketdeck;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RequestsListAdapter extends RecyclerView.Adapter<RequestsListAdapter.RequestHolder> {

    List<RequestObject> requests;

    public RequestsListAdapter(List<RequestObject> requests) {
        this.requests = requests;
    }

    @NonNull
    @Override
    public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RequestHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class RequestHolder extends RecyclerView.ViewHolder {

        public RequestHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
