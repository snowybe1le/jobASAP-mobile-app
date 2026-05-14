package com.example.mobileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context context;
    private List<ChatMessage> chatList;
    private String currentUserId;

    public ChatAdapter(Context context, List<ChatMessage> chatList, String currentUserId) {
        this.context = context;
        this.chatList = chatList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage msg = chatList.get(position);

        // hide both bubbles first to prevent recycled view issues
        holder.leftBubble.setVisibility(View.GONE);
        holder.rightBubble.setVisibility(View.GONE);

        if (msg.getSenderId() != null && msg.getSenderId().equals(currentUserId)) {
            // current user sent this message -> right bubble
            holder.rightBubble.setVisibility(View.VISIBLE);
            holder.rightMessage.setText(msg.getMessage() != null ? msg.getMessage() : "");
            holder.rightTime.setText(formatTime(msg.getTimestamp()));
        } else {
            // other user -> left bubble
            holder.leftBubble.setVisibility(View.VISIBLE);
            holder.leftMessage.setText(msg.getMessage() != null ? msg.getMessage() : "");
            holder.leftTime.setText(formatTime(msg.getTimestamp()));
        }
    }

    @Override
    public int getItemCount() {
        return chatList != null ? chatList.size() : 0;
    }

    private String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftBubble, rightBubble;
        TextView leftMessage, leftTime, rightMessage, rightTime;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            leftBubble = itemView.findViewById(R.id.leftBubble);
            rightBubble = itemView.findViewById(R.id.rightBubble);
            leftMessage = itemView.findViewById(R.id.leftMessage);
            leftTime = itemView.findViewById(R.id.leftTime);
            rightMessage = itemView.findViewById(R.id.rightMessage);
            rightTime = itemView.findViewById(R.id.rightTime);
        }
    }
}
