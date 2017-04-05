package com.leon.lamti.cc.games_activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.leon.lamti.cc.R;
import com.leon.lamti.cc.animations_image_game.ResizeAnimation;
import com.leon.lamti.cc.dragNdrop.MyDragListener;
import com.leon.lamti.cc.dragNdrop.MyTouchListener;
import com.leon.lamti.cc.home.HomeActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ImageMemoryActivity extends AppCompatActivity {

    // Firebase
    private FirebaseStorage mStorage;
    private StorageReference mStorRef;
    private DatabaseReference imagesUrlsDbRef;
    private StorageReference mStorageRef;  //mStorageRef was previously used to transfer data.

    // Views
    private ImageView circle, image1, image2, image3, image4, image5, image6, image7, image8, image9;
    private LinearLayout linearLayout1, linearLayout2, linearLayout3, linearLayout4,
            linearLayout5, linearLayout6, linearLayout7, linearLayout8, linearLayout9;
    private Button checkB, backIMAB, playAgainIMAB;
    private FloatingActionButton startB;
    private TextView recordTV, timerTitleTV;
    private ConstraintLayout homeLayout;
    private ViewGroup aim;
    private TapTargetView tapTargetView;
    private View myRect;

    // Variables
    private String child = "";
    private boolean fabFlag = true;

    // Tables
    private ArrayList<ImageView> images;
    //private ArrayList<String> table;
    private ArrayList<Integer> table2;
    private ArrayList<Integer> table3;

    // Constants
    private static final int GALLERY_INTENT = 2;
    private long ANIM_DURATION = 1000;

    // Others
    private MediaPlayer mediaPlayer;
    private ProgressDialog mProDialog;
    private Transition transition;
    private Random randomGenerator;
    private SharedPreferences sharedPreferences;
    private Chronometer chronometer;
    private String[] children = {"a1.png", "a2.png", "a3.png", "a4.png", "a5.png", "a6.png", "a7.png", "a8.png", "a9.png", "a10.png",
            "a11.png", "a12.png", "a13.png", "a14.png", "a15.png", "a16.png", "a17.png", "a18.png", "a19.png", "a20.png",
            "a21.png", "a22.png", "a23.png", "a24.png", "a25.png"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_memory);

        sharedPreferences = getSharedPreferences("infoPrefs", MODE_PRIVATE);

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // -- views --
        aim = (ViewGroup) findViewById(R.id.activity_image_memory);
        homeLayout = (ConstraintLayout) findViewById(R.id.activity_image_memory);
        chronometer = (Chronometer)findViewById(R.id.timerTV);
        recordTV = (TextView) findViewById(R.id.recorTimerTV);
        timerTitleTV = (TextView) findViewById(R.id.timerTitleTV);
        image1 = (ImageView) findViewById(R.id.image1IV);
        image2 = (ImageView) findViewById(R.id.image2IV);
        image3 = (ImageView) findViewById(R.id.image3IV);
        image4 = (ImageView) findViewById(R.id.image4IV);
        image5 = (ImageView) findViewById(R.id.image5IV);
        image6 = (ImageView) findViewById(R.id.image6IV);
        image7 = (ImageView) findViewById(R.id.image7IV);
        image8 = (ImageView) findViewById(R.id.image8IV);
        image9 = (ImageView) findViewById(R.id.image9IV);

        linearLayout1 = (LinearLayout) findViewById(R.id.ll1);
        linearLayout2 = (LinearLayout) findViewById(R.id.ll2);
        linearLayout3 = (LinearLayout) findViewById(R.id.ll3);
        linearLayout4 = (LinearLayout) findViewById(R.id.ll4);
        linearLayout5 = (LinearLayout) findViewById(R.id.ll5);
        linearLayout6 = (LinearLayout) findViewById(R.id.ll6);
        linearLayout7 = (LinearLayout) findViewById(R.id.ll7);
        linearLayout8 = (LinearLayout) findViewById(R.id.ll8);
        linearLayout9 = (LinearLayout) findViewById(R.id.ll9);

        startB = (FloatingActionButton) findViewById(R.id.startB);
        checkB = (Button) findViewById(R.id.checkB);
        backIMAB = (Button) findViewById(R.id.backIMAB);
        playAgainIMAB = (Button) findViewById(R.id.playAgainIMAB);
        mProDialog = new ProgressDialog(this);
        mediaPlayer = new MediaPlayer();

        recordTV.setText("Ρεκόρ: " + sharedPreferences.getString("record", "59:99"));

        startB.setRippleColor(getResources().getColor(R.color.colorPrimary));
        startB.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkB.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            checkB.setTextColor(getResources().getColor(R.color.white));
            backIMAB.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            backIMAB.setTextColor(getResources().getColor(R.color.white));
            playAgainIMAB.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            playAgainIMAB.setTextColor(getResources().getColor(R.color.white));
        }

        // -- firebase --
        mStorage = FirebaseStorage.getInstance();
        mStorRef = mStorage.getReference().child("Images").child("Android");
        imagesUrlsDbRef = FirebaseDatabase.getInstance().getReference().child("imagesUrls");
        imagesUrlsDbRef.keepSynced(true);

        // TapTargetView
        final TapTargetView.Listener ttvListener = new TapTargetView.Listener() {
            @Override
            public void onTargetClick(TapTargetView view) {

                tapTargetView.dismiss(true);
                startChronometer();

            }

            @Override
            public void onTargetLongClick(TapTargetView view) {

            }
        };

        sharedPreferences = getSharedPreferences("infoPrefs", MODE_PRIVATE);

        // Tap targets
        if ( sharedPreferences.getBoolean("helper", true) ) {

            tapTargetView = new TapTargetView.Builder(this)
                    .title("Παιχνίδι Εικόνων")
                    .description("Απομνημόνευσε την σειρά των εικόνων και πάτησε το κουμπί για να ανακατευτούν. Θα καταφέρεις να τις ξαναβάλεις στην σωστή σειρά;")
                    .listener(ttvListener)
                    .outerCircleColor(R.color.colorPrimary)
                    .targetCircleColor(R.color.colorAccent)
                    .textColor(R.color.white)
                    .textTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
                    .dimColor(R.color.colorBlack)
                    .tintTarget(false)
                    .drawShadow(true)
                    .cancelable(false)
                    .showFor(startB);
        } else {

            startChronometer();
        }

        getStarted();


        startB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mediaPlayer.reset();
                mediaPlayer = MediaPlayer.create(ImageMemoryActivity.this,R.raw.se_bell);
                mediaPlayer.start();

                // fab animatioqn
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    int x = (int) startB.getX() + startB.getWidth() / 2;
                    int y = (int) startB.getY() + startB.getHeight() / 2;

                    animateRevealColorFromCoordinates(startB, homeLayout, x, y);

                } else {

                    startB.setVisibility(View.GONE);
                    checkB.setVisibility(View.VISIBLE);
                }

                go();
            }
        });

        backIMAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        playAgainIMAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mediaPlayer.reset();
                mediaPlayer = MediaPlayer.create(ImageMemoryActivity.this,R.raw.se_bell);
                mediaPlayer.start();

                homeLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                backIMAB.setVisibility(View.GONE);
                playAgainIMAB.setVisibility(View.GONE);
                checkB.setVisibility(View.GONE);
                startB.setVisibility(View.VISIBLE);

                startChronometer();
                fabFlag = true;
                getStarted();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void animateRevealColorFromCoordinates(View currentView, final View viewRoot, int x, int y) {

        float finalRadius = (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight());

        android.animation.Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, x, y, 0, finalRadius);
        anim.setDuration(600);
        anim.addListener(new android.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(android.animation.Animator animator) {
                viewRoot.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                startB.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(android.animation.Animator animator) {
                //viewRoot.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                checkB.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(android.animation.Animator animator) {

            }

            @Override
            public void onAnimationRepeat(android.animation.Animator animator) {

            }
        });
        anim.start();
    }

    private void getStarted() {

        table3 = new ArrayList();
        randomGenerator = new Random();
        int randomInt;

        for ( int i=0; i<=8; i++ ) {

            do {
                randomInt = randomGenerator.nextInt(25);
                //Log.v("Random", "number: " + randomInt);
            } while ( table3.contains(randomInt) );

            //table.add(imagesUrls.get(randomInt));
            table3.add(randomInt);
        }
        getFireImageFromDb( table3 );
    }

    private void go() {

        if ( fabFlag ) {

            // shuffle images
            linearLayout1.removeAllViews();
            linearLayout2.removeAllViews();
            linearLayout3.removeAllViews();
            linearLayout4.removeAllViews();
            linearLayout5.removeAllViews();
            linearLayout6.removeAllViews();
            linearLayout7.removeAllViews();
            linearLayout8.removeAllViews();
            linearLayout9.removeAllViews();

            table2 = new ArrayList();
            int ranum;

            for ( int i=0; i<=8; i++ ) {

                do {
                    ranum = randomGenerator.nextInt(9);
                    Log.v("Random", "2 - number: " + ranum);
                } while ( table2.contains(ranum) );

                table2.add(ranum);
            }

            linearLayout1.addView(images.get(table2.get(table3.size() - 8)));
            linearLayout2.addView(images.get(table2.get(table3.size() - 3)));
            linearLayout3.addView(images.get(table2.get(table3.size() - 4)));
            linearLayout4.addView(images.get(table2.get(table3.size() - 5)));
            linearLayout5.addView(images.get(table2.get(table3.size() - 7)));
            linearLayout6.addView(images.get(table2.get(table3.size() - 1)));
            linearLayout7.addView(images.get(table2.get(table3.size() - 9)));
            linearLayout8.addView(images.get(table2.get(table3.size() - 6)));
            linearLayout9.addView(images.get(table2.get(table3.size() - 2)));

            // -- Drag n Drop --
            image1.setOnTouchListener(new MyTouchListener());
            image2.setOnTouchListener(new MyTouchListener());
            image3.setOnTouchListener(new MyTouchListener());
            image4.setOnTouchListener(new MyTouchListener());
            image5.setOnTouchListener(new MyTouchListener());
            image6.setOnTouchListener(new MyTouchListener());
            image7.setOnTouchListener(new MyTouchListener());
            image8.setOnTouchListener(new MyTouchListener());
            image9.setOnTouchListener(new MyTouchListener());

            linearLayout1.setOnDragListener(new MyDragListener(ImageMemoryActivity.this));
            linearLayout2.setOnDragListener(new MyDragListener(ImageMemoryActivity.this));
            linearLayout3.setOnDragListener(new MyDragListener(ImageMemoryActivity.this));
            linearLayout4.setOnDragListener(new MyDragListener(ImageMemoryActivity.this));
            linearLayout5.setOnDragListener(new MyDragListener(ImageMemoryActivity.this));
            linearLayout6.setOnDragListener(new MyDragListener(ImageMemoryActivity.this));
            linearLayout7.setOnDragListener(new MyDragListener(ImageMemoryActivity.this));
            linearLayout8.setOnDragListener(new MyDragListener(ImageMemoryActivity.this));
            linearLayout9.setOnDragListener(new MyDragListener(ImageMemoryActivity.this));

            fabFlag = false;

        } else {

            // check if right
            if ( linearLayout1.getChildAt(0) == image1  && linearLayout2.getChildAt(0) == image2 &&
                    linearLayout3.getChildAt(0) == image3 && linearLayout4.getChildAt(0) == image4 &&
                    linearLayout5.getChildAt(0) == image5 && linearLayout6.getChildAt(0) == image6 &&
                    linearLayout7.getChildAt(0) == image7 && linearLayout8.getChildAt(0) == image8 &&
                    linearLayout9.getChildAt(0) == image9 ) {

                Toast.makeText(ImageMemoryActivity.this, getResources().getString(R.string.right), Toast.LENGTH_SHORT).show();
                Log.v("Shuffle", " 1 --> " + linearLayout1.getChildAt(0).toString());
                Log.v("Shuffle", " 2 --> " + image1.toString());

                fabFlag = true;

            } else {

                Toast.makeText(ImageMemoryActivity.this, getResources().getString(R.string.wrong), Toast.LENGTH_SHORT).show();
            }

        }
    }

    public boolean checkIfRight(View view) {

        // check if right
        if ( linearLayout1.getChildAt(0) == image1  && linearLayout2.getChildAt(0) == image2 &&
                linearLayout3.getChildAt(0) == image3 && linearLayout4.getChildAt(0) == image4 &&
                linearLayout5.getChildAt(0) == image5 && linearLayout6.getChildAt(0) == image6 &&
                linearLayout7.getChildAt(0) == image7 && linearLayout8.getChildAt(0) == image8 &&
                linearLayout9.getChildAt(0) == image9 ) {

            //createSnackBar(getResources().getString(R.string.right));
            Toast.makeText(ImageMemoryActivity.this, getResources().getString(R.string.right), Toast.LENGTH_SHORT).show();
            Log.v("Shuffle", " 1 --> " + linearLayout1.getChildAt(0).toString());
            Log.v("Shuffle", " 2 --> " + image1.toString());

            checkB.setVisibility(View.GONE);
            backIMAB.setVisibility(View.VISIBLE);
            playAgainIMAB.setVisibility(View.VISIBLE);

            mediaPlayer.reset();
            mediaPlayer = MediaPlayer.create(ImageMemoryActivity.this,R.raw.se_correct);
            mediaPlayer.start();

            stopTime = chronometer.getBase() - SystemClock.elapsedRealtime();
            //Toast.makeText(this, "Your time: " + chronometer.getText(), Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            long a = stopTime;
            long b = sharedPreferences.getLong("recordLong", stopTime);
            Log.d("TIMER", "a = " + a + ", b = " + b);
            if ( a > b ) {
                editor.putString("record", chronometer.getText().toString()).apply();
                editor.putLong("recordLong", stopTime).apply();
            }

            timerTitleTV.setVisibility(View.VISIBLE);
            chronometer.setVisibility(View.GONE);
            timerTitleTV.setText("Συγχαρητήρια, νίκησες το παιχνίδι με χρόνο: " + chronometer.getText());
            stopChronometer();

            recordTV.setText("Ρεκόρ: " + sharedPreferences.getString("record", "59:99"));

            return true;

        } else {

            mediaPlayer.reset();
            mediaPlayer = MediaPlayer.create(ImageMemoryActivity.this,R.raw.se_wrong);
            mediaPlayer.start();

            //createSnackBar(getResources().getString(R.string.wrong));
            Toast.makeText(ImageMemoryActivity.this, getResources().getString(R.string.wrong), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    long stopTime = 0;
    private void startChronometer() {
        chronometer.setVisibility(View.VISIBLE);
        timerTitleTV.setVisibility(View.GONE);
        chronometer.setBase(SystemClock.elapsedRealtime() + stopTime);
        chronometer.start();
    }

    private void stopChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        stopTime = 0;
        chronometer.stop();
    }


    private void getFireImageFromDb ( ArrayList<Integer> ranIntTable) {

        urlsList = new ArrayList<>();
        images = new ArrayList<>();

        images.add(0, image1);
        images.add(1, image2);
        images.add(2, image3);
        images.add(3, image4);
        images.add(4, image5);
        images.add(5, image6);
        images.add(6, image7);
        images.add(7, image8);
        images.add(8, image9);

        HashMap<Integer, String> x = new HashMap<>();

        x.put(0, "image1");
        x.put(1, "image2");
        x.put(2, "image3");
        x.put(3, "image4");
        x.put(4, "image5");
        x.put(5, "image6");
        x.put(6, "image7");
        x.put(7, "image8");
        x.put(8, "image9");
        x.put(9, "image10");
        x.put(10, "image11");
        x.put(11, "image12");
        x.put(12, "image13");
        x.put(13, "image14");
        x.put(14, "image15");
        x.put(15, "image16");
        x.put(16, "image17");
        x.put(17, "image18");
        x.put(18, "image19");
        x.put(19, "image20");
        x.put(20, "image21");
        x.put(21, "image22");
        x.put(22, "image23");
        x.put(23, "image24");
        x.put(24, "image25");

        final ArrayList<String> y = new ArrayList<>();

        Collections.sort(ranIntTable);

        for ( int i=0; i<ranIntTable.size(); i++ ) {

            y.add(i, x.get(ranIntTable.get(i)));
        }

        //Collections.sort(y.subList(1, y.size()));
        //Collections.sort(y);

        Log.d("AAAAA", " y: " + y.toString() );

        /*do{

        }while( n== 8 );*/

        imagesUrlsDbRef.child(y.get(0)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String url = dataSnapshot.getValue().toString();

                Picasso.with(ImageMemoryActivity.this).load(url).networkPolicy(NetworkPolicy.OFFLINE).into(image1, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(ImageMemoryActivity.this).load(url).fit().centerCrop().into(image1);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        imagesUrlsDbRef.child(y.get(1)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String url = dataSnapshot.getValue().toString();

                Picasso.with(ImageMemoryActivity.this).load(url).networkPolicy(NetworkPolicy.OFFLINE).into(image2, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(ImageMemoryActivity.this).load(url).fit().centerCrop().into(image2);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        imagesUrlsDbRef.child(y.get(2)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String url = dataSnapshot.getValue().toString();

                Picasso.with(ImageMemoryActivity.this).load(url).networkPolicy(NetworkPolicy.OFFLINE).into(image3, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(ImageMemoryActivity.this).load(url).fit().centerCrop().into(image3);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        imagesUrlsDbRef.child(y.get(3)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String url = dataSnapshot.getValue().toString();

                Picasso.with(ImageMemoryActivity.this).load(url).networkPolicy(NetworkPolicy.OFFLINE).into(image4, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(ImageMemoryActivity.this).load(url).fit().centerCrop().into(image4);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        imagesUrlsDbRef.child(y.get(4)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String url = dataSnapshot.getValue().toString();

                Picasso.with(ImageMemoryActivity.this).load(url).networkPolicy(NetworkPolicy.OFFLINE).into(image5, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(ImageMemoryActivity.this).load(url).fit().centerCrop().into(image5);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        imagesUrlsDbRef.child(y.get(5)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String url = dataSnapshot.getValue().toString();

                Picasso.with(ImageMemoryActivity.this).load(url).networkPolicy(NetworkPolicy.OFFLINE).into(image6, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(ImageMemoryActivity.this).load(url).fit().centerCrop().into(image6);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        imagesUrlsDbRef.child(y.get(6)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String url = dataSnapshot.getValue().toString();

                Picasso.with(ImageMemoryActivity.this).load(url).networkPolicy(NetworkPolicy.OFFLINE).into(image7, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(ImageMemoryActivity.this).load(url).fit().centerCrop().into(image7);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        imagesUrlsDbRef.child(y.get(7)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String url = dataSnapshot.getValue().toString();

                Picasso.with(ImageMemoryActivity.this).load(url).networkPolicy(NetworkPolicy.OFFLINE).into(image8, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(ImageMemoryActivity.this).load(url).fit().centerCrop().into(image8);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        imagesUrlsDbRef.child(y.get(8)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String url = dataSnapshot.getValue().toString();

                Picasso.with(ImageMemoryActivity.this).load(url).networkPolicy(NetworkPolicy.OFFLINE).into(image9, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(ImageMemoryActivity.this).load(url).fit().centerCrop().into(image9);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*imagesUrlsDbRef.orderByChild("").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //Log.d("AAAAA", (n++) + " --> key: " + dataSnapshot.getKey() );
                //Log.d("AAAAA", (n++) + " --> val: " + dataSnapshot.getValue() );

                //Log.d("AAAAA", " --> url: " + url );

                //final ImageView currentImage = images[];

                if ( n<9 && y.get(n).equals(dataSnapshot.getKey()) ) {

                    //Log.d("AAAAA", (n++) + " --> key: " + dataSnapshot.getKey() );
                    //Log.d("AAAAA", (n++) + " --> y.key: " + y.get(n) );

                    final String url = dataSnapshot.getValue().toString();
                    final ImageView currentImage = images.get(v);

                    Log.d("AAAAA", " current image: " + currentImage );

                    n++;
                    v++;

                    Picasso.with(ImageMemoryActivity.this).load(url).networkPolicy(NetworkPolicy.OFFLINE).into(currentImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(ImageMemoryActivity.this).load(url).fit().centerCrop().into(currentImage);
                        }
                    });

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
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // If there's a download in progress, save the reference so you can query it later
        if (mStorageRef != null) {
            outState.putString("reference", mStorageRef.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // If there was a download in progress, get its reference and create a new StorageReference
        final String stringRef = savedInstanceState.getString("reference");
        if (stringRef == null) {
            return;
        }
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(stringRef);
        //FileDownloadTask downloadTask = firebaseStorage.getReferenceFromUrl(url).getFile(localFile);
        // Find all DownloadTasks under this StorageReference (in this example, there should be one)
        List tasks = mStorageRef.getActiveDownloadTasks();
        if (tasks.size() > 0) {
            // Get the task monitoring the download
            FileDownloadTask task = (FileDownloadTask) tasks.get(0);

            // Add new listeners to the task using an Activity scope
            task.addOnSuccessListener(this, new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {

                    String s = o.toString();
                    Log.d("FDT", "o: " + s);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        /*Uri uri = data.getData();

        mStorRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Uri downloadUri = taskSnapshot.getDownloadUrl();

                Picasso.with(ImageMemoryActivity.this).load(downloadUri).fit().centerCrop().into(circle);
            }
        });*/


    }



    // NOT USED
    int n = 1;
    int v = 0;
    private ArrayList<String> urlsList;
    private View achievementV;
    private Button playAgainB, quitB;

    private void fireImageTry() {

         /*imageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image*//*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        fireImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                child = "firebase.png-large";
                getFireImage();
            }
        });

        circleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                child = "circle.jpg";
                getFireImage();
            }
        });*/
    }

    private void animationTry() {


        // START FAB animation try
        //startB.animate().scaleX(1f).scaleY(1f);
        //TransitionManager.beginDelayedTransition(aim, transition);


                   /* ConstraintLayout.LayoutParams layoutParams = new
                            ConstraintLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            ConstraintLayout.LayoutParams.WRAP_CONTENT);
                    *//*layoutParams.setLayoutDirection(layoutParams.bottomToTop);
                    layoutParams.setLayoutDirection(layoutParams.leftToRight);*//*
                    layoutParams.setLayoutDirection(80);*/

                    /*RelativeLayout.LayoutParams layoutParams = new
                            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    //layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    startB.setLayoutParams(layoutParams);*/



        //Transition enterTransition = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            //myRect = (View) findViewById(R.id.rect);

            int transitionId = R.transition.empty_transition;
            TransitionInflater inflater = TransitionInflater.from(ImageMemoryActivity.this);
            transition = inflater.inflateTransition(transitionId);

            transition = getWindow().getSharedElementEnterTransition();


            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    //animateRevealShow(myRect);
                    Log.d("AAA", "tra started!");
                    //Toast.makeText(ImageMemoryActivity.this, "transition started!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onTransitionEnd(Transition transition) {

                    //animate check button
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                        /*int transition2Id = R.transition.activity_slide;
                        TransitionInflater inflater2 = TransitionInflater.from(ImageMemoryActivity.this);
                        Transition transition2 = inflater2.inflateTransition(transition2Id);
                        TransitionManager.beginDelayedTransition(aim, transition2);*/
                    }

                    checkB.setVisibility(View.VISIBLE);
                    final Animation animTranslate = AnimationUtils.loadAnimation(ImageMemoryActivity.this, R.anim.anim_translate);
                    checkB.startAnimation(animTranslate);

                    startB.animate().scaleX(0f).scaleY(0f);
                    animateRevealShow(myRect);
                    Log.d("AAA", "tra ended!");
                    //Toast.makeText(ImageMemoryActivity.this, "tra ended!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                }

                @Override
                public void onTransitionPause(Transition transition) {
                }

                @Override
                public void onTransitionResume(Transition transition) {
                }});

            /*Transition sharedElementReturnTransition = getWindow().getSharedElementReturnTransition();
            sharedElementReturnTransition.setStartDelay(ANIM_DURATION);

            Transition returnTransition = getWindow().getReturnTransition();
            returnTransition.setDuration(ANIM_DURATION);*/
        }
    }

    private void createSnackBar(String snackText) {

        CoordinatorLayout rootLayout = (CoordinatorLayout) findViewById(R.id.activity_image_memory);
        Snackbar snackbar = Snackbar.make(rootLayout, snackText, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void playAgain( View view ) {

        getStarted();

        Animation fadeOutCheckAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        Animation fadeInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);

        startB.startAnimation(fadeInAnim);
        playAgainB.startAnimation(fadeOutCheckAnim);
        quitB.startAnimation(fadeOutCheckAnim);

        RelativeLayout.LayoutParams layoutParams = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.setMargins(0, 0, 0, 40);

        startB.setLayoutParams(layoutParams);

        startB.animate().scaleX(1f).scaleY(1f);

        myRect.setVisibility(View.GONE);


        playAgainB.setVisibility(View.GONE);
        quitB.setVisibility(View.GONE);
        startB.setVisibility(View.VISIBLE);
    }

    public void quit ( View view ) {

        Intent i = new Intent( ImageMemoryActivity.this, HomeActivity.class );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    // Achievement
    private void initAchievement() {

        //playAgainB = (Button) findViewById(R.id.playAgainB);
        //quitB = (Button) findViewById(R.id.quitB);

        //final View achievementV = (View) findViewById(R.id.achievementV);
        //achievementV = (View) findViewById(R.id.achievementV);
        //final ImageView achievementIV = (ImageView) findViewById(R.id.achievementIV);
        //final TextView achievementTV = (TextView) findViewById(R.id.achievementTV);

        final Handler handler = new Handler();
        final float scale = this.getResources().getDisplayMetrics().density;
        int wantedSize = 200;
        final int pixels = (int) (wantedSize * scale + 0.5f);

        int wantedSize2 = 60;
        final int pixels2 = (int) (wantedSize2 * scale + 0.5f);

        Animation fadeOutCheckAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        checkB.startAnimation(fadeOutCheckAnim);
        checkB.setVisibility(View.GONE);

        // Enter Animation
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //enterReveal(achievementV);
                Animation myAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.my_anim);
                myAnim.setInterpolator(new LinearOutSlowInInterpolator());

                achievementV.startAnimation(myAnim);
                achievementV.setVisibility(View.VISIBLE);

            }
        }, 300);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Animation myAnim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.my_anim);
                myAnim2.setInterpolator(new BounceInterpolator());
                //achievementIV.startAnimation(myAnim2);
                //achievementIV.setVisibility(View.VISIBLE);
            }
        }, 700);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                ResizeAnimation resizeAnim = new ResizeAnimation(achievementV, pixels);
                resizeAnim.setDuration(800);
                resizeAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                //resizeAnim.setInterpolator(new BounceInterpolator());
                resizeAnim.setInterpolator(new OvershootInterpolator());
                achievementV.startAnimation(resizeAnim);

                final Animation fadeAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //achievementTV.startAnimation(fadeAnim);
                        //achievementTV.setVisibility(View.VISIBLE);
                    }
                }, 600);
            }
        }, 1300);


        // Exit Animation
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Animation fadeOutAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);

                //achievementTV.startAnimation(fadeOutAnim);
                //achievementTV.setVisibility(View.GONE);
            }
        }, 3500);



        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                ResizeAnimation resizeAnim2 = new ResizeAnimation(achievementV, pixels2);
                resizeAnim2.setDuration(800);
                resizeAnim2.setInterpolator(new AccelerateDecelerateInterpolator());
                //resizeAnim.setInterpolator(new BounceInterpolator());
                //resizeAnim2.setInterpolator(new OvershootInterpolator());
                achievementV.startAnimation(resizeAnim2);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 700);

            }
        }, 4100);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Animation myAnim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.my_anim_inverse);
                //myAnim2.setInterpolator(new BounceInterpolator());
                myAnim2.setInterpolator(new AnticipateOvershootInterpolator());
                //achievementIV.startAnimation(myAnim2);
                //achievementIV.setVisibility(View.GONE);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Animation myAnim3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.my_anim_inverse);
                        //myAnim3.setInterpolator(new LinearOutSlowInInterpolator());
                        myAnim3.setInterpolator(new AnticipateOvershootInterpolator());

                        achievementV.startAnimation(myAnim3);
                        achievementV.setVisibility(View.GONE);


                        Animation fadeInButtons = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
                        playAgainB.startAnimation(fadeInButtons);
                        quitB.startAnimation(fadeInButtons);
                        playAgainB.setVisibility(View.VISIBLE);
                        quitB.setVisibility(View.VISIBLE);

                    }
                }, 150);
            }
        }, 5000);

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

        // remove listener
        /*anim.addListener(new android.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(android.animation.Animator animation) {

            }

            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().getEnterTransition().removeListener(mEnterTransitionListener);
                }
            }

            @Override
            public void onAnimationCancel(android.animation.Animator animation) {

            }

            @Override
            public void onAnimationRepeat(android.animation.Animator animation) {

            }
        });*/

        anim.start();
    }

    private void getFireImage(final String child, final ImageView currentImage ) {


        mStorRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                String rPath = mStorRef.getName().toString();
                Log.d("RefPath", "path: " + uri);
            }
        });

        Log.d("GetFireImages", "path: " + mStorRef.getBucket().length());


        mStorRef.child(child).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {

                String uriPath = uri.getPath().toString();
                String imageName = uriPath.substring(uriPath.lastIndexOf("/") + 1);

                //Log.d("GetFireImages", "path: " + uriPath + "\n" + "name: " + imageName);

                //Picasso.with(ImageMemoryActivity.this).load(uri).fit().centerCrop().into(currentImage);

                Picasso.with(ImageMemoryActivity.this).load(uri).networkPolicy(NetworkPolicy.OFFLINE).into(currentImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(ImageMemoryActivity.this).load(uri).fit().centerCrop().into(currentImage);
                    }
                });
            }
        });
    }
}
