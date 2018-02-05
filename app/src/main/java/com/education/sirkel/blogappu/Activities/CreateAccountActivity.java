package com.education.sirkel.blogappu.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.education.sirkel.blogappu.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private Button createAccountBtn;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private StorageReference mFirebaseStorage;
    private ProgressDialog mProgressdialog;
    private ImageButton profileImg;
    private Uri resultUri = null;
    private final static int GALERY_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        setUI();

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create account
                createAccount();
            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galery = new Intent();
                galery.setAction(Intent.ACTION_GET_CONTENT);
                galery.setType("image/*");
                startActivityForResult(galery,GALERY_CODE);
            }
        });

    }

    private void createAccount() {
        final String fName = firstName.getText().toString().trim();
        final String lName = lastName.getText().toString().trim();
        final String em = email.getText().toString().trim();
        final String pw = password.getText().toString().trim();

        if (!TextUtils.isEmpty(fName) && !TextUtils.isEmpty(lName)
                && !TextUtils.isEmpty(em) && !TextUtils.isEmpty(pw)){

            mProgressdialog.setMessage("Creating Account");
            mProgressdialog.show();

            mAuth.createUserWithEmailAndPassword(em,pw).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    if (authResult != null){

                        StorageReference imagePath = mFirebaseStorage.child(resultUri.getLastPathSegment());
                        imagePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                String userid = mAuth.getCurrentUser().getUid();
                                DatabaseReference currentDb = mDatabaseReference.child(userid);
                                currentDb.child("firstname").setValue(fName);
                                currentDb.child("lastname").setValue(lName);
                                currentDb.child("email").setValue(em);
                                currentDb.child("pass").setValue(pw);
                                currentDb.child("image").setValue(resultUri.toString());

                                mProgressdialog.dismiss();

                                //Go to postList

                                Intent intent = new Intent(CreateAccountActivity.this, PostListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });

                    }
                }
            });

        }
    }

    private void setUI (){
        firstName = (EditText) findViewById(R.id.firstNameReg);
        lastName = (EditText) findViewById(R.id.lastNameReg);
        email = (EditText) findViewById(R.id.emailReg);
        password = (EditText) findViewById(R.id.passReg);
        createAccountBtn = (Button) findViewById(R.id.createAccountReg);
        profileImg = (ImageButton) findViewById(R.id.profilePict);
        mProgressdialog = new ProgressDialog(this);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("MUsers");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirebaseStorage = FirebaseStorage.getInstance().getReference().child("MBlog_Profile_Pics");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALERY_CODE && resultCode == RESULT_OK){
            // ref: https://github.com/ArthurHub/Android-Image-Cropper
            //Crop Image
            Uri mImageUri = data.getData();
            CropImage.activity(mImageUri)
                    .setAspectRatio(1,1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                //tambah bagian ini setelah kopas di gugel
                profileImg.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
