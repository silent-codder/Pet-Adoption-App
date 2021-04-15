package com.silentcodder.petadoption.Adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.silentcodder.petadoption.Model.PostData;
import com.silentcodder.petadoption.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    List<PostData> postData;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    public CommentAdapter(List<PostData> postData) {
        this.postData = postData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_view, parent, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String UserId = postData.get(position).getUserId();
        String Comment = postData.get(position).getComment();

        holder.mComment.setText(Comment);

        firebaseFirestore.collection("Users").document(UserId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String UserName = task.getResult().getString("UserName");
                    String ProfileUrl = task.getResult().getString("ProfileImgUrl");

                    holder.mUserName.setText(UserName);
                    if (!TextUtils.isEmpty(ProfileUrl)){
                        Picasso.get().load(ProfileUrl).into(holder.mProfile);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return postData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView mProfile;
        TextView mComment,mUserName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mProfile = itemView.findViewById(R.id.profile);
            mComment = itemView.findViewById(R.id.comment);
            mUserName = itemView.findViewById(R.id.userName);
        }
    }
}
