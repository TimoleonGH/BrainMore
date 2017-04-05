package com.leon.lamti.cc.animationAccount;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.opengl.EGLSurface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.leon.lamti.cc.R;
import com.leon.lamti.cc.home.HomeActivity;
import com.leon.lamti.cc.signInNOTUSED.SetupActivity;

public class CreateAccountActivity extends AppCompatActivity {

    // Firebase
    private FirebaseAuth mAuth;

    // Firebase Database
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRefUsers;

    // Views
    private EditText mEmail, mPassword, mFirstName, mLastName, mPhone, mAddress, mCity;
    private Button mSignIn;

    // Variables
    private String email, pass;
    private int interest = 0;

    // Progress Dialog
    private ProgressDialog mProDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_create_account);

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
        mEmail = (EditText) findViewById(R.id.emailCET);
        mPassword = (EditText) findViewById(R.id.passwordCET);
        mFirstName = (EditText) findViewById(R.id.firstNameCET);
        mLastName = (EditText) findViewById(R.id.lastNameCET);
        mCity = (EditText) findViewById(R.id.cityCET);
        mAddress = (EditText) findViewById(R.id.addressCET);
        mPhone = (EditText) findViewById(R.id.phoneCET);
        mSignIn = (Button) findViewById(R.id.signInCreateAccountCB);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mEmail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
            mPassword.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
            mFirstName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
            mLastName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
            mCity.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
            mPhone.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
            mAddress.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));

            //mEmail.setHint("");
        }

        mEmail.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // your code here....

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mEmail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    mPassword.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mFirstName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mLastName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mCity.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mPhone.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mAddress.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
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
                    mPassword.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    mFirstName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mLastName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mCity.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mPhone.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mAddress.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                }

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                return false;
            }
        });

        mFirstName.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // your code here....

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mEmail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mPassword.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mFirstName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    mLastName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mCity.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mPhone.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mAddress.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                }

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                return false;
            }
        });

        mLastName.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // your code here....

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mEmail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mPassword.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mFirstName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mLastName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    mCity.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mPhone.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mAddress.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                }

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                return false;
            }
        });

        mCity.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // your code here....

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mEmail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mPassword.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mFirstName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mLastName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mCity.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    mPhone.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mAddress.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                }

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                return false;
            }
        });

        mPhone.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // your code here....

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mEmail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mPassword.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mFirstName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mLastName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mCity.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mPhone.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    mAddress.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                }

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                return false;
            }
        });

        mAddress.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // your code here....

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mEmail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mPassword.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mFirstName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mLastName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mCity.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mPhone.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEditText)));
                    mAddress.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                }

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                return false;
            }
        });

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //mEmail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            mPassword.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            mFirstName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            mLastName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            mCity.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            mAddress.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            mPhone.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));

            *//*if ( mEmail.isFocused() ) {

                mEmail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            }*//*
        }*/


        // Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDbRefUsers = mDatabase.getReference("users");

        // offline functionality
        mDbRefUsers.keepSynced(true);

        mProDialog = new ProgressDialog(CreateAccountActivity.this);

        // Click Listeners
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProDialog.setMessage("Creating account...");
                mProDialog.show();

                createAccount();
            }
        });
     }

    private void createAccount() {

        email = mEmail.getText().toString();
        pass = mPassword.getText().toString();

        if ( validateForm() ) {

            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    setupAccount();
                }
            });
        }
    }

    private void setupAccount() {

        String userID = mAuth.getCurrentUser().getUid();

        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String telephone = mPhone.getText().toString();
        String address = mAddress.getText().toString();
        String city = mCity.getText().toString();


        UserObject uo = new UserObject();

        uo.setEmail(email);
        uo.setPassword(pass);
        uo.setFirstName(firstName);
        uo.setLastName(lastName);
        uo.setTelephone(telephone);
        uo.setAddress(address);
        uo.setCity(city);
        uo.setInteresting(interest);
        uo.setHistory("nullara!");

        GameObject go1 = new GameObject();
        GameObject go2 = new GameObject();
        GameObject go3 = new GameObject();

        go1.setName(getString(R.string.shapes_copy));
        go1.setFlag(true);

        go2.setName(getString(R.string.memory_images));
        go2.setFlag(true);

        go3.setName(getString(R.string.sentences));
        go3.setFlag(true);


        GamesObject go = new GamesObject();

        go.setGame1(go1);
        go.setGame2(go2);
        go.setGame3(go3);

        uo.setGames(go);

        // Insert user account to database with key the userId
        mDbRefUsers.child(userID).setValue(uo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mProDialog.dismiss();

                Intent homeIntent = new Intent( CreateAccountActivity.this, HomeActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(homeIntent);
                finish();
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

    // Popup Menu
    public void popupPopupMenu( View view) {

        PopupMenu popupMenu = new PopupMenu(CreateAccountActivity.this, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.item1:
                        //Toast.makeText(CreateAccountActivity.this, "Logotherapia", Toast.LENGTH_SHORT).show();
                        interest = 1;
                        return true;
                    case R.id.item2:
                        //Toast.makeText(CreateAccountActivity.this, "Mathisiako", Toast.LENGTH_SHORT).show();
                        interest = 2;
                        return true;
                    case R.id.item3:
                        //Toast.makeText(CreateAccountActivity.this, "Psichologiki upostiriksi", Toast.LENGTH_SHORT).show();
                        interest = 3;
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }
}
