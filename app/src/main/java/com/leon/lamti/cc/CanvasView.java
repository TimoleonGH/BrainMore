package com.leon.lamti.cc;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Set;

import static android.R.attr.bitmap;
import static android.R.attr.radius;
import static com.leon.lamti.cc.R.id.csI;
import static com.leon.lamti.cc.R.id.frameFl;
import static com.leon.lamti.cc.R.id.signature_canvas;

public class CanvasView extends View {

    public int width = 100;
    public int height = 100;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    Context context;
    private Paint mPaint;
    private float mX, mY;
    private static final float TOLERANCE = 5;

    private int oSCWidth;
    private int oSCHeight;

    private Paint bitmapPaint;
    private BitmapFactory.Options mBitmapOptions;
    private SharedPreferences sharedPreferences;

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

        //sharedPreferences = context.getSharedPreferences("memoryPrefs", Context.MODE_PRIVATE);

        bitmapPaint = new Paint(Paint.DITHER_FLAG);

        // we set a new Path
        mPath = new Path();

        // and we set a new Paint with the desired attributes
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(20f);

        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        this.setDrawingCacheEnabled(true);
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        oSCWidth = w;
        oSCHeight = h;

        /*if(mBitmap != null) {
            // Explicitly free memory of old bitmap
            mBitmap = null;
            mBitmap.recycle();
        }*/

       /* BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.id.signature_canvas, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;*/

        /*mBitmapOptions.inBitmap = mBitmap;

        mBitmap = BitmapFactory.decodeFile("filename", mBitmapOptions);

        mBitmapOptions.inMutable = true;*/


        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        //CanvasView cv = (CanvasView) findViewById(R.id.signature_canvas);
        //mBitmap = Bitmap.createBitmap(FixOOM.decodeSampledBitmapFromResource(context.getResources(), R.id.signature_canvas, 100, 100) );

        mBitmap = createBitmap(w, h);
        //mBitmap = doIt();

        if ( mBitmap != null ) {
            mCanvas = new Canvas(mBitmap);
        }
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //canvas.restore();

        if ( mBitmap != null ) {
            // draw the mPath with the mPaint on the canvas when onDraw
            canvas.drawPath(mPath, mPaint);
            canvas.drawBitmap(mBitmap, 0, 0, bitmapPaint);
        }
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    public void clearCanvas() {
        mPath.reset();
        invalidate();
    }

    // when ACTION_UP stop touch
    private void upTouch() {
        mPath.lineTo(mX, mY);

        // commit the path to our offscreen
        mCanvas.drawPath(mPath,mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                invalidate();
                break;
        }
        return true;
    }

    // --- My methods ---
    public void changeSize ( int size ) {

        /*mPaint = new Paint();
        //mPaint.setColor(0xFFFF0000);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        //mPaint.setStrokeWidth(3);*/

        switch (size) {
            case 0:
                mPaint.setStrokeWidth(10f);
                break;
            case 1:
                mPaint.setStrokeWidth(16f);
                break;
            case 2:
                mPaint.setStrokeWidth(20f);
                break;
        }

        //newCanvas.drawPath(mPath, newPaint);
    }

    public void changeColor( int color ) {

        switch (color) {
            case 0:
                mPaint.setColor(Color.RED);
                break;
            case 1:
                mPaint.setColor(Color.BLUE);
                break;
            case 2:
                mPaint.setColor(Color.GREEN);
                break;
            case 3:
                mPaint.setColor(Color.WHITE);
                break;
            default:
                mPaint.setColor(Color.BLACK);
                break;
        }
        //newCanvas.drawPath(mPath, newPaint);
    }

    public Paint getPaint() {

        return mPaint;
    }


    // Tut Methods

    public Bitmap getBitmap() {
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);

        return bmp;
    }

    //Clear screen
    public void clear() {
        mBitmap.eraseColor(Color.WHITE);
        //mBitmap = Bitmap.createBitmap(oSCWidth, oSCHeight, Bitmap.Config.ARGB_8888);
        invalidate();
        System.gc();
    }

    public void clearPreviousCanvas() {

        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
    }

    public void eraser( boolean b ) {

        if ( b ) {
            //mPaint.setAlpha(0xFF);//transperent color
            mPaint.setStrokeWidth(20f);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {

            mPaint.setXfermode(null);
        }
    }



    public Bitmap createdBitmap;

    public Bitmap createBitmap( int w, int h ) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 2;
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[16 * 1024];

        /*if (createdBitmap != null) {
            createdBitmap.recycle();
            createdBitmap = null;
        }*/

        if ( createdBitmap == null ) {
            createdBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
        }

        return createdBitmap;
    }

    public void setPathColor(int color) {
        mPaint.setColor(color);
    }

    public int recycleBitmap() {

        mBitmap.recycle();

        return mBitmap.getByteCount();
    }

    public int reloadBitmap() {

        return 0;
    }

    public int getMemory() {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.getMemoryClass(); //return the memory size limit in MB
    }

    public static int[] getBitmapDetails(Resources res, int id ) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, id, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;

        int[] output = {imageHeight, imageWidth};

        return output;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}