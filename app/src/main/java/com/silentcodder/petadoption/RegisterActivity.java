package com.silentcodder.petadoption;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

public class RegisterActivity extends AppCompatActivity {

    CountryCodePicker mCpp;
    EditText mMobileNumber,mFirstName,mLastName;
    Button mBtnContinue;
    TextView mLoginTxt;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseFirestore = FirebaseFirestore.getInstance();

        findIDs();

        mLoginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
        mCpp.registerCarrierNumberEditText(mMobileNumber);
        ProgressBar progressBar = findViewById(R.id.loader);
        mBtnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = mFirstName.getText().toString();
                String lastName = mLastName.getText().toString();
                if (TextUtils.isEmpty(mCpp.getFullNumberWithPlus())){
                    mMobileNumber.setError("Mobile Number");
                }else if (TextUtils.isEmpty(firstName)){
                    mFirstName.setError("First Name");
                }else if (TextUtils.isEmpty(lastName)){
                    mLastName.setError("Last Name");
                }else {
                    mBtnContinue.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(RegisterActivity.this, RegisterOtpActivity.class);
                    intent.putExtra("MobileNumber",mCpp.getFullNumberWithPlus().replace(" ",""));
                    intent.putExtra("FirstName",firstName);
                    intent.putExtra("LastName",lastName);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void findIDs() {
        mCpp = findViewById(R.id.cpp);
        mLoginTxt = findViewById(R.id.loginTxt);
        mMobileNumber = findViewById(R.id.mobileNumber);
        mFirstName = findViewById(R.id.firstName);
        mLastName = findViewById(R.id.lastName);
        mBtnContinue = findViewById(R.id.btnContinue);
    }
}