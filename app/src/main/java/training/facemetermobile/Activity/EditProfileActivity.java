package training.facemetermobile.Activity;


/**
 * Created by Samwi on 26/09/2016.
 */

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import training.facemetermobile.Configuration.Config;
import training.facemetermobile.Configuration.RealmManager;
import training.facemetermobile.Model.Person;
import training.facemetermobile.Model.RealmPerson;
import training.facemetermobile.R;

public class EditProfileActivity extends AppCompatActivity {

    @BindView(R.id.name_person) TextView namePersons;
    @BindView(R.id.age_person) TextView agePersons;
    @BindView(R.id.height_person) TextView heightPersons;
    @BindView(R.id.weight_person) TextView weightPersons;
    @BindView(R.id.description_person) TextView descriptionPersons;
    @BindView(R.id.fab) FloatingActionButton floatbutton;

    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText editTextHeights, editTextWeights, editTextAges, editTextDescriptions;

    String ageP = "", heightP = "", weightP = "", desP = "", nameP = "",
           age = "", height = "", weight = "", description = "", personName = "",nameRealm;

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ButterKnife.bind(this);

        Firebase.setAndroidContext(this);
        RealmManager.initializeRealmConfig(getApplicationContext());
        realm = RealmManager.getRealm();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            if (bundle.containsKey("personName")) {
                personName = bundle.getString("personName", "");
            }
        }

//       Log.e(TAG, "Name: " + personName );
        if (personName != "") {
            namePersons.setText(personName);
        }


        if (ageP != null) {
            Firebase ref = new Firebase(Config.FIREBASE_URL);
            ref.child("Person").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Person person = postSnapshot.getValue(Person.class);
                        nameP = person.getName();

                        if (person.getName().contentEquals(personName)) {
                            ageP = person.getAge();
                            heightP = person.getHeight();
                            weightP = person.getWeight();
                            desP = person.getDescription();

                            agePersons.setText(ageP);
                            heightPersons.setText(heightP);
                            weightPersons.setText(weightP);
                            descriptionPersons.setText(desP);
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });

        }

        floatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (personName != "") {

                    MaterialDialog dialog = new MaterialDialog.Builder(EditProfileActivity.this)
                            .title("Edit Profile")
                            .customView(R.layout.edit_item_profile, true)
                            .positiveText(android.R.string.ok)
                            .negativeText(android.R.string.cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    final Firebase ref = new Firebase(Config.FIREBASE_URL);
                                    final Person person = new Person();

                                    editTextAges = (EditText) dialog.getCustomView().findViewById(R.id.editTextAge);
                                    editTextHeights = (EditText) dialog.getCustomView().findViewById(R.id.editTextHeight);
                                    editTextWeights = (EditText) dialog.getCustomView().findViewById(R.id.editTextWeight);
                                    editTextDescriptions = (EditText) dialog.getCustomView().findViewById(R.id.editTextDescription);

                                    age = editTextAges.getText().toString().trim();
                                    height = editTextHeights.getText().toString().trim();
                                    weight = editTextWeights.getText().toString().trim();
                                    description = editTextDescriptions.getText().toString().trim();

                                    if (TextUtils.isEmpty(age)) {
                                        Toast.makeText(getApplicationContext(), "Enter your age first!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    if (TextUtils.isEmpty(height)) {
                                        Toast.makeText(getApplicationContext(), "Enter your height first!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    if (TextUtils.isEmpty(weight)) {
                                        Toast.makeText(getApplicationContext(), "Enter your weight first!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    if (TextUtils.isEmpty(description)) {
                                        Toast.makeText(getApplicationContext(), "Enter your description first!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    agePersons.setText(age);
                                    heightPersons.setText(height);
                                    weightPersons.setText(weight);
                                    descriptionPersons.setText(description);

                                    person.setName(personName);
                                    person.setAge(age);
                                    person.setHeight(height);
                                    person.setWeight(weight);
                                    person.setDescription(description);

                                    Firebase objRef = ref.child("Person");
                                    Query updateProfile = objRef.orderByChild("name").equalTo(personName);

                                    updateProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot tasksSnapshot) {
                                            if (tasksSnapshot.exists()) {
                                                for (DataSnapshot snapshot : tasksSnapshot.getChildren()) {
                                                    snapshot.getRef().child("name").setValue(personName);
                                                    snapshot.getRef().child("age").setValue(age);
                                                    snapshot.getRef().child("height").setValue(height);
                                                    snapshot.getRef().child("weight").setValue(weight);
                                                    snapshot.getRef().child("description").setValue(description);
                                                }
                                            } else {
                                                Firebase newRef = ref.child("Person").push();
                                                newRef.setValue(person);
                                                Log.e(TAG, "namePersons: " + personName);
                                                Log.e(TAG, "nameP: " + person.getName());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {
                                            System.out.println("The read failed: " + firebaseError.getMessage());
                                        }
                                    });

                                    realm = Realm.getDefaultInstance();
                                    realm.beginTransaction();
                                    RealmPerson realperson = realm.createObject(RealmPerson.class);
                                    RealmPerson user = realm.where(RealmPerson.class).equalTo("nameRealm", nameRealm).findFirst();
                                    if(user == null) {
                                        realperson.setNameRealm(personName);
                                        realperson.setAgeRealm(age);
                                        realperson.setHeightRealm(height);
                                        realperson.setWeightRealm(weight);
                                        realperson.setDescriptionRealm(description);
                                        realm.insertOrUpdate(realperson);

                                        Log.e(TAG, "Age: " + age);
                                        Log.e(TAG, "Height: " + height);

                                        realm.commitTransaction();
                                    }
                                    else{
                                        realperson.setAgeRealm(age);
                                        realperson.setHeightRealm(height);
                                        realperson.setWeightRealm(weight);
                                        realperson.setDescriptionRealm(description);
                                        realm.copyFromRealm(realperson);

                                        Log.e(TAG, "Weight: " + weight);
                                        Log.e(TAG, "Description: " + description);
                                        realm.commitTransaction();
                                    }

                                }
                            }).build();
                    dialog.show();

                } else {
                    Toast.makeText(getApplicationContext(), "You Must Loggin First!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
    }

}




