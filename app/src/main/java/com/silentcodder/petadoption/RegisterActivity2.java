package com.silentcodder.petadoption;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class RegisterActivity2 extends AppCompatActivity {

    EditText mAddressLine;
    EditText mCity;
    EditText mState;
    EditText mPinCode;
    Button mBtnDone;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        mAddressLine = findViewById(R.id.addressLine);
        mCity = findViewById(R.id.city);
        mState = findViewById(R.id.state);
        mPinCode = findViewById(R.id.pinCode);
        mBtnDone = findViewById(R.id.btnDone);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        UserId = firebaseAuth.getCurrentUser().getUid();

        mBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String AddressLine = mAddressLine.getText().toString();
                String City = mCity.getText().toString();
                String State = mState.getText().toString();
                String PinCode = mPinCode.getText().toString();

                if (TextUtils.isEmpty(AddressLine)){
                    mAddressLine.setError("Fill address");
                }else if (TextUtils.isEmpty(City)){
                    mCity.setError("Your City ?");
                }else if (TextUtils.isEmpty(State)){
                    mState.setError("Your State ?");
                }else if(TextUtils.isEmpty(PinCode)){
                    mPinCode.setError("Area Pin code ?");
                }else {
                    addData(AddressLine,City,State,PinCode);
                }
            }
        });

    }

    private void addData(String addressLine, String city, String state, String pinCode) {

        HashMap<String,Object> map = new HashMap<>();
        map.put("AddressLine",addressLine);
        map.put("City",city);
        map.put("State",state);
        map.put("PinCode",pinCode);

        firebaseFirestore.collection("Users").document(UserId)
                .collection("Address").document(UserId)
                .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(RegisterActivity2.this,MainActivity.class));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity2.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}