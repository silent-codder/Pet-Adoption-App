   package com.silentcodder.petadoption;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.silentcodder.petadoption.Fragments.AddFragment;
import com.silentcodder.petadoption.Fragments.HomeFragment;
import com.silentcodder.petadoption.Fragments.NotificationFragment;
import com.silentcodder.petadoption.Fragments.ProfileFragment;
import com.silentcodder.petadoption.Fragments.SearchFragment;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

   public class MainActivity extends AppCompatActivity {

       Fragment fragment;

       FirebaseAuth firebaseAuth;
       FirebaseFirestore firebaseFirestore;
       String UserId;
       AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();
        builder = new AlertDialog.Builder(this);

        ConnectivityManager ConnectionManager=(ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()==true )
        {

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

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {

                } else {
                    String  token = task.getResult().getToken();
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("token",token);
                    firebaseFirestore.collection("Tokens").document(UserId)
                            .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                    Log.i("FCM TOKEN", token);
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.search:
                        fragment = new SearchFragment();
                        break;
//                    case R.id.add:
//                        fragment = new AddFragment();
//                        break;
                    case R.id.notification:
                        fragment = new NotificationFragment();
                        break;
                    case R.id.profile:
                        fragment = new ProfileFragment();
                        break;
                }

                if(fragment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();
                }

                return true;
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
    }

       protected void onStart() {
           super.onStart();
           ConnectivityManager ConnectionManager=(ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);
           NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
           if(networkInfo != null && networkInfo.isConnected()==true )
           {

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