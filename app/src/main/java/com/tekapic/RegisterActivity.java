package com.tekapic;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;
    private boolean userRegisteredSuccessfully;
    private DatabaseReference mUsersDB;


    public void register(View view) {

        String email = mEmailEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();


        if(TextUtils.isEmpty(email)) {
            showAlertDialog("Error", "Email cannot be empty.");
        }
        else if(TextUtils.isEmpty(password)) {
            showAlertDialog("Error", "Password cannot be empty.");
        }
        else {
            //sign up with firebase
            mDialog.setMessage("Please wait...");
            mDialog.show();
            mDialog.setCancelable(false);

            registerUserToFirebase(email, password);
        }

    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(userRegisteredSuccessfully) {
                    mAuth.signOut();
                    finish();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    userRegisteredSuccessfully = false;
                }
            }
        });


        builder.create().show();
    }

    private void registerUserToFirebase(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
               .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {

                       mDialog.dismiss();

                       if(!task.isSuccessful()) {
                           //error registering users
                           Log.i("info", "error registering users");
                           Log.i("info", task.getException().getMessage());

                           showAlertDialog("Error", task.getException().getMessage());
//
                       }
                       else {
                           //success
                            //take the user to LoginActivity
                           final FirebaseUser currentUser = task.getResult().getUser();

                           UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                   .build();

                           currentUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   userRegisteredSuccessfully = true;

                                   com.tekapic.model.User newUser = new com.tekapic.model.User(currentUser.getEmail(), currentUser.getUid());
                                   mUsersDB.child(currentUser.getUid()).setValue(newUser);

                                   showAlertDialog("Affirmation!", "You have successfully registered.");

                               }
                           });

                       }
                   }
               });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logInMenu:
                finish();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailEditText = findViewById(R.id.emailEditTextRegister);
        mPasswordEditText = findViewById(R.id.passwordEditTextRegister);

        mAuth = FirebaseAuth.getInstance();
        mUsersDB = FirebaseDatabase.getInstance().getReference().child("Users");

        mDialog = new ProgressDialog(this);

        userRegisteredSuccessfully = false;
    }
}
