package training.facemetermobile.Activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.facebook.login.LoginManager;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import training.facemetermobile.Configuration.Config;
import training.facemetermobile.Model.Person;
import training.facemetermobile.R;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    public static String[] gridViewStrings = {
            "Take Photo",
            "Awesomeness Photo",
            "Hit Rate",
            "Crash Report",
            "Multiple Photo",
            "Database",

    };
    public static int[] gridViewImages = {
            R.drawable.face1,
            R.drawable.face2,
            R.drawable.face3,
            R.drawable.face4,
            R.drawable.face5,
            R.drawable.face6

    };

    int[] image = {
            R.drawable.b2,
            R.drawable.unitednations,
            R.drawable.b3
    };

    ActionBarDrawerToggle drawerToggle;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private GoogleApiClient mGoogleApiClient;
    Realm realm;

    private Uri mCropImageUri;

    private String personName ="",emailProfile ="",personPhotoUrl =""
            ,personFacebookName="",emailFacebookProfile ="", personFacebookPhotoUrl ="";
    private static final String TAG = MainActivity.class.getSimpleName();

    private PendingIntent alarmIntent;
    private ShareActionProvider mShareActionProvider;


    private double latCurPerson,longCurPerson;

    @BindView(R.id.name_profile) TextView nameProfile;
    @BindView(R.id.email) TextView emailProfiles;
    @BindView(R.id.image_profile) ImageView imageProfile;

    @BindView(R.id.grid_view_image_text) GridView androidGridView;
    @BindView(R.id.slider) SliderLayout imageSlider;
    @BindView(R.id.android_coordinator_layout) DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view) NavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//      Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        String token = FirebaseInstanceId.getInstance().getToken();
        Firebase.setAndroidContext(this);
        String msg = "Token Format : "+token;
        Log.d(TAG, msg);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null){
            if(bundle.containsKey("personName")||bundle.containsKey("personFacebookName")){
                personName = bundle.getString("personName","");
                emailProfile = bundle.getString("emailProfile","");
                personPhotoUrl = bundle.getString("personPhotoUrl","");
                personFacebookName = bundle.getString("personFacebookName","");
                emailFacebookProfile = bundle.getString("emailFacebookProfile","");
                personFacebookPhotoUrl= bundle.getString("personFacebookPhotoUrl","");
            }
        }

        if(personName!=""||emailProfile!=""||personFacebookName!=""){
            nameProfile.setText(personName);
            emailProfiles.setText(emailProfile);

            if(personPhotoUrl!=""){
                Glide.with(getApplicationContext()).load(personPhotoUrl)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageProfile);
            }

            FirebaseStorage storage = FirebaseStorage.getInstance();
            final StorageReference downloadimageref = storage.getReferenceFromUrl(Config.FIREBASE_STORAGE);
            downloadimageref.child("images/"+personName+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getApplicationContext()).load(uri)
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imageProfile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });

            Firebase ref = new Firebase(Config.FIREBASE_URL);
            Person person = new Person();

            ref.child("Person").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Person person = postSnapshot.getValue(Person.class);

                        if (person.getName().contentEquals(personName)){
                            latCurPerson= person.getLatitude();
                            longCurPerson=person.getLongitude();
                        }

                        if((latCurPerson-person.getLatitude()<0.001)||(longCurPerson-person.getLongitude()<0.001)){
                     //       Log.d(TAG, "LatcurPerson:" + latCurPerson);
                     //       Log.d(TAG, "OrangLatPerson:" + person.getLatitude());
                            Log.d(TAG, person.getName());
                            AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                            Intent intent = new Intent(MainActivity.this, FindNearbyActivity.class);
                            alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
                            alarmMgr.set( AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), alarmIntent );
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });

            imageProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });
        }


        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Hannibal",R.drawable.hannibal);
        file_maps.put("House of Cards",R.drawable.house);
        file_maps.put("Game of Thrones", R.drawable.game_of_thrones);

        for(String name : file_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);

            imageSlider.addSlider(textSliderView);
        }
        imageSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        imageSlider.setCustomAnimation(new DescriptionAnimation());
        imageSlider.setDuration(4000);
        imageSlider.addOnPageChangeListener(this);

        CustomGridViewActivity adapterViewAndroid = new CustomGridViewActivity(MainActivity.this, gridViewStrings, gridViewImages);
        androidGridView.setAdapter(adapterViewAndroid);
        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                if (i == 0) {
                    Intent intent = new Intent(view.getContext(), FaceTrackerActivity.class);
                    startActivityForResult(intent, 0);
                }
                if (i == 1) {
                    Intent myIntent = new Intent(view.getContext(), AwesomenessRateActivity.class);
                    startActivityForResult(myIntent, 0);
                }

                if (i == 2) {
                    Intent myIntent = new Intent(view.getContext(), HitRateActivity.class);
                    startActivityForResult(myIntent, 0);
                }

                if (i == 3) {
                    Intent myIntent = new Intent(view.getContext(), CrashActivity.class);
                    startActivityForResult(myIntent, 0);
                }

                if (i == 4) {
                    Intent myIntent = new Intent(view.getContext(), HitRateActivity.class);
                    startActivityForResult(myIntent, 0);
                }

                if (i == 5) {
                    Intent myIntent = new Intent(view.getContext(), ListDatabaseActivity.class);
                    startActivityForResult(myIntent, 0);
                }
            }
        });

        initInstances();
    }

