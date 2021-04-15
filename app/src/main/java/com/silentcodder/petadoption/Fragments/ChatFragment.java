package com.silentcodder.petadoption.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.silentcodder.petadoption.Adapter.ChatAdapter;
import com.silentcodder.petadoption.Adapter.ChatListAdapter;
import com.silentcodder.petadoption.Adapter.PostAdapter;
import com.silentcodder.petadoption.Model.ChatData;
import com.silentcodder.petadoption.Model.PostData;
import com.silentcodder.petadoption.Notification.APIService;
import com.silentcodder.petadoption.Notification.Client;
import com.silentcodder.petadoption.Notification.Data;
import com.silentcodder.petadoption.Notification.NotificationSender;
import com.silentcodder.petadoption.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    EditText mMsg;
    ImageView mBtnSend,mBtnBack,mBtnMenu;
    CircleImageView mProfile;
    TextView mUserName;

    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String UserId,UserName,ProfileUrl,PostId,ReceiverId,ReceiveId,ChatId;

    List<ChatData> chatData;
    ChatAdapter chatAdapter;
    String fcmUrl = "https://fcm.googleapis.com/";

    SwipeRefreshLayout swipeRefreshLayout;
    private String CurrentUserName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        mMsg = view.findViewById(R.id.msg);
        mBtnSend = view.findViewById(R.id.btnSend);
        mBtnBack = view.findViewById(R.id.back);
        mBtnMenu = view.findViewById(R.id.btnMenu);
        mProfile = view.findViewById(R.id.profile);
        mUserName = view.findViewById(R.id.userName);
        recyclerView = view.findViewById(R.id.recycleView);
        swipeRefreshLayout = view.findViewById(R.id.refresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });



        Bundle bundle = this.getArguments();
        if (bundle!=null){
            PostId = bundle.getString("PostId");
            UserName = bundle.getString("UserName");
            ProfileUrl = bundle.getString("ProfileUrl");
            ReceiveId = bundle.getString("ReceiverId");
            ChatId = bundle.getString("ChatId");

            mUserName.setText(UserName);
            if (!TextUtils.isEmpty(ProfileUrl)){
                Picasso.get().load(ProfileUrl).into(mProfile);
            }
        }



        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        UserId = firebaseAuth.getCurrentUser().getUid();

        if (!TextUtils.isEmpty(PostId)){
            firebaseFirestore.collection("Posts").document(PostId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                ReceiverId = task.getResult().getString("UserId");
                            }
                        }
                    });
        }


        firebaseFirestore.collection("Users").document(UserId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        CurrentUserName = task.getResult().getString("UserName");
                    }
                });

        mUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ProfileViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("UserId",UserId);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();
            }
        });

//        Toast.makeText(getContext(), "ChatId : " + ChatId, Toast.LENGTH_SHORT).show();
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Msg = mMsg.getText().toString();
                if (TextUtils.isEmpty(Msg)){
                    Toast.makeText(getContext(), "type msg...", Toast.LENGTH_SHORT).show();
                }else {
                   if (TextUtils.isEmpty(ReceiverId)){
                       ReceiverId = ReceiveId;
                   }



                    if (TextUtils.isEmpty(ChatId)){
                        ChatId = firebaseFirestore.collection("Chats").document().getId();
                    }

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("Msg",Msg);
                    map.put("SenderId",UserId);
                    map.put("ChatId",ChatId);
                    map.put("ReceiverId",ReceiverId);
                    map.put("TimeStamp",System.currentTimeMillis());

                    HashMap<String, Object> Receiver = new HashMap<>();
                    Receiver.put("ReceiverId",ReceiverId);
                    Receiver.put("SenderId",UserId);
                    Receiver.put("ChatId",ChatId);
                    Receiver.put("TimeStamp",System.currentTimeMillis());
                    HashMap<String, Object> Sender = new HashMap<>();
                    Sender.put("SenderId",UserId);
                    Sender.put("ChatId",ChatId);
                    Sender.put("ReceiverId",ReceiverId);
                    Sender.put("TimeStamp",System.currentTimeMillis());

                    firebaseFirestore.collection("Tokens").document(ReceiverId).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        String Token = task.getResult().getString("token");
                                        String title = CurrentUserName + " send message";
                                        String data = "Message : " + Msg;
                                        sendNotification(Token,title,data);
                                    }
                                }
                            });

//                    Toast.makeText(getContext(), "ChatID : " + ChatId, Toast.LENGTH_LONG).show();

                    firebaseFirestore.collection("Users").document(UserId).collection("ChatUser").document(ReceiverId).set(Receiver);
                    firebaseFirestore.collection("Users").document(ReceiverId).collection("ChatUser").document(UserId).set(Sender);

                    firebaseFirestore.collection("Chats").document(ChatId).collection("Msg")
                            .add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()){
                                mMsg.setText("");
                                loadData();
                            }
                        }
                    });
                }
            }
        });


        loadData();

        return view;
    }

    private void loadData() {
        swipeRefreshLayout.setRefreshing(false);
        chatData = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatData);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);

//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2, GridLayoutManager.HORIZONTAL,false));
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,new RecyclerView.State(), chatAdapter.getItemCount());
        recyclerView.setAdapter(chatAdapter);
        recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
        recyclerView.scrollToPosition(chatAdapter.getItemCount()-1);

        firebaseFirestore.collection("Chats").document(ChatId)
                .collection("Msg").orderBy("TimeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        for (DocumentChange doc : value.getDocumentChanges()){
                            if (doc.getType() == DocumentChange.Type.ADDED){
                                ChatData mChatData = doc.getDocument().toObject(ChatData.class);
                                chatData.add(mChatData);
                                chatAdapter.notifyDataSetChanged();
                            }
                        }
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
}