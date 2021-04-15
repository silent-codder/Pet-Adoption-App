package com.silentcodder.petadoption.Adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.silentcodder.petadoption.Model.NotificationData;
import com.silentcodder.petadoption.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    List<NotificationData> notificationData;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String UserId;

    public NotificationAdapter(List<NotificationData> notificationData) {
        this.notificationData = notificationData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_like_view, parent, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String userId = notificationData.get(position).getUserId();
        String postId = notificationData.get(position).getPostId();

        firebaseFirestore.collection("Posts").document(postId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                String ImgUrl = task.getResult().getString("PetImgUrl");
                                if (!TextUtils.isEmpty(ImgUrl)){
                                    Picasso.get().load(ImgUrl).into(holder.mPostImg);
                                }
                            }
                    }
                });

        firebaseFirestore.collection("Users").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            String UserName = task.getResult().getString("UserName");
                            String ProfileImgUrl = task.getResult().getString("ProfileImgUrl");

                            if (!TextUtils.isEmpty(ProfileImgUrl)){
                                Picasso.get().load(ProfileImgUrl).into(holder.mProfile);
                            }
                            holder.mMsg.setText(UserName + " like your post");
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return notificationData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mMsg;
        CircleImageView mProfile;
        ImageView mPostImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mProfile = itemView.findViewById(R.id.profile);
            mMsg = itemView.findViewById(R.id.msg);
            mPostImg = itemView.findViewById(R.id.postImg);
        }
    }
}
