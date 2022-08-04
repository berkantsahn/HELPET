package com.helpetapplicationgmail.helpet.Search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.helpetapplicationgmail.helpet.R;
import com.helpetapplicationgmail.helpet.Utils.GridImageAdapter;

import java.util.ArrayList;

/**
 * Created by acer on 11.04.2018.
 */

public class SearchNormalFragment extends Fragment {

    private static final String TAG = "SearchNormalFragment";
    private static final int NUM_GRID_COLUMNS = 3;
    private GridView gridViewNormalSearch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_normalphotos, container, false);
        gridViewNormalSearch = (GridView) view.findViewById(R.id.gridViewNormalSearch);

        return view;
    }



    private void setupImageGrid(ArrayList<String> imgURLs) {


        //fotoğraflarımızın farklı boyutlarda olsa bile profilde aynı boyutta sağlamasını sağlayacak kodlar
        int gridWith = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWith / NUM_GRID_COLUMNS;
        gridViewNormalSearch.setColumnWidth(imageWidth);

        GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, "", imgURLs);
        gridViewNormalSearch.setAdapter(adapter);
    }
}
