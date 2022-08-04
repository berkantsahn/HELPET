package com.helpetapplicationgmail.helpet.Search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.helpetapplicationgmail.helpet.R;

/**
 * Created by acer on 11.04.2018.
 */

public class SearchPostFragment extends Fragment {

    private static final String TAG = "SearchPostFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_posts, container, false);
        return view;
    }

}
