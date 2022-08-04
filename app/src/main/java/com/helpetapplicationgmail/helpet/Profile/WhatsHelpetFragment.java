package com.helpetapplicationgmail.helpet.Profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.helpetapplicationgmail.helpet.Others.SliderAdapterWhatsHelpet;
import com.helpetapplicationgmail.helpet.R;

public class WhatsHelpetFragment extends Fragment {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private SliderAdapterWhatsHelpet sliderAdapterWhatsHelpet;


    private Button mNextBtn;
    private Button mBackBtn;

    private int mCurrentPage;


    private TextView[] mDots;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_whatshelpet, container, false);

        super.onCreate(savedInstanceState);


        mSlideViewPager=(ViewPager) view.findViewById(R.id.slideViewPager);
        mDotLayout=(LinearLayout) view.findViewById(R.id.dotsLayout);

        mNextBtn=(Button) view.findViewById(R.id.nextBtn);
        mBackBtn=(Button) view.findViewById(R.id.prevBtn);

        sliderAdapterWhatsHelpet = new SliderAdapterWhatsHelpet(getActivity());

        mSlideViewPager.setAdapter(sliderAdapterWhatsHelpet);

        addDotsIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListener);

        //OnClickListeners

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentPage + 1);

            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentPage - 1);

            }
        });

        return view;

    }


    public void addDotsIndicator(int position){
        mDots=new TextView[8];
        mDotLayout.removeAllViews();
        for (int i=0; i<mDots.length;i++){
            mDots[i]=new TextView(getActivity());
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));


            mDotLayout.addView(mDots[i]);
        }
        if (mDots.length>0){
            mDots[position].setTextColor(getResources().getColor(R.color.white));
        }
    }
    ViewPager.OnPageChangeListener viewListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {

            addDotsIndicator(i);
            mCurrentPage=i;

            if(i==0){
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(false);
                mBackBtn.setVisibility(View.INVISIBLE);

                mNextBtn.setText("İleri");
                mBackBtn.setText("");
            }
            else if (i==mDots.length-1){
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);

                mNextBtn.setText("Bitti");
                mBackBtn.setText("Geri");
            }
            else {
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);

                mNextBtn.setText("İleri");
                mBackBtn.setText("Geri");
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
}
