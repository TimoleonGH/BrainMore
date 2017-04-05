package com.leon.lamti.cc.animationAccount;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Transition;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leon.lamti.cc.R;
import com.leon.lamti.cc.home.HomeActivity;
import com.leon.lamti.cc.signInNOTUSED.SetupActivity;
import com.leon.lamti.cc.signInNOTUSED.SignInActivity;

public class LoginActivity extends AppCompatActivity {

    // Views
    private EditText mEmail, mPassword;
    private Button mLogin;
    private ConstraintLayout cl;

    // Variables
    private String email = "null email";
    private String password = "null pass";

    // Firebase Auth
    private FirebaseAuth mAuth;

    // Firebase Database
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDbRefUsers = mDatabase.getReference("users");

    // Progress Dialog
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_login);

        // Activity Animation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            final Transition fade = getWindow().getEnterTransition();
            fade.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onTransitionEnd(Transition transition) {

                    fade.removeListener(this);
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
        }

        // Views
        mEmail = (EditText) findViewById(R.id.emailLoginCET);
        mPassword = (EditText) findViewById(R.id.passwordLoginCET);
        mLogin = (Button) findViewById(R.id.loginLoginCB);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mEmail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            mPassword.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        }

        mEmail.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // your code here....

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mEmail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                    mPassword.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                }

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                return false;
            }
        });

        mPassword.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // your code here....

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mEmail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mPassword.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                }

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                return false;
            }
        });

        // Firebase init
        mAuth = FirebaseAuth.getInstance();


        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkLogin();
            }
        });
    }

    // Check login
    private void checkLogin() {

        email = mEmail.getText().toString();
        password = mPassword.getText().toString();

        if ( validateForm() ) {

            mProgressDialog = new ProgressDialog( LoginActivity.this );
            mProgressDialog.setMessage("Checking login...");
            mProgressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if ( task.isSuccessful() ) {

                        mProgressDialog.dismiss();

                        checkIfUserExists();

                    } else {

                        mProgressDialog.dismiss();

                        Toast.makeText(LoginActivity.this, "Error Login", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    private void checkIfUserExists() {

        final String userID = mAuth.getCurrentUser().getUid();

        mDbRefUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if ( dataSnapshot.hasChild(userID) ) {

                    Intent loginIntent = new Intent(LoginActivity.this, HomeActivity.class);
                    //loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    finish();

                } else {

                    Intent setupIntent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                    //setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    setupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(setupIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Required.");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Required.");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
    }
}
