package com.leon.lamti.cc.signInNOTUSED;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
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
import com.leon.lamti.cc.animationAccount.UserObject;
import com.leon.lamti.cc.home.HomeActivity;

public class SetupActivity extends AppCompatActivity {

    // Firebase
    private FirebaseAuth mAuth;

    // Firebase Database
    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    // Views
    private EditText mEmail, mPassword, mFirstName, mLastName, mTelephone, mAddress, mCity;
    private Button setupButton;

    // Variables
    private String email, pass;
    private int interest = 0;

    // Progress Dialog
    private ProgressDialog mProDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        // Views
        mEmail = (EditText) findViewById(R.id.setupEmail);
        mPassword = (EditText) findViewById(R.id.setupPassword);
        mFirstName = (EditText) findViewById(R.id.setupFirstName);
        mLastName = (EditText) findViewById(R.id.setupLastName);
        mTelephone = (EditText) findViewById(R.id.setupTelephone);
        mAddress = (EditText) findViewById(R.id.setupAddress);
        mCity = (EditText) findViewById(R.id.setupCity);
        setupButton = (Button) findViewById(R.id.setupB);


        // Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        // offline functionality
        usersRef.keepSynced(true);

        mProDialog = new ProgressDialog(SetupActivity.this);

        // Click Listeners
        setupButton.setOnClickListener(new View.OnClickListener() {
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
        String telephone = mTelephone.getText().toString();
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

        // Insert user account to database with key the userId
        usersRef.child(userID).setValue(uo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mProDialog.dismiss();

                Intent homeIntent = new Intent( SetupActivity.this, HomeActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    public void goToLogin ( View view ) {

        Intent i = new Intent( SetupActivity.this, SignInActivity.class );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    // Popup Menu
    public void popupPopupMenu( View view) {

        PopupMenu popupMenu = new PopupMenu(SetupActivity.this, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.item1:
                        Toast.makeText(SetupActivity.this, "Logotherapia", Toast.LENGTH_SHORT).show();
                        interest = 1;
                        return true;
                    case R.id.item2:
                        Toast.makeText(SetupActivity.this, "Mathisiako", Toast.LENGTH_SHORT).show();
                        interest = 2;
                        return true;
                    case R.id.item3:
                        Toast.makeText(SetupActivity.this, "Psichologiki upostiriksi", Toast.LENGTH_SHORT).show();
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
