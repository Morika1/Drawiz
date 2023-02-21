package com.example.drawiz.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.drawiz.R;
import com.example.drawiz.logic.UserInfo;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {



    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();

        firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser == null){
            login();
            //saveUserToDB(firebaseUser);
        }

        else { // user logged in
            readUserFromDB();
        }
    }


    private void login(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build());

// Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
    }

    // See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if(result.getResultCode() == RESULT_OK){
            firebaseUser= mAuth.getCurrentUser();
        }
        userInfo = new UserInfo().
                setUserId(firebaseUser.getUid()).
                setUserName(firebaseUser.getDisplayName());
        saveUserToDB(userInfo);
        openHomePage(userInfo);
    }

    private void saveUserToDB(UserInfo userInfo){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref= db.getReference("loginManager");
        ref.child("allUsers").child(userInfo.getUserId()).setValue(userInfo);

    }

    private void readUserFromDB() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("loginManager");
        ref.child("allUsers").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    userInfo = new UserInfo().
                            setUserId(firebaseUser.getUid()).
                            setUserName(firebaseUser.getDisplayName());
                    saveUserToDB(userInfo);

                }
                else{
                    userInfo = (UserInfo) snapshot.getValue(UserInfo.class);
                }

                openHomePage(userInfo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //handle error

            }
        });
    }

    private void openHomePage(UserInfo userInfo) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(HomeActivity.KEY_USER, userInfo);
        startActivity(intent);
        finish();

    }
}