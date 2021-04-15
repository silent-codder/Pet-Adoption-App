package com.silentcodder.petadoption.Fragments;

import android.media.MediaPlayer;
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

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.silentcodder.petadoption.Adapter.CommentAdapter;
import com.silentcodder.petadoption.Model.PostData;
import com.silentcodder.petadoption.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class ProfilePostViewFragment extends Fragment {

    ImageView mPostImage,mUnlike,mBack,mShare;
    CardView mBtnAdoption;
    LottieAnimationView mLike;
    TextView mPetName,mAge,mAbout,mUserName;
    CircleImageView mProfileImg;
    Button mBtnFollow;
    EditText mComment;
    ImageView mPostComment;
    RecyclerView recyclerView;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    List<PostData> postData;
    CommentAdapter postAdapter;
    String UserName;
    String ProfileUrl,PostUserId;
    String ChatId,PetName,PostId,Age,About,Sex,ImgUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_post_view, container, false);

        mPostImage = view.findViewById(R.id.postImg);
        mUnlike = view.findViewById(R.id.unlike);
        mBack = view.findViewById(R.id.back);
        mShare = view.findViewById(R.id.share);
        mBtnAdoption = view.findViewById(R.id.btnAdoption);
        mLike = view.findViewById(R.id.like);
        mPetName = view.findViewById(R.id.petName);
        mAge = view.findViewById(R.id.age);
        mAbout = view.findViewById(R.id.about);
        mUserName = view.findViewById(R.id.userName);
        mProfileImg = view.findViewById(R.id.profile);
        mBtnFollow = view.findViewById(R.id.btnFollow);
        mComment = view.findViewById(R.id.comment);
        mPostComment = view.findViewById(R.id.commentPost);
        recyclerView = view.findViewById(R.id.recycleView);

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

        mPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Comment = mComment.getText().toString();
                if (TextUtils.isEmpty(Comment)){
                    mComment.setError("Please write comment");
                }else {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("Comment",Comment);
                    map.put("UserId",firebaseAuth.getCurrentUser().getUid());
                    map.put("TimeStamp",System.currentTimeMillis());

                    firebaseFirestore.collection("Posts").document(PostId)
                            .collection("Comments").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()){
                                mComment.setText("");
                            }
                        }
                    });
                }
            }
        });

        mPetName.setText(PetName);
        mAge.setText(Age + " ," + Sex);
        mAbout.setText(About);

        firebaseFirestore.collection("Posts").document(PostId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            String ImgUrl = task.getResult().getString("PetImgUrl");
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
                            mLike.playAnimation();
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
                            mLike.playAnimation();
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

        postData = new ArrayList<>();
        postAdapter = new CommentAdapter(postData);

//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2, GridLayoutManager.HORIZONTAL,false));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,new RecyclerView.State(), postAdapter.getItemCount());
        recyclerView.setAdapter(postAdapter);

        firebaseFirestore.collection("Posts").document(PostId)
                .collection("Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (DocumentChange doc : value.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){
                        String PostId = doc.getDocument().getId();
                        PostData mPostData = doc.getDocument().toObject(PostData.class).withId(PostId);
                        postData.add(mPostData);
                        postAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        return view;
    }
}