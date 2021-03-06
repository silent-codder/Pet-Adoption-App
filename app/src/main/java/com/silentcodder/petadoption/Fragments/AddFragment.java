package com.silentcodder.petadoption.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.silentcodder.petadoption.Model.ChatData;
import com.silentcodder.petadoption.Notification.APIService;
import com.silentcodder.petadoption.Notification.Client;
import com.silentcodder.petadoption.Notification.Data;
import com.silentcodder.petadoption.Notification.NotificationSender;
import com.silentcodder.petadoption.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class AddFragment extends Fragment {

    TextView mBtnAddImg;
    ImageView mImage;
    EditText mPetName,mAge,mAbout;
    Button mBtnUpload;
    RadioButton mBoy,mGirl,mDog,mCat,mBird;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String UserId,Sex,Category,docId;

    ProgressDialog progressDialog;
    ProgressBar progressBar;
    Uri profileImgUri;
    StorageReference storageReference;

    String fcmUrl = "https://fcm.googleapis.com/";
    List<String > token;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        mBtnAddImg = view.findViewById(R.id.addImg);
        mImage = view.findViewById(R.id.image);
        mPetName = view.findViewById(R.id.petName);
        progressBar = view.findViewById(R.id.loader);
        mAge = view.findViewById(R.id.age);
        mDog = view.findViewById(R.id.dog);
        mCat = view.findViewById(R.id.cat);
        mBird = view.findViewById(R.id.bird);
        mAbout = view.findViewById(R.id.about);
        mBtnUpload = view.findViewById(R.id.btnUpload);
        mBoy = view.findViewById(R.id.boy);
        mGirl = view.findViewById(R.id.girl);

        token = new ArrayList<String>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(getContext());
        UserId = firebaseAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        mBtnAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImg();
            }
        });

        mBoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sex = "Boy";
            }
        });
        mGirl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sex = "Girl";
            }
        });

        mDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category = "Dog";
            }
        });
        mCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category = "Cat";
            }
        });
        mBird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category = "Bird";
            }
        });

        mBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PetName = mPetName.getText().toString();
                String Age = mAge.getText().toString();
                String About = mAbout.getText().toString();

                if (TextUtils.isEmpty(PetName)){
                    mPetName.setError("");
                }else if (TextUtils.isEmpty(Age)){
                    mAge.setError("");
                }else if (TextUtils.isEmpty(Sex)){
                    Snackbar snackbar = Snackbar.make(getView(), "Select Pet Sex",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }else if (TextUtils.isEmpty(Category)){
                    Snackbar snackbar = Snackbar.make(getView(), "Select Pet Category",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }else {
                    mBtnUpload.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("PetName",PetName);
                    map.put("Age",Age);
                    map.put("About",About);
                    map.put("UserId",UserId);
                    map.put("TimeStamp",System.currentTimeMillis());
                    map.put("Sex", Sex);
                    map.put("Category",Category);

                    docId = firebaseFirestore.collection("Posts").document().getId();

                    firebaseFirestore.collection("Posts")
                            .document(docId).set(map);
                    AddImg(docId);
                    notification();
                }

            }
        });

        return view;
    }

    private void notification() {
        firebaseFirestore.collection("Tokens").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange doc : value.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){
//                        token.add(doc.getDocument().getString("token"));
                          String token = doc.getDocument().getString("token");
                          String Title = "New Post on 'Pet Adoption'";
                          String Msg = "Tap to see";
                          sendNotification(token,Title,Msg);
                    }
                }
            }
        });
    }

    private void UploadImg() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setOutputCompressQuality(70)
                .start(getActivity(),this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profileImgUri = result.getUri();
                mImage.setVisibility(View.VISIBLE);
                mImage.setImageURI(profileImgUri);
                mBtnAddImg.setVisibility(View.GONE);
                progressDialog.dismiss();
//                AddImg();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void AddImg(String Id) {

        StorageReference profileImgPath = storageReference.child("PetImages").child(System.currentTimeMillis() + ".jpg");

        profileImgPath.putFile(profileImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profileImgPath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        String ProfileUri = task.getResult().toString();
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("PetImgUrl" , ProfileUri);

                        firebaseFirestore.collection("Posts").document(Id).update(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Upload Successfully..", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        Fragment fragment = new HomeFragment();
                                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Storage error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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