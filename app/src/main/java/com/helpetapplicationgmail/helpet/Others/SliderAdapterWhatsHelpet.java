package com.helpetapplicationgmail.helpet.Others;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.helpetapplicationgmail.helpet.R;

public class SliderAdapterWhatsHelpet extends PagerAdapter{
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapterWhatsHelpet(Context context){

        this.context=context;

    }

    //Arrays
    public int[] slide_images={

            R.drawable.ic_post_white,
            R.drawable.ic_normal_photos_white,
            R.drawable.ic_messages_white,
            R.drawable.ic_white_home,
            R.drawable.ic_search_white,
            R.drawable.ic_white_circle,
            R.drawable.ic_white_alert,
            R.drawable.ic_person_white
    };

    public String[] slide_headings={
            "DURUM",
            "GÖNDERİ",
            "MESAJLAR",
            "ANASAYFA",
            "KEŞFET",
            "PAYLAŞIM",
            "BİLDİRİMLER",
            "PROFİL"

    };

    public String[] slide_descs={

            "Bu kısımda paylaştığınız veya paylaşılan yazıları görürsünüz."+"",
            "Bu kısımda paylaştığınız veya paylaşılan fotoğrafları ve varsa açıklamalarını görürsünüz." +" ",
            "Bu kısımda Mesajlarınızın olduğunu bölüme yönlendirilirsiniz,geçmiş konuşmaları görebilir veya yeni konuşma başlatabilirsiniz."+"",
            "Bu kısımda Anasayfaya yönlendirilirsiniz.Anasayfada paylaşılan gönderi ve durumları görebilirsiniz."+"",
            "Bu kısımda Uygulamaya kayıtlı olup fakat sizin takip etmediğiniz kullanıcıların paylaştıkları durumları ve gönderilerini burada görebilirsiniz."+"",
            "Bu kısımda Uygulamada paylaşmak istediğiniz Normal Paylaşım,Durum Paylaşım veya Yardım seçeneklerinden birini seçerek fotoğraf çekebilir,yazı yazabilir ve bunları paylaşabilirsiniz."+"",
            "Bu kısımda gönderilerinize yapılan beğeni ve yorumları bildirim olarak alırsınız ve görüntüleyebilirsiniz."+"",
            "Bu kısımda kendi profilinize ulaşabilir bilgilerinizi görüntüleyebilirsiniz."+"",
    };


    @Override
    public int getCount(){
        return slide_headings.length;
    }
    @Override
    public boolean isViewFromObject (View  view, Object o){
        return view==(RelativeLayout) o ;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.slide_layout,container,false);
        ImageView slideImageView=(ImageView) view.findViewById(R.id.slideImage);
        TextView slideHeading=(TextView) view.findViewById(R.id.slideHeading);
        TextView slideDescription=(TextView) view.findViewById(R.id.slide_desc);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_descs[position]);

        container.addView(view );
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container,int position,Object object) {
        container.removeView((RelativeLayout)object);
    }
}
