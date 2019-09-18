package com.tekapic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekapic.model.Picture;
import com.tekapic.model.User;

import java.util.ArrayList;
import java.util.Collections;



public class AllPictutesPeopleFragment extends Fragment  implements PicturesRecyclerViewAdapter.ListItemClickListener {



    private TextView noPicturesText, textView2;
    private boolean isUserhasPics = false;
//    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private DatabaseReference picturesDatabaseReference;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private PicturesRecyclerViewAdapter adapter;
    private ArrayList<Picture> picturesList=new ArrayList<Picture>() ;
    private PicturesRecyclerViewAdapter.ListItemClickListener mOnClickListener;
    private Context context;
    private boolean privateAccount;
    private DatabaseReference databaseReference2;
    private DatabaseReference databaseReference3;

    public static int flag;
    public static User user;
    public static int firstVisibleItemPosition;






    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AllPictutesPeopleFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AllPictutesPeopleFragment newInstance(String param1, String param2) {
        AllPictutesPeopleFragment fragment = new AllPictutesPeopleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    private void popUpAlertDialogConnectionError() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
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
    public void onPause() {
        super.onPause();

        firstVisibleItemPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        firstVisibleItemPosition = 0;
        View view = getLayoutInflater().inflate(R.layout.all_pictures_tab, null);
        TabLayout tabLayout = getActivity().findViewById(R.id.tabs_people);
        tabLayout.getTabAt(0).setCustomView(view);

        textView2 = getActivity().findViewById(R.id.t2);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_all_pictutes_people, container, false);


        noPicturesText = rootView.findViewById(R.id.textAllPicturesPeopleNoPics);

        picturesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUserId()).child("Pictures");

        mAuth = FirebaseAuth.getInstance();


        mRecyclerView = rootView.findViewById(R.id.allPicturesPeopleFragmentRecyclerView);

        mRecyclerView.setHasFixedSize(true);

        linearLayoutManager = new GridLayoutManager(getContext(),3);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(3));

        mOnClickListener = this;
        context = getContext();

        //here******************

       databaseReference2 =   FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUserId());
       databaseReference3 = databaseReference2;




//*************************************************************





        return rootView;
    }





    private void checkIfUserHasAnyPictures() {
        picturesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    noPicturesText.setVisibility(View.VISIBLE);
                    isUserhasPics = false;
                    mRecyclerView.setVisibility(View.GONE);
                }
                else {
                    noPicturesText.setVisibility(View.GONE);
                    isUserhasPics = true;
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPictures() {

        checkIfUserHasAnyPictures();


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersdRef = rootRef.child("Users").child(user.getUserId()).child("Pictures");

        ValueEventListener eventListener = new ValueEventListener() {

            boolean wasCalled = false;


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(wasCalled) {
                    picturesList.clear();

                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String pictureUrl = ds.child("pictureUrl").getValue(String.class);

                    String date = ds.child("date").getValue(String.class);

                    String pictureId = ds.child("pictureId").getValue(String.class);

                    Boolean me = (Boolean)ds.child("me").getValue();
                    Boolean family = (Boolean)ds.child("family").getValue();
                    Boolean friends = (Boolean)ds.child("friends").getValue();
                    Boolean love = (Boolean)ds.child("love").getValue();
                    Boolean pets = (Boolean)ds.child("pets").getValue();
                    Boolean nature = (Boolean)ds.child("nature").getValue();
                    Boolean sport = (Boolean)ds.child("sport").getValue();
                    Boolean persons = (Boolean)ds.child("persons").getValue();
                    Boolean animals = (Boolean)ds.child("animals").getValue();
                    Boolean vehicles = (Boolean)ds.child("vehicles").getValue();
                    Boolean views = (Boolean)ds.child("views").getValue();
                    Boolean food = (Boolean)ds.child("food").getValue();
                    Boolean things = (Boolean)ds.child("things").getValue();
                    Boolean funny = (Boolean)ds.child("funny").getValue();
                    Boolean places = (Boolean)ds.child("places").getValue();
                    Boolean art = (Boolean)ds.child("art").getValue();

                    Picture picture = new Picture(pictureId, pictureUrl, date, me, family,friends,love, pets,  nature,  sport,  persons, animals,  vehicles, views, food, things, funny, places,  art);

                    picturesList.add(picture);

                }

                if(wasCalled) {
                    adapter.notifyDataSetChanged();
                }

                textView2.setText("(" + Integer.toString(picturesList.size()) + ")");

                Collections.reverse(picturesList);
                adapter = new PicturesRecyclerViewAdapter(picturesList, mOnClickListener, context);
                mRecyclerView.setAdapter(adapter);

                wasCalled = true;
                mRecyclerView.scrollToPosition(firstVisibleItemPosition);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        usersdRef.addListenerForSingleValueEvent(eventListener);

    }

    @Override
    public void onListItemClick(int clickedItemIndex, Picture picture, int picturesListSize, ArrayList<Picture> picturesList) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return;
        }

        PicturePeopleActivity.picturesList.clear();
        PicturePeopleActivity.clickedItemIndex = clickedItemIndex;

        for(Picture p : picturesList) {
            PicturePeopleActivity.picturesList.add(p);
        }

        PicturePeopleActivity.isPictureFromAlbum = false;

        PicturePeopleActivity.index = ProfilePeopleActivity.index;
        Intent intent = new Intent(getActivity(), PicturePeopleActivity.class);
        startActivity(intent);

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();

        picturesList.clear();

        databaseReference2.child("privateAccount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Boolean aBoolean = (Boolean) snapshot.getValue();
                privateAccount = aBoolean.booleanValue();

                if(!privateAccount) {
                    getPictures();

                }
                else {

                    databaseReference3.child("Favorites").addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                String id = ds.child("userId").getValue(String.class);

                                Log.i("id", ds.child("userId").getValue(String.class));

                                if(id.equals(mAuth.getUid())) {

                                    Log.i("xxx", "yyyyyyyyyyyyyyyyyyyyy");

                                    privateAccount = false;

                                    getPictures();

                                    break;
                                }
                            }
                            if(privateAccount) {

                                noPicturesText.setVisibility(View.VISIBLE);
                                noPicturesText.setText("This Account is Private");


                                picturesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        textView2.setText("(" + Long.toString(dataSnapshot.getChildrenCount()) +")");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
}
