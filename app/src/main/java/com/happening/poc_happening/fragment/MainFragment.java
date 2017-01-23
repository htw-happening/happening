package com.happening.poc_happening.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.happening.poc_happening.R;

public class MainFragment extends Fragment {

    private static MainFragment instance = null;
    private View rootView = null;

    public static MainFragment getInstance() {
        instance = new MainFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }
}
