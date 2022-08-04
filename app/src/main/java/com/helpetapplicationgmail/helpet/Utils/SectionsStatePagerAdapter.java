package com.helpetapplicationgmail.helpet.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by acer on 12.03.2018.
 */

public class SectionsStatePagerAdapter extends FragmentStatePagerAdapter {


    private final List<Fragment> myFragmentList = new ArrayList<>();

    //eğer bir fragman objem varsa bu objemin numarasını çekmeye yarayacak.
    private final HashMap<Fragment, Integer> myFragments = new HashMap<>();

    // eğer fragman ismim varsa bu objemin numarasını çekmeye yarayacak.
    private final HashMap<String, Integer> myFragmentNumbers = new HashMap<>();

    //eğer fragman numaram varsa bu fragmanın ismini çekmeye yarayacak.
    private final HashMap<Integer, String> myFragmentNames = new HashMap<>();

    public SectionsStatePagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position){
        return myFragmentList.get(position);
    }

    @Override
    public int getCount(){
        return myFragmentList.size();
    }


    //public -> AccountSettingsActivity kullanılacak. -> setupFragments
    public void addFragment(Fragment fragment, String fragmentName){
        myFragmentList.add(fragment);
        myFragments.put(fragment, myFragmentList.size()-1);
        myFragmentNumbers.put(fragmentName, myFragmentList.size()-1);
        myFragmentNames.put(myFragmentList.size()-1, fragmentName);
    }

    /** eğer fragman adım varsa numarasını çekmeye yarayacak. **/
    public Integer getFragmentNumber(String fragmentName){
        if (myFragmentNumbers.containsKey(fragmentName)){
            return myFragmentNumbers.get(fragmentName);
        }
        else{
            return null;
        }
    }

    /** eğer fragman objem varsa numarasını çekmeye yarayacak. **/
    public Integer getFragmentNumber(Fragment fragment){
        if (myFragmentNumbers.containsKey(fragment)){
            return myFragmentNumbers.get(fragment);
        }
        else{
            return null;
        }
    }

    /** eğer fragman numaram varsa ismini çekmeye yarayacak. **/
    public String getFragmentName(Integer fragmentNumber){
        if (myFragmentNames.containsKey(fragmentNumber)){
            return myFragmentNames.get(fragmentNumber);
        }
        else{
            return null;
        }
    }




}
