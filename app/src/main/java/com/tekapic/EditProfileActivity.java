package com.tekapic;

import android.*;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditProfileActivity extends AppCompatActivity {

    private ProgressDialog mDialog;
    private EditText emailEditText, usernameEditText, passwordEditText, newPasswordEditText;
    private FirebaseAuth mAuth;
    private String un;
    private DatabaseReference usersDatabaseReference;
    private Context context;




    private String picName;
    private String mCurrentPhotoPath;
    private Uri mPhotoUri;
    private ImageView imageView;
    private StorageReference mStorage;
    private FirebaseStorage storageReference;
    private Uri profilePictureUri = null;
    private String profilePictureUrl;
    private boolean toRemoveOnly = false;
    private BottomNavigationView bottomNavigationView;



    private static final String dstDir = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator + Environment.DIRECTORY_PICTURES + File.separator + "Tekapic";
    private static final int REQUEST_PHOTO_CAPTURE = 1;
    private static final int REQUEST_PHOTO_PICK = 2;


    private void setProfilePicture(Uri uri) {
        Glide.with(this)
                .load(uri)
                .into(imageView);

        profilePictureUri = uri;

        if(!profilePictureUrl.equals("none")) {
            deleteProfilePictureFromFirebaseStorage();
            return;
        }

        uploadProfilePictureToFirebaseStorage();
    }


    public void reqeustAddingProfilePicture() {


        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return;
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);


        builder.setPositiveButton("   Take a picture", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Take a picture
                dispatchTakePhotoIntent();
            }
        });

        builder.setNegativeButton("   Choose a picture", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Choose photo
                dispatchChoosePhotoIntent();
            }
        });
        builder.setNeutralButton("   Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //cancel
               if(!profilePictureUrl.equals("none")) {

                   Glide.with(context)
                           .load(R.drawable.profile_pic)
                           .into(imageView);

                   toRemoveOnly = true;


                   deleteProfilePictureFromFirebaseStorage();
               }
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();


        // set the buttons to the center of the screen
        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        final Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        final Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);

        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        LinearLayout.LayoutParams negativeButtonLL = (LinearLayout.LayoutParams) negativeButton.getLayoutParams();
        LinearLayout.LayoutParams neutralButtonLL = (LinearLayout.LayoutParams) neutralButton.getLayoutParams();

        positiveButtonLL.gravity = Gravity.CENTER;
        negativeButtonLL.gravity = Gravity.CENTER;
        neutralButtonLL.gravity = Gravity.CENTER;

        positiveButton.setLayoutParams(positiveButtonLL);
        negativeButton.setLayoutParams(negativeButtonLL);
        neutralButton.setLayoutParams(neutralButtonLL);

        positiveButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_photo_camera_black_24dp, 0, 0, 0);
        negativeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_image_black_24dp, 0, 0, 0);
        neutralButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_delete_black_24dp, 0, 0, 0);
//
//        negativeButtonLL.leftMargin = 45;
        negativeButton.setLayoutParams(negativeButtonLL);
