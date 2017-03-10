package com.ohwittmannone.just_paws.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
import java.util.Iterator;

/**
 * Created by Courtney on 2017-02-20.
 */

public class AnimalFragment extends Fragment{

    ArrayList<AnimalType> mAnimalModelList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private AnimalAdapter animalAdapter;
    private RecyclerView.LayoutManager mCardLayoutManager;
    private ProgressBar progressBar;
    private DatabaseReference reference;
    private ArrayList<String> favouritesList;

    private ChildEventListener mChildEventListener;

    public final static int SHOW_FAV = 0;
    public final static int NO_FAV = 1;
    private boolean isFavourite;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getAnimals();
        getFavourites();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_animals, container, false);

        //cardview
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.card_view);

        //mCardAdapter = new AnimalAdapter(mAimalTypeList);
        animalAdapter = new AnimalAdapter(getActivity().getApplicationContext(), mAnimalModelList);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(id == R.id.fav_toggle && user != null){
            if(!isFavourite) {
                item.setIcon(R.drawable.ic_favorite_white_36dp);
                animalAdapter.setFavToggle(SHOW_FAV);
                isFavourite = true;
            }
            else{
                item.setIcon(R.drawable.ic_favorite_border_white_36dp);
                animalAdapter.setFavToggle(NO_FAV);
                isFavourite = false;
            }
            return true;
        }
        else {
            Toast.makeText(getContext().getApplicationContext(), "Login to filter by favourites", Toast.LENGTH_SHORT).show();
            return false;
        }

    }




    private void getAnimals(){
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final AnimalType animalItem = (AnimalType)dataSnapshot.getValue(AnimalType.class);
                mAnimalModelList.add(animalItem);

                animalAdapter.reset(mAnimalModelList);
                mRecyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                AnimalType animalItem = (AnimalType)dataSnapshot.getValue(AnimalType.class);
                for (int i = 0; i < mAnimalModelList.size(); i++){
                    AnimalType animal = mAnimalModelList.get(i);
                    if(animalItem.getId().equals(animalItem.getId())){
                        mAnimalModelList.set(i, animalItem);
                        break;
                    }

                    animalAdapter.reset(mAnimalModelList);
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final AnimalType animalItem = (AnimalType)dataSnapshot.getValue(AnimalType.class);
                for (Iterator<AnimalType> iterator = mAnimalModelList.iterator(); iterator.hasNext();){
                    AnimalType animalType = iterator.next();
                    if (animalType.getId().equals(animalItem.getId())){
                        iterator.remove();
                        break;
                    }
                }
                animalAdapter.reset(mAnimalModelList);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseDatabase.getInstance().getReference(Common.ANIMALTYPE).addChildEventListener(mChildEventListener);
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
