package com.silentcodder.petadoption.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.silentcodder.petadoption.Notification.APIService;
import com.silentcodder.petadoption.Notification.Client;
import com.silentcodder.petadoption.Notification.Data;
import com.silentcodder.petadoption.Notification.NotificationSender;
import com.silentcodder.petadoption.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class CommentFragment extends Fragment {

    EditText mComment;
    ImageView mPostComment;
    RecyclerView recyclerView;

    String fcmUrl = "https://fcm.googleapis.com/",CurrentUserName,PostId,PostUserId;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    List<PostData> postData;
    CommentAdapter postAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        Bundle bundle = this.getArguments();
        if (bundle!=null){
            PostId = bundle.getString("PostId");
            PostUserId = bundle.getString("PostUserId");
        }

        mComment = view.findViewById(R.id.comment);
        mPostComment = view.findViewById(R.id.commentPost);
        recyclerView = view.findViewById(R.id.recycleView);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    CurrentUserName = task.getResult().getString("UserName");
                }
            }
        });

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
                                firebaseFirestore.collection("Tokens").document(PostUserId).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    String token = task.getResult().getString("token");
                                                    String Title = CurrentUserName + " comment on your post";
                                                    String Msg = "Comment : " + Comment ;
                                                    sendNotification(token,Title,Msg);
                                                }
                                            }
                                        });

                                mComment.setText("");
                            }
                        }
                    });
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

                if (!value.isEmpty()){
                    ImageView imageView = view.findViewById(R.id.empty);
                    TextView textView = view.findViewById(R.id.textEmpty);
                    imageView.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                }

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
}