package com.silentcodder.petadoption.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.silentcodder.petadoption.Fragments.PostViewFragment;
import com.silentcodder.petadoption.Fragments.ProfileViewFragment;
import com.silentcodder.petadoption.Model.PostData;
import com.silentcodder.petadoption.Notification.APIService;
import com.silentcodder.petadoption.Notification.Client;
import com.silentcodder.petadoption.Notification.Data;
import com.silentcodder.petadoption.Notification.NotificationSender;
import com.silentcodder.petadoption.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    List<PostData>postData;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String fcmUrl = "https://fcm.googleapis.com/",CurrentUserName;
    Context context;
    private String ImgUrl;

    public PostAdapter(List<PostData> postData) {
        this.postData = postData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_view,parent , false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String PetName = postData.get(position).getPetName();
        String Age = postData.get(position).getAge();
        String Sex = postData.get(position).getSex();
        String About = postData.get(position).getAbout();
        ImgUrl = postData.get(position).getPetImgUrl();
        String PostUserId = postData.get(position).getUserId();
        String PostId = postData.get(position).PostId;

        String UserId = firebaseAuth.getCurrentUser().getUid();

        holder.mPetName.setText(PetName);
        holder.mAge.setText(Age + ", " +Sex);
        holder.mAbout.setText(About);

        Picasso.get().load(ImgUrl).into(holder.PostImg);

        firebaseFirestore.collection("Users").document(UserId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        CurrentUserName = task.getResult().getString("UserName");
                    }
                });

        firebaseFirestore.collection("Users").document(PostUserId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String UserName = task.getResult().getString("UserName");
                    String ProfileUrl = task.getResult().getString("ProfileImgUrl");
                    if (!TextUtils.isEmpty(ProfileUrl)){
                        Picasso.get().load(ProfileUrl).into(holder.mProfileImg);
                    }
                    holder.mUserName.setText(UserName);
                }
            }
        });

        holder.mUnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("TimeStamp",System.currentTimeMillis());
                map.put("UserId",UserId);
                firebaseFirestore.collection("Posts").document(PostId).collection("Likes")
                        .document(UserId).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.like_sound);
                            mediaPlayer.start();
                            holder.mUnlike.setVisibility(View.GONE);
                            holder.mLike.setVisibility(View.VISIBLE);
                            holder.mLike.playAnimation();

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
                                                notification.put("UserId",UserId);
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

        holder.PostImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }

            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("TimeStamp",System.currentTimeMillis());
                    map.put("UserId",UserId);
                    firebaseFirestore.collection("Posts").document(PostId).collection("Likes")
                            .document(UserId).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.like_sound);
                                mediaPlayer.start();
                                holder.mUnlike.setVisibility(View.GONE);
                                holder.mLike.setVisibility(View.VISIBLE);
                                holder.mLike.playAnimation();
                            }
                        }
                    });
                    return super.onDoubleTap(e);
                }
            });
        });

        holder.mLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Posts").document(PostId).collection("Likes").document(UserId)
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            holder.mUnlike.setVisibility(View.VISIBLE);
                            holder.mLike.setVisibility(View.GONE);
                            holder.mLike.playAnimation();
                        }
                    }
                });
            }
        });

        firebaseFirestore.collection("Posts").document(PostId).collection("Likes")
                .document(UserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()){
                    holder.mLike.setVisibility(View.VISIBLE);
                    holder.mUnlike.setVisibility(View.GONE);
                }else {
                    holder.mLike.setVisibility(View.GONE);
                    holder.mUnlike.setVisibility(View.VISIBLE);
                }
            }
        });

        firebaseFirestore.collection("Posts").document(PostId).collection("Likes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        String  count = String.valueOf(value.size());
                        holder.mLikeCount.setText(count + " Likes");
                    }
                });

        holder.PostImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SharedPreferences sharedPreferences = context.getSharedPreferences("PostData", 0);
//                Editor editor = sharedPreferences.edit();
//                editor.putString("PetName",PetName);
//                editor.putString("Age",Age);
//                editor.putString("Sex",Sex);
//                editor.putString("About",About);
//                editor.putString("PostId",PostId);
//                editor.putString("ImgUrl",ImgUrl);
//                editor.putString("PostUserId",PostUserId);
//                editor.commit();

                Bundle bundle = new Bundle();
                bundle.putString("PetName",PetName);
                bundle.putString("Age",Age);
                bundle.putString("Sex",Sex);
                bundle.putString("About",About);
                bundle.putString("PostId",PostId);
                bundle.putString("ImgUrl",ImgUrl);
                bundle.putString("PostUserId",PostUserId);

                Log.d(TAG, "onClick Img Url : " + PostId);

                Fragment fragment = new PostViewFragment();
                fragment.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();
            }
        });

        holder.mUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment fragment = new ProfileViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("UserId",PostUserId);
                fragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();
            }
        });

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

    @Override
    public int getItemCount() {
        return postData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mPetName,mAge,mAbout,mLikeCount;
        ImageView mUnlike;
        ImageView PostImg;
        ImageView mBtnMenu,mProfileImg;
        TextView mUserName,mFollowText;
        LottieAnimationView mLike;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mPetName = itemView.findViewById(R.id.petName);
            mAge = itemView.findViewById(R.id.age);
            mAbout = itemView.findViewById(R.id.about);
            mUnlike = itemView.findViewById(R.id.unlike);
            mLike = itemView.findViewById(R.id.like);
            PostImg = itemView.findViewById(R.id.postImg);
            mLikeCount = itemView.findViewById(R.id.likeCount);
            mBtnMenu = itemView.findViewById(R.id.btnMenu);
            mProfileImg = itemView.findViewById(R.id.profile);
            mUserName = itemView.findViewById(R.id.userName);
            mFollowText = itemView.findViewById(R.id.followerText);
        }
    }
}
