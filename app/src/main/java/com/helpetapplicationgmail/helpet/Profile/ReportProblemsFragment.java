package com.helpetapplicationgmail.helpet.Profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.helpetapplicationgmail.helpet.R;

/**
 * Created by acer on 11.04.2018.
 */

public class ReportProblemsFragment extends Fragment {

    private static final String TAG = "ReportProblemsFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_problems, container, false);



        //geridön butonu -> ProfileActivity
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ProfileActivity'e geri yönlendiriliyor.");
                getActivity().finish();
            }
        });

        return view;
    }

}
