package com.tekapic;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import java.util.HashMap;
import java.util.Map;
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


    private String picName;
    private String mCurrentPhotoPath;
    private Uri mPhotoUri;
    private ImageView imageView;
    private StorageReference mStorage;
    private FirebaseUser currentUser;
    private Uri profilePictureUri = null;


    private static final String dstDir = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator + Environment.DIRECTORY_PICTURES + File.separator + "Tekapic";
    private static final int REQUEST_PHOTO_CAPTURE = 1;
    private static final int REQUEST_PHOTO_PICK = 2;


    private void setProfilePicture(Uri uri) {
        Glide.with(this)
                .load(uri)
                .into(imageView);

        profilePictureUri = uri;
    }


    public void reqeustAddingProfilePicture() {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);


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
        builder.setNeutralButton("   Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //cancel
                dialog.dismiss();
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
        neutralButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel_black_24dp, 0, 0, 0);
//
//        negativeButtonLL.leftMargin = 45;
        negativeButton.setLayoutParams(negativeButtonLL);
//
//        neutralButtonLL.rightMargin = 100;
        neutralButton.setLayoutParams(neutralButtonLL);

    }




    public void addProfilePictueImage(View view) {
        reqeustAddingProfilePicture();
    }

    public void addProfilePictueText(View view) {
        reqeustAddingProfilePicture();

    }


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
                           currentUser = task.getResult().getUser();

                           UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                   .build();

                           currentUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   userRegisteredSuccessfully = true;

                                   String username = mUsernameEditText.getText().toString().trim();

                                   com.tekapic.model.User newUser =
                                           new com.tekapic.model.User(currentUser.getEmail(), username, currentUser.getUid(), "public", 0, false, "none");
                                   mUsersDB.child(currentUser.getUid()).setValue(newUser);

                                   SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                   SharedPreferences.Editor editor = preferences.edit();
                                   editor.putString("email", currentUser.getEmail());
                                   editor.apply();

                                   showAlertDialog("", "You have registered successfully.");

                                   if(profilePictureUri != null) {
                                       uploadProfilePictureToFirebaseStorage();
                                   }


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

        imageView = findViewById(R.id.profile_picture_register);

        mEmailEditText = findViewById(R.id.emailEditTextRegister);
        mUsernameEditText = findViewById(R.id.usernameEditTextRegister);
        mPasswordEditText = findViewById(R.id.passwordEditTextRegister);

        mAuth = FirebaseAuth.getInstance();
        mUsersDB = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorage = FirebaseStorage.getInstance().getReference();


        mDialog = new ProgressDialog(this);

        userRegisteredSuccessfully = false;
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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

        StorageReference userImageRef = mStorage.child("profilePictures").child(currentUser.getUid())
                .child(profilePictureUri.getLastPathSegment());
        userImageRef.putFile(profilePictureUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                Task<Uri> uploadedImageUri = task.getResult().getMetadata().getReference().getDownloadUrl();

                uploadedImageUri.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        Uri uploadedImageUri = task.getResult();

                        String pictureUrl = uploadedImageUri.toString();

                        mUsersDB.child(currentUser.getUid()).child("profilePictureUrl").setValue(pictureUrl);



                    }
                });
            }
        });
    }

}
