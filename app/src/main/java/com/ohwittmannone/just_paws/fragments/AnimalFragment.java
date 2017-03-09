package com.ohwittmannone.just_paws.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.ohwittmannone.just_paws.R;
import com.ohwittmannone.just_paws.adapters.AnimalAdapter;
import com.ohwittmannone.just_paws.models.AnimalType;
import com.ohwittmannone.just_paws.utils.Common;

import java.util.ArrayList;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by Courtney on 2017-02-20.
 */

public class AnimalFragment extends Fragment{

    ArrayList<AnimalType> animal = new ArrayList<>();
    ArrayList<AnimalType> filteredanimal = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private AnimalAdapter animalAdapter;
    private RecyclerView.LayoutManager mCardLayoutManager;
    private ProgressBar progressBar;
    private DatabaseReference reference;
    private ArrayList<String> favouritesList;

    DatabaseReference mDatabaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getFavourites();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_animals, container, false);

        //cardview
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.card_view);

        //setup firebase
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Common.ANIMALTYPE);

        //create adapter class
        //mCardAdapter = new AnimalAdapter(mAimalTypeList);
        animalAdapter = new AnimalAdapter(getActivity().getApplicationContext(),retrieve());
        mRecyclerView.setAdapter(animalAdapter);

        //add linear layout manager
        mCardLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mCardLayoutManager);

        progressBar = (ProgressBar)rootView.findViewById(R.id.progressbardog);


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query){
                animalAdapter.setCurrentSearchText(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText){
                animalAdapter.setCurrentSearchText(newText);
                return false;
            }
        });
    }


    //Read
    public ArrayList<AnimalType> retrieve(){
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fetchData(dataSnapshot);
                Log.i(TAG, "onChildAdded");
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return animal;
    }

    private void fetchData (DataSnapshot dataSnapshot){
        animal.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            AnimalType animalType = new AnimalType();
            animalType.setId(ds.getValue(AnimalType.class).getId());
            animalType.setPetInfo(ds.getValue(AnimalType.class).getPetInfo());
            animalType.setImgURL(ds.getValue(AnimalType.class).getImgURL());
            animalType.setPetName(ds.getValue(AnimalType.class).getPetName());

            animal.add(0, animalType);
            animalAdapter.notifyDataSetChanged();
            mRecyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);


        }
    }

    public void getFavourites(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            String userUid = user.getUid();
            reference = FirebaseDatabase.getInstance().getReference(Common.USER).child(userUid).child(Common.FAVOURITE);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                    favouritesList = (ArrayList<String>)dataSnapshot.getValue(t);
                        animalAdapter.setFavouritesList(favouritesList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }




}
