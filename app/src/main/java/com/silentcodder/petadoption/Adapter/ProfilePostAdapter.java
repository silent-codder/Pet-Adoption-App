package com.silentcodder.petadoption.Adapter;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.silentcodder.petadoption.Fragments.PostViewFragment;
import com.silentcodder.petadoption.Fragments.ProfilePostViewFragment;
import com.silentcodder.petadoption.Model.PostData;
import com.silentcodder.petadoption.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.ContentValues.TAG;

public class ProfilePostAdapter extends RecyclerView.Adapter<ProfilePostAdapter.ViewHolder>{

    List<PostData> postData;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    Context context;
    AlertDialog.Builder builder;

    public ProfilePostAdapter(List<PostData> postData) {
        this.postData = postData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_view_profile,parent,false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        context = parent.getContext();
        builder = new AlertDialog.Builder(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String ImgUrl = postData.get(position).getPetImgUrl();
        String PostId = postData.get(position).PostId;
        String UserId = postData.get(position).getUserId();
        String PetName = postData.get(position).getPetName();
        String Age = postData.get(position).getAge();
        String Sex = postData.get(position).getSex();
        String About = postData.get(position).getAbout();
        String PostUserId = postData.get(position).getUserId();

        Picasso.get().load(ImgUrl).into(holder.mPostImg);

        if (UserId.equals(firebaseAuth.getCurrentUser().getUid())){
            holder.mBtnDelete.setVisibility(View.VISIBLE);
        }

        firebaseFirestore.collection("Posts").document(PostId).collection("Likes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        String  count = String.valueOf(value.size());
                        holder.mLikeCount.setText(count);
                    }
                });

        holder.mPostImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("PetName",PetName);
                bundle.putString("Age",Age);
                bundle.putString("Sex",Sex);
                bundle.putString("About",About);
                bundle.putString("PostId",PostId);
                bundle.putString("ImgUrl",ImgUrl);
                bundle.putString("PostUserId",PostUserId);

                Log.d(TAG, "onClick Img Url : " + PostId);

                Fragment fragment = new ProfilePostViewFragment();
                fragment.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();
            }
        });

        holder.mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Uncomment the below code to Set the message and title from the strings.xml file
                builder.setMessage("Do you want to delete post ?") .setTitle("Delete Post");

                //Setting message manually and performing action on button click
                builder.setMessage("Do you want to delete this post ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               firebaseFirestore.collection("Posts").document(PostId).delete();
                                postData.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,postData.size());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();

                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mPostImg,mBtnDelete;
        TextView mLikeCount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mPostImg = itemView.findViewById(R.id.postImg);
            mLikeCount = itemView.findViewById(R.id.likeCount);
            mBtnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
