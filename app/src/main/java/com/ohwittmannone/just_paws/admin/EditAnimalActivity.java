package com.ohwittmannone.just_paws.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ohwittmannone.just_paws.BaseCompatActivity;
import com.ohwittmannone.just_paws.MainActivity;
import com.ohwittmannone.just_paws.R;
import com.ohwittmannone.just_paws.models.AnimalType;
import com.ohwittmannone.just_paws.utils.Common;

/**
 * Created by Courtney on 2017-03-30.
 */

public class EditAnimalActivity extends BaseCompatActivity {
    private ImageView back;
    private EditText editAnimalName, editAnimalDescription, editAnimalURL;
    String animalId, nameValue, descriptionValue, urlValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_animal);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                animalId = null;
            } else {
                animalId = extras.getString("ANIMAL_ID");
            }
        } else {
            animalId = (String) savedInstanceState.getSerializable("ANIMAL_ID");
        }

        back = (ImageView) findViewById(R.id.back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editAnimalName = (EditText) findViewById(R.id.edit_animal_name);
        editAnimalDescription= (EditText) findViewById(R.id.edit_animal_description);
        editAnimalURL = (EditText) findViewById(R.id.edit_animal_url);

        getData(animalId);

        Button submitEditBtn = (Button) findViewById(R.id.btn_edit_animal);
        submitEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress("Editing animal...");
                AnimalType animalType = new AnimalType();
                animalType.setImgURL(editAnimalURL.getText().toString());
                animalType.setPetName(editAnimalName.getText().toString());
                animalType.setPetInfo(editAnimalDescription.getText().toString());

                submitChanges(animalType, animalId);
            }
        });
    }

    private void getData(String animalId){

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.ANIMALTYPE).child(animalId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AnimalType animalType = dataSnapshot.getValue(AnimalType.class);
                nameValue = animalType.getPetName();
                descriptionValue = animalType.getPetInfo();
                urlValue = animalType.getImgURL();

                editAnimalName.setText(nameValue);
                editAnimalDescription.setText(descriptionValue);
                editAnimalURL.setText(urlValue);

                reference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void submitChanges(AnimalType animalType, String animalId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Common.ANIMALTYPE).child(animalId);
        animalType.setId(reference.getKey());
        reference.setValue(animalType);
        hideProgress();
        Intent intent = new Intent(EditAnimalActivity.this, MainActivity.class);
        Toast.makeText(EditAnimalActivity.this, "Animal edited", Toast.LENGTH_SHORT).show();
        startActivity(intent);

    }
}
