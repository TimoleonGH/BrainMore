package com.leon.lamti.cc.not_used;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.leon.lamti.cc.R;
import com.leon.lamti.cc.decoration.DividerItemDecoration;
import com.leon.lamti.cc.home.BaseActivity;
import com.leon.lamti.cc.signInNOTUSED.SignInActivity;

import java.util.HashMap;

public class SentencesActivity extends BaseActivity {


    private static SharedPreferences sharedPreferences;
    private String TAG = "alright";

    private static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference myRef;

    private RecyclerView firebaseRecyclerView;

    private TextView mUserEmail, mUserName, m01, m03;
    private EditText m02;
    private Button mSignOut, mNext;
    private boolean pausedFlag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentences);

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference("setenses");
        myRef.keepSynced(true);


        mUserEmail = (TextView) findViewById(R.id.userEmailET);
        mUserName = (TextView) findViewById(R.id.userNameET);
        m01 = (TextView) findViewById(R.id.tv01);
        m02 = (EditText) findViewById(R.id.et02);
        m03 = (TextView) findViewById(R.id.tv03);
        mSignOut = (Button) findViewById(R.id.signOutB);
        mNext = (Button) findViewById(R.id.nextB);


        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signOut();
            }
        });

        // Check if user is signed in
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    mUserEmail.setText(user.getEmail());
                    mUserName.setText(user.getDisplayName());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        sharedPreferences = getSharedPreferences("prefa", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("number", 1);
        editor.commit();

        // Firebase Recycler View
        firebaseRecyclerView = (RecyclerView) findViewById(R.id.sentencesRV);
        firebaseRecyclerView.setHasFixedSize(true);
        firebaseRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Divider
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.line_divider);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(dividerDrawable);
        firebaseRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private FirebaseRecyclerAdapter<SingleSentence, SentencesViewHolder> firebaseRecyclerAdapter;
    private boolean checkOneTime = false;
    private boolean anwserRigthFlag = false;
    private int rightAnwsersCounter = 0;
    private static int rightAnwsersCounterStatic = 0;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        final Query query = FirebaseDatabase.getInstance().getReference().child("setenses").orderByChild("userID");

        final Object[] userModelString = {"ums"};
        final boolean[] containsYeah = {false};
        final boolean[] containsVeryTrue = {false};

        // Firebase Recycler Adapter
        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<SingleSentence, SentencesViewHolder>(
                        SingleSentence.class,
                        R.layout.row_sentences,
                        SentencesViewHolder.class,
                        query
                ) {
                    @Override
                    protected void populateViewHolder(final SentencesViewHolder viewHolder, SingleSentence model, int position) {


                        if ( model.getUserID().get(mAuth.getCurrentUser().getUid() ) != null ) {

                            userModelString[0] = model.getUserID().get(mAuth.getCurrentUser().getUid());
                            containsYeah[0] = model.getUserID().get(mAuth.getCurrentUser().getUid()).toString().contains("yeah");
                            containsVeryTrue[0] = model.getUserID().get(mAuth.getCurrentUser().getUid()).toString().contains("veryTrue");
                        }


                        //Log.d("FRV", "model: " + model.getSs1());
                        viewHolder.text1.setText(model.getSs1());
                        viewHolder.anwserTV.setText(model.getSs2());
                        viewHolder.text2.setText(model.getSs3());

                        if (!sharedPreferences.getBoolean("firstTimeFlag", false)) {
                            if (position == 0) {

                            } else {
                                viewHolder.sentencesCardView.setVisibility(View.GONE);
                            }
                        }

                        if ( model.getUserID().get(mAuth.getCurrentUser().getUid() ) != null ) {

                            Log.d("AAAAAAAAAA", "model userID --> " + model.getUserID().get(mAuth.getCurrentUser().getUid()).toString());

                            if ( model.getUserID().get(mAuth.getCurrentUser().getUid()).toString().contains("yeah")
                                    && model.getUserID().get(mAuth.getCurrentUser().getUid()).toString().contains("veryTrue") ) {

                                viewHolder.okFab.setVisibility(View.GONE);
                                viewHolder.okFab.setRippleColor(getResources().getColor(R.color.colorPrimary));
                                viewHolder.okFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

                                viewHolder.editText.setVisibility(View.GONE);
                                viewHolder.text2.setVisibility(View.GONE);
                                viewHolder.text1.setText( viewHolder.text1.getText() + " " + viewHolder.anwserTV.getText()
                                        + " " + viewHolder.text2.getText());

                                if ( model.getUserID().get(mAuth.getCurrentUser().getUid()).toString().contains("rightGreen") ) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        viewHolder.sentencesCardView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(
                                                R.color.colorGreen)));
                                    }

                                } else if ( model.getUserID().get(mAuth.getCurrentUser().getUid()).toString().contains("wrongRed") ) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        viewHolder.sentencesCardView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(
                                                R.color.colorRed)));
                                    }

                                } else {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        viewHolder.sentencesCardView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(
                                                R.color.white)));
                                    }
                                }

                                viewHolder.sentencesCardView.setVisibility(View.VISIBLE);
                            }

                            else if ( model.getUserID().get(mAuth.getCurrentUser().getUid()).toString().contains("yeah")
                                    && !model.getUserID().get(mAuth.getCurrentUser().getUid()).toString().contains("veryTrue")) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    viewHolder.sentencesCardView.setBackgroundTintList(ColorStateList.valueOf(
                                            getResources().getColor(R.color.white)));
                                }

                                viewHolder.okFab.setVisibility(View.VISIBLE);
                                viewHolder.sentencesCardView.setVisibility(View.VISIBLE);

                                viewHolder.editText.setVisibility(View.VISIBLE);
                                viewHolder.text2.setVisibility(View.VISIBLE);

                            } else {

                                viewHolder.sentencesCardView.setVisibility(View.GONE);

                                if ( model.getUserID().get(mAuth.getCurrentUser().getUid()).toString().contains("rightGreen") ) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        viewHolder.sentencesCardView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(
                                                R.color.colorGreen)));
                                    }

                                } else if ( model.getUserID().get(mAuth.getCurrentUser().getUid()).toString().contains("wrongRed") ) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        viewHolder.sentencesCardView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(
                                                R.color.colorRed)));
                                    }

                                } else {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        viewHolder.sentencesCardView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(
                                                R.color.white)));
                                    }
                                }
                            }

                        }

                        checkOneTime = true;
                        pausedFlag = false;
                    }

                    @Override
                    public void onBindViewHolder(final SentencesViewHolder viewHolder, final int position) {
                        super.onBindViewHolder(viewHolder, position);

                        Log.d("AAA", "number2: " + sharedPreferences.getInt("number2", 0) );

                        //if ( sharedPreferences.getInt("number2", 0) >= 2) {
                        if ( false ) {
                            firebaseRecyclerView.scrollToPosition(position);
                        }

                        viewHolder.okFab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                    if (viewHolder.editText.getText().toString().equals(viewHolder.anwserTV.getText())) {

                                        rightAnwsersCounter++;
                                        anwserRigthFlag = true;

                                    } else {

                                        anwserRigthFlag = false;
                                    }

                                if ( position == 0) {

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("firstTimeFlag", true);
                                    editor.commit();
                                }

                                if ( position == firebaseRecyclerAdapter.getItemCount()-2 ) {

                                    rightAnwsersCounterStatic = rightAnwsersCounter;
                                }

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("number2", position);
                                editor.commit();

                                checkOneTime = false;

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    viewHolder.sentencesCardView.setBackgroundTintList(ColorStateList.valueOf(
                                            getResources().getColor(R.color.colorAccent)));
                                }


                                viewHolder.setCardVisible(SentencesActivity.this, firebaseRecyclerAdapter, position, anwserRigthFlag);

                            }
                        });

                    }
                };
        firebaseRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class SentencesViewHolder extends RecyclerView.ViewHolder {

        TextView text1, text2, anwserTV;
        EditText editText;
        FloatingActionButton okFab;
        ImageView x,v;
        CardView sentencesCardView;

        public SentencesViewHolder(View view) {
            super(view);

            text1 = (TextView) view.findViewById(R.id.senTV1);
            text2 = (TextView) view.findViewById(R.id.senTV2);
            anwserTV = (TextView) view.findViewById(R.id.anwserTV);
            editText = (EditText) view.findViewById(R.id.senET);
            okFab = (FloatingActionButton) view.findViewById(R.id.okFAB);
            x = (ImageView) view.findViewById(R.id.xIV);
            v = (ImageView) view.findViewById(R.id.vIV);
            sentencesCardView = (CardView) view.findViewById(R.id.sentencesCardView);
        }

        public void setFirstCardVisible(FirebaseRecyclerAdapter adapter) {

            final DatabaseReference ref = adapter.getRef(0);
            final FirebaseAuth auth = FirebaseAuth.getInstance();

            ref.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    ref.child("userID").child(auth.getCurrentUser().getUid()).setValue("yeah");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setCardVisible(final Context context, final FirebaseRecyclerAdapter adapter , final int position, final boolean rightAnwser) {

            final DatabaseReference ref = adapter.getRef(position + 1);
            final FirebaseAuth auth = FirebaseAuth.getInstance();
            final int childrenNumber = adapter.getItemCount();


            ref.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    if ( ( sharedPreferences.getInt("number", 0) + 1 ) ==  childrenNumber ) {

                        int x = childrenNumber - 1;
                        Toast.makeText(context, "Right anwsers: " + rightAnwsersCounterStatic + "/" + x, Toast.LENGTH_SHORT).show();

                    } else {

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("number", sharedPreferences.getInt("number", 0) + 1);
                        editor.commit();

                        //Toast.makeText(context, "Number: " + sharedPreferences.getInt("number", 0), Toast.LENGTH_SHORT).show();
                        ref.child("userID").child(auth.getCurrentUser().getUid()).child("turnVisibilityOn").setValue("yeah");
                        ref.child("userID").child(auth.getCurrentUser().getUid()).child("isSolved").setValue("iAmSoSorry");
                    }

                    final DatabaseReference refSolved = adapter.getRef(position);

                    refSolved.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            refSolved.child("userID").child(auth.getCurrentUser().getUid()).child("isSolved").setValue("veryTrue");
                            refSolved.child("userID").child(auth.getCurrentUser().getUid()).child("turnVisibilityOn").setValue("yeah");

                            if ( rightAnwser ) {

                                refSolved.child("userID").child(auth.getCurrentUser().getUid())
                                        .child("cardBgColor").setValue("rightGreen");

                            } else {

                                refSolved.child("userID").child(auth.getCurrentUser().getUid())
                                        .child("cardBgColor").setValue("wrongRed");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public static class SingleSentence {

        private String ss1, ss2, ss3;
        private HashMap<String, Object> userID;

        public SingleSentence(){}

        public SingleSentence( String ss1, String ss2, String ss3, HashMap<String, Object> userID ) {

            this.ss1 = ss1;
            this.ss2 = ss2;
            this.ss3 = ss3;
            this.userID = userID;
        }

        public HashMap<String, Object> getUserID() {
            return userID;
        }

        public void setUserID(HashMap<String, Object> userID) {
            this.userID = userID;
        }

        public String getSs1() {
            return ss1;
        }

        public void setSs1(String ss1) {
            this.ss1 = ss1;
        }

        public String getSs2() {
            return ss2;
        }

        public void setSs2(String ss2) {
            this.ss2 = ss2;
        }

        public String getSs3() {
            return ss3;
        }

        public void setSs3(String ss3) {
            this.ss3 = ss3;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        checkOneTime = false;
        pausedFlag = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

        checkOneTime = false;
        //firebaseRecyclerAdapter.cleanup();
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
        Intent intent = new Intent( SentencesActivity.this, SignInActivity.class );
        startActivity(intent);
        finish();
    }

    private void updateUI(FirebaseUser user) {

        if ( user == null ) {

            mUserEmail.setText("");
            mUserName.setText("");
        }
    }

    // action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_account:
                // location found
                signOut();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.dialog_sentences))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        myRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                if( !myRef.child(dataSnapshot.getKey()).equals(null)) {

                                    myRef.child(dataSnapshot.getKey()).child("userID").child(mAuth.getCurrentUser().getUid()).setValue(null);
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

                        SentencesActivity.this.finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .show();
    }
}
