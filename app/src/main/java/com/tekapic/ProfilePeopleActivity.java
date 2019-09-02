package com.tekapic;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekapic.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfilePeopleActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private boolean isInFavorites;
    public static User user;
    public static int index = 0;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;
    private TextView textView1, textView2, textView3, textView4;
    private MenuItem item;
    private String reportReason = "";
    private AlertDialog alertDialog;




    private void deleteFromFavorites() {
        databaseReference.removeValue();
        isInFavorites = false;
    }

    private void saveToFavorites() {
        databaseReference.child("userId").setValue(user.getUserId());
        isInFavorites = true;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_people);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.profile_people_nav);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarpeople);
        setSupportActionBar(toolbar);
        toolbar.setTitle(user.getUsername());

       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.profile_container_people);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_people);

        isInFavorites = false;
        mAuth = FirebaseAuth.getInstance();
        databaseReference =   FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid()).child("Favorites")
                .child(user.getUserId());

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));




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

                //do stuff here
//                Toast.makeText(ProfileActivity.this, "selected: " + Integer.toString(tab.getPosition()), Toast.LENGTH_SHORT).show();
//                textView3.setTypeface(textView3.getTypeface(), Typeface.BOLD);
//                textView4.setTypeface(textView4.getTypeface(), Typeface.BOLD);




                /////////////////////////




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

        if(user.getProfilePictureUrl().equals("none")) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUserId());

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String url = dataSnapshot.child("profilePictureUrl").getValue(String.class);
                    if(!url.equals("none")) {
                        user.setProfilePictureUrl(url);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_people, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.favorites_people);

        checkIfInFavorites(item);

        return super.onPrepareOptionsMenu(menu);
    }

    private void checkIfInFavorites(MenuItem i) {

        item = i;
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    item.setIcon(R.drawable.ic_star_black_24dp);
                    isInFavorites = true;
                }
                else {
                    item.setIcon(R.drawable.ic_star_border_black_24dp);
                    isInFavorites = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showStatusReport(String message) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(message);

        builder1.setPositiveButton(
                "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();
    }

    private void setPictureReportReason() {

        reportReason = "";

        final String[] reasons =
                {"Sexual content", "Violent or repulsive content", "Hateful or abusive content",
                        "Harmful or dangerous acts", "Spam or misleading"};

        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);

        builder1.setTitle("Report Picture");

        builder1.setSingleChoiceItems(reasons, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                switch (item) {
                    case 0:
                        reportReason = reasons[0];
                        break;
                    case 1:
                        reportReason = reasons[1];
                        break;
                    case 2:
                        reportReason = reasons[2];
                        break;
                    case 3:
                        reportReason = reasons[3];
                        break;
                    case 4:
                        reportReason = reasons[4];
                        break;
                }


            }
        });

        builder1.setPositiveButton(
                "REPORT",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if(isNetworkConnected() == false) {
                            popUpAlertDialogConnectionError();
                            return;
                        }

                        makePictureReportToFirebase();

                    }
                });

        builder1.setNegativeButton(
                "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });


        alertDialog = builder1.create();

        alertDialog.show();

        // Initially disable the button
        ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

//        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//
//                     button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                    if (button != null) {
//                        button.setEnabled(false);
//                    }
//
//            }
//        });



    }

    private void makePictureReportToFirebase() {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Reports");

        String reportId = databaseReference.push().getKey();

        databaseReference.child(reportId).child("reportId").setValue(reportId);

        databaseReference.child(reportId).child("reportReason").setValue(reportReason);
        databaseReference.child(reportId).child("pictureUrl").setValue(user.getProfilePictureUrl());
        databaseReference.child(reportId).child("userIdWhoGotReported").setValue(user.getUserId());
        databaseReference.child(reportId).child("userIdOfReporter").setValue(mAuth.getUid());
        databaseReference.child(reportId).child("pictureId").setValue("none");

        String timeStamp;
        timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

        databaseReference.child(reportId).child("date").setValue(timeStamp);


        Toast.makeText(this, "Thank you for your report!", Toast.LENGTH_SHORT).show();

    }

    private void sendEmail(){

        String report;

        String userIdOfReporter = mAuth.getUid();

        String userIdWhoGotReported = user.getUserId();
        String picuteUrlWhichReported = user.getProfilePictureUrl();


        report = "Report Profile Picture\n\n";


        report = report + "Picute url which reported:\n"  + picuteUrlWhichReported + "\n";

        report = report + "User Id who got reported:\n" + userIdWhoGotReported + "\n\n";

        report = report + "User Id of reporter:\n" + userIdOfReporter;

        BackgroundMail.newBuilder(this)
                .withUsername("tekapicreporter@gmail.com")
                .withPassword("K67vDe3VzAq7i")
                .withMailto("tekapic2018@gmail.com")
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("Report Profile Picture Abuse")
                .withBody(report)

                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        showStatusReport("Your report has been submitted successfully.");
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        showStatusReport("Unfortunately unable to submit your report at this time, please try again");
                    }
                })

                .send();

    }

    private void reportProfilePicture() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Report profile picture");
        builder.setMessage("Are you sure you want to report " +  user.getUsername() + "'s profile picture?");


        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                setPictureReportReason();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.circle_image, null);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {

                Log.i("dialog", user.getProfilePictureUrl());

                ImageView image = (ImageView) dialog.findViewById(R.id.profile_CircleImageView);

                Glide.with(getApplicationContext())
                        .load(user.getProfilePictureUrl())
                        .into(image);
            }
        });
        dialog.show();


    }

    private void userWithNoProfilePicture() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Report profile picture");
        builder1.setMessage(user.getUsername() + " doesn't have a profile picture.");

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();

    }






    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return false;
        }

        switch (item.getItemId()) {

            case R.id.report_profile_pic:

                if(user.getProfilePictureUrl().equals("none")) {
                    userWithNoProfilePicture();
                }
                else {
                    reportProfilePicture();
                }

                return true;


            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.favorites_people:
                if(!isInFavorites) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfilePeopleActivity.this);
                    builder.setTitle("Add " + user.getUsername() + " to favorites?");

                    builder.setMessage("If your account is private, " + user.getUsername() +  " will be able to see your pictures.");

                    builder.setCancelable(false);

                    builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveToFavorites();
                            Toast.makeText(getApplicationContext(), user.getUsername() + " added to your favorites", Toast.LENGTH_LONG).show();
                            item.setTitle("Remove from favorites");
                            item.setIcon(R.drawable.ic_star_black_24dp);
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });


                    final AlertDialog dialog = builder.create();
                    dialog.show();



                }
                else {


                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfilePeopleActivity.this);
                    builder.setTitle("Remove " + user.getUsername() + " from favorites?");

                    builder.setMessage("If your account is private, " +  user.getUsername()  + " will not be able to see your pictures anymore.");

                    builder.setCancelable(false);

                    builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            deleteFromFavorites();
                            Toast.makeText(getApplicationContext(), user.getUsername() + " removed from your favorites", Toast.LENGTH_LONG).show();
                            item.setTitle("Add to favorites");
                            item.setIcon(R.drawable.ic_star_border_black_24dp);

                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });


                    final AlertDialog dialog = builder.create();
                    dialog.show();



                }
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
            View rootView = inflater.inflate(R.layout.fragment_profile_people, container, false);
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
                    AllPictutesPeopleFragment.user = user;
                    fragment = new AllPictutesPeopleFragment();
                    break;
                case 1:
                    AlbumsPeopleFragment.user = user;
                    fragment = new AlbumsPeopleFragment();
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

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {

                        case R.id.nav_explore:
                            startActivity(new Intent(ProfilePeopleActivity.this, ExploreActivity.class));
                            break;

                        case R.id.nav_search:
                            startActivity(new Intent(ProfilePeopleActivity.this, SearchActivity.class));
                            break;

                        case R.id.nav_add_picture:
                            startActivity(new Intent(ProfilePeopleActivity.this, AddPictureActivity.class));
                            break;

                        case R.id.nav_profile:
                            startActivity(new Intent(ProfilePeopleActivity.this, ProfileActivity.class));
                            break;

                    }

                    return true;
                }
            };

    @Override
    protected void onResume() {
        super.onResume();

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(index);
        menuItem.setChecked(true);

        SpannableStringBuilder title = new SpannableStringBuilder(menuItem.getTitle());
        StyleSpan styleSpan = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        title.setSpan(styleSpan, 0, title.length(), 0);
        menuItem.setTitle((title));


        if(!isNetworkConnected()) {
            popUpAlertDialogConnectionError();
        }



    }



}
