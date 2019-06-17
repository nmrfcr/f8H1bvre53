package com.tekapic;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlbumsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlbumsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumsFragment extends Fragment implements AlbumsRecyclerViewAdapter.ListItemClickListener {

    private TextView textView, textView2;
    private Button button;
    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private ArrayList<Album> albumsList=new ArrayList<Album>() ;
    private AlbumsRecyclerViewAdapter adapter;
    private Map<String, Boolean> albumsMap = new HashMap<>();
    private Context context;
    private AlbumsRecyclerViewAdapter.ListItemClickListener mOnClickListener;
    public static int firstVisibleItemPosition = 0;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AlbumsFragment() {
        // Required empty public constructor
    }
    private void getUserAlbums() {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference usersdRef = rootRef.child("Users").child(mAuth.getUid()).child("Pictures");

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

                    String me = ds.child("me").getValue(String.class);
                    String family = ds.child("family").getValue(String.class);
                    String friends = ds.child("friends").getValue(String.class);
                    String love = ds.child("love").getValue(String.class);
                    String pets = ds.child("pets").getValue(String.class);
                    String nature = ds.child("nature").getValue(String.class);
                    String sport = ds.child("sport").getValue(String.class);
                    String persons = ds.child("persons").getValue(String.class);
                    String animals = ds.child("animals").getValue(String.class);
                    String vehicles = ds.child("vehicles").getValue(String.class);
                    String views = ds.child("views").getValue(String.class);
                    String food = ds.child("food").getValue(String.class);
                    String things = ds.child("things").getValue(String.class);
                    String funny = ds.child("funny").getValue(String.class);
                    String places = ds.child("places").getValue(String.class);
                    String art = ds.child("art").getValue(String.class);

                    if(me.equals("1") && meDoesntExist) {
                        albumsMap.put("me", true);
                        meDoesntExist = false;
                    }
                    if(family.equals("1") && familyDoesntExist) {
                        albumsMap.put("family", true);
                        familyDoesntExist = false;
                    }
                    if(friends.equals("1") && friendsDoesntExist) {
                        albumsMap.put("friends", true);
                        friendsDoesntExist = false;
                    }
                    if(love.equals("1") && loveDoesntExist) {
                        albumsMap.put("love", true);
                        loveDoesntExist = false;
                    }
                    if(pets.equals("1") && petsDoesntExist) {
                        albumsMap.put("pets", true);
                        petsDoesntExist = false;
                    }
                    if(nature.equals("1") && natureDoesntExist) {
                        albumsMap.put("nature", true);
                        natureDoesntExist = false;
                    }
                    if(sport.equals("1") && sportDoesntExist) {
                        albumsMap.put("sport", true);
                        sportDoesntExist = false;
                    }
                    if(persons.equals("1") && personsDoesntExist) {
                        albumsMap.put("persons", true);
                        personsDoesntExist = false;
                    }
                    if(animals.equals("1") && animalsDoesntExist) {
                        albumsMap.put("animals", true);
                        animalsDoesntExist = false;
                    }
                    if(vehicles.equals("1") && vehiclesDoesntExist) {
                        albumsMap.put("vehicles", true);
                        vehiclesDoesntExist = false;
                    }
                    if(views.equals("1") && viewsDoesntExist) {
                        albumsMap.put("views", true);
                        viewsDoesntExist = false;
                    }
                    if(food.equals("1") && foodDoesntExist) {
                        albumsMap.put("food", true);
                        foodDoesntExist = false;
                    }
                    if(things.equals("1") && thingsDoesntExist) {
                        albumsMap.put("things", true);
                        thingsDoesntExist = false;
                    }
                    if(funny.equals("1") && funnyDoesntExist) {
                        albumsMap.put("funny", true);
                        funnyDoesntExist = false;
                    }
                    if(places.equals("1") && placesDoesntExist) {
                        albumsMap.put("places", true);
                        placesDoesntExist = false;
                    }
                    if(art.equals("1") && artDoesntExist) {
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        usersdRef.addValueEventListener(eventListener);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlbumsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlbumsFragment newInstance(String param1, String param2) {
        AlbumsFragment fragment = new AlbumsFragment();
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

        View view = getLayoutInflater().inflate(R.layout.albums_tab, null);
        TabLayout tabLayout = getActivity().findViewById(R.id.tabs);
        tabLayout.getTabAt(1).setCustomView(view);

        textView2 = getActivity().findViewById(R.id.t4);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_albums, container, false);

        textView = rootView.findViewById(R.id.albumsTextView);
        button =  rootView.findViewById(R.id.albumsButton);

        mOnClickListener = this;

        context = getContext();

        mAuth = FirebaseAuth.getInstance();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());


        mRecyclerView = rootView.findViewById(R.id.albumsFragmentRecyclerView);

        checkIfUserHasAnyPictures();


        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        getUserAlbums();

        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(firstVisibleItemPosition);
        firstVisibleItemPosition = 0;

        return  rootView;

    }

    @Override
    public void onListItemClick(int clickedItemIndex, String album) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return;
        }

        PicturesActivity.wantedAlbum = album;
        Intent intent = new Intent(getActivity(), PicturesActivity.class);
        startActivity(intent);

    }
    private void checkIfUserHasAnyPictures() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());

        databaseReference.child("Pictures").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    textView.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
//                    isUserhasPics = false;
                    mRecyclerView.setVisibility(View.GONE);

                }
                else {
                    textView.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
//                    isUserhasPics = true;
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
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

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
        }
    }
    @Override
    public void onPause() {
        super.onPause();

        firstVisibleItemPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

    }
}
