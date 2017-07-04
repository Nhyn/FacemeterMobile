package training.facemetermobile.Activity;

/**
 * Created by agrfiqie6136 on 04/10/2016.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import training.facemetermobile.R;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth auth;
    String personPhotoUrl;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;
    private CallbackManager callbackmanager;

    @BindView(R.id.email) EditText inputEmail;
    @BindView(R.id.password) EditText inputPassword;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.btn_signup) Button btnSignup;
    @BindView(R.id.btn_login) Button btnLogin;
    @BindView(R.id.btn_Google) Button btnGoogle;
    @BindView(R.id.btn_Facebook) LoginButton btnFacebook;
    @BindView(R.id.btn_reset_password) Button btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackmanager = CallbackManager.Factory.create();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        ButterKnife.bind(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(AppIndex.API).build();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String personName = user.getDisplayName();
                                String emailProfile = user.getEmail();

                                if (!task.isSuccessful()) {
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("personName", personName);
                                    intent.putExtra("emailProfile", emailProfile);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });

//              Crashlytics.setUserIdentifier(email);
//              Crashlytics.setUserEmail(email);
//              Crashlytics.setUserName();
            }
        });


        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        btnFacebook.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        btnFacebook.registerCallback(callbackmanager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        AccessToken accessToken = loginResult.getAccessToken();
                        Profile profile = Profile.getCurrentProfile();

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        Log.v("LoginActivity Response ", response.toString());

                                        try {
                                            String personFacebookName = object.getString("name");
                                            String emailFacebookProfile = object.getString("email");
                                            String id = Profile.getCurrentProfile().getId();

                                            Log.v("Email = ", " " + emailFacebookProfile);

                                            String personFacebookPhotoUrl = new URL("http://graph.facebook.com/" + id + "/picture?type=large").toString();

                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            intent.putExtra("personFacebookName", personFacebookName);
                                            intent.putExtra("emailFacebookProfile", emailFacebookProfile);
                                            intent.putExtra("personPhotoUrl", personFacebookPhotoUrl);
                                            startActivity(intent);
                                            finish();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onCancel() {
                        Log.e(TAG, "Gagal login Facebook");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.e(TAG, "Error login Facebook");
                    }
                });

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            progressBar.setVisibility(View.GONE);
            GoogleSignInAccount acct = result.getSignInAccount();

//          Log.e(TAG, "Display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            String emailProfile = acct.getEmail();
            if(acct.getPhotoUrl()!=null){personPhotoUrl = acct.getPhotoUrl().toString();}

//          Log.e(TAG, "Name: " + personName + ", email: " + emailProfile+ ", Image: " + personPhotoUrl);

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("personName", personName);
            intent.putExtra("emailProfile", emailProfile);
            intent.putExtra("personPhotoUrl", personPhotoUrl);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressBar.setVisibility(View.VISIBLE);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Login Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
        mGoogleApiClient.disconnect();
    }
}
