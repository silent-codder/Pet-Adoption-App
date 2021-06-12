package com.silentcodder.petadoption.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.silentcodder.petadoption.Adapter.PostAdapter;
import com.silentcodder.petadoption.Model.PostData;
import com.silentcodder.petadoption.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String UserId;

    RecyclerView recyclerView;

    List<PostData>postData;
    PostAdapter postAdapter;

    RelativeLayout mDog,mCat,mFish,mBird;
    TextView mDogCount,mCatCount,mFishCount,mBirdCount;
    EditText mSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycleView);
        UserId = firebaseAuth.getCurrentUser().getUid();

        mDog = view.findViewById(R.id.dog);
        mCat = view.findViewById(R.id.cat);
        mFish = view.findViewById(R.id.fish);
        mBird= view.findViewById(R.id.bird);
        mDogCount = view.findViewById(R.id.dogCount);
        mCatCount = view.findViewById(R.id.catCount);
        mFishCount = view.findViewById(R.id.fishCount);
        mBirdCount = view.findViewById(R.id.birdCount);
        mSearch = view.findViewById(R.id.search);

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SearchFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();
            }
        });

        loadMorePost();

        CategoryPetCount();

        mDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new PetViewByCategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Category","Dog");
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();
            }
        });

        mCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new PetViewByCategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Category","Cat");
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();
            }
        });

        mFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new PetViewByCategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Category","Fish");
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();
            }
        });

        mBird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new PetViewByCategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Category","Bird");
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();
            }
        });

        return view;
    }

    private void CategoryPetCount() {
        firebaseFirestore.collection("Posts").whereEqualTo("Category","Dog")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        int v = value.size();
                        mDogCount.setText("Total Of " + String.valueOf(v));
                    }
                });
        firebaseFirestore.collection("Posts").whereEqualTo("Category","Cat")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        int v = value.size();
                        mCatCount.setText("Total Of " + String.valueOf(v));
                    }
                });
        firebaseFirestore.collection("Posts").whereEqualTo("Category","Fish")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        int v = value.size();
                        mFishCount.setText("Total Of " + String.valueOf(v));
                    }
                });
        firebaseFirestore.collection("Posts").whereEqualTo("Category","Bird")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        int v = value.size();
                        mBirdCount.setText("Total Of " + String.valueOf(v));
                    }
                });
    }

    private void loadMorePost() {
        postData = new ArrayList<>();
        postAdapter = new PostAdapter(postData);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,new RecyclerView.State(), postAdapter.getItemCount());
        recyclerView.setAdapter(postAdapter);

        firebaseFirestore.collection("Posts").orderBy("TimeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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
    }
}