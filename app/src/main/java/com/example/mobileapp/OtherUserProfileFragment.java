package com.example.mobileapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class OtherUserProfileFragment extends Fragment {

    private String userUid;

    FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_other_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        TextView usernameText = view.findViewById(R.id.usernameText);
        ImageView profilePic = view.findViewById(R.id.profilePic); // make sure this exists in xml
        MaterialButton messageButton = view.findViewById(R.id.messageButton);
        MaterialButton reviewButton = view.findViewById(R.id.reviewButton);

        // get UID from arguments
        if (getArguments() != null) {
            userUid = getArguments().getString("userUid");
        }

        // fetch username + avatar from Firestore
        if (userUid != null) {
            db.collection("users")
                    .document(userUid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            String avatarId = documentSnapshot.getString("avatarId");

                            usernameText.setText(username);

                            if (avatarId != null) {
                                int avatarResId = getResources().getIdentifier(
                                        avatarId,
                                        "drawable",
                                        requireContext().getPackageName()
                                );

                                if (avatarResId != 0) {
                                    profilePic.setImageResource(avatarResId);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to load user profile", Toast.LENGTH_SHORT).show()
                    );
        }

        messageButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("receiverUid", userUid); // use UID, not email

            Navigation.findNavController(v)
                    .navigate(R.id.action_profile_to_chat, bundle);
        });

        reviewButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("reviewedUserId", userUid);

            Navigation.findNavController(v)
                    .navigate(R.id.action_otherUserProfile_to_reviewListFragment, bundle);
        });
    }
}
