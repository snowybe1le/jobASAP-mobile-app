package com.example.mobileapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatListFragment extends Fragment {

    private static final String TAG = "ChatListDebug";

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();

    private FirebaseFirestore firestore;
    private String currentUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firestore = FirebaseFirestore.getInstance();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d(TAG, "Current user uid: " + currentUid);

        userAdapter = new UserAdapter(userList, getContext(), this::openChat);
        recyclerView.setAdapter(userAdapter);

        loadRecentChats();
    }

    private void loadRecentChats() {
        firestore.collection("chats")
                .get()
                .addOnSuccessListener(chatSnapshot -> {
                    userList.clear();

                    for (DocumentSnapshot chatDoc : chatSnapshot) {

                        String chatId = chatDoc.getId();
                        String[] uids = chatId.split("_");
                        if (uids.length != 2) continue;

                        if (!uids[0].equals(currentUid) && !uids[1].equals(currentUid)) {
                            continue;
                        }

                        String otherUid = uids[0].equals(currentUid) ? uids[1] : uids[0];

                        // fetch last message
                        chatDoc.getReference()
                                .collection("messages")
                                .orderBy("timestamp", Query.Direction.DESCENDING)
                                .limit(1)
                                .get()
                                .addOnSuccessListener(messages -> {
                                    if (messages.isEmpty()) return;

                                    DocumentSnapshot msgDoc = messages.getDocuments().get(0);
                                    String lastMessage = msgDoc.getString("message");
                                    long timestamp = msgDoc.getLong("timestamp") != null
                                            ? msgDoc.getLong("timestamp") : 0;

                                    fetchUserByUid(otherUid, lastMessage, timestamp);
                                });
                    }
                });
    }

    private void fetchUserByUid(String uid, String lastMessage, long timestamp) {
        firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (!userDoc.exists()) return;

                    String username = userDoc.getString("username");
                    String avatarId = userDoc.getString("avatarId");

                    User user = new User(uid, username, lastMessage, timestamp); // use uid instead of email
                    user.setAvatarId(avatarId);

                    userList.add(user);

                    // sort by latest message
                    Collections.sort(userList, (u1, u2) ->
                            Long.compare(u2.getLastMessageTimestamp(), u1.getLastMessageTimestamp())
                    );

                    userAdapter.notifyDataSetChanged();
                });
    }

    private void openChat(User user) {
        Bundle bundle = new Bundle();
        bundle.putString("receiverUid", user.getEmail()); // rename to uid
        bundle.putString("receiverName", user.getName());

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_chatListFragment_to_chatFragment, bundle);
    }
}
