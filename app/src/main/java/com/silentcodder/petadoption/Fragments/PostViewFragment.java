package com.silentcodder.petadoption.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.silentcodder.petadoption.Adapter.CommentAdapter;
import com.silentcodder.petadoption.Adapter.PostAdapter;
import com.silentcodder.petadoption.Model.PostData;
import com.silentcodder.petadoption.Model.PostId;
import com.silentcodder.petadoption.Notification.APIService;
import com.silentcodder.petadoption.Notification.Client;
import com.silentcodder.petadoption.Notification.Data;
import com.silentcodder.petadoption.Notification.NotificationSender;
import com.silentcodder.petadoption.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class PostViewFragment extends Fragment {

    ImageView mPostImage,mUnlike,mBack;
    CardView mBtnAdoption;
    ImageView mLike;
    TextView mPetName,mAge,mAbout,mSex;

    String fcmUrl = "https://fcm.googleapis.com/",CurrentUserName;

    String MobileNumber;
    String PostUserId;
    String PetName,PostId,Age,About,Sex,ImgUrl;

    ImageView mComment;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_view, container, false);

        mPostImage = view.findViewById(R.id.postImg);
        mUnlike = view.findViewById(R.id.unlike);
        mBack = view.findViewById(R.id.back);
        mSex = view.findViewById(R.id.sex);
        mBtnAdoption = view.findViewById(R.id.btnAdoption);
        mLike = view.findViewById(R.id.like);
        mPetName = view.findViewById(R.id.petName);
        mAge = view.findViewById(R.id.age);
        mAbout = view.findViewById(R.id.about);
        mComment = view.findViewById(R.id.comment);



        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        Bundle bundle = this.getArguments();
        if (bundle!=null){
            PetName = bundle.getString("PetName");
            Age = bundle.getString("Age");
            Sex = bundle.getString("Sex");
            About = bundle.getString("About");
            PostId = bundle.getString("PostId");
            ImgUrl = bundle.getString("ImgUrl");
            PostUserId = bundle.getString("PostUserId");

            Log.d(TAG, "Bundle Img Url : " + PostId);
        }

        mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new CommentFragment();
                Bundle bundle = new Bundle();
                bundle.putString("PostId",PostId);
                bundle.putString("PostUserId",PostUserId);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();
            }
        });
        mPetName.setText(PetName);
        mAge.setText(Age);
        mSex.setText(Sex);
        mAbout.setText(About);

        firebaseFirestore.collection("Posts").document(PostId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            ImgUrl = task.getResult().getString("PetImgUrl");
                            if (!TextUtils.isEmpty(ImgUrl)){
                                Picasso.get().load(ImgUrl).into(mPostImage);
                            }
                        }
                    }
                });

        mUnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("TimeStamp",System.currentTimeMillis());
                map.put("UserId",FirebaseAuth.getInstance().getCurrentUser());
                firebaseFirestore.collection("Posts").document(PostId).collection("Likes")
                        .document(firebaseAuth.getCurrentUser().getUid()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.like_sound);
                            mediaPlayer.start();
                            mUnlike.setVisibility(View.GONE);
                            mLike.setVisibility(View.VISIBLE);

                            firebaseFirestore.collection("Tokens").document(PostUserId).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){
                                                String Token = task.getResult().getString("token");
                                                String title = CurrentUserName + " like your post";
                                                String data = "See Notification";

                                                HashMap<String, Object> notification = new HashMap<>();
                                                notification.put("PostId",PostId);
                                                notification.put("PostImgUrl",ImgUrl);
                                                notification.put("UserId",firebaseAuth.getCurrentUser().getUid());
                                                notification.put("PostUserId",PostUserId);
                                                notification.put("TimeStamp",System.currentTimeMillis());

                                                firebaseFirestore.collection("Notification").document(PostUserId)
                                                        .collection("Notification").document(PostId).set(notification);

                                                sendNotification(Token,title,data);
                                            }
                                        }
                                    });
                        }
                    }
                });

            }
        });

        mLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Posts").document(PostId).collection("Likes").document(firebaseAuth.getCurrentUser().getUid())
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mUnlike.setVisibility(View.VISIBLE);
                            mLike.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        firebaseFirestore.collection("Posts").document(PostId).collection("Likes")
                .document(firebaseAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()){
                    mLike.setVisibility(View.VISIBLE);
                    mUnlike.setVisibility(View.GONE);
                }else {
                    mLike.setVisibility(View.GONE);
                    mUnlike.setVisibility(View.VISIBLE);
                }
            }
        });




        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        MobileNumber = task.getResult().getString("MobileNumber");
                    }
                });

        mBtnAdoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d(TAG, "ChatId 2st : " + ChatId);
//                Fragment fragment = new ChatFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("PostId",PostId);
//                bundle.putString("UserName",UserName);
//                bundle.putString("ProfileUrl",ProfileUrl);
//                bundle.putString("ChatId",ChatId);
//                fragment.setArguments(bundle);
//                getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();
//
                boolean install = appInstallOrNot("com.whatsapp");

                String msg = "I'm interested in your pet " + PetName;
                String number = MobileNumber ;

                if (number.equals("+919623921310")){
                    number = "+917387607015";
                }

                if (install){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(String.format("https://api.whatsapp.com/send?phone=%s&text=%s", number, msg)));
                    startActivity(intent);
                }else {
                    Toast.makeText(getContext(), "WhatsApp not install", Toast.LENGTH_SHORT).show();
                }

//                Toast.makeText(getContext(), "Pet Not Available", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void sendNotification(String token, String title, String msg) {
        Data data = new Data(title,msg);
        NotificationSender notificationSender = new NotificationSender(data,token);

        APIService apiService = Client.getRetrofit(fcmUrl).create(APIService.class);

        apiService.sendNotification(notificationSender).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private boolean appInstallOrNot(String s) {
        PackageManager packageManager = getContext().getPackageManager();
        boolean app_installed;
        try {
            packageManager.getPackageInfo(s,PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }catch (PackageManager.NameNotFoundException e){
            app_installed = false;
        }
        return app_installed;
    }


}