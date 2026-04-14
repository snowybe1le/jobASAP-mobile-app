package com.example.mobileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // ===== ADD START =====
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    private List<User> userList;
    private Context context;
    private OnUserClickListener listener;

    public UserAdapter(List<User> userList, Context context, OnUserClickListener listener) {
        this.userList = userList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.userName.setText(user.getName());

        if (user.getLastMessage() != null && !user.getLastMessage().isEmpty()) {
            holder.lastMessage.setText(user.getLastMessage());
        } else {
            holder.lastMessage.setText("Tap to chat");
        }

        // ===== ADD START: set avatar using avatarId =====
        if (holder.avatarImageView != null && user.getAvatarId() != null) {
            int resId = context.getResources().getIdentifier(
                    user.getAvatarId(),
                    "drawable",
                    context.getPackageName()
            );

            if (resId != 0) {
                holder.avatarImageView.setImageResource(resId);
            } else {
                holder.avatarImageView.setImageResource(R.drawable.avatar1); // fallback
            }
        }
        // ===== ADD END =====

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, lastMessage;
        ImageView avatarImageView; // ===== ADD START =====

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            avatarImageView = itemView.findViewById(R.id.profilePic); // ===== ADD START =====
        }
    }
}
