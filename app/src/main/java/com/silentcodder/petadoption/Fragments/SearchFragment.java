package com.silentcodder.petadoption.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.silentcodder.petadoption.Adapter.PostAdapter;
import com.silentcodder.petadoption.Adapter.SearchAdapter;
import com.silentcodder.petadoption.Model.PostData;
import com.silentcodder.petadoption.Model.UserData;
import com.silentcodder.petadoption.R;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String UserId;
    EditText mSearch;
    TextView textView;

    RecyclerView recyclerView;


    List<PostData>postData;
    PostAdapter postAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycleView);
        mSearch = view.findViewById(R.id.btnSearch);
        UserId = firebaseAuth.getCurrentUser().getUid();
        textView = view.findViewById(R.id.notFoundTxt);

        allData();

        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()<1)
                {
                    clear();
                }
                else {
                    LottieAnimationView lottieAnimationView = view.findViewById(R.id.lottie);
                    TextView textView = view.findViewById(R.id.text);
                    lottieAnimationView.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    loadData(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }

    private void allData(){
        postData = new ArrayList<>();
        postAdapter = new PostAdapter(postData);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,new RecyclerView.State(), postAdapter.getItemCount());
        recyclerView.setAdapter(postAdapter);

        firebaseFirestore.collection("Posts")
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

    private void loadData(String s) {
        postData = new ArrayList<>();
        postAdapter = new PostAdapter(postData);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,new RecyclerView.State(), postAdapter.getItemCount());
        recyclerView.setAdapter(postAdapter);

        firebaseFirestore.collection("Posts").orderBy("PetName").startAt(s).endAt(s+"\uf9ff" )
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

    public void clear() {
        int size = postData.size();
        postData.clear();
        postAdapter.notifyItemRangeRemoved(0,size);
    }



}