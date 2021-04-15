package com.silentcodder.petadoption.Adapter;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.silentcodder.petadoption.Fragments.ProfileViewFragment;
import com.silentcodder.petadoption.Model.UserData;
import com.silentcodder.petadoption.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{

    List<UserData> userData;

    public SearchAdapter(List<UserData> userData) {
        this.userData = userData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String UserName = userData.get(position).getUserName();
        String ProfileUrl = userData.get(position).getProfileImgUrl();
        String UserId = userData.get(position).getUserId();

        if (!TextUtils.isEmpty(ProfileUrl)){
            Picasso.get().load(ProfileUrl).into(holder.mProfileImg);
        }

        holder.mUserName.setText(UserName);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment fragment = new ProfileViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("UserId",UserId);
                fragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null)
                        .commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return userData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mUserName;
        CircleImageView mProfileImg;
        RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mUserName = itemView.findViewById(R.id.userName);
            mProfileImg = itemView.findViewById(R.id.profile);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}
