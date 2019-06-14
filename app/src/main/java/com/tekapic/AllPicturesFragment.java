package com.tekapic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekapic.model.Picture;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AllPicturesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AllPicturesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllPicturesFragment extends Fragment {




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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllPicturesFragment.
     */
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


//        actionBar.setSubtitle("(" + Integer.toString(picturesList.size()) +")");

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

                    Picture picture = new Picture(pictureId, pictureUrl, date, me, family, friends, love, pets, nature, sport, persons, animals, vehicles, views, food, things, funny, places, art);


                    picturesList.add(picture);

                }

                if(wasCalled) {
                    adapter.notifyDataSetChanged();
                    Log.i("HomeActivity", "notifyDataSetChanged was Called");

                }


//                actionBar.setSubtitle("(" + Integer.toString(picturesList.size()) + ")");
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

        textView =rootView.findViewById(R.id.allPicturesTextView);
        button =  rootView.findViewById(R.id.allPicturesButton);

        mRecyclerView = rootView.findViewById(R.id.allPicturesRecyclerView);




        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());

        checkIfUserHasAnyPictures();

        mRecyclerView.setHasFixedSize(true);

        linearLayoutManager = new GridLayoutManager(getContext(),3);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(3));

//        mOnClickListener = (PicturesRecyclerViewAdapter.ListItemClickListener) getContext();
        context = getContext();

        getPictures();

        ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(firstVisibleItemPosition);
        firstVisibleItemPosition = 0;






        return rootView;
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

    @Override
    public void onPause() {
        super.onPause();

        firstVisibleItemPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

    }


}