/*
   private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier("12");
        Crashlytics.setUserEmail("fiqie.b@gmail.c.om");
        Crashlytics.setUserName("fiqiebz");
   }
*/

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(item);

        String shareText = "My handsomeness is "+ personName;
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain").setText(shareText).getIntent();
        mShareActionProvider.setShareIntent(shareIntent);
        return true;
    }
    @SuppressWarnings("RestrictedApi")
    private void initInstances() {

        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        };

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.hello_world, R.string.hello_world);
        drawerLayout.setDrawerListener(drawerToggle);



        if(user == null && personName == "" )
        {
            navigation.getMenu().clear();
            navigation.inflateMenu(R.menu.navigation_items_login);
        } else
        {
            navigation.getMenu().clear();
            navigation.inflateMenu(R.menu.navigation_items_logout);
        }

        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                if (user == null && personName == "" ) {
                    switch (id) {
                        case R.id.navigation_item_1:
                            startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
                            break;
                        case R.id.navigation_item_2:
                            startActivity(new Intent(MainActivity.this, FindNearbyActivity.class));
                            break;
                        case R.id.navigation_item_3:
                            startActivity(new Intent(MainActivity.this, FaceHistory.class));
                            break;
                        case R.id.navigation_item_4:
//                          startActivity(new Intent(MainActivity.this, MemoActivity.class));
                            Toast.makeText(getApplicationContext(),"You Have Must Loggin First for Activate This Feature",Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.navigation_item_5:
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            break;
                    }
                }
                else {
                    switch (id) {
                        case R.id.navigation_item_1:
                            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
                            intent.putExtra("personName", personName);
                            startActivity(intent);
                            break;
                        case R.id.navigation_item_2:
                            Intent intent2 = new Intent(MainActivity.this, FindNearbyActivity.class);
                            intent2.putExtra("personName", personName);
                            startActivity(intent2);
                            break;
                        case R.id.navigation_item_3:
                            startActivity(new Intent(MainActivity.this, FaceHistory.class));
                            break;
                        case R.id.navigation_item_4:
                            Calendar cal = Calendar.getInstance();
                            Intent intent4 = new Intent(Intent.ACTION_EDIT);
                            intent4.setType("vnd.android.cursor.item/event");
                            intent4.putExtra("beginTime", cal.getTimeInMillis());
                            intent4.putExtra("allDay", true);
                            intent4.putExtra("rrule", "FREQ=YEARLY");
                            intent4.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
                            intent4.putExtra("title", "Try to Facial for Better in Facemeter");
                            startActivity(intent4);
                            break;
                        case R.id.navigation_item_5:
                            if (auth.getCurrentUser() != null || personName != "" ) {
                              new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Sign Out")
                                    .setMessage("Are you sure you want to sign out?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(user!= null){
                                                auth.signOut();
                                                Toast.makeText(getApplicationContext(),"You Have Been Logged Out",Toast.LENGTH_SHORT).show();
                                                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                                                startActivity(i);
                                            }
                                            if(personName != ""){
                                                Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);
                                                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                                    new ResultCallback<Status>() {
                                                        @Override
                                                        public void onResult(Status status) {
                                                            Toast.makeText(getApplicationContext(),"You Have Been Logged Out",Toast.LENGTH_SHORT).show();
                                                            Intent i=new Intent(getApplicationContext(),MainActivity.class);
                                                            startActivity(i);
                                                        }
                                                    });
                                            }
                                            if(personFacebookName != ""){
                                                 LoginManager.getInstance().logOut();
                                                 Toast.makeText(getApplicationContext(),"You Have Been Logged Out",Toast.LENGTH_SHORT).show();
                                                 Intent i=new Intent(getApplicationContext(),MainActivity.class);
                                                 startActivity(i);
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            break;
                            }
                    }
                }
                return false;
            }
        });
    }


    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//      realm.close();
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//              .enableAutoManage(this, this)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void selectImage() {
        if (CropImage.isExplicitCameraPermissionRequired(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
            }
        } else {
            CropImage.startPickImageActivity(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            boolean requirePermissions = false;
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                requirePermissions = true;
                mCropImageUri = imageUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                }
            } else {
                startCropImageActivity(imageUri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mCropImageUri = result.getUri();

                final String imagefilePath = mCropImageUri.getPath();
                Log.d(TAG, "filepath: " + imagefilePath);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl(Config.FIREBASE_STORAGE);
                StorageReference imagesProfileRef = storageRef.child("images/"+personName+".jpg");
                imagesProfileRef.putFile(mCropImageUri);
                Log.d(TAG, "personName: " + personName);

                Glide.with(getApplicationContext()).load(imagefilePath)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageProfile);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setFixAspectRatio(true)
                .start(this);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        //Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}
}

