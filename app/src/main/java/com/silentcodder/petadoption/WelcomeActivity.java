package com.silentcodder.petadoption;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends AppCompatActivity {

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        builder = new AlertDialog.Builder(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectivityManager ConnectionManager=(ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()==true )
        {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user!=null){
                        startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                        finish();
                    }else {
                        startActivity(new Intent(WelcomeActivity.this,RegisterActivity.class));
                    }
                }
            },3000);

        }
        else
        {

            //Uncomment the below code to Set the message and title from the strings.xml file
            builder.setMessage("") .setTitle("No Internet");

            //Setting message manually and performing action on button click
            builder.setMessage("No internet connection !!")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            alert.show();

        }
    }
}
