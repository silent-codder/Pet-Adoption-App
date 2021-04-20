package com.silentcodder.petadoption.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.silentcodder.petadoption.Adapter.NotificationAdapter;
import com.silentcodder.petadoption.Adapter.PostAdapter;
import com.silentcodder.petadoption.Model.NotificationData;
import com.silentcodder.petadoption.Model.PostData;
import com.silentcodder.petadoption.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String UserId;

    RecyclerView recyclerView;

    List<NotificationData> postData;
    NotificationAdapter postAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycleView);
        swipeRefreshLayout = view.findViewById(R.id.refresh);
        UserId = firebaseAuth.getCurrentUser().getUid();

        loadData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });


        return view;
    }

    private void loadData() {

        postData = new ArrayList<>();
        postAdapter = new NotificationAdapter(postData);

//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2, GridLayoutManager.HORIZONTAL,false));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,new RecyclerView.State(), postAdapter.getItemCount());
        recyclerView.setAdapter(postAdapter);

        firebaseFirestore.collection("Notification").document(UserId)
                .collection("Notification").orderBy("TimeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (!value.isEmpty()){
                            ImageView imageView = getView().findViewById(R.id.empty);
                            TextView textView = getView().findViewById(R.id.textEmpty);
                            imageView.setVisibility(View.GONE);
                            textView.setVisibility(View.GONE);
                        }

                        for (DocumentChange doc : value.getDocumentChanges()){
                            if (doc.getType() == DocumentChange.Type.ADDED){
                                NotificationData mPostData = doc.getDocument().toObject(NotificationData.class);
                                postData.add(mPostData);
                                postAdapter.notifyDataSetChanged();

                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    }
                });
    }


}