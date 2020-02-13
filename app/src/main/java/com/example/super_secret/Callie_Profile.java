package com.example.super_secret;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.super_secret.R;


public class Callie_Profile extends Fragment {
    View view;
    public Callie_Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        return inflater.inflate(R.layout.callie,container,false);
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
