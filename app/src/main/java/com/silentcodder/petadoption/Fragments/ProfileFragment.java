package com.silentcodder.petadoption.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.silentcodder.petadoption.Adapter.PostAdapter;
import com.silentcodder.petadoption.Adapter.ProfilePostAdapter;
import com.silentcodder.petadoption.Model.PostData;
import com.silentcodder.petadoption.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    TextView mUserName,mMobileNumber;
    CircleImageView mProfileImg;
    String UserId;
    Button mBtnEditProfile;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    RecyclerView recyclerView;
    TextView userName;

    List<PostData> postData;
    ProfilePostAdapter postAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mUserName = view.findViewById(R.id.name);
        mMobileNumber = view.findViewById(R.id.mobileNumber);
        mProfileImg = view.findViewById(R.id.profile);
        mBtnEditProfile = view.findViewById(R.id.btnEditProfile);
        recyclerView = view.findViewById(R.id.recycleView);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();

        mBtnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new EditProfileFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();
            }
        });

        firebaseFirestore.collection("Users").document(UserId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            String Name = task.getResult().getString("UserName");
                            String MobileNumber = task.getResult().getString("MobileNumber");
                            String ProfileUrl = task.getResult().getString("ProfileImgUrl");

                            if (!TextUtils.isEmpty(ProfileUrl)){
                                Picasso.get().load(ProfileUrl).into(mProfileImg);
                            }

                            mUserName.setText(Name);
                            mMobileNumber.setText(MobileNumber);
                        }
                    }
                });

        postData = new ArrayList<>();
        postAdapter = new ProfilePostAdapter(postData);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,new RecyclerView.State(), postAdapter.getItemCount());
        recyclerView.setAdapter(postAdapter);

        CollectionReference ref = firebaseFirestore.collection("Posts");

        firebaseFirestore.collection("Posts").whereEqualTo("UserId",UserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
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