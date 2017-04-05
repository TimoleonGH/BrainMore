package com.leon.lamti.cc.signInNOTUSED;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leon.lamti.cc.animationAccount.AccountActivity;
import com.leon.lamti.cc.animationAccount.UserObject;
import com.leon.lamti.cc.home.BaseActivity;
import com.leon.lamti.cc.R;
import com.leon.lamti.cc.home.HomeActivity;

import java.util.ArrayList;

public class SignInActivity extends BaseActivity {

    // Views
    private EditText mEmail, mPassword;
    private TextView mCreateAccount;
    private Button mLogin, matDia;
    private ConstraintLayout cl;

    // Variables
    private String email = "null email";
    private String password = "null pass";
    private String userID = "null name";
    private int i = 0;
    private boolean mHomeFlag = true;

    // Constants
    private final String TAG = "Firebase!";

    // User Object
    private UserObject mUserObject;

    // Firebase Auth
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    // Firebase Database
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("users");


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        // Views
        cl = (ConstraintLayout) findViewById(R.id.activity_sign_in);
        mEmail = (EditText) findViewById(R.id.emailET);
        mPassword = (EditText) findViewById(R.id.passwordET);
        mCreateAccount = (TextView) findViewById(R.id.createAccountTV);
        mLogin = (Button) findViewById(R.id.loginB);

        // Check if user is signed in
        /*mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };*/

        // Click Listeners - Events
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkLogin();
            }
        });

        //Popup Window initialization
        /*// try 1
        init();
        popupInit();

        // try 2
        btnCreatePopup = (Button) findViewById(R.id.popupWindowB);
        btnCreatePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiatePopupWindow();
            }
        });

        matDia = (Button) findViewById(R.id.matDialogB);
        matDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateDialog(savedInstanceState).show();
            }
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        /*if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }*/
    }


    // Check login
    private void checkLogin() {

        email = mEmail.getText().toString();
        password = mPassword.getText().toString();

        if ( validateForm() ) {

            mProgressDialog = new ProgressDialog( SignInActivity.this );
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

                        Toast.makeText(SignInActivity.this, "Error Login", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    private void checkIfUserExists() {

        final String userID = mAuth.getCurrentUser().getUid();

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if ( dataSnapshot.hasChild(userID) ) {

                    Intent loginIntent = new Intent(SignInActivity.this, HomeActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                    finish();

                } else {

                    Intent setupIntent = new Intent(SignInActivity.this, SetupActivity.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    public void goToSetupAccount( View view ) {

        Intent i = new Intent( SignInActivity.this, AccountActivity.class );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }


    // Older...



    private void signIn(final String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {

                            hideProgressDialog();
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(SignInActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            String[] split = email.split("@");
                            String name = split[0];
                            userID = name + "ID";

                            usersRef.child(userID).child("updaterKey").setValue("updated");

                            usersRef.child(userID).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                    String dk =  dataSnapshot.getKey().toString();
                                    String selectedItems = "nullara";

                                    if ( dk.equals("info") ) {

                                        selectedItems = dataSnapshot.getValue().toString();
                                        // Shared Preferences
                                        SharedPreferences sharedpreferences = getSharedPreferences("systemChoiceSP", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putString("choice", selectedItems);
                                        editor.commit();

                                        Log.d(TAG, "#128 - info: " + sharedpreferences.getString("choice", "akuro") + " / s: " + s);
                                        //usersRef.child(userID).child("updaterKey").setValue("-");

                                        Log.d(TAG, " #303 - datasnapshot.getValue: " + dataSnapshot.toString());
                                        usersRef.child(userID).child("updaterKey").removeValue();
                                        mHomeFlag = true;
                                        goToHome();
                                        Log.d(TAG, "#306 - chicou: " + dataSnapshot.getChildrenCount());
                                    }
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private void goToHome () {

        Log.e("goToHome", " Times: " + i);

        if ( i == 0 ) {

            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            i++;

            Log.e("if", " Times: " + i);
        }
    }

    // Popup Menu
    public void popupPopupMenu( View view ) {

        PopupMenu popupMenu = new PopupMenu(SignInActivity.this, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.item1:
                        Toast.makeText(SignInActivity.this, "Comedy Clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.item2:
                        Toast.makeText(SignInActivity.this, "Movies Clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.item3:
                        Toast.makeText(SignInActivity.this, "Music Clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

// Popup Window
    // try 1
    LinearLayout layoutOfPopup;
    PopupWindow popupMessage;
    Button popupButton, insidePopupButton;
    TextView popupText;

    public void init() {

        popupButton = (Button) findViewById(R.id.popupWindowB);
        popupText = new TextView(this);
        insidePopupButton = new Button(this);
        layoutOfPopup = new LinearLayout(this);
        insidePopupButton.setText("OK");
        popupText.setText("This is Popup Window.press OK to dismiss it.");
        popupText.setPadding(0, 0, 0, 20);
        //layoutOfPopup.setOrientation(1);
        layoutOfPopup.addView(popupText);
        layoutOfPopup.addView(insidePopupButton);
    }

    public void popupInit() {

        popupMessage = new PopupWindow(layoutOfPopup, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupMessage.setContentView(layoutOfPopup);

        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupMessage.showAsDropDown(popupButton, 0, 0);
            }
        });
        insidePopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupMessage.dismiss();
            }
        });

    }

    // try 2
    Button btnClosePopup;
    Button btnCreatePopup;
    private PopupWindow pWindow;

    private void initiatePopupWindow() {
        try {


        } catch (Exception e) {
            e.printStackTrace();
        }

        // We need to get the instance of the LayoutInflater
        LayoutInflater inflater = (LayoutInflater) SignInActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.screen_popup, (ViewGroup) findViewById(R.id.popup_element));
        pWindow = new PopupWindow(layout, 900, 560, true);
        pWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

        btnClosePopup = (Button) layout.findViewById(R.id.btn_close_popup);
        btnClosePopup.setOnClickListener(cancel_button_click_listener);

        //Snackbar snackbar = Snackbar.make(cl, "Popup ok", Snackbar.LENGTH_LONG);
        //snackbar.show();
    }

    private View.OnClickListener cancel_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            pWindow.dismiss();
        }
    };

    // Dialog
    private ArrayList mSelectedItems;

    public Dialog onCreateDialog(Bundle savedInstanceState) {


        mSelectedItems = new ArrayList();
        String[] colors = new String[]{
                "Red",
                "Green",
                "Blue",
                "Purple",
                "Olive"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);

        builder.setTitle("This is list choice dialog box");
        builder.setMultiChoiceItems(colors, null,new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                if (isChecked) {
                    // If the user checked the item, add it to the selected items
                    mSelectedItems.add(which);
                }

                else if (mSelectedItems.contains(which)) {
                    // Else, if the item is already in the array, remove it
                    mSelectedItems.remove(Integer.valueOf(which));
                }
            }
        })
                // Set the action buttons
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog



                        //Log.d(TAG, "#485 - mUserObject: " + mUserObject.getInfo().toString());

                        Snackbar snackbar = Snackbar
                                .make(cl, "ArrayList: " + mUserObject.toString(), Snackbar.LENGTH_LONG)
                                .setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Snackbar snackbar1 = Snackbar.make(cl, "Message is restored!", Snackbar.LENGTH_SHORT);
                                        snackbar1.show();
                                    }
                                });

                        //snackbar.show();

                        // Shared Preferences
                        SharedPreferences sharedpreferences = getSharedPreferences("systemChoiceSP", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("choice", mSelectedItems.toString());
                        editor.commit();

                        showProgressDialog();
                    }
                })

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }
}
