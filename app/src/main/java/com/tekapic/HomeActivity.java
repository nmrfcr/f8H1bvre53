package com.tekapic;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.tekapic.model.Picture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_PHOTO_CAPTURE = 1;
    private static final int REQUEST_PHOTO_PICK = 2;
    private static final String dstDir = Environment.getExternalStorageDirectory() +
            File.separator + "Pictures" + File.separator + "Tekapic";

    private FirebaseAuth mAuth;
    private DatabaseReference mStatusDB;
//    private DatabaseReference mUserDB;
    private RecyclerView mRecyclerView;
    private Uri mPhotoUri;
    private Button button;
    private ImageView imageViewIcon;
    private static int lastPosition = 0;
    private boolean isUserhasPics = false;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    boolean[] checkedCategories = new boolean[Picture.numberOfAlbums+1];
    EditText emailEditText, passwordEditText;
    private ProgressDialog mDialog;


//    public void save(int lastPosition) {
//        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
//        editor.putInt("lastPosition", lastPosition);
//        editor.apply();
//    }
//    public void restore() {
//        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//        int restoredText = prefs.getInt("lastPosition", -1);
//        if (restoredText != -1) {
//            int p = prefs.getInt("lastPosition", -1); //0 is the default value.
//            mRecyclerView.scrollToPosition(p);
//
//        }
//    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }




    private void popUpAlertDialogLogOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage("Log Out of Tekapic?");

        builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(isNetworkConnected() == false) {
                    popUpAlertDialogConnectionError();
                    return;
                }

                mAuth.signOut();
                goToLoginActivity();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void buttonClick(View view) {
        popUpAlertDialog();
    }

    private void popUpAlertDialogConnectionError() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Error");
        builder1.setMessage("There might be problems with the server or network connection.");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "TRY AGAIN",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        if(isNetworkConnected() == false) {
//                            popUpAlertDialogConnectionError();
//                        }

                    }
                });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();
    }

    public void popUpAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage("How would you like to add a picture?");

        builder.setPositiveButton("   Take a picture", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Take a picture
//                Toast.makeText(HomeActivity.this, "Take a picture", Toast.LENGTH_SHORT).show();
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

        negativeButtonLL.leftMargin = 45;
        negativeButton.setLayoutParams(negativeButtonLL);

        neutralButtonLL.rightMargin = 100;
        neutralButton.setLayoutParams(neutralButtonLL);



    }


    private void goToLoginActivity() {
        finish();
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
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

    private void updateEmail() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Update Email");
        dialog.setCancelable(false);


        dialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(isNetworkConnected() == false) {
                    popUpAlertDialogConnectionError();
                    return;
                }

//                Toast.makeText(HomeActivity.this, emailEditText.getText().toString() + " " + passwordEditText.getText().toString(), Toast.LENGTH_SHORT).show();
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
                    showAlertDialog("Error", "Enter new email.");
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
        emailEditText.setHint("Enter new email");
        emailEditText.setText(mAuth.getCurrentUser().getEmail());
        layout.addView(emailEditText); // Notice this is an add method

// Add another TextView here for the "Description" label
        passwordEditText = new EditText(this);
        passwordEditText.setHint("Enter your password");
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(passwordEditText); // Another add method

        dialog.setView(layout); // Again this is a set method, not add


        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return false;
        }

        switch (item.getItemId()) {
            case R.id.logoutMenu:
                popUpAlertDialogLogOut();
                return true;
            case R.id.addNewMenu:
                popUpAlertDialog();
                //allert dialog
                return true;
            case R.id.albums:
                if(isUserhasPics) {
                    startActivity(new Intent(HomeActivity.this, AlbumsActivity.class));
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please add pictures first.", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.updateEmailMenu:
                updateEmail();
                return true;
//            case R.id.SortMenu:
//                sort();
//                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mAuth.getCurrentUser() == null) {
            goToLoginActivity();
        }
        checkIfUserHasAnyPictures();

//        if(isUserhasPics) {
//            mRecyclerView.scrollToPosition(lastPosition);
//        }
//        save(lastPosition);
//        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
        if(!isNetworkConnected()) {
            popUpAlertDialogConnectionError();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//
//        if(isUserhasPics) {
//            mRecyclerView.scrollToPosition(lastPosition);
//        }
//    }

    @Override
    protected void onStart() {


        super.onStart();

//        restore();

//        if(isUserhasPics) {
//            mRecyclerView.scrollToPosition(lastPosition);
//        }

        Query query = mStatusDB;
        final FirebaseRecyclerOptions<Picture> options = new FirebaseRecyclerOptions.Builder<Picture>()
                .setQuery(query, Picture.class)
                .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Picture, StatusViewHolder>(options) {


                    @NonNull
                    @Override
                    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.pictures_row, parent, false);

                        return new StatusViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final StatusViewHolder holder, final int position, @NonNull final Picture model) {

//                        Log.i("model" , model.getPictureUrl());
//                        if(model.getPets().equals("1"))

                        checkIfUserHasAnyPictures();

//                            if(model.getMe().equals("1")) {
                                try {
                                    holder.setPictureInLeft(getApplicationContext(), model.getPictureUrl());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
//                            }

                            lastPosition = position;


                        //listen to image button clicks
                        holder.imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(isNetworkConnected() == false) {
                                    popUpAlertDialogConnectionError();
                                    return;
                                }

                                if(model.getPictureUrl().equals("none")) {
                                    return;
                                }

                                lastPosition = position;
//                                Log.i("pic", model.getPictureUrl());
                                //go to PictureActivity
                                Intent intent = new Intent(HomeActivity.this, PictureActivity.class);
//                                intent.putExtra("MyClass", model);
                                PictureActivity.picture = model;
                                PictureActivity.isPictureFromAlbum = false;

                                startActivity(intent);
                            }
                        });
                    }
                };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public static class StatusViewHolder extends RecyclerView.ViewHolder {

        View view;
        public ImageView imageView;



        public StatusViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            imageView = view.findViewById(R.id.rowImageView);
        }

        public void setPictureInLeft(Context context, String pictureUrl) {

            ImageView imageView = view.findViewById(R.id.rowImageView);

            Glide.with(context)
                    .load(pictureUrl)
                    .apply(new RequestOptions().placeholder(R.mipmap.loading_icon))
                    .into(imageView);


//            Picasso.with(context).load(pictureUrl).placeholder(R.mipmap.loading_icon).
//                    memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(imageView);

        }
