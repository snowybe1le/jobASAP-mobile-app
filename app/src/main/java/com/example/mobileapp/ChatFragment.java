package com.example.mobileapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText messageInput;
    private Button sendButton;

    private ChatAdapter adapter;
    private final List<ChatMessage> chatList = new ArrayList<>();

    private FirebaseFirestore db;

    private String currentUid;
    private String receiverUid;
    private String chatId;

    // new UI
    private ImageView chatProfilePic;
    private TextView chatUserName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        messageInput = view.findViewById(R.id.ActEnterMessage);
        sendButton = view.findViewById(R.id.ActButton);

        chatProfilePic = view.findViewById(R.id.chatProfilePic);
        chatUserName = view.findViewById(R.id.chatUserName);

        db = FirebaseFirestore.getInstance();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (getArguments() == null) return view;
        receiverUid = getArguments().getString("receiverUid");

        if (receiverUid == null || currentUid == null) {
            Log.e("ChatFragment", "receiverUid or currentUid is null");
            requireActivity().onBackPressed();
            return view;
        }

        // alphabetically sort uids for chatId
        if (currentUid.compareTo(receiverUid) < 0) {
            chatId = currentUid + "_" + receiverUid;
        } else {
            chatId = receiverUid + "_" + currentUid;
        }

        createChatDocumentIfNeeded();

        adapter = new ChatAdapter(getContext(), chatList, currentUid);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // fetch receiver info to set header UI
        setReceiverInfo();

        loadMessages();

        sendButton.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void setReceiverInfo() {
        db.collection("users")
                .document(receiverUid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    String username = doc.getString("username");
                    String avatarId = doc.getString("avatarId");

                    chatUserName.setText(username);

                    if (avatarId != null) {
                        int resId = getResources().getIdentifier(
                                avatarId,
                                "drawable",
                                requireContext().getPackageName()
                        );
                        if (resId != 0) {
                            chatProfilePic.setImageResource(resId);
                        } else {
                            chatProfilePic.setImageResource(R.drawable.avatar1); // fallback
                        }
                    } else {
                        chatProfilePic.setImageResource(R.drawable.avatar1);
                    }
                })
                .addOnFailureListener(e -> Log.e("ChatDebug", "Failed to fetch receiver info: " + e.getMessage()));
    }

    private void createChatDocumentIfNeeded() {
        Map<String, Object> chatData = new HashMap<>();
        chatData.put("participants", Arrays.asList(currentUid, receiverUid));
        chatData.put("createdAt", System.currentTimeMillis());
        chatData.put("lastUpdated", System.currentTimeMillis());

        db.collection("chats")
                .document(chatId)
                .set(chatData, SetOptions.merge())
                .addOnSuccessListener(aVoid ->
                        Log.d("ChatDebug", "✅ Chat document created/verified: " + chatId))
                .addOnFailureListener(e -> Log.e("ChatDebug", "❌ Failed to create chat: " + e.getMessage()));
    }

    private void loadMessages() {
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("ChatDebug", "Error loading messages: " + error.getMessage());
                        return;
                    }
                    if (value == null) return;

                    chatList.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        ChatMessage msg = doc.toObject(ChatMessage.class);
                        if (msg != null) chatList.add(msg);
                    }

                    adapter.notifyDataSetChanged();
                    if (chatList.size() > 0)
                        recyclerView.scrollToPosition(chatList.size() - 1);
                });
    }

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (text.isEmpty()) return;

        long timestamp = System.currentTimeMillis();

        // fetch sender info using currentUid
        db.collection("users")
                .document(currentUid)
                .get()
                .addOnSuccessListener(senderDoc -> {
                    if (!senderDoc.exists()) return;

                    String senderName = senderDoc.getString("username");
                    String senderAvatarId = senderDoc.getString("avatarId");

                    ChatMessage message = new ChatMessage(
                            currentUid,
                            receiverUid,
                            senderName,
                            senderAvatarId,
                            text,
                            timestamp
                    );

                    // update chat document
                    Map<String, Object> chatUpdate = new HashMap<>();
                    chatUpdate.put("lastMessage", text);
                    chatUpdate.put("lastUpdated", timestamp);

                    db.collection("chats")
                            .document(chatId)
                            .set(chatUpdate, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                db.collection("chats")
                                        .document(chatId)
                                        .collection("messages")
                                        .add(message)
                                        .addOnSuccessListener(msgRef -> messageInput.setText(""))
                                        .addOnFailureListener(e -> Log.e("ChatDebug", "Failed to send message: " + e.getMessage()));
                            });
                })
                .addOnFailureListener(e -> Log.e("ChatDebug", "Failed to fetch sender info: " + e.getMessage()));
    }
}
