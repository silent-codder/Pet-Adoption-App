package com.silentcodder.petadoption;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity {

    CountryCodePicker mCpp;
    EditText mMobileNumber,mName;
    Button mBtnContinue;
    TextView mRegisterTxt;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseFirestore = FirebaseFirestore.getInstance();

        findIDs();
        mRegisterTxt = findViewById(R.id.RegisterTxt);
        mRegisterTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        mCpp.registerCarrierNumberEditText(mMobileNumber);
        ProgressBar progressBar = findViewById(R.id.loader);
        mBtnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(mCpp.getFullNumberWithPlus())){
                    mMobileNumber.setError("Mobile Number");
                }else {
                    firebaseFirestore.collection("Users").whereEqualTo("MobileNumber",mCpp.getFullNumberWithPlus())
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (!value.isEmpty()){
                                        mBtnContinue.setVisibility(View.INVISIBLE);
                                        progressBar.setVisibility(View.VISIBLE);
                                        Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
                                        intent.putExtra("MobileNumber",mCpp.getFullNumberWithPlus().replace(" ",""));
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        mMobileNumber.setError("Mobile number not register ?");
                                    }
                                }
                            });
                }
            }
        });
    }

    private void findIDs() {
        mCpp = findViewById(R.id.cpp);
        mMobileNumber = findViewById(R.id.mobileNumber);
        mBtnContinue = findViewById(R.id.btnContinue);
    }
}