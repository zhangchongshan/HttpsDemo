package com.sonoptek.httpscheckdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;


/**
 * Created by zhangchongshan on 2019/12/17.
 */
public class MySeekBar extends AppCompatSeekBar {
    private int section=4;

    public MySeekBar(Context context) {
        super(context);
    }

    public MySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSection(int section){
        this.section=section;
    }
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        Paint paint=new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        int offest=getWidth()/section;
        for (int i=1;i<=section;i++){

            canvas.drawCircle(offest*i,getHeight()/2,getHeight()/2,paint);
        }
        super.onDraw(canvas);
    }

    @Override
    public void setBackgroundTintMode(@Nullable PorterDuff.Mode tintMode) {
        super.setBackgroundTintMode(tintMode);
    }
}
