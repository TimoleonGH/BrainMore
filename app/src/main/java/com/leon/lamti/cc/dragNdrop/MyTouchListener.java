package com.leon.lamti.cc.dragNdrop;

import android.content.ClipData;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MyTouchListener implements View.OnTouchListener {
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, 0);
            view.setVisibility(View.INVISIBLE);
            Log.d("tagia", "Motion Evaent: " + motionEvent.getAction());
            return true;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE) {
            view.setVisibility(View.VISIBLE);
            Log.d("tagia", "2 Motion Event: " + motionEvent.getAction());
            return true;
        } else {
            Log.d("tagia", "3 Motion Evaent: " + motionEvent.getAction());
            return false;
        }
    }
}