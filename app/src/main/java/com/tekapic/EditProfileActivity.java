package com.tekapic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    private ProgressDialog mDialog;
    private EditText emailEditText, usernameEditText, passwordEditText, newPasswordEditText;
    private FirebaseAuth mAuth;
    private String un;
    private DatabaseReference usersDatabaseReference;






    public void changeUsername(View view) {


        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Change Username");
        dialog.setCancelable(false);


        dialog.setPositiveButton("Change Username", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(isNetworkConnected() == false) {
                    popUpAlertDialogConnectionError();
                    return;
                }

                final String username, password;

                username = usernameEditText.getText().toString();
                final String usernameLowerCase = username.toLowerCase();
                password = passwordEditText.getText().toString();


                if(usernameLowerCase.isEmpty()) {
                    showAlertDialog("Error", "Username cannot be empty.");
                    return;
                }
                if(password.isEmpty()) {
                    showAlertDialog("Error", "Password cannot be empty.");
                    return;
                }
                if(usernameLowerCase.equals(un)) {
                    showAlertDialog("Error", "Enter a new username.");
                    return;
                }


                //check if the username ok

                if(usernameLowerCase.length() < 2) {
                    showAlertDialog("Error", "Username must be at least 2 characters long.");
                    return;
                }

                if(usernameLowerCase.length() > 15) {
                    showAlertDialog("Error", "Username must be at most 15 characters long.");
                    return;
                }

                if(usernameLowerCase.matches("[a-z0-9]*")) {
                    if (usernameLowerCase.matches("[0-9]+")) {
                        showAlertDialog("Error", "Username cannot contain only numbers.");
                        return;
                    }
                    if(Character.isDigit(usernameLowerCase.charAt(0))) {
                        showAlertDialog("Error", "Username cannot start with a digit.");
                        return;
                    }
                }
                else {
                    showAlertDialog("Error", "Username can contain letters or letters with numbers.");
                    return;
                }


                mDialog.setMessage("Please wait...");
                mDialog.show();
                mDialog.setCancelable(false);

                ///
                ///
                ///

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // Get auth credentials from the user for re-authentication
                AuthCredential credential = EmailAuthProvider
                        .getCredential(mAuth.getCurrentUser().getEmail(), password); // Current Login Credentials \\
                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()) {
                                    Log.d("User re-authenticated.", "User re-authenticated.");
                                    //check if username is taken

                                    usersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            Log.i("count", Long.toString(dataSnapshot.getChildrenCount()));

                                            boolean isUsernameTaken = false;
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                                String usnm = ds.child("username").getValue(String.class);

                                                Log.i("username", usnm);

                                                if(usnm.equals(usernameLowerCase)) {
                                                    isUsernameTaken = true;
                                                    break;
                                                }
                                            }

                                            //here***************************
                                            mDialog.dismiss();
                                            if(isUsernameTaken) {
                                                showAlertDialog("Error", "Username is taken.");
                                            }
                                            else {
                                                DatabaseReference updateUsername = usersDatabaseReference.child(mAuth.getUid());
                                                updateUsername.child("username").setValue(usernameLowerCase);
                                                showAlertDialog("Attention!", "Your username has been successfully changed.");
                                                un = usernameLowerCase;

                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });




                                }
                                else {
                                    mDialog.dismiss();
                                    showAlertDialog("Error", task.getException().getMessage());
                                }


                            }
                        });

            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

// Add a TextView here for the "Title" label, as noted in the comments
        usernameEditText = new EditText(this);
        usernameEditText.setHint("Username");
        usernameEditText.setText(un);
        layout.addView(usernameEditText); // Notice this is an add method

