package com.silentcodder.petadoption.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.silentcodder.petadoption.Adapter.ChatListAdapter;
import com.silentcodder.petadoption.Adapter.PostAdapter;
import com.silentcodder.petadoption.Model.ChatData;
import com.silentcodder.petadoption.Model.PostData;
import com.silentcodder.petadoption.R;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {

    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String UserId;

    List<ChatData> postData;
    ChatListAdapter postAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        recyclerView = view.findViewById(R.id.recycleView);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        UserId = firebaseAuth.getCurrentUser().getUid();


        postData = new ArrayList<>();
        postAdapter = new ChatListAdapter(postData);

//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2, GridLayoutManager.HORIZONTAL,false));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,new RecyclerView.State(), postAdapter.getItemCount());
        recyclerView.setAdapter(postAdapter);

        firebaseFirestore.collection("Users").document(UserId).collection("ChatUser")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentChange doc : value.getDocumentChanges()){
                            if (doc.getType() == DocumentChange.Type.ADDED){
                                ChatData mPostData = doc.getDocument().toObject(ChatData.class);
                                postData.add(mPostData);
                                postAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

        return view;
    }
}