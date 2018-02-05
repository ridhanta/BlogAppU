package com.education.sirkel.blogappu.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.education.sirkel.blogappu.Data.BlogRecyclerAdapter;
import com.education.sirkel.blogappu.Model.Blog;
import com.education.sirkel.blogappu.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private ImageButton mPostImage;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mSubmitButton;
    private DatabaseReference mPostDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private StorageReference mStorage;
    private ProgressDialog mProgress;
    private Uri mImageUri;
    private static final int GALERY_CODE = 1;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        setUpUI();

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Posting to our database
                startPosting();
            }
        });

        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go to Library Image
                Intent galeryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galeryIntent.setType("image/*");
                startActivityForResult(galeryIntent,GALERY_CODE);
                //Hasil di ambil di onActivityResult


            }
        });

    }


    //Hasil dari galery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALERY_CODE && resultCode == RESULT_OK){
            mImageUri = data.getData();
            mPostImage.setImageURI(mImageUri);
        }
    }

    private void setUpUI(){

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userId = mUser.getUid();
        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("MBlog").child(userId);
        mStorage = FirebaseStorage.getInstance().getReference();

        mProgress = new ProgressDialog(this);
        mPostImage = (ImageButton) findViewById(R.id.imageButton);
        mPostTitle = (EditText) findViewById(R.id.postTitleEt);
        mPostDesc = (EditText) findViewById(R.id.postDescEt);
        mSubmitButton = (Button) findViewById(R.id.postButton);


    }

    private void startPosting(){
        mProgress.setMessage("Posting to Blog");
        mProgress.show();

        final String titleVal = mPostTitle.getText().toString().trim();
        final String descVal = mPostDesc.getText().toString().trim();

        if (!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal) && mImageUri != null){
            //start Uploading...

//            Blog blog = new Blog("Title", "Desc", "imageUrl", "timeStamp", "userid");
//
//            mPostDatabase.setValue(blog).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    Toast.makeText(getApplicationContext(),"Item Added", Toast.LENGTH_LONG)
//                            .show();
//                    mProgress.dismiss();
//                }
//            });

            //startuploading
            //mImageUri.getLastPathSegment == /image/myphoto.jpeg
            StorageReference filepath = mStorage.child("MBlog_images").
                    child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    DatabaseReference newPost = mPostDatabase.push();

                    //cara memasukan ke database Firebase
                    Map<String, String> dataToSave = new HashMap<>();
                    dataToSave.put("title",titleVal); //"title" harus sama dengan yg di Object tulisannya.
                    dataToSave.put("desc",descVal);
                    dataToSave.put("image", downloadUri.toString());
                    dataToSave.put("timestamp", String.
                            valueOf(java.lang.System.currentTimeMillis())); // Untuk kasih tau tanggal berapa
                    dataToSave.put("userid", mUser.getUid());

                    newPost.setValue(dataToSave);

                    //Old way
                    /**
                     * newPost.child("title").setvalue(titleval);
                     * newPost....
                     */
                    mProgress.dismiss();

                    startActivity(new Intent(AddPostActivity.this, PostListActivity.class));
                    finish();

                }
            });

        }
    }
}
