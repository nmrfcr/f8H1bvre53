package com.tekapic;

/**
 * Created by LEV on 23/12/2018.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class Zoom extends View {

    private Drawable image;
    ImageButton img,img1;
    private int zoomControler=20;

    public Zoom(Context context, Drawable image){
        super(context);

        this.image = image;


//        image=context.getResources().getDrawable(R.drawable.j);
        //image=context.getResources().getDrawable(R.drawable.icon);

        setFocusable(true);

        Log.i("Zoom", "ZoomZoomZoom");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //here u can control the width and height of the images........ this line is very important
        image.setBounds((getWidth()/2)-zoomControler, (getHeight()/2)-zoomControler, (getWidth()/2)+zoomControler, (getHeight()/2)+zoomControler);
        image.draw(canvas);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_DPAD_UP){
            // zoom in
            zoomControler+=10;
        }
        if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN){
            // zoom out
            zoomControler-=10;
        }
        if(zoomControler<10){
            zoomControler=10;
        }

        invalidate();
        return true;
    }
}