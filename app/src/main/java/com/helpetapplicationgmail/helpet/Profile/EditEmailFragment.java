package com.helpetapplicationgmail.helpet.Profile;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.helpetapplicationgmail.helpet.Others.LoginActivity;
import com.helpetapplicationgmail.helpet.R;
import com.helpetapplicationgmail.helpet.Utils.FirebaseMethods;
import com.helpetapplicationgmail.helpet.Utils.UniversalImageLoader;
import com.helpetapplicationgmail.helpet.dialogs.ConfirmPasswordDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

public class EditEmailFragment extends Fragment  implements
        ConfirmPasswordDialog.OnConfirmPasswordListener{
    public void onConfirmPassword(String password){
        Log.d(TAG,"EditEmailFragment:Şifre alındı");
        mAuth=FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), password);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                                Log.d(TAG, "EditEmailFragment:Reauthenticate BAŞARILI");
                                mAuth.fetchSignInMethodsForEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                        try{
                                            if(task.isSuccessful())
                                            {
                                                if(task.getResult().getSignInMethods().size()==1)
                                                {
                                                    Log.d(TAG,"EditEmailFragment:onComplete:Bu email zaten kayıtlı.");
                                                    Toast.makeText(getActivity(),"Girmiş olduğunuz e-posta adresi kullanılmaktadır.",Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    Log.d(TAG,"Email kullanılabilir durumda");
                                                    user.updateEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                Log.d(TAG, "EditEmailFragment:Email güncellendi,doğrulama epostası gönderildi.");
                                                                mFirebaseMethods.sendVerificationEmail();
                                                                mFirebaseMethods.updateEmail(mEmail.getText().toString());
                                                                Log.d(TAG,"Email güncellendi!"+mEmail);
                                                                mAuth.signOut();
                                                                Intent intent=new Intent(getActivity(), LoginActivity.class);
                                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(intent);
                                                                Toast.makeText(getActivity(),"İşlem başarılı.E-posta adresinizin güncellenebilmesi için doğrulama e-postasını onaylayınız.",Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }catch(NullPointerException e){
                                            Log.e(TAG,"onComplete:NullPointerException");
                                        }
                                    }
                                });

                        }else{
                            Log.d(TAG,"Reauthenticate BAŞARISIZ!!");
                            Toast.makeText(getActivity(),"İşlem başarısız,şifrenizi kontrol ediniz",Toast.LENGTH_SHORT).show();
                            mEmail.setText("");
                        }
                    }});}
    private static final String TAG = "EditEmailFragment";
    private FirebaseAuth mAuth;
    private FirebaseMethods mFirebaseMethods;


    TextView mEmail,currentEmail;
    ImageView saveChangesEmail;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_email, container, false);

        mFirebaseMethods= new FirebaseMethods(getActivity());
        mEmail=(TextView) view.findViewById(R.id.new_email);
        currentEmail=(TextView) view.findViewById(R.id.current_email);
        mAuth=FirebaseAuth.getInstance();
        currentEmail.setText(mAuth.getCurrentUser().getEmail().toString());
        saveChangesEmail=(ImageView) view.findViewById(R.id.saveChangesEmail);
        saveChangesEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmPasswordDialog dialog=new ConfirmPasswordDialog();
                dialog.show(getFragmentManager(),getString(R.string.confirm_password_dialog));
                dialog.setTargetFragment(EditEmailFragment.this,1);

            }
        });
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
