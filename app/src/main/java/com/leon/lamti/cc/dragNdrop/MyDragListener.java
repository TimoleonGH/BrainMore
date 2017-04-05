package com.leon.lamti.cc.dragNdrop;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.leon.lamti.cc.R;

public class MyDragListener implements View.OnDragListener {

    Context context;
    Drawable enterShape;
    Drawable normalShape;

   // private ImageMemoryActivity ima = new ImageMemoryActivity();
   // private boolean check = ima.checkIfRight();

    public MyDragListener(Context context) {

        enterShape = context.getResources().getDrawable(R.drawable.shape_droptarget);
        normalShape = context.getResources().getDrawable(R.drawable.shape);
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {

        int action = event.getAction();

        View lastEnteredView;

        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                // do nothing
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                v.setBackgroundDrawable(enterShape);
                lastEnteredView = v;
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                v.setBackgroundDrawable(normalShape);
                break;
            case DragEvent.ACTION_DROP:

                View view = (View) event.getLocalState();       // view = dragged image
                ViewGroup owner = (ViewGroup) view.getParent(); // owner = dragged image layout
                LinearLayout container = (LinearLayout) v;      // container = dropped layout
                View im = (View) container.getChildAt(0);
                // Dropped, reassign View to ViewGroup
                owner.setClipChildren(true);

                if ( !(false) ) {

                owner.removeView(view);
                container.addView(view);
                view.setVisibility(View.VISIBLE);

                container.removeView(im);                      // remove existing image
                owner.addView(im);
                im.setVisibility(View.VISIBLE);

                    Log.v("tagia", "1.results: " + event.getResult());
                    Log.v("TAG --------> ", "Container childCount: " + owner.getChildCount());

                } else {

                    view.setVisibility(View.VISIBLE);
                }

                break;
            case DragEvent.ACTION_DRAG_ENDED:
                v.setBackgroundDrawable(normalShape);


                /*if ( check ) {

                    Toast.makeText(context, "Right - Game Over!", Toast.LENGTH_SHORT).show();
                }*/
            default:
                break;
        }
        return true;
    }
}