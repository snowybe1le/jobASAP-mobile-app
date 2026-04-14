package com.example.mobileapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.android.material.button.MaterialButton;

public class Start extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton signUpButton = view.findViewById(R.id.button);
        MaterialButton signInButton = view.findViewById(R.id.button2);

        // navigate to SignUpFragment
        signUpButton.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_start_to_signup));

        // navigate to SignInFragment
        signInButton.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_start_to_login));
    }
}
