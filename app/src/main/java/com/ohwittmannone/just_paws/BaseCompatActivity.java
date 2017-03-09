package com.ohwittmannone.just_paws;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by Courtney on 2017-02-21.
 */

public class BaseCompatActivity extends AppCompatActivity {

    ActionBar actionBar;
    CollapsingToolbarLayout collapsingToolbar;
    protected ProgressDialog progressDialog;

    public void setupToolbar (String title){
        setupToolbar(title, false);
    }

    public void setupToolbar (String title, boolean displayUp){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setDisplayHomeAsUpEnabled(displayUp);
    }

    public void setupCollapsibleToolbar(String title, boolean setVisibleOnlyCollapse){
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(title);

        if (setVisibleOnlyCollapse){
            collapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
        }
    }

    public void setupCollapsingToolbar(String title){
        setupCollapsibleToolbar(title, false);
    }

    public void setTitle(CharSequence title){
        if (collapsingToolbar != null){
            collapsingToolbar.setTitle(title);
        } else if (actionBar != null){
            actionBar.setTitle(title);
        }
    }

    public void showProgress(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void hideProgress(){
        if(progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}
