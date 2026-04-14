package com.example.mobileapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpFragment extends Fragment {

    EditText emailInput, passwordInput, confirmPasswordInput;
    Button signUpButton;
    TextView footer;

    // firebase auth instance
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        confirmPasswordInput = view.findViewById(R.id.confirmPasswordInput);
        signUpButton = view.findViewById(R.id.signUpButton);
        footer = view.findViewById(R.id.footer);

        signUpButton.setOnClickListener(v -> {
            String e = emailInput.getText().toString().trim();
            String p = passwordInput.getText().toString();
            String c = confirmPasswordInput.getText().toString();

            if (TextUtils.isEmpty(e) || TextUtils.isEmpty(p) || TextUtils.isEmpty(c)) {
                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!p.equals(c)) {
                Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // firebase registration
            mAuth.createUserWithEmailAndPassword(e, p)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Registration successful", Toast.LENGTH_SHORT).show();
                            // navigate to home
                            NavHostFragment.findNavController(this)
                                    .navigate(R.id.action_signUp_to_signIn);
                        } else {
                            Toast.makeText(getActivity(), "Registration failed: " +
                                    task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        footer.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_signUp_to_signIn));
    }
}
