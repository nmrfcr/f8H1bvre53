package com.tekapic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tekapic.model.User;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabaseReference;
    private TextView indicatorText;
    private String profileEmail;
    private Context context;
    private BottomNavigationView bottomNavigationView;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;


    private static String searchText = "";


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
        setContentView(R.layout.activity_search);

        context = this;

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.search_nav);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        profileEmail = mAuth.getCurrentUser().getEmail();

        linearLayout = findViewById(R.id.textAndProBarLayoutSearch);
        indicatorText = findViewById(R.id.results_indicator_text);
        progressBar = findViewById(R.id.progressBarSearch);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");

        mRecyclerView = findViewById(R.id.result_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchView = findViewById(R.id.search);


//        searchView.setIconifiedByDefault(false);

//        searchView.setIconified(false);
//
//        searchView.setFocusable(true);

//        InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        if(!searchText.isEmpty()) {
            searchView.setQuery(searchText, false);
            firebaseUserSearch(searchText.toLowerCase());
        }


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(isNetworkConnected() == false) {
                    popUpAlertDialogConnectionError();
                    return false;
                }
                searchText = query.toLowerCase();
                firebaseUserSearch(query.toLowerCase());

                linearLayout.setVisibility(View.VISIBLE);
                indicatorText.setText("Searching...");
                progressBar.setVisibility(View.VISIBLE);


                mRecyclerView.setVisibility(View.GONE);


                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return false;
            }
        });
    }

    private void firebaseUserSearch(String searchText) {

        Query query = mDatabaseReference.orderByChild("username").startAt(searchText).endAt(searchText + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.GONE);

                if(!dataSnapshot.exists()) {

                    indicatorText.setText("No Results Found");
                    progressBar.setVisibility(View.GONE);

                }
                else {
                    linearLayout.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>
                (options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull final User model) {


                holder.setUsername(model.getUsername());

                String profilePictureUrl = model.getProfilePictureUrl();
                if(!profilePictureUrl.equals("none")) {

                    holder.setProfilePicture(profilePictureUrl, context);
                }


                holder.linearLayout.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(profileEmail.equals(model.getEmail())) {
//                            finish();
                            startActivity(new Intent(SearchActivity.this, ProfileActivity.class));
                            return;
                        }
                        HomePeopleActivity.flag = 0;
                        HomePeopleActivity.user = model;
                        HomePeopleActivity.firstVisibleItemPosition = 0;
                        startActivity(new Intent(SearchActivity.this, HomePeopleActivity.class));
                    }
                });

            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.result_list_layout, parent, false);

                return new UserViewHolder(view);
            }

        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        InputMethodManager imm = (InputMethodManager)getSystemService(
//                Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
//    }

    @Override
    protected void onResume() {
        super.onResume();

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        menuItem.setEnabled(false);



        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
        }
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public TextView textView;
        public LinearLayout linearLayout;
        public ImageView imageView;



        public UserViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            textView = mView.findViewById(R.id.email_result);
            linearLayout = mView.findViewById(R.id.users_list);
            imageView = mView.findViewById(R.id.profile_image);

        }

        public void setUsername(String userEmail) {


            textView.setText(userEmail);

        }

        public void setProfilePicture(String profilePictureUrl, Context context) {

            Glide.with(context)
                    .load(profilePictureUrl)
                    .apply(new RequestOptions().placeholder(R.drawable.profile_pic))
                    .into(imageView);


        }
    }

//    @Override
//    public void onBackPressed() {
//
//        finish();
//        Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
//        startActivity(intent);
//
//
//    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.nav_explore:
                            startActivity(new Intent(SearchActivity.this, ExploreActivity.class));
                            break;
                        case R.id.nav_add_picture:
                            startActivity(new Intent(SearchActivity.this, AddPictureActivity.class));
                            break;
                        case R.id.nav_profile:
                            startActivity(new Intent(SearchActivity.this, ProfileActivity.class));
                            break;
                    }

                    return true;
                }
            };



}
