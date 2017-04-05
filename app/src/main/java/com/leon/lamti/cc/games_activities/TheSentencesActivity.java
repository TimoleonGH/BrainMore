package com.leon.lamti.cc.games_activities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.leon.lamti.cc.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TheSentencesActivity extends AppCompatActivity {

    // firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference sentencesRef;

    // views
    private ConstraintLayout homeLayout;
    private TextView questionTV, scoreTitleTV, scoreTV, errorsTV;
    private EditText answerET;
    private FloatingActionButton okFAB;
    private Button backB, playAgainB;
    private RecyclerView errorsRV;

    // Variables
    private String ss, ss1, ss2, ss3;
    private ArrayList<String> questionArrayList, answerArrayList, errorsStringArrayList;
    private ArrayList<Integer> errorsArrayList;
    private int counter = 0, errorsCounter = 0, rightAnswersCounter = 0;
    private Context context;

    // constants
    private String TAG = "TAGRAM";

    // Others
    private MediaPlayer mediaPlayer;
    private ErrorsRecyclerAdapter recyclerAdapter;
    private TapTargetView tapTargetView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_sentences);

        // init views
        homeLayout = (ConstraintLayout) findViewById(R.id.activity_the_sentences);
        questionTV = (TextView) findViewById(R.id.questionTV);
        scoreTitleTV = (TextView) findViewById(R.id.scoreTitleTV);
        scoreTV = (TextView) findViewById(R.id.scoreTV);
        errorsTV = (TextView) findViewById(R.id.errorsTV);
        errorsRV = (RecyclerView) findViewById(R.id.errorsRV);
        answerET = (EditText) findViewById(R.id.answerET);
        okFAB = (FloatingActionButton) findViewById(R.id.answerFAB);
        backB = (Button) findViewById(R.id.backB);
        playAgainB = (Button) findViewById(R.id.playAgainB);

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("BrainMore");

        // firebase
        sentencesRef = database.getReference().child("setenses");
        sentencesRef.keepSynced(true);

        questionArrayList = new ArrayList<>();
        answerArrayList = new ArrayList<>();
        errorsArrayList = new ArrayList<>();
        errorsStringArrayList = new ArrayList<>();

        mediaPlayer = new MediaPlayer();
        context = TheSentencesActivity.this;

        // start game
        setRandomness();

        // tap target
        tapTarget();

        sentencesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                ss1 = dataSnapshot.child("ss1").getValue(String.class);
                ss2 = dataSnapshot.child("ss2").getValue(String.class);
                ss3 = dataSnapshot.child("ss3").getValue(String.class);

                questionArrayList.add(ss1 + " .... " + ss3);
                answerArrayList.add(ss2);

                for (int i = 0; i < questionArrayList.size(); i++) {
                    Log.d(TAG, "Question arraylist: " + i + " -> " + questionArrayList.get(i));
                    Log.d(TAG, "Answer arraylist: " + i + " -> " + answerArrayList.get(i));
                }

                try {
                    //questionTV.setText(questionArrayList.get(counter));
                    questionTV.setText(questionArrayList.get(randomNumbersList.get(counter)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "Child listener - ss1: " + ss1);
                Log.d(TAG, "Child listener - ss2: " + ss2);
                Log.d(TAG, "Child listener - ss3: " + ss3);

                // check for right answer and then act
                okFAB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //if ( answerET.getText().toString().equalsIgnoreCase(answerArrayList.get(counter)) ) {
                        if ( answerET.getText().toString().equalsIgnoreCase(answerArrayList.get(randomNumbersList.get(counter))) ) {

                            //if ( counter < answerArrayList.size()-1 ) {
                            if ( counter < randomNumbersList.size()-1 ) {

                                rightAnswersCounter++;

                                Animation fadeIn = new AlphaAnimation(0, 1);
                                fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
                                fadeIn.setDuration(1000);

                                Animation fadeOut = new AlphaAnimation(1, 0);
                                fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
                                fadeOut.setStartOffset(1000);
                                fadeOut.setDuration(1000);

                                AnimationSet animation = new AnimationSet(false); //change to false
                                animation.addAnimation(fadeIn);
                                animation.addAnimation(fadeOut);
                                questionTV.setAnimation(animation);
                                questionTV.startAnimation(animation);

                                //questionTV.setText(questionArrayList.get(counter).replace("....", answerArrayList.get(counter)));
                                questionTV.setText(questionArrayList.get(randomNumbersList.get(counter))
                                        .replace("....", answerArrayList.get(randomNumbersList.get(counter))));

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(questionTV, "translationX", 0, questionTV.getWidth());
                                        objectAnimatorX.setInterpolator(new AccelerateInterpolator());
                                        objectAnimatorX.setDuration(300);
                                        objectAnimatorX.start();
                                    }
                                }, 1500);

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ObjectAnimator objectAnimator2X = ObjectAnimator.ofFloat(questionTV, "translationX", 0 - questionTV.getWidth(), 0);
                                        objectAnimator2X.setInterpolator(new AccelerateInterpolator());
                                        objectAnimator2X.setDuration(300);
                                        objectAnimator2X.start();

                                        counter++;
                                        //questionTV.setText(questionArrayList.get(counter));
                                        questionTV.setText(questionArrayList.get(randomNumbersList.get(counter)));
                                        answerET.setText("");
                                    }
                                }, 1800);

                                mediaPlayer.reset();
                                mediaPlayer = MediaPlayer.create(TheSentencesActivity.this,R.raw.se_correct);
                                mediaPlayer.start();

                                Toast.makeText(TheSentencesActivity.this, "Σωστά", Toast.LENGTH_SHORT).show();
                            } else {

                                rightAnswersCounter++;

                                gameOver();
                                //Toast.makeText(TheSentencesActivity.this, "Τέλος παιχνιδιού", Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            //if ( counter < answerArrayList.size()-1 ) {
                            if ( counter < randomNumbersList.size()-1 ) {

                                //errorsStringArrayList.add(questionArrayList.get(counter).replace("....", answerArrayList.get(counter)));
                                errorsStringArrayList.add(questionArrayList.get(randomNumbersList.get(counter))
                                        .replace("....", answerArrayList.get(randomNumbersList.get(counter))));
                                errorsCounter++;

                                Animation fadeIn = new AlphaAnimation(0, 1);
                                fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
                                fadeIn.setDuration(1000);

                                Animation fadeOut = new AlphaAnimation(1, 0);
                                fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
                                fadeOut.setStartOffset(1000);
                                fadeOut.setDuration(1000);

                                AnimationSet animation = new AnimationSet(false); //change to false
                                animation.addAnimation(fadeIn);
                                animation.addAnimation(fadeOut);
                                questionTV.setAnimation(animation);
                                questionTV.startAnimation(animation);

                                //questionTV.setText(questionArrayList.get(counter).replace("....", answerArrayList.get(counter)));
                                questionTV.setText(questionArrayList.get(randomNumbersList.get(counter))
                                        .replace("....", answerArrayList.get(randomNumbersList.get(counter))));
                                questionTV.setTextColor(getResources().getColor(R.color.colorRed));

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(questionTV, "translationX", 0, questionTV.getWidth());
                                        objectAnimatorX.setInterpolator(new AccelerateInterpolator());
                                        objectAnimatorX.setDuration(300);
                                        objectAnimatorX.start();
                                    }
                                }, 1500);


                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ObjectAnimator objectAnimator2X = ObjectAnimator.ofFloat(questionTV, "translationX", 0 - questionTV.getWidth(), 0);
                                        objectAnimator2X.setInterpolator(new AccelerateInterpolator());
                                        objectAnimator2X.setDuration(300);
                                        objectAnimator2X.start();

                                        counter++;
                                        questionTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        //questionTV.setText(questionArrayList.get(counter));
                                        questionTV.setText(questionArrayList.get(randomNumbersList.get(counter)));
                                        answerET.setText("");
                                    }
                                }, 1800);

                                mediaPlayer.reset();
                                mediaPlayer = MediaPlayer.create(TheSentencesActivity.this,R.raw.se_wrong);
                                mediaPlayer.start();

                                Toast.makeText(TheSentencesActivity.this, "Λάθος", Toast.LENGTH_SHORT).show();
                            } else {

                                //errorsStringArrayList.add(questionArrayList.get(counter).replace("....", answerArrayList.get(counter)));
                                errorsStringArrayList.add(questionArrayList.get(randomNumbersList.get(counter))
                                        .replace("....", answerArrayList.get(randomNumbersList.get(counter))));
                                errorsCounter++;
                                gameOver();
                                //Toast.makeText(TheSentencesActivity.this, "Τέλος παιχνιδιού", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
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

    public class ErrorsRecyclerAdapter extends RecyclerView.Adapter<ErrorsRecyclerAdapter.ErrorsViewHolder> {

        private List<String> list;

        public class ErrorsViewHolder extends RecyclerView.ViewHolder {

            public TextView sentence;

            public ErrorsViewHolder(View view) {
                super(view);
                sentence = (TextView) view.findViewById(R.id.sentenceTV);
            }
        }


        public ErrorsRecyclerAdapter(List<String> errorsList) {
            this.list = errorsList;
        }

        @Override
        public ErrorsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.errors_list_row, parent, false);

            return new ErrorsViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ErrorsViewHolder holder, int position) {
            String str = list.get(position);
            holder.sentence.setText( position + 1 + ". " + str);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private List<Integer> randomNumbersList;
    private Random randomGenerator;

    private void setRandomness() {

        randomNumbersList = new ArrayList();
        randomGenerator = new Random();
        int randomInt;

        for ( int i=0; i<=6; i++ ) {

            do {
                randomInt = randomGenerator.nextInt(16);
                //Log.d("Random", "number: " + randomInt);
            } while ( randomNumbersList.contains(randomInt) );

            randomNumbersList.add(randomInt);
        }
        for (int j=0; j<randomNumbersList.size(); j++) {
            Log.d("Random", "number: " + randomNumbersList.get(j));
        }
    }

    private void tapTarget() {

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
                    .title("Παιχνίδι Προτάσεων")
                    .description("Γράψε την λέξη που λείπει από την παραπάνω πρόταση και πάτα το κουμπί.")
                    .listener(ttvListener)
                    .outerCircleColor(R.color.colorPrimary)
                    .targetCircleColor(R.color.colorAccent)
                    .textColor(R.color.white)
                    .textTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
                    .dimColor(R.color.colorBlack)
                    .tintTarget(false)
                    .drawShadow(true)
                    .cancelable(false)
                    .showFor(okFAB);
        }
    }

    private void gameOver() {

        mediaPlayer.reset();
        mediaPlayer = MediaPlayer.create(TheSentencesActivity.this,R.raw.se_bell);
        mediaPlayer.start();

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        recyclerAdapter = new ErrorsRecyclerAdapter(errorsStringArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        errorsRV.setLayoutManager(mLayoutManager);
        errorsRV.setItemAnimator(new DefaultItemAnimator());
        errorsRV.setAdapter(recyclerAdapter);

        int sum = rightAnswersCounter + errorsCounter;
        scoreTV.setText(rightAnswersCounter + " / " + sum);

        questionTV.setVisibility(View.GONE);
        scoreTitleTV.setVisibility(View.VISIBLE);
        scoreTV.setVisibility(View.VISIBLE);
        errorsTV.setVisibility(View.VISIBLE);
        errorsRV.setVisibility(View.VISIBLE);


        answerET.setText("");
        answerET.setVisibility(View.GONE);
        okFAB.setVisibility(View.GONE);
        backB.setVisibility(View.VISIBLE);
        playAgainB.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            backB.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            backB.setTextColor(getResources().getColor(R.color.colorPrimary));
            playAgainB.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            playAgainB.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        playAgainB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setRandomness();
                counter = 0;
                rightAnswersCounter = 0;
                errorsCounter = 0;
                errorsArrayList.clear();
                errorsStringArrayList.clear();

                questionTV.setVisibility(View.VISIBLE);
                questionTV.setText(questionArrayList.get(randomNumbersList.get(counter)));
                scoreTitleTV.setVisibility(View.GONE);
                scoreTV.setVisibility(View.GONE);
                errorsTV.setVisibility(View.GONE);
                errorsRV.setVisibility(View.GONE);

                answerET.setVisibility(View.VISIBLE);
                okFAB.setVisibility(View.VISIBLE);
                backB.setVisibility(View.GONE);
                playAgainB.setVisibility(View.GONE);
            }
        });
    }
}
