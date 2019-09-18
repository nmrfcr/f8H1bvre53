package com.tekapic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountPrivacyActivity extends AppCompatActivity {

    private Switch aSwitch;
    private boolean isSwitchCheckSetted;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;



    public void popUpPrivacyAlertDialog(final boolean privateAccount, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountPrivacyActivity.this);

        if(privateAccount) {
            builder.setTitle("Change to private Account?");
        }
        else{
            builder.setTitle("Change to public Account?");
        }


        builder.setMessage(message);

        builder.setCancelable(false);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(privateAccount) {
                    databaseReference.child("privateAccount").setValue(true);
                }
                else {
                    databaseReference.child("privateAccount").setValue(false);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isSwitchCheckSetted = true;
                if(privateAccount) {
                    aSwitch.setChecked(false);
                }
                else {
                    aSwitch.setChecked(true);
                }
            }
        });


        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_privacy);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.account_privacy_nav);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        isSwitchCheckSetted = false;

        aSwitch = findViewById(R.id.privateAccountSwitch);

        mAuth = FirebaseAuth.getInstance();
        databaseReference =   FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());



        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isSwitchCheckSetted) {
                    isSwitchCheckSetted = false;
                    return;
                }

                if(isChecked) {

                    if(isNetworkConnected() == false) {
                        aSwitch.setChecked(false);
                        popUpAlertDialogConnectionError();
                        return;
                    }

                    popUpPrivacyAlertDialog(true, R.string.privacy_private_description);
                }
                else {

                    if(isNetworkConnected() == false) {
                        aSwitch.setChecked(true);
                        popUpAlertDialogConnectionError();
                        return;
                    }

                    popUpPrivacyAlertDialog(false, R.string.privacy_public_description);
                }
            }
        });



        databaseReference.child("privateAccount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Boolean privateAccount = (Boolean) snapshot.getValue();

                if(privateAccount) {
                    isSwitchCheckSetted = true;
                    aSwitch.setChecked(true);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
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

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {

                        case R.id.nav_explore:
                            startActivity(new Intent(AccountPrivacyActivity.this, ExploreActivity.class));
                            break;
                        case R.id.nav_search:
                            startActivity(new Intent(AccountPrivacyActivity.this, SearchActivity.class));
                            break;

                        case R.id.nav_add_picture:
                            startActivity(new Intent(AccountPrivacyActivity.this, AddPictureActivity.class));
                            break;

                        case R.id.nav_profile:
                            startActivity(new Intent(AccountPrivacyActivity.this, ProfileActivity.class));

                            break;
                    }

                    return true;
                }
            };




}
