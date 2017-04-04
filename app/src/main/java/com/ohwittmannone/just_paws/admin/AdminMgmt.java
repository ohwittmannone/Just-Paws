package com.ohwittmannone.just_paws.admin;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ohwittmannone.just_paws.BaseCompatActivity;
import com.ohwittmannone.just_paws.R;
import com.ohwittmannone.just_paws.adapters.AdminAdapter;
import com.ohwittmannone.just_paws.models.User;
import com.ohwittmannone.just_paws.utils.Common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Courtney on 2017-03-31.
 */

public class AdminMgmt extends BaseCompatActivity {

    private RecyclerView mRecyclerView;
    private AdminAdapter mAdapter;
    private RecyclerView.LayoutManager mCardLayoutManager;

    private ChildEventListener mChildEventListener;

    public List<User> mUserList = new ArrayList<>();
    private List<User> mAdminList = new ArrayList<>();

    private DatabaseReference adminReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_mgmt);

        getUserList();

        mRecyclerView = (RecyclerView) findViewById(R.id.adminRecyclerView);
        mAdapter = new AdminAdapter(mUserList);
        mRecyclerView.setAdapter(mAdapter);

        mCardLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mCardLayoutManager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null){
            setupToolbar("Administration Console", true);
        }

        SearchView searchView = (SearchView) findViewById(R.id.searchView_admin);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.setCurrentSearchText(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.setCurrentSearchText(newText);
                return false;
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    private void getUserList(){
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final User userName = (User) dataSnapshot.getValue(User.class);
                mUserList.add(userName);
                mAdapter.reset(mUserList);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                /*User userName = (User) dataSnapshot.getValue(User.class);
                for (int i = 0; i < mUserList.size(); i++) {
                    User user = mUserList.get(i);
                    if (userName.getId().equals(userName.getId())) {
                        mUserList.set(i, userName);
                        break;
                    }
                }

                mAdapter.reset(mUserList);*/
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                /*final User userName = (User) dataSnapshot.getValue(User.class);
                for (Iterator<User> iterator = mUserList.iterator(); iterator.hasNext(); ) {
                    User user = iterator.next();
                    if (user.getId().equals(userName.getId())) {
                        iterator.remove();
                        break;
                    }
                }
                mAdapter.reset(mUserList);*/

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseDatabase.getInstance().getReference(Common.USER).addChildEventListener(mChildEventListener);

    }

}
