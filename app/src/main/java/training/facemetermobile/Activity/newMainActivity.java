package training.facemetermobile.Activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

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
import com.mikepenz.fastadapter.utils.RecyclerViewCacheUtil;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.HashMap;

import io.realm.Realm;
import training.facemetermobile.Configuration.Config;
import training.facemetermobile.Model.Person;
import training.facemetermobile.R;

/**
 * Created by agrfiqie6136 on 07/11/2016.
 */

public class newMainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,BaseSliderView.OnSliderClickListener
        , ViewPagerEx.OnPageChangeListener{

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
    CollapsingToolbarLayout collapsingToolbarLayoutAndroid;
    GridView androidGridView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigation;
    private ViewFlipper myViewFlipper;
    private float initialXPoint;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private TextView nameProfile,emailProfiles;
    private ImageView imageProfile;
    private GoogleApiClient mGoogleApiClient;

    private SliderLayout mDemoSlider;

    Realm realm;

    private Uri mCropImageUri;

    private String personName ="",emailProfile ="",personPhotoUrl =""
            ,personFacebookName="",emailFacebookProfile ="", personFacebookPhotoUrl ="",imagefilePath="";
    private static final String TAG = MainActivity.class.getSimpleName();

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private Context context;

    private static final int PROFILE_SETTING = 100000;

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;

    private double latCurPerson,longCurPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//      Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_new_main);


        mDemoSlider = (SliderLayout)findViewById(R.id.slider);

        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Hannibal",R.drawable.hannibal);
        file_maps.put("House of Cards",R.drawable.house);
        file_maps.put("Game of Thrones", R.drawable.game_of_thrones);

        for(String name : file_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);


        String token = FirebaseInstanceId.getInstance().getToken();
        Firebase.setAndroidContext(this);
        String msg = "Token Format : "+token;
        Log.d(TAG, msg);

        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(newMainActivity.this, LoginActivity.class));
                }
            }
        };

        //myViewFlipper = (ViewFlipper) findViewById(R.id.myflipper);

        nameProfile = (TextView) findViewById(R.id.name_profile);
        emailProfiles = (TextView) findViewById(R.id.email);
        imageProfile = (ImageView)findViewById(R.id.image_profile);

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

            FirebaseStorage storage = FirebaseStorage.getInstance();
            final StorageReference downloadimageref = storage.getReferenceFromUrl("gs://facemeter-fd8fc.appspot.com");
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
                    // Handle any errors
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
                            Log.d(TAG, "LatcurPerson:" + latCurPerson);
                            Log.d(TAG, "ada orang didekat!!");
                            Log.d(TAG, "OrangLatPerson:" + person.getLatitude());
                            AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                            Intent intent = new Intent(newMainActivity.this, FindNearbyActivity.class);
                            alarmIntent = PendingIntent.getBroadcast(newMainActivity.this, 0, intent, 0);
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

        CustomGridViewActivity adapterViewAndroid = new CustomGridViewActivity(newMainActivity.this, gridViewStrings, gridViewImages);
        androidGridView = (GridView) findViewById(R.id.grid_view_image_text);
        androidGridView.setAdapter(adapterViewAndroid);
        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                if (i == 0) {
                    Intent intent = new Intent(view.getContext(), AwesomenessRateActivity.class);
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

        final IProfile profile = new ProfileDrawerItem().withName(personName).withEmail(emailProfile).withIcon(personPhotoUrl).withIdentifier(100);

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        profile,
                        //don't ask but google uses 14dp for the add account icon in gmail but 20dp for the normal icons (like manage account)
                        new ProfileSettingDrawerItem().withName("Add Account").withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_plus).actionBar().paddingDp(5).colorRes(R.color.material_drawer_primary_text)).withIdentifier(PROFILE_SETTING),
                        new ProfileSettingDrawerItem().withName("Manage Account").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(100001)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)

                        selectImage();

                        if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_SETTING) {
                            if (auth.getCurrentUser() != null || personName != "" ) {
                                new AlertDialog.Builder(newMainActivity.this)
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

                                if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_SETTING) {
                                    int count = 100 + headerResult.getProfiles().size() + 1;
                                    IProfile newProfile = new ProfileDrawerItem().withNameShown(true).withName(personName + count).withEmail(emailProfile).withIcon(personPhotoUrl).withIdentifier(count);
                                    if (headerResult.getProfiles() != null) {
                                        //we know that there are 2 setting elements. set the new profile above them ;)
                                        headerResult.addProfile(newProfile, headerResult.getProfiles().size() - 2);
                                    } else {
                                        headerResult.addProfiles(newProfile);
                                    }
                                }
                            }

                        }

                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }

                })
                .withSavedInstance(savedInstanceState)
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withHasStableIds(true)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Profile").withDescription("Setting your profile here!").withIcon(GoogleMaterial.Icon.gmd_account).withIdentifier(1).withSelectable(false),
                        new PrimaryDrawerItem().withName("Find Nearby").withDescription("Find Score Nearby").withIcon(GoogleMaterial.Icon.gmd_my_location).withIdentifier(2).withSelectable(false),
                        new PrimaryDrawerItem().withName("History").withDescription("History of Your Face Score").withIcon(GoogleMaterial.Icon.gmd_dock).withIdentifier(3).withSelectable(false),
                        new PrimaryDrawerItem().withName("Facial Date").withDescription("Improve Your Score With Facial Time").withIcon(GoogleMaterial.Icon.gmd_calendar_account).withIdentifier(4).withSelectable(false).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_red_700)),
                        new SecondaryDrawerItem().withName("Sign In").withIcon(GoogleMaterial.Icon.gmd_my_location).withIdentifier(20).withSelectable(false)
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //check if the drawerItem is set.
                        //there are different reasons for the drawerItem to be null
                        //--> click on the header
                        //--> click on the footer
                        //those items don't contain a drawerItem

                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1) {
                                intent = new Intent(newMainActivity.this, EditProfileActivity.class);
                                intent.putExtra("personName", personName);
                            } else if (drawerItem.getIdentifier() == 2) {
                                intent = new Intent(newMainActivity.this, FindNearbyActivity.class);
                                intent.putExtra("personName", personName);
                            } else if (drawerItem.getIdentifier() == 3) {
                                intent = new Intent(newMainActivity.this, FaceHistory.class);
                            } else if (drawerItem.getIdentifier() == 4) {
                                if(personName==""){
                                    Toast.makeText(getApplicationContext(),"You Have Must Loggin First for Activate This Feature",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Calendar cal = Calendar.getInstance();
                                    Intent intent4 = new Intent(Intent.ACTION_EDIT);
                                    intent4.setType("vnd.android.cursor.item/event");
                                    intent4.putExtra("beginTime", cal.getTimeInMillis());
                                    intent4.putExtra("allDay", true);
                                    intent4.putExtra("rrule", "FREQ=YEARLY");
                                    intent4.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
                                    intent4.putExtra("title", "Try to Facial for Better in Facemeter");
                                    startActivity(intent4);
                                    }
                            } else if (drawerItem.getIdentifier() == 5) {
                                intent = new Intent(newMainActivity.this, LoginActivity.class);
                            }
                            if (intent != null) {
                                newMainActivity.this.startActivity(intent);
                            }
                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        new RecyclerViewCacheUtil<IDrawerItem>().withCacheSize(2).apply(result.getRecyclerView(), result.getDrawerItems());

        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 11
            result.setSelection(21, false);

            //set the active profile
            headerResult.setActiveProfile(profile);
        }

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
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialXPoint = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float finalx = event.getX();
                if (initialXPoint > finalx) {
                    if (myViewFlipper.getDisplayedChild() == image.length)
                        break;
                    myViewFlipper.showNext();
                } else {
                    if (myViewFlipper.getDisplayedChild() == 0)
                        break;
                    myViewFlipper.showPrevious();
                }
                break;
        }
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //@Override
    //public void onPostCreate(Bundle savedInstanceState) {
     //   super.onPostCreate(savedInstanceState);
     //   drawerToggle.syncState();
    //}

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

            // For API >= 23 we need to check specifically that we have permissions to read external storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
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

                imagefilePath = mCropImageUri.getPath();
                Log.d(TAG, "filepath: " + imagefilePath);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://facemeter-fd8fc.appspot.com");
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
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}
}