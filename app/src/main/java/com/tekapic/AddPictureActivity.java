package com.tekapic;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPictureActivity extends AppCompatActivity {

    private static final int REQUEST_PHOTO_CAPTURE = 1;
    private static final int REQUEST_PHOTO_PICK = 2;
    private String picName;
    private Uri mPhotoUri;
    private String mCurrentPhotoPath;

    private static final String dstDir = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator + Environment.DIRECTORY_PICTURES + File.separator + "Tekapic";
    private BottomNavigationView bottomNavigationView;




    public void popUpAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddPictureActivity.this);
        builder.setMessage("How would you like to add a picture?");

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_PHOTO_CAPTURE && resultCode == RESULT_OK) {

            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);


//            PostActivity.pictureUri = contentUri;
            Intent intent = new Intent(this, PostActivity.class);
            intent.putExtra("imageUri", contentUri);
            startActivity(intent);


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

//            PostActivity.pictureUri = mPhotoUri;
            Intent intent = new Intent(this, PostActivity.class);
            intent.putExtra("imageUri", mPhotoUri);
            startActivity(intent);

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

    public void addPicture(View view) {
        popUpAlertDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_picture);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.add_picture_nav);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        SpannableStringBuilder title = new SpannableStringBuilder(menuItem.getTitle());
        StyleSpan styleSpan = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        title.setSpan(styleSpan, 0, title.length(), 0);
        menuItem.setTitle((title));

        popUpAlertDialog();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {

                        case R.id.nav_explore:
                            startActivity(new Intent(AddPictureActivity.this, ExploreActivity.class));
                            break;

                        case R.id.nav_search:
                            startActivity(new Intent(AddPictureActivity.this, SearchActivity.class));
                            break;

                        case R.id.nav_add_picture:
                            popUpAlertDialog();
                            break;

                        case R.id.nav_profile:
                            startActivity(new Intent(AddPictureActivity.this, ProfileActivity.class));

                            break;
                    }

                    return true;
                }
            };




}
