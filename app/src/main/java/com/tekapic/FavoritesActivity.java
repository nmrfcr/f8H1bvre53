package com.tekapic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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


public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference favoritesDatabaseReference;
    private Context context;
    private TextView indicatorText;
    private FirebaseAuth mAuth;
    private android.support.v7.app.ActionBar actionBar;
    private String userIdOfDeletedUser = "";
    private BottomNavigationView bottomNavigationView;




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
        setContentView(R.layout.activity_favorites);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.favorites_nav);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        context = this;

        actionBar = getSupportActionBar();

        indicatorText = findViewById(R.id.favorites_indicator_text);

        mAuth = FirebaseAuth.getInstance();

        mDatabaseReference =  FirebaseDatabase.getInstance().getReference().child("Users");

        favoritesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid()).child("Favorites");


        mRecyclerView = findViewById(R.id.favorites_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseGetFavorites();

    }

    private void firebaseGetFavorites() {

        Query query = favoritesDatabaseReference;

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    actionBar.setSubtitle("(0)");
                    indicatorText.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);

                }
                else {
                    indicatorText.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    actionBar.setSubtitle("(" + Long.toString(dataSnapshot.getChildrenCount()) +")");
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
            protected void onBindViewHolder(@NonNull final UserViewHolder holder, int position, @NonNull final User model) {


                mDatabaseReference.child(model.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(!dataSnapshot.exists()) {
                            holder.setUsername("Deleted User");
                            userIdOfDeletedUser = dataSnapshot.getKey();

                            holder.linearLayout.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    removeDeletedUserFromFavorites();
                                }
                            });

                        }
                        else {

                            final User user = new User();
                            user.setUserId(dataSnapshot.child("userId").getValue(String.class));
                            user.setUsername(dataSnapshot.child("username").getValue(String.class));
                            user.setProfilePictureUrl(dataSnapshot.child("profilePictureUrl").getValue(String.class));

                            holder.setUsername(user.getUsername());
                            holder.setProfilePicture(user.getProfilePictureUrl(), context);

                            holder.linearLayout.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    ProfilePeopleActivity.user = user;
                                    ProfilePeopleActivity.index = 3;
                                    startActivity(new Intent(FavoritesActivity.this, ProfilePeopleActivity.class));

                                }
                            });

                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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

            if(profilePictureUrl.equals("none")) {
                return;
            }

            Glide.with(context)
                    .load(profilePictureUrl)
                    .apply(new RequestOptions().placeholder(R.drawable.profile_pic))
                    .into(imageView);


        }
    }

    private void removeDeletedUserFromFavorites() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("This user is no longer exists");

        builder1.setPositiveButton(
                "Remove",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("User Id of Deleted User", userIdOfDeletedUser);
                        favoritesDatabaseReference.child(userIdOfDeletedUser).removeValue();
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
                            startActivity(new Intent(FavoritesActivity.this, ExploreActivity.class));
                            break;
                        case R.id.nav_search:
                            startActivity(new Intent(FavoritesActivity.this, SearchActivity.class));
                            break;

                        case R.id.nav_add_picture:
                            startActivity(new Intent(FavoritesActivity.this, AddPictureActivity.class));
                            break;

                        case R.id.nav_profile:
                            startActivity(new Intent(FavoritesActivity.this, ProfileActivity.class));

                            break;
                    }

                    return true;
                }
            };



}
