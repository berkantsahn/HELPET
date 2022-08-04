package com.helpetapplicationgmail.helpet.Likes;

/**
 * Created by acer on 10.04.2018.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.helpetapplicationgmail.helpet.R;

public class bildirimAdapter extends ArrayAdapter<bildirimClass> {

    private bildirimClass[] bildirimListe;
    int resource;

    public bildirimAdapter(@NonNull Context context, int resource, @NonNull bildirimClass[] bildirimListe) {
        super(context, resource, bildirimListe);
        this.resource=resource;
        this.bildirimListe=bildirimListe;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View satir;
        LayoutInflater layoutInflater=(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        satir=layoutInflater.inflate(resource,null);

        TextView kullaniciad=(TextView) satir.findViewById(R.id.username);
        ImageView kullanici_resim=(ImageView) satir.findViewById(R.id.profile_image);


        bildirimClass bildirimler = bildirimListe[position];


        kullaniciad.setText(bildirimler.getKullanici_ad());
        kullanici_resim.setImageResource(bildirimler.getKullanici_resim());

        return satir;

    }

}
