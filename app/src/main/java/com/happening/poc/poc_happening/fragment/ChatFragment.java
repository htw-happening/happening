package com.happening.poc.poc_happening.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.happening.poc.poc_happening.R;

/**
 * Created by kaischulz on 10.12.16.
 */

public class ChatFragment extends Fragment {

    private static ChatFragment instance = null;
    private View rootView = null;

    public static ChatFragment getInstance() {
        instance = new ChatFragment();
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        return container;
    }
}
