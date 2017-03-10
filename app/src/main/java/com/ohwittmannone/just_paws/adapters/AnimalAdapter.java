package com.ohwittmannone.just_paws.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ohwittmannone.just_paws.DetailCardLayout;
import com.ohwittmannone.just_paws.models.AnimalType;
import com.ohwittmannone.just_paws.R;
import com.ohwittmannone.just_paws.utils.Common;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.ohwittmannone.just_paws.fragments.AnimalFragment.NO_FAV;
import static com.ohwittmannone.just_paws.fragments.AnimalFragment.SHOW_FAV;

/**
 * Created by Courtney on 2016-10-24.
 */

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.ViewHolder> {

    private Context mContext;
    private static List<AnimalType> mDisplayListData = new ArrayList<>();
    private List<AnimalType> mDataOriginalCopy;

    private String mCurrentSearchText;

    private List<String> favouritesList;

    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference reference;

    private int mCurrentFavType = 1;



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView petInfo;
        public ImageView imgURL, btnFavourite;
        public ProgressBar progressBar;

        private Context itemContext;
        public ViewHolder(View v){
            super(v);
            imgURL = (ImageView) v.findViewById(R.id.pet_image);
            petInfo = (TextView) v.findViewById(R.id.pet_description);
            progressBar = (ProgressBar) v.findViewById(R.id.progressbar);
            btnFavourite = (ImageView) v.findViewById(R.id.favouriteBtn);

            itemContext = v.getContext();



            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){

            Intent intent= new Intent(itemContext, DetailCardLayout.class);
            Integer position = getAdapterPosition();
            intent.putExtra("CARDVIEW_POSITION", position);

            v.getContext().startActivity(intent);
        }
    }

    //constructor
    public AnimalAdapter(Context mContext, List<AnimalType> animals){
        this.mContext = mContext;
        //this.mDisplayListData = animals;
        this.mDataOriginalCopy = animals;

    }

    //create new views
    @Override
    public AnimalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_card_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);

        mContext = parent.getContext();

        return viewHolder;
    }

    //replace contents of view
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position){

        final AnimalType animalModel = mDisplayListData.get(position);
        holder.petInfo.setText(mDisplayListData.get(position).getPetName());
        ImageView img = holder.imgURL;
        if(mDisplayListData.get(position).getImgURL() != null && mDisplayListData.get(position).getImgURL().length()>0){
            Picasso.with(mContext).load(mDisplayListData.get(position).getImgURL()).resize(310,200).centerCrop().placeholder(R.drawable.placeholder).into(holder.imgURL, new Callback() {
                @Override
                public void onSuccess() {
                    holder.progressBar.setVisibility(View.GONE);
                    holder.petInfo.setVisibility(View.VISIBLE);
                    holder.imgURL.setVisibility(View.VISIBLE);
                    holder.btnFavourite.setVisibility(View.VISIBLE);
                }
                @Override
                public void onError(){

                }
            });
        }
        else {
            Picasso.with(mContext).load(R.drawable.placeholder).into(img);
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //if user logged in
        if (user != null) {
            //first check if favourited
            if (favouritesList != null && favouritesList.contains(animalModel.getId())) {
                holder.btnFavourite.setImageResource(R.drawable.ic_favorite_white_36dp);
            } else {
                holder.btnFavourite.setImageResource(R.drawable.ic_favorite_border_white_36dp);
            }

            String userUid = user.getUid();
            reference = FirebaseDatabase.getInstance().getReference(Common.USER).child(userUid).child(Common.FAVOURITE);
            holder.btnFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //if favourited, unfavourite
                    if (favouritesList != null && favouritesList.contains(animalModel.getId())){
                        holder.btnFavourite.setImageResource(R.drawable.ic_favorite_border_white_36dp);
                        favouritesList.remove(animalModel.getId());
                        reference.setValue(favouritesList);
                    }
                    else{
                        holder.btnFavourite.setImageResource(R.drawable.ic_favorite_white_36dp);
                        if(favouritesList == null){ //if favourites list is null
                            favouritesList = new ArrayList<>();
                            favouritesList.add(animalModel.getId());
                        }else{
                            favouritesList.add(animalModel.getId());
                        }
                        reference.setValue(favouritesList);
                    }
                }
            });

        } else {
            //if user not logged in
            holder.btnFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, "Login to favourite", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }


    public static List<AnimalType> getAnimalList(){
        return mDisplayListData;
    }



    public void setFavouritesList(List<String> favouritesList){
        this.favouritesList = favouritesList;
        notifyDataSetChanged();
    }

    public void generateData(){
        mDisplayListData.clear();

        if (mCurrentSearchText != null && !mCurrentSearchText.isEmpty()){
            String text = mCurrentSearchText.toLowerCase();
            for (AnimalType item : mDataOriginalCopy){
                if(item.getPetName().toLowerCase().contains(text)){
                    mDisplayListData.add(item);
                }
            }
        }
        else {
            mDisplayListData.addAll(mDataOriginalCopy);
        }

        //check fav toggle
        if (mCurrentFavType == SHOW_FAV ){
            for (Iterator<AnimalType> iterator = mDisplayListData.iterator(); iterator.hasNext();){
                AnimalType item = iterator.next();
                if(favouritesList != null && !favouritesList.contains(item.getId())){
                    iterator.remove();
                }
            }
        }

        notifyDataSetChanged();
    }

    public void setCurrentSearchText (String text){
        mCurrentSearchText = text;
        generateData();
    }

    //call this method when the server data is changed
    public void reset(List<AnimalType> data){
        this.mDataOriginalCopy = data;
        generateData();
    }


    //return size of dataset
    public int getItemCount(){
        return mDisplayListData.size();
    }

    public void setFavToggle(int type){
        mCurrentFavType = type;
        generateData();
    }

    public int getmCurrentFavType(){
        return mCurrentFavType;
    }

}
