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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class LikesActivity extends AppCompatActivity {

    private Query query;
    private ValueEventListener valueEventListener;

    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference databaseReferenceLikes;
    private Context context;

    private FirebaseAuth mAuth;
    private androidx.appcompat.app.ActionBar actionBar;
    private String userIdOfDeletedUser = "";

    private BottomNavigationView bottomNavigationView;

    public static String userId, pictureId;
//    public static int flag;
    public static int index;



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
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
        setContentView(R.layout.activity_likes);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.likes_nav);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        context = this;

        actionBar = getSupportActionBar();

        mAuth = FirebaseAuth.getInstance();

        mDatabaseReference =  FirebaseDatabase.getInstance().getReference().child("Users");

        databaseReferenceLikes = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId).child("Pictures").child(pictureId).child("Likes");

        mRecyclerView = findViewById(R.id.likes_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }



    private void getLikesFromFirebase() {

        query = databaseReferenceLikes;

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                   onBackPressed();
                }
                else {
                    actionBar.setSubtitle("(" + Long.toString(dataSnapshot.getChildrenCount()) +")");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReferenceLikes.addValueEventListener(valueEventListener);




        final FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        FirebaseRecyclerAdapter<User, LikesActivity.UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, LikesActivity.UserViewHolder>
                (options) {
            @Override
            protected void onBindViewHolder(@NonNull final LikesActivity.UserViewHolder holder, int position, @NonNull final User model) {


                mDatabaseReference.child(model.getUserId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {


                        if(!dataSnapshot.exists()) {
                            holder.setUsername("Deleted User");
                            userIdOfDeletedUser = dataSnapshot.getKey();

                            holder.linearLayout.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                    if(mAuth.getUid().equals(userId)) {
                                        removeDeletedUserFromLikes();
                                    }

                                }
                            });

                        }
                        else {

                            final User user = new User();
                            user.setUserId(dataSnapshot.child("userId").getValue(String.class));
                            user.setUsername(dataSnapshot.child("username").getValue(String.class));
                            user.setProfilePictureUrl(dataSnapshot.child("profilePictureUrl").getValue(String.class));

                            holder.setUsername(user.getUsername());

                            try {
                                holder.setProfilePicture(user.getProfilePictureUrl(), context);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            holder.linearLayout.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                    if(mAuth.getUid().equals(user.getUserId())) {
                                        startActivity(new Intent(LikesActivity.this, ProfileActivity.class));
                                        return;
                                    }

                                    ProfilePeopleActivity.user = user;
                                    ProfilePeopleActivity.index = index;
                                    startActivity(new Intent(LikesActivity.this, ProfilePeopleActivity.class));


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
            public LikesActivity.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.result_list_layout, parent, false);

                return new LikesActivity.UserViewHolder(view);
            }

        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }



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


        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return;
        }

        getLikesFromFirebase();


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

    @Override
    protected void onPause() {

        databaseReferenceLikes.removeEventListener(valueEventListener);


        super.onPause();
    }

    private void removeDeletedUserFromLikes() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("This user is no longer exists");

        builder1.setPositiveButton(
                "Remove",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("User Id of Deleted User", userIdOfDeletedUser);
                        databaseReferenceLikes.child(userIdOfDeletedUser).removeValue();
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
                            startActivity(new Intent(LikesActivity.this, ExploreActivity.class));
                            break;
                        case R.id.nav_search:
                            startActivity(new Intent(LikesActivity.this, SearchActivity.class));
                            break;

                        case R.id.nav_add_picture:
                            startActivity(new Intent(LikesActivity.this, AddPictureActivity.class));
                            break;

                        case R.id.nav_profile:
                            startActivity(new Intent(LikesActivity.this, ProfileActivity.class));

                            break;
                    }

                    return true;
                }
            };


}
