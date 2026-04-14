package com.example.mobileapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Profile extends Fragment {

    FirebaseAuth auth;
    FirebaseFirestore db;

    EditText nameEt, usernameEt;
    ImageView profilePic;

    String uid;

    // avatars you created
    String[] avatars = {
            "avatar1", "avatar2", "avatar3",
            "avatar4", "avatar5", "avatar6"
    };

    int avatarIndex = 0;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = auth.getCurrentUser().getUid();

        // views
        nameEt = view.findViewById(R.id.Name);
        usernameEt = view.findViewById(R.id.Username);
        profilePic = view.findViewById(R.id.ProfilePictureUser);
//
//        Button jobOfferedButton = view.findViewById(R.id.JobOffered);
//        Button jobAcceptedButton = view.findViewById(R.id.JobAccepted);
        Button reviewButton = view.findViewById(R.id.Review);

        // -------------------------------
        // LOAD USER PROFILE
        // -------------------------------
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        nameEt.setText(doc.getString("name"));
                        usernameEt.setText(doc.getString("username"));

                        String avatarId = doc.getString("avatarId");
                        if (avatarId != null) {
                            int resId = getResources().getIdentifier(
                                    avatarId,
                                    "drawable",
                                    requireContext().getPackageName()
                            );
                            profilePic.setImageResource(resId);

                            for (int i = 0; i < avatars.length; i++) {
                                if (avatars[i].equals(avatarId)) {
                                    avatarIndex = i;
                                    break;
                                }
                            }
                        }
                    } else {
                        // first-time user
                        profilePic.setImageResource(R.drawable.avatar1);
                        db.collection("users")
                                .document(uid)
                                .set(Collections.singletonMap("avatarId", "avatar1"),
                                        SetOptions.merge());
                    }
                });

        // -------------------------------
        // SAVE NAME & USERNAME (auto-save)
        // -------------------------------
        View.OnFocusChangeListener saveListener = (v, hasFocus) -> {
            if (!hasFocus) {
                Map<String, Object> data = new HashMap<>();
                data.put("name", nameEt.getText().toString().trim());
                data.put("username", usernameEt.getText().toString().trim());

                db.collection("users")
                        .document(uid)
                        .set(data, SetOptions.merge());
            }
        };

        nameEt.setOnFocusChangeListener(saveListener);
        usernameEt.setOnFocusChangeListener(saveListener);

        // -------------------------------
        // AVATAR CLICK (cycle + save)
        // -------------------------------
        profilePic.setOnClickListener(v -> {
            avatarIndex = (avatarIndex + 1) % avatars.length;
            String selectedAvatar = avatars[avatarIndex];

            int resId = getResources().getIdentifier(
                    selectedAvatar,
                    "drawable",
                    requireContext().getPackageName()
            );
            profilePic.setImageResource(resId);

            db.collection("users")
                    .document(uid)
                    .update("avatarId", selectedAvatar);
        });

        // -------------------------------
        // JOB OFFERED NAV
        // -------------------------------
//        jobOfferedButton.setOnClickListener(v -> {
//            try {
//                NavHostFragment.findNavController(Profile.this)
//                        .navigate(R.id.action_to_jobOffered);
//            } catch (Exception e) {
//                Log.e("ProfileDebug", "JobOffered nav error: " + e.getMessage());
//            }
//        });
//
//        // -------------------------------
//        // JOB ACCEPTED NAV
//        // -------------------------------
//        jobAcceptedButton.setOnClickListener(v -> {
//            try {
//                NavHostFragment.findNavController(Profile.this)
//                        .navigate(R.id.action_to_jobAccepted);
//            } catch (Exception e) {
//                Log.e("ProfileDebug", "JobAccepted nav error: " + e.getMessage());
//            }
//        });

        // -------------------------------
        // REVIEW NAV
        // -------------------------------
        reviewButton.setOnClickListener(v -> {
            try {
                NavHostFragment.findNavController(Profile.this)
                        .navigate(R.id.action_profile_to_reviewList);
            } catch (Exception e) {
                Log.e("ProfileDebug", "Review nav error: " + e.getMessage());
            }
        });
    }
}
