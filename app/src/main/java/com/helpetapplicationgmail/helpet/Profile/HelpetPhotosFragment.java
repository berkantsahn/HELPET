package com.helpetapplicationgmail.helpet.Profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.helpetapplicationgmail.helpet.Models.PhotoHelp;
import com.helpetapplicationgmail.helpet.R;
import com.helpetapplicationgmail.helpet.Utils.GridImageAdapter;

import java.util.ArrayList;

public class HelpetPhotosFragment extends Fragment {

    private static final String TAG = "HelpetPhotosFragment";
    private static final int NUM_GRID_COLUMNS = 4;
    private GridView gridViewHelp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help_photos_profile, container, false);
        gridViewHelp = (GridView) view.findViewById(R.id.gridViewHelp);

        setupImageGrid();
        return view;
    }
    private void setupImageGrid() {
        Log.d(TAG, "setupImageGrid: setting up image gridview.");

        final ArrayList<PhotoHelp> photos = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference
                .child(getString(R.string.dbname_user_photos_help))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    photos.add(singleSnapshot.getValue(PhotoHelp.class));
                }

                //setting up same size of grid elements
                int gridWith = getResources().getDisplayMetrics().widthPixels;
                int imageWidth = gridWith / NUM_GRID_COLUMNS;
                gridViewHelp.setColumnWidth(imageWidth);

                final ArrayList<String> imgUrls = new ArrayList<String>();
                for(int i = 0; i<photos.size();i++){
                    imgUrls.add(photos.get(i).getImage_path());
                }

                GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, "", imgUrls);
                gridViewHelp.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });




    }

}
