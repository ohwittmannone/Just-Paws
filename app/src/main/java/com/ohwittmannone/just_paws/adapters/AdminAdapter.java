package com.ohwittmannone.just_paws.adapters;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ohwittmannone.just_paws.R;
import com.ohwittmannone.just_paws.models.User;
import com.ohwittmannone.just_paws.utils.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Courtney on 2017-03-31.
 */

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.ViewHolder> {
    private List<User> mOriginalUserList = new ArrayList<>();
    private static List<User> mDisplayUserListData = new ArrayList<>();
    private List<User> mAdminList = new ArrayList<>();

    private String mCurrentSearchText;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameText;
        public ImageView adminToggle;

        public ViewHolder(View v) {
            super(v);

            nameText = (TextView) v.findViewById(R.id.user_name);
            adminToggle = (ImageView) v.findViewById(R.id.admin_icon);
        }
    }

    //constructor
    public AdminAdapter(List<User> mOriginalUserList) {
        this.mOriginalUserList = mOriginalUserList;
    }

    //create views
    @Override
    public AdminAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);

        return viewHolder;
    }

    //replace contents of view
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //put in data
        final User userModel = mDisplayUserListData.get(holder.getAdapterPosition());
        holder.nameText.setText(userModel.getName());

        //check if admin
        if (mAdminList != null) {
            if (userModel.isAdmin()) {
                holder.adminToggle.setImageResource(R.drawable.ic_person_teal_24dp);
            } else {
                holder.adminToggle.setImageResource(R.drawable.ic_person_black_24dp);
            }

            holder.adminToggle.setOnClickListener(new View.OnClickListener() {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.USER).child(userModel.getId()).child("admin");

                @Override
                public void onClick(View view) {
                    //make not admin
                    if (mAdminList != null && userModel.isAdmin()) {
                        holder.adminToggle.setImageResource(R.drawable.ic_person_black_24dp);
                        mAdminList.remove(userModel);
                        userModel.setAdmin(false);
                        reference.setValue(false);
                    } else {
                        holder.adminToggle.setImageResource(R.drawable.ic_person_teal_24dp);
                        if (mAdminList == null) { //if favourites list is null
                            mAdminList = new ArrayList<>();
                            mAdminList.add(userModel);
                        } else {
                            mAdminList.add(userModel);
                        }
                        userModel.setAdmin(true);
                        reference.setValue(true);
                    }
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return mDisplayUserListData.size();
    }

    //call this method when the server data is changed
    public void reset(List<User> userData) {
        this.mOriginalUserList = userData;
        generateData();
    }

    public void generateData() {
        mDisplayUserListData.clear();

        if (mCurrentSearchText != null && !mCurrentSearchText.isEmpty()) {
            String text = mCurrentSearchText.toLowerCase();
            for (User item : mOriginalUserList) {
                if (item.getName().toLowerCase().contains(text)) {
                    mDisplayUserListData.add(item);
                }
            }
        } else {
            mDisplayUserListData.addAll(mOriginalUserList);
        }

        notifyDataSetChanged();
    }

    public void setCurrentSearchText(String text) {
        mCurrentSearchText = text;
        generateData();
    }

}