//
//        neutralButtonLL.rightMargin = 100;
        neutralButton.setLayoutParams(neutralButtonLL);

    }




    public void changeProfilePictueImage(View view) {
        reqeustAddingProfilePicture();
    }

    public void changeProfilePictueText(View view) {
        reqeustAddingProfilePicture();

    }
















    public void changeUsername(View view) {


        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Change Username");
//        dialog.setCancelable(false);


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

                Log.i("username", username);

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

//                    if(Character.isDigit(usernameLowerCase.charAt(0))) {
//                        showAlertDialog("Error", "Username cannot start with a digit.");
//                        return;
//                    }

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
//        dialog.setCancelable(false);


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
//        dialog.setCancelable(false);


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

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.edit_profile_nav);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        context = this;

        imageView = findViewById(R.id.profile_picture_editpro);

        mDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        storageReference =  FirebaseStorage.getInstance().getReference().getStorage();


        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        usersDatabaseReference.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                un = dataSnapshot.child("username").getValue(String.class);

                profilePictureUrl = dataSnapshot.child("profilePictureUrl").getValue(String.class);

                if(!profilePictureUrl.equals("none")) {

                    Glide.with(context)
                            .load(profilePictureUrl)
                            .apply(new RequestOptions().placeholder(R.drawable.profile_pic))
                            .into(imageView);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHOTO_CAPTURE : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
//                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    dispatchTakePhotoIntent();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            case REQUEST_PHOTO_PICK : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
//                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    dispatchChoosePhotoIntent();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }









    @RequiresApi(api = Build.VERSION_CODES.M)
    private void dispatchChoosePhotoIntent() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PHOTO_PICK);
        }
        else {
            Intent choosePhotoIntent = new Intent(Intent.ACTION_PICK);
            choosePhotoIntent.setType("image/*");
            startActivityForResult(choosePhotoIntent, REQUEST_PHOTO_PICK);
        }
    }








    @RequiresApi(api = Build.VERSION_CODES.M)
    private void dispatchTakePhotoIntent() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_PHOTO_CAPTURE);
        }
        else if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},  REQUEST_PHOTO_CAPTURE);
        }
        else  {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mPhotoUri = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                    startActivityForResult(takePictureIntent, REQUEST_PHOTO_CAPTURE);

                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PIC_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        picName = image.getName();
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_PHOTO_CAPTURE && resultCode == RESULT_OK) {

            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);


            setProfilePicture(contentUri);


            //save picture to Pictures/Tekapic
            copyFileOrDirectory(mCurrentPhotoPath, dstDir);

            //save picture to gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File file = new File(dstDir +File.separator + picName);
            Uri uri = Uri.fromFile(file);
            mediaScanIntent.setData(uri);
            this.sendBroadcast(mediaScanIntent);

        }
        else if(requestCode == REQUEST_PHOTO_PICK && resultCode == RESULT_OK) {
            //success choosing photo
            mPhotoUri = data.getData();

            setProfilePicture(mPhotoUri);


        }

    }

    public static void copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();

                int filesLength = files.length;

                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);
                }
            } else {
                copyFile(src, dst);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {

        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {

            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }

        }
    }








    private void uploadProfilePictureToFirebaseStorage() {

        StorageReference userImageRef = mStorage.child("profilePictures").child(mAuth.getUid())
                .child(profilePictureUri.getLastPathSegment());
        userImageRef.putFile(profilePictureUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                Task<Uri> uploadedImageUri = task.getResult().getMetadata().getReference().getDownloadUrl();

                uploadedImageUri.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if(task.isSuccessful()) {

                            Uri uploadedImageUri = task.getResult();

                            profilePictureUrl = uploadedImageUri.toString();

                            usersDatabaseReference.child(mAuth.getUid()).child("profilePictureUrl").setValue(profilePictureUrl);

                        }



                    }
                });
            }
        });
    }



    private void deleteProfilePictureFromFirebaseStorage() {

        StorageReference photoRef = storageReference.getReferenceFromUrl(profilePictureUrl);

        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                if (toRemoveOnly) {
                    usersDatabaseReference.child(mAuth.getUid()).child("profilePictureUrl").setValue("none");
                    toRemoveOnly = false;
                    return;
                }

                uploadProfilePictureToFirebaseStorage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        SpannableStringBuilder title = new SpannableStringBuilder(menuItem.getTitle());
        StyleSpan styleSpan = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        title.setSpan(styleSpan, 0, title.length(), 0);
        menuItem.setTitle((title));


        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {

                        case R.id.nav_explore:
                            startActivity(new Intent(EditProfileActivity.this, ExploreActivity.class));
                            break;
                        case R.id.nav_search:
                            startActivity(new Intent(EditProfileActivity.this, SearchActivity.class));
                            break;

                        case R.id.nav_add_picture:
                            startActivity(new Intent(EditProfileActivity.this, AddPictureActivity.class));
                            break;

                        case R.id.nav_profile:
                            startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));

                            break;
                    }

                    return true;
                }
            };


}
