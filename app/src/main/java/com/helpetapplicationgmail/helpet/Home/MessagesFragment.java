package com.helpetapplicationgmail.helpet.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.helpetapplicationgmail.helpet.R;

/**
 * Created by acer on 5.03.2018.
 */

public class MessagesFragment extends Fragment {

    private static final String TAG = "Messages Fragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        return view;
    }
}
