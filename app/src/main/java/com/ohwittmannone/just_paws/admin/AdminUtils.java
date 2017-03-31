package com.ohwittmannone.just_paws.admin;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.ohwittmannone.just_paws.DetailCardLayout;
import com.ohwittmannone.just_paws.models.User;
import com.ohwittmannone.just_paws.utils.Common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Courtney on 2017-03-31.
 */

public class AdminUtils {

    private static DatabaseReference reference, referenceDelete, referenceDeleteUser;

    public static void deleteAnimal(final String petID, Context context){
        reference = FirebaseDatabase.getInstance().getReference(Common.ANIMALTYPE).child(petID);
        reference.removeValue();

        referenceDelete = FirebaseDatabase.getInstance().getReference(Common.USER);

        referenceDelete.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {
                    };
                    List<String> userFavourites = user.getFavourites();
                    /*for (int i = 0; i < userFavourites.size(); i++){
                        if (userFavourites.get(i).equals(petID)){

                        }
                    }*/
                    if (userFavourites != null) {
                        for (Iterator<String> iterator = userFavourites.iterator(); iterator.hasNext(); ) {
                            String favouriteAnimal = iterator.next();
                            if (favouriteAnimal.equals(petID)) {
                                iterator.remove();
                                referenceDeleteUser = FirebaseDatabase.getInstance().getReference(Common.USER).child(user.getId()).child(Common.FAVOURITE);
                                referenceDeleteUser.setValue(userFavourites);
                                break;
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toast.makeText(context, "Animal deleted", Toast.LENGTH_SHORT).show();
    }
}
