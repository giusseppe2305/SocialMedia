package com.optic.socialmedia.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.optic.socialmedia.R;
import com.optic.socialmedia.activities.EditProfileActivity;

public class ProfileFragment extends Fragment {

    LinearLayout mEditProfileLinearLayout;
    View mView;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView= inflater.inflate(R.layout.fragment_profile, container, false);
        mEditProfileLinearLayout = mView.findViewById(R.id.editProfileLinearLayoutProfileFragment);
        mEditProfileLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });
        return mView;
    }
}