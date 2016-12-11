package com.happening.poc.poc_happening.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.happening.poc.poc_happening.R;

/**
 * Created by kaischulz on 11.12.16.
 */

public class Bt4Controls extends Fragment {

    private static Bt4Controls instance = null;
    private View rootView = null;

    public static Bt4Controls getInstance() {
        instance = new Bt4Controls();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bt4controls, container, false);
        return rootView;
    }

}
