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
import androidx.navigation.fragment.NavHostFragment;
import com.google.firebase.auth.FirebaseAuth;

public class SignInFragment extends Fragment {

    private EditText username, password;
    private Button signIn;
    private TextView signupFooter;

    // firebase auth instance
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        username = view.findViewById(R.id.signinUsername);
        password = view.findViewById(R.id.signinPassword);
        signIn = view.findViewById(R.id.signInButton);
        signupFooter = view.findViewById(R.id.signupFooter);

        signIn.setOnClickListener(v -> {
            String e = username.getText().toString().trim();
            String p = password.getText().toString();

            if (TextUtils.isEmpty(e) || TextUtils.isEmpty(p)) {
                Toast.makeText(v.getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(e, p)
                    .addOnCompleteListener(task -> {
                        if (!isAdded()) return;

                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show();

                            NavHostFragment.findNavController(this)
                                    .navigate(R.id.action_signIn_to_home);
                        } else {
                            Toast.makeText(
                                    requireContext(),
                                    "Login failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });

        signupFooter.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_signIn_to_signUp));
    }
}
