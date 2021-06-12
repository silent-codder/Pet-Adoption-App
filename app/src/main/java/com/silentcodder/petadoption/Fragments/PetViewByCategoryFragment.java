package com.silentcodder.petadoption.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.silentcodder.petadoption.Adapter.PostAdapter;
import com.silentcodder.petadoption.Model.PostData;
import com.silentcodder.petadoption.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PetViewByCategoryFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String UserId,Category;
    TextView mBtnBack;

    RecyclerView recyclerView;

    List<PostData> postData;
    PostAdapter postAdapter;


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pet_view_by_category, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycleView);
        UserId = firebaseAuth.getCurrentUser().getUid();
        mBtnBack = view.findViewById(R.id.btnBack);

        Bundle bundle = this.getArguments();
        if (bundle!=null){
            Category = bundle.getString("Category");
            mBtnBack.setText(Category + " Category");
            loadMorePost(Category);
        }

        return view;
    }

    private void loadMorePost(String category) {
        postData = new ArrayList<>();
        postAdapter = new PostAdapter(postData);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,new RecyclerView.State(), postAdapter.getItemCount());
        recyclerView.setAdapter(postAdapter);

        firebaseFirestore.collection("Posts").whereEqualTo("Category",category)
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