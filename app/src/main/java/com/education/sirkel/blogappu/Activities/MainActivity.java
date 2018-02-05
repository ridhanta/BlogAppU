package com.education.sirkel.blogappu.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.education.sirkel.blogappu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private Button loginButton;
    private Button createButton;
    private EditText emailField;
    private EditText passField;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpUI();


    }

    public void setUpUI(){

        loginButton = (Button) findViewById(R.id.loginButton);
        createButton = (Button) findViewById(R.id.createAccBtn);
        emailField = (EditText) findViewById(R.id.loginEmail);
        passField = (EditText) findViewById(R.id.loginPass);

        loginButton.setOnClickListener(this);
        createButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                mUser = firebaseAuth.getCurrentUser();

                if (mUser != null){
                    Toast.makeText(MainActivity.this,"Signed in", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, PostListActivity.class));
                    finish();
                }else {
                    Toast.makeText(MainActivity.this,"Not Signed in", Toast.LENGTH_LONG).show();
                }

            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.loginButton:
                if (!TextUtils.isEmpty(emailField.getText().toString()) &&
                        !TextUtils.isEmpty(passField.getText().toString())){

                    String email = emailField.getText().toString();
                    String pass = passField.getText().toString();

                    login(email,pass);

                }else {

                }

            break;

            case  R.id.createAccBtn:

                startActivity(new Intent(MainActivity.this, CreateAccountActivity.class));

            break;
        }
    }

    private void login(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            //Success
                            Toast.makeText(MainActivity.this,"Success login", Toast.LENGTH_LONG)
                                    .show();
                            startActivity(new Intent(MainActivity.this, PostListActivity.class));
                            finish();
                        }else {
                            //Failed
                            Toast.makeText(MainActivity.this,"Failed", Toast.LENGTH_LONG)
                                    .show();
                        }

                    }
                });
    }
}
