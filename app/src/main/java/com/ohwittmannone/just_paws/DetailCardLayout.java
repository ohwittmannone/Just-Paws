package com.ohwittmannone.just_paws;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.ohwittmannone.just_paws.Picasso.PicassoClient;
import com.ohwittmannone.just_paws.adapters.AnimalAdapter;
import com.ohwittmannone.just_paws.models.AnimalType;
import com.ohwittmannone.just_paws.utils.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Courtney on 2017-01-13.
 */

public class DetailCardLayout extends AppCompatActivity {

    private Integer position;
    public List<AnimalType> mAnimalList;
    private List<String> favouritesList;

    private TextView petInfo, petName;
    private ImageView imgURL, btnFavourite;
    public ImageView btnBack;

    private DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_detailed);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                position = null;
            } else {
                position = extras.getInt("CARDVIEW_POSITION");
            }
        } else {
            position = (Integer) savedInstanceState.getSerializable("CARDVIEW_POSITION");
        }

        //set text and image
        petInfo = (TextView) findViewById(R.id.detail_pet_description);
        imgURL = (ImageView) findViewById(R.id.detail_pet_image);
        petName = (TextView) findViewById(R.id.petName);

        mAnimalList = AnimalAdapter.getAnimalList();

        petInfo.setText(mAnimalList.get(position).getPetInfo());
        petName.setText(mAnimalList.get(position).getPetName());
        PicassoClient.downloadImage(this, mAnimalList.get(position).getImgURL(), imgURL);

        //back button
        btnBack = (ImageView) findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        new myTask().execute();







    }

    //async
    class myTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            getFavourites();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //favouriting
            final AnimalType animalModel = mAnimalList.get(position);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            btnFavourite = (ImageView) findViewById(R.id.favouriteBtn);
            if (user != null) {
                //first check if favourited
                if (favouritesList != null && favouritesList.contains(animalModel.getId())) {
                    btnFavourite.setImageResource(R.drawable.ic_favorite_white_36dp);
                } else {
                    btnFavourite.setImageResource(R.drawable.ic_favorite_border_white_36dp);
                }
                btnFavourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //if favourited, unfavourite
                        if (favouritesList != null && favouritesList.contains(animalModel.getId())) {
                            btnFavourite.setImageResource(R.drawable.ic_favorite_border_white_36dp);
                            favouritesList.remove(animalModel.getId());
                            reference.setValue(favouritesList);
                        } else {
                            btnFavourite.setImageResource(R.drawable.ic_favorite_white_36dp);
                            if (favouritesList == null) { //if favourites list is null
                                favouritesList = new ArrayList<>();
                                favouritesList.add(animalModel.getId());
                            } else {
                                favouritesList.add(animalModel.getId());
                            }
                            reference.setValue(favouritesList);
                        }

                    }
                });
            }
            else{
                btnFavourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(DetailCardLayout.this, "Login to favourite", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
    private void getFavourites(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            String userUid = user.getUid();
            reference = FirebaseDatabase.getInstance().getReference(Common.USER).child(userUid).child(Common.FAVOURITE);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {
                    };
                    favouritesList = (ArrayList<String>) dataSnapshot.getValue(t);
                    if (favouritesList != null) {
                        setFavouritesList(favouritesList);
                    } else {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    public void setFavouritesList(List<String> favouritesList){
        this.favouritesList = favouritesList;
    }


}