//        public void setPictureInCenter(Context context, String pictureUrl) {
//            ImageButton userImageButton = view.findViewById(R.id.userImageButton2);
//            Picasso.with(context).load(pictureUrl).placeholder(R.mipmap.ic_launcher).into(userImageButton);
//        }
//        public void setPictureInRight(Context context, String pictureUrl) {
//            ImageButton userImageButton = view.findViewById(R.id.userImageButton3);
//            Picasso.with(context).load(pictureUrl).placeholder(R.mipmap.ic_launcher).into(userImageButton);
//        }

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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_PHOTO_CAPTURE);
        }
        else if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},  REQUEST_PHOTO_CAPTURE);
        }
        else  {
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(takePhotoIntent.resolveActivity(getPackageManager()) != null) {



                Log.i("perAsk","in else");
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");


                mPhotoUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);



                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);



                startActivityForResult(intent, REQUEST_PHOTO_CAPTURE);






            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_PHOTO_CAPTURE && resultCode == RESULT_OK) {



            copyFileOrDirectory(getRealPathFromURI(mPhotoUri), dstDir);

            File src = new File(getRealPathFromURI(mPhotoUri));

            File file = new File(getRealPathFromURI(mPhotoUri));

            file.delete();

            File finalFile = new File(dstDir + File.separator + src.getName());

            Uri uri = Uri.fromFile(finalFile);

            finish();
            PostActivity.pictureUri = uri;
            Intent intent = new Intent(this, PostActivity.class);
            startActivity(intent);



        }
        else if(requestCode == REQUEST_PHOTO_PICK && resultCode == RESULT_OK) {
            //success choosing photo
            mPhotoUri = data.getData();

            finish();
            PostActivity.pictureUri = mPhotoUri;
            Intent intent = new Intent(this, PostActivity.class);
            startActivity(intent);

//            mUserImageView.setImageURI(mPhotoUri);
        }

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

//
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        // Save UI state changes to the savedInstanceState.
//        // This bundle will be passed to onCreate if the process is
//        // killed and restarted.
//        savedInstanceState.putInt("MyInt", lastPosition);
//        Log.i("onSaveInstanceState", Integer.toString(lastPosition));
//
//        // etc.
//    }

//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        // Restore UI state from the savedInstanceState.
//        // This bundle has also been passed to onCreate.
//        int myInt = savedInstanceState.getInt("MyInt");
//        Log.i("onRestoreInstanceState", Integer.toString(myInt));
//        mRecyclerView.scrollToPosition(myInt);
//
//    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    //****************onCreate()********************//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDialog = new ProgressDialog(this);

//
//        mDialog.setMessage("Please wait...");
//        mDialog.show();
//        mDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();
//        if (mAuth.getCurrentUser() == null) {
//            goToLoginActivity();
//            return;
//        }

        button = findViewById(R.id.addNewPicButton);
        imageViewIcon = findViewById(R.id.imageViewHomeIcon);

        mStatusDB = FirebaseDatabase.getInstance().getReference().child(mAuth.getUid());


