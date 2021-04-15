package com.silentcodder.petadoption.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.silentcodder.petadoption.Fragments.ChatFragment;
import com.silentcodder.petadoption.Model.ChatData;
import com.silentcodder.petadoption.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder>{

    List<ChatData> chatData;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String ReceiverId,senderId,UserName,ProfileUrl,ChatId;
    Context context;

    public ChatListAdapter(List<ChatData> chatData) {
        this.chatData = chatData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_view, parent, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReceiverId = chatData.get(position).getReceiverId();
        senderId =  chatData.get(position).getSenderId();
        ChatId = chatData.get(position).getChatId();

        //Toast.makeText(context, "ChatID : " + ChatId, Toast.LENGTH_SHORT).show();


        if (ReceiverId.equals(firebaseAuth.getCurrentUser().getUid())){
             ReceiverId = chatData.get(position).getSenderId();
            firebaseFirestore.collection("Users").document(ReceiverId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            ProfileUrl = task.getResult().getString("ProfileImgUrl");
                            UserName = task.getResult().getString("UserName");

                            holder.mUserName.setText(UserName);

                            if (!TextUtils.isEmpty(ProfileUrl)){
                                Picasso.get().load(ProfileUrl).into(holder.mProfile);
                            }
                        }
                    });
        }

        firebaseFirestore.collection("Users").document(ReceiverId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        ProfileUrl = task.getResult().getString("ProfileImgUrl");
                        UserName = task.getResult().getString("UserName");

                        holder.mUserName.setText(UserName);

                        if (!TextUtils.isEmpty(ProfileUrl)){
                            Picasso.get().load(ProfileUrl).into(holder.mProfile);
                        }
                    }
                });


        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Bundle bundle = new Bundle();
                bundle.putString("ReceiverId",ReceiverId);
                bundle.putString("UserName",UserName);
                bundle.putString("ProfileUrl",ProfileUrl);
                bundle.putString("ChatId",ChatId);
                Fragment fragment = new ChatFragment();
                fragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mUserName;
        CircleImageView mProfile;
        RelativeLayout mRelativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mProfile = itemView.findViewById(R.id.profile);
            mUserName = itemView.findViewById(R.id.userName);
            mRelativeLayout = itemView.findViewById(R.id.relativeLayout);

        }
    }
}
