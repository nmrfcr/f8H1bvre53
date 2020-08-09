package com.tekapic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity  {

    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private Context context;
    private TextView textView1, textView2, textView3, textView4;
    private DatabaseReference mDatabaseReference;



    private SectionsPagerAdapter mSectionsPagerAdapter;


    private ViewPager mViewPager;

    public void share(View v) {
        startActivity(new Intent(ProfileActivity.this, AddPictureActivity.class));
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {

                        case R.id.nav_explore:
                            startActivity(new Intent(ProfileActivity.this, ExploreActivity.class));
                            break;

                        case R.id.nav_search:
                            startActivity(new Intent(ProfileActivity.this, SearchActivity.class));
                            break;

                        case R.id.nav_add_picture:
                            startActivity(new Intent(ProfileActivity.this, AddPictureActivity.class));
                            break;

                    }

                    return true;
                }
            };


    @Override
    protected void onResume() {
        super.onResume();

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        menuItem.setEnabled(false);

        SpannableStringBuilder title = new SpannableStringBuilder(menuItem.getTitle());
        StyleSpan styleSpan = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        title.setSpan(styleSpan, 0, title.length(), 0);
        menuItem.setTitle((title));


        if(!isNetworkConnected()) {
            popUpAlertDialogConnectionError();
        }

        checkWarnings();


    }

    private void checkWarnings() {
        mDatabaseReference.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                boolean warnForViolatingTermsOfUse = (Boolean)dataSnapshot.child("warnForViolatingTermsOfUse").getValue();
                if(warnForViolatingTermsOfUse) {
                    //warn
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setCancelable(false);
                    builder1.setMessage("You upload and share illigal content, " +
                            "therefore this illegal content was deleted, your option for sharing and uploading pictures might be blocked.");

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mDatabaseReference.child(mAuth.getUid()).child("warnForViolatingTermsOfUse").setValue(false);
                                }
                            });

                    AlertDialog alertDialog = builder1.create();
                    alertDialog.show();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        context = this;

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.profile2_nav);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("Profile");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.profile_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override

            public void onTabSelected(TabLayout.Tab tab) {

                textView1 = findViewById(R.id.t1);
                textView2 = findViewById(R.id.t2);
                textView3 = findViewById(R.id.t3);
                textView4 = findViewById(R.id.t4);

                int c = ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null);

                if(tab.getPosition() == 0) {
                    textView1.setTextColor(c);
                    textView2.setTextColor(c);
                    textView1.setTypeface(textView1.getTypeface(), Typeface.BOLD);
                    textView2.setTypeface(textView2.getTypeface(), Typeface.BOLD);
                }
                else {
                    textView3.setTextColor(c);
                    textView4.setTextColor(c);
                    textView3.setTypeface(textView3.getTypeface(), Typeface.BOLD);
                    textView4.setTypeface(textView4.getTypeface(), Typeface.BOLD);
                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                textView1 = findViewById(R.id.t1);
                textView2 = findViewById(R.id.t2);
                textView3 = findViewById(R.id.t3);
                textView4 = findViewById(R.id.t4);

                int c = ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null);

                if(tab.getPosition() == 0) {
                    textView1.setTextColor(c);
                    textView2.setTextColor(c);
                    textView1.setTypeface(null, Typeface.NORMAL);
                    textView2.setTypeface(null, Typeface.NORMAL);

                }
                else {
                    textView3.setTextColor(c);
                    textView4.setTextColor(c);
                    textView3.setTypeface(null, Typeface.NORMAL);
                    textView4.setTypeface(null, Typeface.NORMAL);
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
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

    private void popUpAlertDialogLogOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setMessage("Log Out of Tekapix?");

        builder.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(isNetworkConnected() == false) {
                    popUpAlertDialogConnectionError();
                    return;
                }

                mAuth.signOut();

                Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                finish();
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

        case R.id.editProfileMenu:
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            return true;

        case R.id.favoritesMenu:
            startActivity(new Intent(ProfileActivity.this, FavoritesActivity.class));
            return true;

        case R.id.accountPrivacyMenu:
            startActivity(new Intent(ProfileActivity.this, AccountPrivacyActivity.class));
            return true;

    }
    return super.onOptionsItemSelected(item);
}



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;

            switch(position) {
                case 0:
                    fragment = new AllPicturesFragment();
                    break;
                case 1:
                    fragment = new AlbumsFragment();
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }
}
