package com.leon.lamti.cc.home;

//TODO: Home Activity: BottomSheet - Rate App - Settings
//TODO: Shapes Copy Activity: Design - Fab menu - drawer / bottomSheet - Load images - End dialog - Back Button - settings
//TODO: Image Memory Game Activity: Load images offline - End dialog - Back Button - settings
//TODO: Sentences Activity: End dialog - Setting
//TODO: Audio activity + Others activities

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.leon.lamti.cc.games_activities.ImageMemoryActivity;
import com.leon.lamti.cc.R;
import com.leon.lamti.cc.games_activities.TheSentencesActivity;
import com.leon.lamti.cc.animationAccount.AccountActivity;
import com.leon.lamti.cc.bottomSheet.SampleModel;
import com.leon.lamti.cc.bottomSheet.SampleSheetAdapter;
import com.leon.lamti.cc.games_activities.ShapesCopyActivity;
import com.leon.lamti.cc.decoration.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    // Firebase Auth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Firebase Database
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef;
    private DatabaseReference myRef;

    private RecyclerView mFireMenuRV;
    private FirebaseRecyclerAdapter<MenuItemObject, MenuItemViewHolder> mFireMenuRvAdapter;

    private LinearLayoutManager llm;

    // Views
    //private RecyclerView mRecyclerView;
    //private RecyclerAdapter mAdapter;
    private FloatingActionButton fab;

    // Tables
    private List mList = new  ArrayList<>();

    // Variables
    private String TAG = "homeTag";
    private int mSystemChoice;
    private boolean ftb = false;

    // Others

    // Tap Target View
    TapTargetView tapTargetView;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Lollipop animation
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            Explode explode = new Explode();
            explode.excludeTarget(R.id.toolbar, true);
            explode.excludeTarget(R.id.collapsing_toolbar, true);
            getWindow().setExitTransition(explode);
            //getWindow().setAllowEnterTransitionOverlap(false);
        }

        // Collapsing toolbar
        //ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_IMAGE);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Brain More");

        collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        //collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.color2));
        //collapsingToolbar.setContentScrimColor(getResources().getColor(R.color.color2));
        //collapsingToolbar.setStatusBarScrimColor(getResources().getColor(R.color.color1));

        // Views Initialization
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setRippleColor(getResources().getColor(R.color.colorPrimary));
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

        //mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        // Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        usersRef = database.getReference("users");
        usersRef.keepSynced(true);

        // Check if user is signed in
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    //Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    //Log.d(TAG, "onAuthStateChanged:signed_out");

                    Intent loginIntent = new Intent(HomeActivity.this, AccountActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                    finish();
                }
            }
        };

        // Bottom Sheet
        //final List bsl = fillBottomSheet();

        final List<SampleModel> bsl = new ArrayList<>();

        if ( mAuth.getCurrentUser() != null ) {
            bsl.add(new SampleModel(mAuth.getCurrentUser().getEmail(), R.mipmap.ic_ac_lt));
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // animation
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                    /*CoordinatorLayout viewRoot = (CoordinatorLayout) findViewById(R.id.activity_home);
                    int transitionId = R.transition.changeBounds_with_arcmotion;
                    TransitionInflater inflater = TransitionInflater.from(HomeActivity.this);
                    Transition transition = inflater.inflateTransition(transitionId);
                    TransitionManager.beginDelayedTransition(viewRoot, transition);*/

                    /*RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );

                    layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                    fab.setLayoutParams(layoutParams);*/
                }

                createSheetDialog(bsl);
            }
        });

        // Initialize recycler view
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));



        /*usersRef.child(mAuth.getCurrentUser().getUid()).child("games").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if ( dataSnapshot.getKey().equals("game1") && dataSnapshot.getValue().equals("True") ) {

                    mList.add(getResources().getString(R.string.shapes_copy));

                } else if ( dataSnapshot.getKey().equals("game2") && dataSnapshot.getValue().equals("True") ) {

                    mList.add(getResources().getString(R.string.memory_images));

                } else if ( dataSnapshot.getKey().equals("game3") && dataSnapshot.getValue().equals("True") ) {

                    mList.add(getResources().getString(R.string.sentences));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                *//*if ( dataSnapshot.getKey().equals("game1") && dataSnapshot.getValue().equals("True") ) {

                    mList.add(getResources().getString(R.string.shapes_copy));

                } else if ( dataSnapshot.getKey().equals("game2") && dataSnapshot.getValue().equals("True") ) {

                    mList.add(getResources().getString(R.string.memory_images));

                } else if ( dataSnapshot.getKey().equals("game3") && dataSnapshot.getValue().equals("True") ) {

                    mList.add(getResources().getString(R.string.sentences));
                }*//*
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
        });*/


        /*sharedpreferences = getSharedPreferences("systemChoiceSP", Context.MODE_PRIVATE);
        if ( sharedpreferences.getString("choice", "ole").indexOf("0") >= 0 ) {

            mSystemChoice = 1;
        }*/
        //Log.d("Firebase!", "#151 - system choice: " + mSystemChoice);

        //mAdapter = new RecyclerAdapter(this, mList, mSystemChoice);
        //mRecyclerView.setAdapter(mAdapter);

        /*mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Movie movie = movieList.get(position);
                //Toast.makeText(getApplicationContext(),   " is selected!", Toast.LENGTH_SHORT).show();

                Intent intent;


                    if ( mList.get(position).toString().equals(getString(R.string.shapes_copy)) ) {

                        //Toast.makeText(HomeActivity.this, "0 "  + position, Toast.LENGTH_SHORT).show();

                        intent = new Intent(HomeActivity.this, ShapesCopyActivity.class);
                        startActivity(intent);

                    } else if ( mList.get(position).toString().equals(getString(R.string.memory_images)) ) {

                        //Toast.makeText(HomeActivity.this, "1 "  + position, Toast.LENGTH_SHORT).show();

                        intent = new Intent(HomeActivity.this, ImageMemoryActivity.class);
                        startActivity(intent);

                    } else if ( mList.get(position).toString().equals(getString(R.string.sentences)) ) {

                        //Toast.makeText(HomeActivity.this, "2 " + position, Toast.LENGTH_SHORT).show();

                        goToSentences();
                    }
            }

            @Override
            public void onLongClick(View view, int position) {

                //Toast.makeText(HomeActivity.this, "Long click pressed!", Toast.LENGTH_SHORT).show();
            }
        }));*/

        // Divider
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.line_divider);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(dividerDrawable);
        //mRecyclerView.addItemDecoration(dividerItemDecoration);

        // Fire RecyclerView init
        mFireMenuRV = (RecyclerView) findViewById(R.id.recycler_view);
        mFireMenuRV.setHasFixedSize(true);

        llm = new LinearLayoutManager(this);
        mFireMenuRV.setLayoutManager(llm);
        mFireMenuRV.addItemDecoration(dividerItemDecoration);

        // Bottom Sheet initialization
        initBottomSheet();


        // TapTargetView
        final TapTargetView.Listener ttvListener = new TapTargetView.Listener() {
            @Override
            public void onTargetClick(TapTargetView view) {

                tapTargetView.dismiss(true);

            }

            @Override
            public void onTargetLongClick(TapTargetView view) {

            }
        };

        sharedPreferences = getSharedPreferences("infoPrefs", MODE_PRIVATE);

        // Tap targets
        if ( sharedPreferences.getBoolean("helper", true) ) {

            tapTargetView = new TapTargetView.Builder(this)
                    .title("Στείλε μήνυμα στον Διαχειριστή")
                    .description("Πάτα το κουμπί για να στείλεις μήνυμα στον διαχειριστή.")
                    .listener(ttvListener)
                    .outerCircleColor(R.color.colorPrimary)
                    .targetCircleColor(R.color.colorAccent)
                    .textColor(R.color.white)
                    .textTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
                    .dimColor(R.color.colorBlack)
                    .tintTarget(false)
                    .drawShadow(true)
                    .cancelable(false)
                    .showFor(fab);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkIfUserExists();
        mAuth.addAuthStateListener(mAuthListener);

        if ( mAuth.getCurrentUser() != null ) {

            Query fireQuery = database.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("games");

            // Firebase Recycler View methods
            mFireMenuRvAdapter = new FirebaseRecyclerAdapter<MenuItemObject, MenuItemViewHolder>(
                    MenuItemObject.class,
                    R.layout.row_layout,
                    MenuItemViewHolder.class,
                    fireQuery
            ) {
                @Override
                protected void populateViewHolder(MenuItemViewHolder viewHolder, MenuItemObject model, int position) {

                    Log.d("TAG", "flag: " + model.getFlag());
                    if ( model.getFlag() != null && model.getFlag() ) {

                        String s = model.getName();

                        if ( s.equals("shapes_copy")) {

                            s = getString(R.string.shapes_copy);
                        } else if ( s.equals("memory_images")) {

                            s = getString(R.string.memory_images);
                        } else if ( s.equals("sentences") ) {

                            s = getString(R.string.sentences);
                        }

                        viewHolder.itemName.setText(s);
                        //viewHolder.cardView.setVisibility(View.VISIBLE);
                        //viewHolder.menuRL.setVisibility(View.VISIBLE);

                    } else {

                        //viewHolder.itemName.setText("Locked");
                        //viewHolder.itemName.setTextColor(getResources().getColor(R.color.colorRed));
                        //viewHolder.cardView.setVisibility(View.GONE);
                        //viewHolder.menuRL.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onBindViewHolder(final MenuItemViewHolder viewHolder, final int position) {
                    super.onBindViewHolder(viewHolder, position);

                    viewHolder.menuRL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            //Toast.makeText(HomeActivity.this, "pos: " + position, Toast.LENGTH_SHORT).show();

                            if ( viewHolder.itemName.getText().equals("Αντιγραφή Σχημάτων") ) {

                                goTo(ShapesCopyActivity.class);

                            } else if ( viewHolder.itemName.getText().equals("Μνήμη - Εικόνες") ) {

                                goTo(ImageMemoryActivity.class);

                            } else if ( viewHolder.itemName.getText().equals("Προτάσεις") ) {

                                goToSentences();
                            }
                        }
                    });
                }
            };

            mFireMenuRV.setAdapter(mFireMenuRvAdapter);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    // Fire Recycler View Methods
    public static class MenuItemObject {

        private String name;
        private Boolean flag;

        public MenuItemObject() {

        }

        public MenuItemObject ( String name, Boolean flag ) {

            this.name = name;
            this.flag = flag;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getFlag() {
            return flag;
        }

        public void setFlag(Boolean flag) {
            this.flag = flag;
        }
    }

    public static class MenuItemViewHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        CardView cardView;
        RelativeLayout menuRL;

        public MenuItemViewHolder(View itemView) {
            super(itemView);

            itemName = (TextView) itemView.findViewById(R.id.menuItemNameTV);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            menuRL = (RelativeLayout) itemView.findViewById(R.id.menuRL);
        }
    }


    private void goTo ( Class className ) {

        Intent i = new Intent( HomeActivity.this, className );
        startActivity(i);
    }

    private void goToSentences() {

        //Intent intent = new Intent(HomeActivity.this, SentencesActivity.class);
        Intent intent = new Intent(HomeActivity.this, TheSentencesActivity.class);

        /*SharedPreferences sharedPreferences = getSharedPreferences("prefa", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstTimeFlag", false);
        editor.putInt("number2", 0);
        editor.commit();

        myRef = database.getReference("setenses");
        myRef.keepSynced(true);

        ftb = false;

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.d("AAA", dataSnapshot.getKey() + " / " + dataSnapshot.getValue());
                if( !myRef.child(dataSnapshot.getKey()).equals(null)) {

                    myRef.child(dataSnapshot.getKey()).child("userID").child(mAuth.getCurrentUser().getUid()).child("cardBgColor").setValue("white");

                    if ( ftb ) {
                        myRef.child(dataSnapshot.getKey()).child("userID").child(mAuth.getCurrentUser().getUid()).child("turnVisibilityOn").setValue("nope");
                        myRef.child(dataSnapshot.getKey()).child("userID").child(mAuth.getCurrentUser().getUid()).child("isSolved").setValue("iAmSoSorry");
                    } else {
                        myRef.child(dataSnapshot.getKey()).child("userID").child(mAuth.getCurrentUser().getUid()).child("turnVisibilityOn").setValue("yeah");
                        myRef.child(dataSnapshot.getKey()).child("userID").child(mAuth.getCurrentUser().getUid()).child("isSolved").setValue("iAmSoSorry");
                        ftb = true;
                    }
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
        });*/
        startActivity(intent);
    }


    private void animateRevealShow( View view ){

        // get the center for the clipping circle
        int cx = view.getMeasuredWidth() / 2;
        int cy = view.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(view.getWidth(), view.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        android.animation.Animator anim = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        }

        // make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        //anim.setStartDelay(1000);

        anim.start();
    }

    // User methods
    private void signOut() {

        mAuth.signOut();
        //updateUI(null);
        Intent intent = new Intent( HomeActivity.this, AccountActivity.class );
        startActivity(intent);
        finish();
    }

    private void checkIfUserExists() {

        if ( mAuth.getCurrentUser() != null ) {
            final String userID = mAuth.getCurrentUser().getUid();

            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if ( !dataSnapshot.hasChild(userID) ) {

                        Intent loginIntent = new Intent(HomeActivity.this, AccountActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginIntent);

                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    // Bottom
    private BottomSheetBehavior<View> behavior;

    private void initBottomSheet() {

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_home);

        View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);

        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });
    }


    // Bottom Sheet / Create Dialog

    private BottomSheetDialog dialog;

    //@OnClick(R.id.fab)
    /*public void onShowButtonClick() {
        //behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        createDialog();
    }*/

    private boolean dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            return true;
        }

        return false;
    }

    private List fillBottomSheet() {

        final List<SampleModel> list = new ArrayList<>();

        if ( mAuth.getCurrentUser() != null) {

            String emailSearch = mAuth.getCurrentUser().getEmail();
            String[] spliter = emailSearch.split("@");
            String name = spliter[0];
            String nameID = name + "ID";

            usersRef.child(nameID).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    Log.d("DSLOG", ": " + dataSnapshot.getValue());

                    list.add(new SampleModel(dataSnapshot.getKey() + ": " + dataSnapshot.getValue().toString(), R.mipmap.ic_ac_lt));
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

        return list;
    }

    private void createSheetDialog( List list) {
        if (dismissDialog()) {
            return;
        }

        SampleSheetAdapter adapterBS = new SampleSheetAdapter(list);
        adapterBS.setOnItemClickListener(new SampleSheetAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SampleSheetAdapter.ItemHolder item, int position) {
                //dismissDialog();
            }
        });

        View view = getLayoutInflater().inflate(R.layout.sheet_main, null);
        /*RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterBS);*/

        final EditText messageET = (EditText) view.findViewById(R.id.messageET);
        Button sendB = (Button) view.findViewById(R.id.sendB);

        sendB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 1st try
                String message = messageET.getText().toString();

                /*AsyncTaskRunner runner = new AsyncTaskRunner();

                runner.execute(message);*/

                sendEmail("Title", message);

            }
        });

        dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();
    }


    // Send email from you email app
    private void sendEmail( String title, String subject ) {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"admin@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, title );
        i.putExtra(Intent.EXTRA_TEXT   , subject);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(HomeActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    // action bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);

        if ( sharedPreferences.getBoolean("helper", true) ) {

            menu.findItem(R.id.action_helper).setChecked(true);
        } else {
            menu.findItem(R.id.action_helper).setChecked(false);
        }

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
            case R.id.action_helper:
                // location found

                if ( !sharedPreferences.getBoolean("helper", false) ) {

                    item.setChecked(true);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("helper", true);
                    editor.commit();

                } else  {

                    item.setChecked(false);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("helper", false);
                    editor.commit();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
