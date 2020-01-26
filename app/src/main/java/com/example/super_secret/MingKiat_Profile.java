package com.example.super_secret;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


public class MingKiat_Profile extends Fragment {
    View view;
    public MingKiat_Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mingkiat,container,false);
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
