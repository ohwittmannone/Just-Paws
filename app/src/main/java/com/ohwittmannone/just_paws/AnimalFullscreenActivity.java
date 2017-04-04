package com.ohwittmannone.just_paws;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ohwittmannone.just_paws.Picasso.PicassoClient;
import com.ohwittmannone.just_paws.adapters.AnimalAdapter;
import com.ohwittmannone.just_paws.models.AnimalType;

import java.util.List;

/**
 * Created by Courtney on 2017-04-04.
 */

public class AnimalFullscreenActivity extends Activity {
    private Integer position;
    public List<AnimalType> mAnimalList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_animal);

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
        ImageView imgDisplay;
        ImageView btnClose;

        imgDisplay = (ImageView) findViewById(R.id.imgDisplay);
        btnClose = (ImageView) findViewById(R.id.btnClose);

        mAnimalList = AnimalAdapter.getAnimalList();

        PicassoClient.downloadInsideImage(this, mAnimalList.get(position).getImgURL(), imgDisplay);

        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnimalFullscreenActivity.this.finish();
            }
        });


    }
}
