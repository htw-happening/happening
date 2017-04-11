package com.happening.chat.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.happening.chat.R;

public class Bt2Controls extends Fragment {

    private static Bt2Controls instance = null;
    private View rootView = null;

    public static Bt2Controls getInstance() {
        instance = new Bt2Controls();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_b2controls, container, false);
        return rootView;
    }

}