// Add another TextView here for the "Description" label
        passwordEditText = new EditText(this);
        passwordEditText.setHint("Password");
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(passwordEditText); // Another add method

        dialog.setView(layout); // Again this is a set method, not add


        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

    }

    public void changeEmail(View view) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Change Email");
        dialog.setCancelable(false);


        dialog.setPositiveButton("Change Email", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(isNetworkConnected() == false) {
                    popUpAlertDialogConnectionError();
                    return;
                }

                final String email, password;

                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();

                if(email.isEmpty()) {
                    showAlertDialog("Error", "Email cannot be empty.");
                    return;
                }
                if(password.isEmpty()) {
                    showAlertDialog("Error", "Password cannot be empty.");
                    return;
                }
                if(email.equals(mAuth.getCurrentUser().getEmail())) {
                    showAlertDialog("Error", "Enter a new email.");
                    return;
                }

                mDialog.setMessage("Please wait...");
                mDialog.show();
                mDialog.setCancelable(false);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // Get auth credentials from the user for re-authentication
                AuthCredential credential = EmailAuthProvider
                        .getCredential(mAuth.getCurrentUser().getEmail(), password); // Current Login Credentials \\
                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mDialog.dismiss();

                                if(task.isSuccessful()) {
                                    Log.d("User re-authenticated.", "User re-authenticated.");
                                    //Now change your email address \\
                                    //----------------Code for Changing Email Address----------\\
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    mDialog.setMessage("Please wait...");
                                    mDialog.show();
                                    mDialog.setCancelable(false);

                                    user.updateEmail(email)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    mDialog.dismiss();

                                                    if (task.isSuccessful()) {
                                                        Log.d("email updated", "User email address updated.");
                                                        showAlertDialog("Attention!", "Your email address has been successfully changed.");

                                                        //here

                                                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                                        rootRef.child("Users").child(uid).child("email").setValue(email);

                                                    }
                                                    else {
                                                        showAlertDialog("Error", task.getException().getMessage());

                                                    }
                                                }
                                            });
                                    //----------------------------------------------------------\\
                                }
                                else {
                                    showAlertDialog("Error", task.getException().getMessage());
                                }


                            }
                        });




            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

// Add a TextView here for the "Title" label, as noted in the comments
        emailEditText = new EditText(this);
        emailEditText.setHint("Email");
        emailEditText.setText(mAuth.getCurrentUser().getEmail());
        layout.addView(emailEditText); // Notice this is an add method

// Add another TextView here for the "Description" label
        passwordEditText = new EditText(this);
        passwordEditText.setHint("Password");
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(passwordEditText); // Another add method

        dialog.setView(layout); // Again this is a set method, not add


        AlertDialog alertDialog = dialog.create();
        alertDialog.show();


    }



    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
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


    public void changePassword(View view) {



        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Change Password");
        dialog.setCancelable(false);


        dialog.setPositiveButton("Change Password", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(isNetworkConnected() == false) {
                    popUpAlertDialogConnectionError();
                    return;
                }

                final String newPassword, oldPassword;

                newPassword = newPasswordEditText.getText().toString();
                oldPassword = passwordEditText.getText().toString();

                if(newPassword.isEmpty()) {
                    showAlertDialog("Error", "New password cannot be empty.");
                    return;
                }
                if(oldPassword.isEmpty()) {
                    showAlertDialog("Error", "Old password cannot be empty.");
                    return;
                }

                //check if pass is strong method

                if(newPassword.length() < 6) {
                    showAlertDialog("Error", "New password must be at least 6 characters.");
                    return;
                }
                if(isPasswordStrong(newPassword) == false) {
                    showAlertDialog("Error", "Please choose a stronger new password. try a mix of letters and digits.");
                    return;
                }


                mDialog.setMessage("Please wait...");
                mDialog.show();
                mDialog.setCancelable(false);


                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // Get auth credentials from the user for re-authentication
                AuthCredential credential = EmailAuthProvider
                        .getCredential(mAuth.getCurrentUser().getEmail(), oldPassword); // Current Login Credentials \\
                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mDialog.dismiss();

                                if(task.isSuccessful()) {
                                    Log.d("User re-authenticated.", "User re-authenticated.");
                                    //Now change your email address \\
                                    //----------------Code for Changing Email Address----------\\
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    mDialog.setMessage("Please wait...");
                                    mDialog.show();
                                    mDialog.setCancelable(false);

                                    user.updatePassword(newPassword)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    mDialog.dismiss();

                                                    if (task.isSuccessful()) {
                                                        showAlertDialog("Attention!", "Your password has been successfully changed.");
                                                    }
                                                    else {
                                                        showAlertDialog("Error", task.getException().getMessage());

                                                    }
                                                }
                                            });
                                    //----------------------------------------------------------\\
                                }
                                else {
                                    showAlertDialog("Error", task.getException().getMessage());
                                }


                            }
                        });




            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

// Add a TextView here for the "Title" label, as noted in the comments
        newPasswordEditText = new EditText(this);
        newPasswordEditText.setHint("New password");
        newPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(newPasswordEditText); // Notice this is an add method

// Add another TextView here for the "Description" label
        passwordEditText = new EditText(this);
        passwordEditText.setHint("Old password");
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(passwordEditText); // Another add method

        dialog.setView(layout); // Again this is a set method, not add


        AlertDialog alertDialog = dialog.create();
        alertDialog.show();




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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        usersDatabaseReference.child(mAuth.getUid()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                un = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
