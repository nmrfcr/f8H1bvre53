package com.tekapic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference databaseReferenceLikes;

    private FirebaseAuth mAuth;
    private android.support.v7.app.ActionBar actionBar;


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


        actionBar = getSupportActionBar();

        mAuth = FirebaseAuth.getInstance();

        mDatabaseReference =  FirebaseDatabase.getInstance().getReference().child("Users");

        databaseReferenceLikes = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mAuth.getUid()).child("Pictures").child(PictureActivity.picture.getPictureId()).child("Likes");

        mRecyclerView = findViewById(R.id.likes_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getLikesFromFirebase();
    }

    private void getLikesFromFirebase() {

        Query query = databaseReferenceLikes;

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                //go back
                }
                else {
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

        FirebaseRecyclerAdapter<User, LikesActivity.UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, LikesActivity.UserViewHolder>
                (options) {
            @Override
            protected void onBindViewHolder(@NonNull final LikesActivity.UserViewHolder holder, int position, @NonNull final User model) {


                mDatabaseReference.child(model.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final User user = new User();
                        user.setUserId(dataSnapshot.child("userId").getValue(String.class));
                        user.setUsername(dataSnapshot.child("username").getValue(String.class));
                        holder.setDetails(user.getUsername());

                        holder.textView.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                if(mAuth.getUid().equals(user.getUserId())) {
                                    finish();
                                    startActivity(new Intent(LikesActivity.this, HomeActivity.class));
                                    return;
                                }

                                HomePeopleActivity.flag = 2;
                                HomePeopleActivity.user = user;
                                HomePeopleActivity.firstVisibleItemPosition = 0;
                                startActivity(new Intent(LikesActivity.this, HomePeopleActivity.class));
                            }
                        });
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

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
        }
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public TextView textView;



        public UserViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            textView = mView.findViewById(R.id.email_result);
        }

        public void setDetails(String userEmail) {


            textView.setText(userEmail);

        }
    }

//    @Override
//    public void onBackPressed() {
//        finish();
//        startActivity(new Intent(LikesActivity.this, HomeActivity.class));
//    }

}
