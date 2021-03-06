package com.tekapic;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekapic.model.Album;
import com.tekapic.model.Picture;
import com.tekapic.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AlbumsPeopleFragment extends Fragment implements AlbumsRecyclerViewAdapter.ListItemClickListener {

    private boolean privateAccount;
    private TextView noPicturesText;
    private TextView textView, textView2;
    private Button button;
    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private ArrayList<Album> albumsList=new ArrayList<Album>() ;
    private AlbumsRecyclerViewAdapter adapter;
    private Map<String, Boolean> albumsMap = new HashMap<>();
    private Context context;
    private AlbumsRecyclerViewAdapter.ListItemClickListener mOnClickListener;
    private DatabaseReference databaseReference2;
    private DatabaseReference databaseReference3;


    public static int firstVisibleItemPosition;
    public static int flag;
    public static User user;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AlbumsPeopleFragment() {
        // Required empty public constructor
    }




    private void getUserAlbums() {

        checkIfUserHasAnyPictures();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference usersdRef = rootRef.child("Users").child(ProfilePeopleActivity.user.getUserId()).child("Pictures");

        ValueEventListener eventListener = new ValueEventListener() {

            boolean wasCalled = false;

            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean meDoesntExist = true, familyDoesntExist = true, friendsDoesntExist = true;
                boolean loveDoesntExist = true, petsDoesntExist = true, natureDoesntExist = true;
                boolean sportDoesntExist = true, personsDoesntExist = true, animalsDoesntExist = true;
                boolean vehiclesDoesntExist = true, viewsDoesntExist = true, foodDoesntExist = true;
                boolean thingsDoesntExist = true, funnyDoesntExist = true, placesDoesntExist = true;
                boolean artDoesntExist = true;

                if(wasCalled) {
                    albumsList.clear();
                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

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

                    if(me && meDoesntExist) {
                        albumsMap.put("me", true);
                        meDoesntExist = false;
                    }
                    if(family && familyDoesntExist) {
                        albumsMap.put("family", true);
                        familyDoesntExist = false;
                    }
                    if(friends && friendsDoesntExist) {
                        albumsMap.put("friends", true);
                        friendsDoesntExist = false;
                    }
                    if(love && loveDoesntExist) {
                        albumsMap.put("love", true);
                        loveDoesntExist = false;
                    }
                    if(pets && petsDoesntExist) {
                        albumsMap.put("pets", true);
                        petsDoesntExist = false;
                    }
                    if(nature && natureDoesntExist) {
                        albumsMap.put("nature", true);
                        natureDoesntExist = false;
                    }
                    if(sport && sportDoesntExist) {
                        albumsMap.put("sport", true);
                        sportDoesntExist = false;
                    }
                    if(persons && personsDoesntExist) {
                        albumsMap.put("persons", true);
                        personsDoesntExist = false;
                    }
                    if(animals && animalsDoesntExist) {
                        albumsMap.put("animals", true);
                        animalsDoesntExist = false;
                    }
                    if(vehicles && vehiclesDoesntExist) {
                        albumsMap.put("vehicles", true);
                        vehiclesDoesntExist = false;
                    }
                    if(views && viewsDoesntExist) {
                        albumsMap.put("views", true);
                        viewsDoesntExist = false;
                    }
                    if(food && foodDoesntExist) {
                        albumsMap.put("food", true);
                        foodDoesntExist = false;
                    }
                    if(things && thingsDoesntExist) {
                        albumsMap.put("things", true);
                        thingsDoesntExist = false;
                    }
                    if(funny && funnyDoesntExist) {
                        albumsMap.put("funny", true);
                        funnyDoesntExist = false;
                    }
                    if(places && placesDoesntExist) {
                        albumsMap.put("places", true);
                        placesDoesntExist = false;
                    }
                    if(art && artDoesntExist) {
                        albumsMap.put("art", true);
                        artDoesntExist = false;
                    }

                }

                if(meDoesntExist) {
                    albumsMap.put("me", false);
                }
                if(familyDoesntExist) {
                    albumsMap.put("family", false);
                }
                if(friendsDoesntExist) {
                    albumsMap.put("friends", false);
                }
                if(loveDoesntExist) {
                    albumsMap.put("love", false);
                }
                if(petsDoesntExist) {
                    albumsMap.put("pets", false);
                }
                if(natureDoesntExist) {
                    albumsMap.put("nature", false);
                }
                if(sportDoesntExist) {
                    albumsMap.put("sport", false);
                }
                if(personsDoesntExist) {
                    albumsMap.put("persons", false);
                }
                if(animalsDoesntExist) {
                    albumsMap.put("animals", false);
                }
                if(vehiclesDoesntExist) {
                    albumsMap.put("vehicles", false);
                }
                if(viewsDoesntExist) {
                    albumsMap.put("views", false);
                }
                if(foodDoesntExist) {
                    albumsMap.put("food", false);
                }
                if(thingsDoesntExist) {
                    albumsMap.put("things", false);
                }
                if(funnyDoesntExist) {
                    albumsMap.put("funny", false);
                }
                if(placesDoesntExist) {
                    albumsMap.put("places", false);
                }
                if(artDoesntExist) {
                    albumsMap.put("art", false);
                }

                for(int i = 0; i < Picture.numberOfAlbums; i++) {
                    if(albumsMap.get(Picture.albumsNames[i])) {

                        if(isAdded()) {
                            int id = getResources().getIdentifier("com.tekapic:drawable/" + Picture.albumsNames[i], null, null);
                            albumsList.add(new Album(Picture.albumsNames[i], id));
                        }
                    }
                }


                if(wasCalled) {
                    adapter.notifyDataSetChanged();
                }


                ////////////////set here number of alubmsssssssssssssssssssssssss
                textView2.setText("(" + Integer.toString(albumsList.size()) + ")");


                if(albumsList.size() == 0) {
                    ///////////////////////////hereeeeeeeeeeeeeeeeeeeeeeeee
                    //show text and button
                    checkIfUserHasAnyPictures();
                    return;
                }


                adapter = new AlbumsRecyclerViewAdapter(albumsList, mOnClickListener, context);
                mRecyclerView.setAdapter(adapter);

                GridLayoutManager mGridLayoutManager = new GridLayoutManager(getContext(), 3);
                mRecyclerView.setLayoutManager(mGridLayoutManager);

                wasCalled = true;

                mRecyclerView.scrollToPosition(firstVisibleItemPosition);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        usersdRef.addListenerForSingleValueEvent(eventListener);
    }
    private void checkIfUserHasAnyPictures() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(ProfilePeopleActivity.user.getUserId());

        databaseReference.child("Pictures").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    noPicturesText.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
                else {
                    noPicturesText.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    // TODO: Rename and change types and number of parameters
    public static AlbumsPeopleFragment newInstance(String param1, String param2) {
        AlbumsPeopleFragment fragment = new AlbumsPeopleFragment();
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

        firstVisibleItemPosition = 0;

        View view = getLayoutInflater().inflate(R.layout.albums_tab, null);
        TabLayout tabLayout = getActivity().findViewById(R.id.tabs_people);
        tabLayout.getTabAt(1).setCustomView(view);

        textView2 = getActivity().findViewById(R.id.t4);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_albums_people, container, false);

        noPicturesText = rootView.findViewById(R.id.textAlbumsPeopleNoPics);

        mOnClickListener = this;
        context = getContext();

        mAuth = FirebaseAuth.getInstance();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView = rootView.findViewById(R.id.albumsPeopleFragmentRecyclerView);


        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);


        //here******************

        databaseReference2 =   FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUserId());
        databaseReference3 = databaseReference2;




//*************************************************************


        return  rootView;

    }



    @Override
    public void onListItemClick(int clickedItemIndex, String album) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return;
        }

        if(privateAccount) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            builder1.setMessage("You need to be in " + ProfilePeopleActivity.user.getUsername() + "'s favorites.");

            builder1.setPositiveButton(
                    "CLOSE",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            AlertDialog alertDialog = builder1.create();
            alertDialog.show();

            return;
        }

        PicturesPeopleActivity.username = user.getUsername();
        PicturesPeopleActivity.index = ProfilePeopleActivity.index;
        PicturesPeopleActivity.wantedAlbum = album;
        Intent intent = new Intent(getActivity(), PicturesPeopleActivity.class);
        startActivity(intent);


    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onResume() {
        super.onResume();

        albumsList.clear();

        databaseReference2.child("privateAccount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Boolean aBoolean = (Boolean) snapshot.getValue();
                privateAccount = aBoolean.booleanValue();

                if(!privateAccount) {
                    getUserAlbums();
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

                                    getUserAlbums();

                                    break;
                                }

                            }

                            if(privateAccount) {
                                getUserAlbums();
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
    @Override
    public void onPause() {
        super.onPause();

        firstVisibleItemPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

    }
}
