package com.tekapic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekapic.model.Picture;

import java.util.ArrayList;
import java.util.Collections;



public class AllPicturesFragment extends Fragment implements PicturesRecyclerViewAdapter.ListItemClickListener {

    private TextView textView2;
    private TabLayout tabLayout;
    private View view;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private RecyclerView mRecyclerView;
    private TextView textView;
    private Button button;
    private LinearLayoutManager linearLayoutManager;
    private PicturesRecyclerViewAdapter adapter;
    private ArrayList<Picture> picturesList=new ArrayList<Picture>() ;
    private PicturesRecyclerViewAdapter.ListItemClickListener mOnClickListener;
    private Context context;

    public static int firstVisibleItemPosition = 0;
    public static boolean isUserhasPics = false;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AllPicturesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AllPicturesFragment newInstance(String param1, String param2) {
        AllPicturesFragment fragment = new AllPicturesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        View view = getLayoutInflater().inflate(R.layout.all_pictures_tab, null);
        TabLayout tabLayout = getActivity().findViewById(R.id.tabs);
        tabLayout.getTabAt(0).setCustomView(view);

         textView2 = getActivity().findViewById(R.id.t2);





    }
    private void checkIfUserHasAnyPictures() {
        databaseReference.child("Pictures").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    textView.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                    isUserhasPics = false;
                    mRecyclerView.setVisibility(View.GONE);

                }
                else {
                    textView.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
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

        textView2.setText("(" + Integer.toString(picturesList.size()) + ")");


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

                    Picture picture = new Picture(pictureId, pictureUrl, date, me, family, friends, love, pets, nature, sport, persons, animals, vehicles, views, food, things, funny, places, art);


                    picturesList.add(picture);

                }

                if(wasCalled) {
                    adapter.notifyDataSetChanged();
                    Log.i("HomeActivity", "notifyDataSetChanged was Called");

                }


//                actionBar.setSubtitle("(" + Integer.toString(picturesList.size()) + ")");
                textView2.setText("(" + Integer.toString(picturesList.size()) + ")");
                Collections.reverse(picturesList);
                adapter = new PicturesRecyclerViewAdapter(picturesList, mOnClickListener, context);
                mRecyclerView.setAdapter(adapter);

                wasCalled = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.child("Pictures").addValueEventListener(eventListener);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_all_pictures, container, false);




        textView = rootView.findViewById(R.id.allPicturesTextView);
        button =  rootView.findViewById(R.id.allPicturesButton);

        mRecyclerView = rootView.findViewById(R.id.allPicturesRecyclerView);




        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());

        checkIfUserHasAnyPictures();

        mRecyclerView.setHasFixedSize(true);

        linearLayoutManager = new GridLayoutManager(getContext(),3);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(3));

        mOnClickListener =  this;
        context = getContext();

        getPictures();

        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(firstVisibleItemPosition);
        firstVisibleItemPosition = 0;






        return rootView;
    }

    @Override
    public void onListItemClick(int clickedItemIndex, Picture picture, int picturesListSize, ArrayList<Picture> picturesList) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return;
        }

        PictureActivity.picturesList.clear();
        PictureActivity.clickedItemIndex = clickedItemIndex;

        for(Picture p : picturesList) {
            PictureActivity.picturesList.add(p);
        }

        PictureActivity.isPictureFromAlbum = false;



        Intent intent = new Intent(getActivity(), PictureActivity.class);
        startActivity(intent);



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



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onPause() {
        super.onPause();

        firstVisibleItemPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

    }





}
