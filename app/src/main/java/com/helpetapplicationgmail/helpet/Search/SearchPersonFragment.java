package com.helpetapplicationgmail.helpet.Search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.helpetapplicationgmail.helpet.Models.Users;
import com.helpetapplicationgmail.helpet.Profile.ProfileActivity;
import com.helpetapplicationgmail.helpet.R;
import com.helpetapplicationgmail.helpet.Utils.UserListAdapter;
import com.helpetapplicationgmail.helpet.Utils.ViewProfileActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by acer on 11.04.2018.
 */
public class SearchPersonFragment extends Fragment {
    private static final String TAG = "SearchPersonFragment";
    //widgets
    private EditText mSearchParam;
    private ListView mListView;
    //vars
    private List<Users> mUserList;
    private UserListAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_persons, container, false);
        mSearchParam = (EditText) view.findViewById(R.id.editTextSearch);
        mListView = (ListView) view.findViewById(R.id.listViewSearch);
        closeKeyboard();
        initTextListener();
        return view;
    }
    private void initTextListener(){
        Log.d(TAG, "initTextListener: initializing");

        mUserList = new ArrayList<>();
        mSearchParam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String text = mSearchParam.getText().toString().toLowerCase(Locale.getDefault());
                searchForMatch(text);
            }
        });
    }
    private void updateUsersList(){
        Log.d(TAG, "updateUsersList: updating users list");
        mAdapter = new UserListAdapter(getActivity(), R.layout.layout_user_listitem, mUserList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected user:" + mUserList.get(position).toString());
                //navigate to profile activity
                Intent intent = new Intent(getActivity(), ViewProfileActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.search_activity));
                intent.putExtra(getString(R.string.intent_user), mUserList.get(position));
                startActivity(intent);
                mAdapter.clear();
                mListView.setAdapter(mAdapter);
            }
        });
    }
    private void searchForMatch(String keyword) {
        Log.d(TAG, "searchForMatch: searching for a match:"+keyword);
        mUserList.clear();
        //update the users listview
        if(keyword.length() == 0) {
        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(getString(R.string.dbname_users))
                    .orderByChild(getString(R.string.field_username)).equalTo(keyword);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot:dataSnapshot.getChildren()){
                        Log.d(TAG, "onDataChange: Found user:" + singleSnapshot.getValue(Users.class).toString());
                        mUserList.add(singleSnapshot.getValue(Users.class));
                        //update the users listview
                        updateUsersList();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
    private void closeKeyboard(){
        View view = getActivity().getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
