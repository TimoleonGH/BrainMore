package com.leon.lamti.cc.games_activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.leon.lamti.cc.CanvasView;
import com.leon.lamti.cc.R;
import com.leon.lamti.cc.decoration.DividerItemDecoration;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Random;

public class ShapesCopyActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Views
    private CanvasView customCanvas;
    private ConstraintLayout cl;
    private ImageView image;
    private Button compareB;
    private FloatingActionButton bsFAB, niFAB;

    // Firebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference imagesDbReference;

    // Others
    private Bitmap toCompareBitmap;
    private ProgressDialog mProgressDialog;
    private Boolean compFlag;

    private String randomDbUrl;
    private String randomImageKey;

    // Tap Target View
    private TapTargetView tapTargetView;

    private SharedPreferences sharedPreferences;
    private AsyncTaskRunner runner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shapes_copy);

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       /* getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);*/

        // Drawer Final
        final DrawerLayout drawerFinal = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerFinal, toolbar, R.string.app_name, R.string.app_name);
        drawerFinal.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer_final_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerFinal.openDrawer(Gravity.LEFT);
            }
        });

        // Views
        customCanvas = (CanvasView) findViewById(R.id.signature_canvas);
        cl = (ConstraintLayout) findViewById(R.id.activity_shapes_copy);
        image = (ImageView) findViewById(R.id.csI);
        compareB = (Button) findViewById(R.id.compareB);
        bsFAB = (FloatingActionButton) findViewById(R.id.showBsFab);
        niFAB = (FloatingActionButton) findViewById(R.id.nextImageFab);

        bsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgressDialog = new ProgressDialog( ShapesCopyActivity.this );
                mProgressDialog.setMessage("Υπολογισμός ομοιοτήτων...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();

                runner = new AsyncTaskRunner();
                runner.execute();
            }
        });

        // Divider
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.line_divider);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(dividerDrawable);

        // Firebase init
        firebaseDatabase = FirebaseDatabase.getInstance();
        imagesDbReference = firebaseDatabase.getReference().child("imagesUrls");
        imagesDbReference.keepSynced(true);


        Random rand = new Random();
        int  n = rand.nextInt(24) + 1;

        randomImageKey = "image" + n;

        imagesDbReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if ( dataSnapshot.getKey().equals(randomImageKey) ) {
                    randomDbUrl = dataSnapshot.getValue().toString();
                    getFireImage(randomDbUrl);
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

        niFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Random rand = new Random();
                int  n = rand.nextInt(24) + 1;

                randomImageKey = "image" + n;

                imagesDbReference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        if ( dataSnapshot.getKey().equals(randomImageKey) ) {
                            randomDbUrl = dataSnapshot.getValue().toString();
                            getFireImage(randomDbUrl);
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

                clearCanvas();
            }
        });

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
                    .title("Έλεγχος Ομοιότητας")
                    .description("Ζωγράφισε το παραπάνω σχήμα και πάτα το κουμπί για να δεις αν τα κατάφερες.")
                    .listener(ttvListener)
                    .outerCircleColor(R.color.colorPrimary)
                    .targetCircleColor(R.color.colorAccent)
                    .textColor(R.color.white)
                    .textTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
                    .dimColor(R.color.colorBlack)
                    .tintTarget(false)
                    .drawShadow(true)
                    .cancelable(false)
                    .showFor(bsFAB);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Long tm = Runtime.getRuntime().totalMemory();
        Long fm = Runtime.getRuntime().freeMemory();

        Log.d("TAGARA", "tm: " + tm + " / fm: " + fm);
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //customCanvas.recycleBitmap();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Toast.makeText(ShapesCopyActivity.this, "low memory", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {  // could be in onPause or onStop
        Picasso.with(this).cancelRequest(target);

        /*if ( toCompareBitmap != null ) {
            toCompareBitmap.recycle();
        }

        if ( customCanvas != null ) {
            customCanvas.clear();
        }*/

        /*if ( customCanvas != null ) {

            int i = customCanvas.recycleBitmap();

            Log.d("TAG", "i: " + i );

            customCanvas.clearCanvas();
            customCanvas.destroyDrawingCache();
            customCanvas.clearPreviousCanvas();

            System.gc();
        }*/

        super.onDestroy();
    }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {

            compareImages();
            //isImagesSimilar();
            return "resp";
        }

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation

            if ( compFlag ) {

                Toast.makeText(ShapesCopyActivity.this, "Συγχαρητήρια, τα κατάφερες!", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(ShapesCopyActivity.this, "Λάθος!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }

        @Override
        protected void onProgressUpdate(String... text) {
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }

    private void getFireImage( String stringUrl ) {

        final Uri uri = Uri.parse(stringUrl);

        Picasso.with(ShapesCopyActivity.this).load(uri).networkPolicy(NetworkPolicy.OFFLINE).fit().centerInside().into(image, new Callback() {
            //Picasso.with(ShapesCopyActivity.this).load(uri).fit().centerInside().into(image, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

                Picasso.with(ShapesCopyActivity.this).load(uri).fit().centerCrop().into(image);
            }
        });
    }

    public void clearCanvas() {

        customCanvas.clearCanvas();
        customCanvas.destroyDrawingCache();
    }

    public void compareImages() {

        ImageView imageView = (ImageView) findViewById(R.id.csI);
        imageView.buildDrawingCache();

        Bitmap bmapImage = imageView.getDrawingCache();
        Bitmap bmapCanvas = customCanvas.getBitmap();

        compareImagesPixelToPixel(bmapImage, bmapCanvas);
    }

    public boolean compareImagesPixelToPixel(Bitmap i1, Bitmap i2) {

        compFlag = false;

        //if (i1.getHeight() != i2.getHeight()) return false;
        //if (i1.getWidth() != i2.getWidth()) return false;
        int pixelsMatch = 0;
        int pixelsNotMatch = 0;

        mainLoop:
        for (int y = 0; y < i1.getHeight(); ++y) {
            for (int x = 0; x < i1.getWidth(); ++x) {
                //Log.d("AAA", "i1 width: " + i1.getWidth() + " / i2 width: " + i2.getWidth() + " / x: " + x );
                if (i1.getPixel(x, y) != i2.getPixel(x, y)) {

                    //return false;
                    pixelsNotMatch++;
                } else {

                    //Log.d("PIXEL", "I1 pixels: " + i1.getPixel(x,y) + " / I2 pixels: " + i2.getPixel(x,y));
                    //Log.d("PIXEL", "match: " + pixelsMatch + " not : " + pixelsNotMatch);
                    pixelsMatch++;

                    if ( pixelsMatch >= 5000 ) {

                        compFlag = true;
                        break mainLoop;
                    }
                }
            }
        }

        Log.d("PIXEL", "height: " + i1.getHeight() + " width : " + i1.getWidth());
        Log.d("PIXEL", "height2: " + i2.getHeight() + " width2 : " + i2.getWidth());
        Log.d("PIXEL", "final match: " + pixelsMatch + " not : " + pixelsNotMatch);

        mProgressDialog.dismiss();

        return true;
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            toCompareBitmap = bitmap;
            //Toast.makeText(ShapesCopyActivity.this, "Bitmap loaded: " + toCompareBitmap, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };


    // Drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        int id = item.getItemId();

        //CanvasView cv = (CanvasView) findViewById(R.id.signature_canvas);

        switch (id) {
            case R.id.small: customCanvas.changeSize(0);
                customCanvas.eraser(false);
                break;
            case R.id.medium: customCanvas.changeSize(1);
                customCanvas.eraser(false);
                break;
            case R.id.large: customCanvas.changeSize(2);
                customCanvas.eraser(false);
                break;
            case R.id.black: customCanvas.changeColor(4);
                customCanvas.eraser(false);
                break;
            case R.id.red: customCanvas.changeColor(0);
                customCanvas.eraser(false);
                break;
            case R.id.blue: customCanvas.changeColor(1);
                customCanvas.eraser(false);
                break;
            case R.id.green: customCanvas.changeColor(2);
                customCanvas.eraser(false);
                break;
            case R.id.paint: customCanvas.changeColor(4);
                customCanvas.eraser(false);
                break;
            case R.id.eraser: customCanvas.changeColor(3);
                customCanvas.eraser(true);
                break;
            case R.id.clearAll: customCanvas.clearPreviousCanvas();
                customCanvas.eraser(false);
                customCanvas.destroyDrawingCache();
                break;
            default: break;
        }

        DrawerLayout drawerFinal = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerFinal.closeDrawer(GravityCompat.START);

        return true;
    }


    // Not Used
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void isImagesSimilar() {

        ImageView imageView = (ImageView) findViewById(R.id.csI);
        imageView.buildDrawingCache();

        Bitmap bmapImage = imageView.getDrawingCache();
        Bitmap bmapCanvas = customCanvas.getBitmap();

        int iw = bmapImage.getWidth();
        int cw = bmapCanvas.getWidth();

        mProgressDialog.dismiss();

        if ( cw <= ( iw + 50) )  {

            compFlag = true;
            //Toast.makeText(ShapesCopyActivity.this, "Συγχαρητήρια, τα κατάφερες!", Toast.LENGTH_SHORT).show();
        } else {
            compFlag = false;
            //Toast.makeText(ShapesCopyActivity.this, "Λάθος, ξαναπροσπάθησε.", Toast.LENGTH_SHORT).show();
        }
        Log.d("SIMILARITIES", "iw: " + iw + ", cw: " + cw);
    }

    private void unloadBackground() {

        if (image != null)
            image.setBackgroundDrawable(null);
        if (toCompareBitmap!= null) {
            toCompareBitmap.recycle();
        }
        toCompareBitmap = null;
    }
}
