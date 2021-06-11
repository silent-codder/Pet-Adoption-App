package com.silentcodder.petadoption.Fragments;

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
import android.widget.ImageView;
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
    TextView userName;
    ImageView mChat;
    SwipeRefreshLayout swipeRefreshLayout;

    List<PostData>postData;
    PostAdapter postAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycleView);
        UserId = firebaseAuth.getCurrentUser().getUid();




        loadMorePost();

        return view;
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