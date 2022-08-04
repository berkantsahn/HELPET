package com.helpetapplicationgmail.helpet.dialogs;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.helpetapplicationgmail.helpet.R;

import org.w3c.dom.Text;

/**
 * Created by ipekh on 25.4.2018.
 */
public class ConfirmPasswordDialog extends android.support.v4.app.DialogFragment{

    private static final String TAG="ConfirmPasswordDialog";

    public interface OnConfirmPasswordListener {
        public void onConfirmPassword(String password);
    }
    OnConfirmPasswordListener mOnConfirmPasswordListener;
    TextView mPassword;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dialog_confirm_password,container,false);
        Log.d(TAG,"onCreate-->ConfirmPasswordDialog: Başlatıldı");
        mPassword=(TextView) view.findViewById(R.id.confirm_password);
        //Dialog onaylanırsa:
        TextView confirmDialog=(TextView) view.findViewById(R.id.dialogConfirm);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Doğrula seçeneğine tıklandı,Re-authentication için şifre doğrulanıyor.");

                String password=mPassword.getText().toString();
                //Durum1:Şifre girilmişse kontrole gönder.
                if (!password.equals("")) {
                    mOnConfirmPasswordListener.onConfirmPassword(password);
                    getDialog().dismiss();
                }
                //Durum2:Şifre girilmeden işleme izin verme.
                else{
                    Toast.makeText(getActivity(),"Şifrenizi giriniz!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Dialog iptal edilirse:
        TextView cancelDialog=(TextView) view.findViewById(R.id.dialogCancel);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"İptal seçeneğine tıklandı,dialog kapatılıyor.");
                getDialog().dismiss();
            }
        });
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            //eklendiğinde fragmana yönlendir
            mOnConfirmPasswordListener=(OnConfirmPasswordListener) getTargetFragment();
        }
        catch(ClassCastException e){
            Log.e(TAG,"onAttach: ClassCastException:"+e.getMessage());
        }
    }
}
