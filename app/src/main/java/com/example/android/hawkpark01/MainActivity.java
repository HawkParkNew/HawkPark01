package com.example.android.hawkpark01;

import com.example.android.hawkpark01.models.HomeLotDB;
import com.example.android.hawkpark01.models.UserDB;
import com.example.android.hawkpark01.utils.SpaceCalculator;
import com.google.android.gms.common.api.GoogleApiClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.content.Intent;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static android.R.attr.accountType;
import static com.example.android.hawkpark01.utils.GeofenceConstants.lot60;
import static com.example.android.hawkpark01.utils.Utils.EMAIL_KEY;
import static com.example.android.hawkpark01.utils.Utils.ID_KEY;

/**
 * google sign-in code modified from-
 * https://firebase.google.com/docs/auth/android/google-signin
 */

public class
MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference userDatabaseReference;

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private TextView tv_welcome, mStatusTextView;
    SessionManager session;
    String R2P = "N";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        R2P="N";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize db and get references to required child nodes
        FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
        userDatabaseReference = mdatabase.getReference("users");

        //initialize session manager for shared prefs
        session = new SessionManager(getApplicationContext());

        //initialize buttons and text views
        SignInButton btn_login_submit = (SignInButton) findViewById(R.id.btn_sign_in);
        Button btn_logout = (Button) findViewById(R.id.btn_logout_lf);
        Button btn_revoke = (Button) findViewById(R.id.btn_revoke_access);
        mStatusTextView = (TextView)findViewById(R.id.tv_status_lf);

        //set listeners on buttons
        btn_login_submit.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        btn_revoke.setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    // get email,Uid,name, photoUrl
                    final String firebaseID = user.getUid();
                    String email = user.getEmail();
                    String displayName = user.getDisplayName();
                    String photoUrl = user.getPhotoUrl().toString();

                    writeNewUser(userDatabaseReference,firebaseID,displayName,email,photoUrl);
                    //TODO-IF TIME PERMITS- IF R2P N, THEN PROMPT USER TO CREATE ACCOUNT- USE DIALOG - DIRECT USER TO REG IF YES ELSE TO HOME PAGE
                    //TODO-check if lotsdb has been updated in the past 30 minutes else update from assumptions(Space Calculator)

                    //todo--if photoUrl is null substitute with our own generic
                    //Store userid as shared pref
                    session.createUserSPSession(firebaseID,displayName,photoUrl,email);

                    //direct user to home activity
                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(i);
                }
                else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }
    //handle button click events
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_sign_in) {
            signIn();
        } else if (i == R.id.btn_logout_lf) {
            signOut();
        } else if (i == R.id.btn_revoke_access) {
            revokeAccess();
        }
    }
    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        //TODO CREATE METHOD TO ATTACH AND DETACH DB READ LISTENERS AS REQUIRED
        //detachDatabaseReadListener();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else {
                // Google Sign In failed, update UI appropriately
                updateUI(null);
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, R.string.toast_auth_failed_ma,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void signOut() {
        //firebase sign-out
        mAuth.signOut();
        //google sign-out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
        //SharedPref logout
        if(session.isLoggedIn())
            session.logoutUser();
    }
    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();
        //SharedPref logout
        if(session.isLoggedIn())
            session.logoutUser();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });
    }
    private void updateUI(FirebaseUser user) {

        if (user != null) {
            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
            findViewById(R.id.btn_sign_in).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);
            findViewById(R.id.btn_sign_in).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }
    @Override

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, getText(R.string.x_connect_play_services_toast), Toast.LENGTH_SHORT).show();
    }

    public static void writeNewUser(DatabaseReference databaseReference, String userId, String name,
                                    String email,String photoUrl) {
        UserDB user = new UserDB(userId,name, email);
        databaseReference.child(userId).setValue(user);
    }
}
