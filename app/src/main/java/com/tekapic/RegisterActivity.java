package com.tekapic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mUsernameEditText;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;
    private boolean userRegisteredSuccessfully;
    private DatabaseReference mUsersDB;
    private String buttonName = "Ok";



    private  boolean isValidEmailAddress(String email) {

        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);

        if(matcher.find() == false) {
            //Wrong email format
            return false;
        }

        return true;
    }

    private boolean isPasswordStrong(String password) {

        boolean hasLetter = false;
        boolean hasDigit = false;

            for (int i = 0; i < password.length(); i++) {
                char x = password.charAt(i);
                if (Character.isLetter(x)) {

                    hasLetter = true;
                }

                else if (Character.isDigit(x)) {

                    hasDigit = true;
                }

                // no need to check further, break the loop
                if(hasLetter && hasDigit){

                    break;
                }

            }
            if (hasLetter && hasDigit) {
//                System.out.println("STRONG");
                return true;
            } else {
//                System.out.println("NOT STRONG");
                return false;
            }
//        } else {
//            System.out.println("HAVE AT LEAST 8 CHARACTERS");
//        }

    }


    public void register(View view) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return;
        }

        final String email = mEmailEditText.getText().toString().trim();
        final String username = mUsernameEditText.getText().toString().trim();
        final String password = mPasswordEditText.getText().toString().trim();


        if(TextUtils.isEmpty(email)) {
            showAlertDialog("Error", "Email cannot be empty.");
            return;
        }

        if(TextUtils.isEmpty(username)) {
            showAlertDialog("Error", "Username cannot be empty.");
            return;
        }

        if(TextUtils.isEmpty(password)) {
            showAlertDialog("Error", "Password cannot be empty.");
            return;
        }

        if(isValidEmailAddress(email) == false) {
            showAlertDialog("Error", "Enter a correct Email Address.");
            return;
        }

        //check if the username ok

        if(username.length() < 2) {
            showAlertDialog("Error", "Username must be at least 2 characters long.");
            return;
        }

        if (username.matches("[0-9]+")) {
            showAlertDialog("Error", "Username cannot contain only numbers.");
            return;

        }
        else if (username.matches("[a-z]+")) {
            //good username
        }
        else {

//            if(Character.isDigit(username.charAt(0))) {
//                showAlertDialog("Error", "Username cannot start with a digit.");
//                return;
//
//            }
//            else {
//                //good username
//
//            }

        }

        //********************check here if username is not taken******************///
        mDialog.setMessage("Please wait...");
        mDialog.show();
        mDialog.setCancelable(false);

        mUsersDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isUsernametaken = false;

                Log.i("count", Long.toString(dataSnapshot.getChildrenCount()));



                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        String usnm = ds.child("username").getValue(String.class);



                        Log.i("username", usnm);

                            if(usnm.equals(username)) {
                                isUsernametaken = true;
                                break;
                            }



                    }

                mDialog.dismiss();


                if(isUsernametaken) {
                    showAlertDialog("Error", "Username is taken.");

                    return;
                }

                if(password.length() < 6) {
                    showAlertDialog("Error", "Password must be at least 6 characters.");
                    return;
                }
                if(isPasswordStrong(password) == false) {
                    showAlertDialog("Error", "Please choose a stronger password. try a mix of letters and digits.");
                    return;
                }




                //sign up with firebase
                mDialog.setMessage("Please wait...");
                mDialog.show();
                mDialog.setCancelable(false);

                registerUserToFirebase(email, password);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(!userRegisteredSuccessfully);

        if(title.isEmpty()) {
            buttonName = "Log In";
        }


        builder.setPositiveButton(buttonName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(userRegisteredSuccessfully) {
                    finish();
                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));

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

                                   String username = mUsernameEditText.getText().toString().trim();

                                   com.tekapic.model.User newUser = new com.tekapic.model.User(currentUser.getEmail(), username, currentUser.getUid(), "public", 0, false);
                                   mUsersDB.child(currentUser.getUid()).setValue(newUser);

                                   SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                   SharedPreferences.Editor editor = preferences.edit();
                                   editor.putString("email", currentUser.getEmail());
                                   editor.apply();

                                   showAlertDialog("", "You have registered successfully.");




                               }
                           });

                       }
                   }
               });
    }





    public void onBackPressed() {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
    }


    private void popUpAlertDialogConnectionError() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Error");
        builder1.setMessage("There might be problems with the server or network connection.");

        builder1.setPositiveButton(
                "TRY AGAIN",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailEditText = findViewById(R.id.emailEditTextRegister);
        mUsernameEditText = findViewById(R.id.usernameEditTextRegister);
        mPasswordEditText = findViewById(R.id.passwordEditTextRegister);

        mAuth = FirebaseAuth.getInstance();
        mUsersDB = FirebaseDatabase.getInstance().getReference().child("Users");

        mDialog = new ProgressDialog(this);

        userRegisteredSuccessfully = false;
    }
}
