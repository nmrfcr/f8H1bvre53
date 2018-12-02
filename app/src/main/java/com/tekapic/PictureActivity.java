package com.tekapic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tekapic.model.Picture;

import java.util.ArrayList;

public class PictureActivity extends AppCompatActivity {

    private ImageView imageView;
    public static Picture picture;
    public static boolean isPictureFromAlbum;
    private DatabaseReference mStatusDB;
    private FirebaseAuth mAuth;
    private FirebaseStorage storageReference;
    private ProgressDialog mDialog;

    public static int clickedItemIndex;
    public static int picturesListSize;
    public static ArrayList<Picture> picturesList=new ArrayList<Picture>() ;




    private void deledePictureFromFirebase() {

        mDialog.setMessage("Deleting...");
        mDialog.show();
        mDialog.setCancelable(false);
//        Toast.makeText(getApplicationContext(), "Deleting..", Toast.LENGTH_SHORT).show();


        mStatusDB.child(picture.getPictureId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDialog.dismiss();
//                Toast.makeText(getApplicationContext(), "Picture deleted.", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(PictureActivity.this, HomeActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                mDialog.dismiss();

                Toast.makeText(getApplicationContext(), "Error! picture wasn't deleted.", Toast.LENGTH_SHORT).show();
            }
        });





        StorageReference photoRef = storageReference.getReferenceFromUrl(picture.getPictureUrl());

        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.i("onSuccess", "picture deleted from storage");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.i("onFailure", "picture wasn't deleted from storage");
            }
        });
    }

    private void popUpAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PictureActivity.this);
        builder.setTitle("Delete Picture");
        builder.setMessage("Are you sure you want to delete this picture?");

        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //check network state
                if(isNetworkConnected() == false) {
                    popUpAlertDialogConnectionError();
                    return;
                }

                deledePictureFromFirebase();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void setPictureUrl(Context context, String pictureUrl) {

        Glide.with(context)
                .load(pictureUrl)
                .into(imageView);
//
//        Glide
//                .with( context )
//                .load(pictureUrl)
//                .thumbnail( 0.1f )
//                .into(imageView);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return false;
        }

        switch (item.getItemId()) {
            case R.id.editPictureMenu:
//                Toast.makeText(this, "Edit", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PictureActivity.this, EditActivity.class);
//                intent.putExtra("MyClass", picture);
                startActivity(intent);
                return true;
            case R.id.deletePictureMenu:
                popUpAlertDialog();
                return true;

            case android.R.id.home:
                goBack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picture_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        mDialog = new ProgressDialog(this);

        imageView = findViewById(R.id.photo_view);

        mAuth = FirebaseAuth.getInstance();
        mStatusDB = FirebaseDatabase.getInstance().getReference().child(mAuth.getUid());
        storageReference =  FirebaseStorage.getInstance().getReference().getStorage();

//        picture = (Picture) getIntent().getSerializableExtra("MyClass");
        setPictureUrl(this, picture.getPictureUrl());


        imageView.setOnTouchListener(new OnSwipeTouchListener(PictureActivity.this) {
            public void onSwipeRight() {

                if(isNetworkConnected() == false) {
                    popUpAlertDialogConnectionError();
                    return;
                }

                if(clickedItemIndex == 0 && picturesListSize == 0) {
                    return;
                }

                if(clickedItemIndex == 0) {
                    return;
                }

//                Toast.makeText(getApplicationContext(), "right", Toast.LENGTH_SHORT).show();
                setPictureUrl(getApplicationContext(), picturesList.get(clickedItemIndex-1).getPictureUrl());
                --clickedItemIndex;



            }
            public void onSwipeLeft() {

                if(isNetworkConnected() == false) {
                    popUpAlertDialogConnectionError();
                    return;
                }

                if(clickedItemIndex == 0 && picturesListSize == 1) {
                    return;
                }

                if((clickedItemIndex + 1) == picturesListSize) {
                    return;
                }

//                Toast.makeText(getApplicationContext(), "left", Toast.LENGTH_SHORT).show();

                setPictureUrl(getApplicationContext(), picturesList.get(clickedItemIndex+1).getPictureUrl());
                ++clickedItemIndex;


            }

        });



    }

    private void goBack() {
        finish();
        if(isPictureFromAlbum) {
            startActivity(new Intent(PictureActivity.this, PicturesActivity.class));
        }
        else {
            startActivity(new Intent(PictureActivity.this, HomeActivity.class));

        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        goBack();
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
}
