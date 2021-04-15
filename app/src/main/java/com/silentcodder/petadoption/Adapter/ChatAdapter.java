package com.silentcodder.petadoption.Adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.silentcodder.petadoption.Model.ChatData;
import com.silentcodder.petadoption.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{

    private static final int MSG_LEFT = 0;
    private static final int MSG_RIGHT = 1;

    List<ChatData> chatData;
    FirebaseAuth firebaseAuth;
    String UserId,SenderId;
    public ChatAdapter(List<ChatData> chatData) {
        this.chatData = chatData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        firebaseAuth = FirebaseAuth.getInstance();
        if (viewType == MSG_RIGHT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_view_right,parent,false);
            return new ViewHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_view_left,parent,false);
            return new ViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserId = firebaseAuth.getCurrentUser().getUid();
        SenderId = chatData.get(position).getSenderId();
        long timeStamp = chatData.get(position).getTimeStamp();
        String Msg = chatData.get(position).getMsg();
        if (!TextUtils.isEmpty(Msg)){
            Date d = new Date(timeStamp);

            DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
            String currentDate = dateFormat.format(d.getTime());
            DateFormat dateFormat1 = new SimpleDateFormat("MMM dd");
            String Date = dateFormat1.format(d.getTime());

            holder.mTime.setText(currentDate + "\n" + Date);
            holder.mShowMsg.setText(Msg);
        }
    }

    @Override
    public int getItemCount() {
        return chatData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mShowMsg;
        TextView mTime;
        CircleImageView mProfile;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mShowMsg = itemView.findViewById(R.id.showMsg);
            mProfile = itemView.findViewById(R.id.profile);
            mTime = itemView.findViewById(R.id.time);
        }
    }

    @Override
    public int getItemViewType(int position) {
            firebaseAuth = FirebaseAuth.getInstance();
            String senderId = chatData.get(position).getSenderId();
            String UserId = firebaseAuth.getCurrentUser().getUid();
            if (!TextUtils.isEmpty(senderId)){
                if (senderId.equals(UserId)){
                    return MSG_RIGHT;
                }else {
                    return MSG_LEFT;
                }
            }

       return MSG_LEFT;
    }
}