//        mUserDB = FirebaseDatabase.getInstance().getReference().child("Users");


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);


        mRecyclerView = findViewById(R.id.homeRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(HomeActivity.this, 3);

//        mGridLayoutManager.setReverseLayout(true);

        mRecyclerView.setLayoutManager(mGridLayoutManager);


//        //take the latest data to RecyclerView
//        mGridLayoutManager.setReverseLayout(true);
//        mGridLayoutManager.setStackFromEnd(true);

        checkIfUserHasAnyPictures();

        initialSorting();

    }


    public void checkIfUserHasAnyPictures() {
        mStatusDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    button.setVisibility(View.VISIBLE);
                    imageViewIcon.setVisibility(View.VISIBLE);
                    isUserhasPics = false;
                }
                else {
                    button.setVisibility(View.GONE);
                    imageViewIcon.setVisibility(View.GONE);
                    isUserhasPics = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    /****************The starting  proccess of the pictures making**************/


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
//
//
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
//
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//
//    public void takeAPicture(View view) {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
//        }else
//        {
//            Log.i("perAsk","in else");
//            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//            StrictMode.setVmPolicy(builder.build());
//
//            ContentValues values = new ContentValues();
//            values.put(MediaStore.Images.Media.TITLE, "New Picture");
//            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
//
//
//            imageUri = getContentResolver().insert(
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//
//
//
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//
//
//
//            startActivityForResult(intent, 111);
//
//        }
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(resultCode == -1) {
//
//            copyFileOrDirectory(getRealPathFromURI(imageUri), dstDir);
//
//            File src = new File(getRealPathFromURI(imageUri));
//
//            File file = new File(getRealPathFromURI(imageUri));
//
//            file.delete();
//
//            File finalFile = new File(dstDir + File.separator + src.getName());
//
//            Uri uri = Uri.fromFile(finalFile);
//
//            InputStream iStream = null;
//            try {
//                iStream = getContentResolver().openInputStream(uri);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            try {
//                pictureInByteArray = getBytes(iStream);
//                openPostActivity();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public byte[] getBytes(InputStream inputStream) throws IOException {
//        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
//        int bufferSize = 1024;
//        byte[] buffer = new byte[bufferSize];
//
//        int len = 0;
//        while ((len = inputStream.read(buffer)) != -1) {
//            byteBuffer.write(buffer, 0, len);
//        }
//        return byteBuffer.toByteArray();
//    }
//
    public String getRealPathFromURI(Uri contentUri) {

        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);

    }

    /****************The end of the proccess of pictures making**************/

    //*************Sorting ***************************////

    public void initialSorting() {
        for(int i = 0; i < Picture.numberOfAlbums+1; i++) {
            checkedCategories[i] = true;
        }
    }


    public void sort() {
        final ArrayList<String> categories = new ArrayList<String>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select categories:");

// add a checkbox list
        builder.setMultiChoiceItems(Picture.albumsNames, checkedCategories, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // user checked or unchecked a box
                switch (which) {
                    case 0:
                        if(isChecked) {
                            categories.add("me");
                        }
                        else {
                            categories.remove("me");
                        }
                    case 1:
                        if(isChecked) {
                            categories.add("family");
                        }
                        else {
                            categories.remove("family");
                        }
                    case 2:
                        if(isChecked) {
                            categories.add("friends");
                        }
                        else {
                            categories.remove("friends");
                        }
                    case 3:
                        if(isChecked) {
                            categories.add("love");
                        }
                        else {
                            categories.remove("love");
                        }
                    case 4:
                        if(isChecked) {
                            categories.add("pets");
                        }
                        else {
                            categories.remove("pets");
                        }
                    case 5:
                        if(isChecked) {
                            categories.add("nature");
                        }
                        else {
                            categories.remove("nature");
                        }
                    case 6:
                        if(isChecked) {
                            categories.add("sport");
                        }
                        else {
                            categories.remove("sport");
                        }
                    case 7:
                        if(isChecked) {
                            categories.add("persons");
                        }
                        else {
                            categories.remove("persons");
                        }
                    case 8:
                        if(isChecked) {
                            categories.add("animals");
                        }
                        else {
                            categories.remove("animals");
                        }
                    case 9:
                        if(isChecked) {
                            categories.add("vehicles");
                        }
                        else {
                            categories.remove("vehicles");
                        }

                    case 10:
                        if(isChecked) {
                            categories.add("views");
                        }
                        else {
                            categories.remove("views");
                        }
                    case 11:
                        if(isChecked) {
                            categories.add("food");
                        }
                        else {
                            categories.remove("food");
                        }

                    case 12:
                        if(isChecked) {
                            categories.add("things");
                        }
                        else {
                            categories.remove("things");
                        }
                    case 13:
                        if(isChecked) {
                            categories.add("funny");
                        }
                        else {
                            categories.remove("funny");
                        }
                    case 14:
                        if(isChecked) {
                            categories.add("places");
                        }
                        else {
                            categories.remove("places");
                        }
                    case 15:
                        if(isChecked) {
                            categories.add("art");
                        }
                        else {
                            categories.remove("art");
                        }

                }

            }
        });

// add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK

                for(String category: categories) {
                    Log.i("category ", category);
                }
            }
        });
//        builder.setNegativeButton("Cancel", null);

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();


    }




}
