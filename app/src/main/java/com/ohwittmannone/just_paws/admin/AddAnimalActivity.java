package com.ohwittmannone.just_paws.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ohwittmannone.just_paws.BaseCompatActivity;
import com.ohwittmannone.just_paws.R;
import com.ohwittmannone.just_paws.models.AnimalType;
import com.ohwittmannone.just_paws.utils.Common;

import java.util.UUID;

/**
 * Created by Courtney on 2017-03-30.
 */

public class AddAnimalActivity extends BaseCompatActivity {
    private ImageView back;
    private EditText animalName, animalDescription, animalURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_animal);

        back = (ImageView) findViewById(R.id.back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        animalName = (EditText) findViewById(R.id.input_add_animal_name);
        animalDescription = (EditText) findViewById(R.id.input_add_animal_description);
        animalURL = (EditText) findViewById(R.id.input_add_animal_url);

        Button submitBtn = (Button) findViewById(R.id.btn_add_animal);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress("Adding animal...");
                AnimalType animalType = new AnimalType();
                animalType.setImgURL(animalURL.getText().toString());
                animalType.setPetName(animalName.getText().toString());
                animalType.setPetInfo(animalDescription.getText().toString());

                submit(animalType);

            }
        });
    }

    private void submit(AnimalType animalType){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.ANIMALTYPE).push();
        animalType.setId(reference.getKey());
        reference.setValue(animalType);
        hideProgress();
        finish();
    }
}
