package com.sonoptek.httpscheckdemo;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TestLayout extends LinearLayout {
    private static TestLayout theLayout;
    public static TestLayout getTheLayout(){
        return theLayout;
    }
    public TestLayout(Context context) {
        super(context);
    }

    public TestLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        theLayout=this;
        TextView textView=new TextView(context);
        textView.setText("自定义View");
        textView.setTextColor(Color.parseColor("#ff0000"));
        textView.setTextSize(40.0f);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0,1);
        params.setMargins(30,10,30,0);
        addView(textView,params);
        Button button=new Button(context);
        button.setText("hhhhh");
        button.setTextColor(Color.parseColor("#ff0000"));
        button.setTextSize(20.0f);
        LinearLayout.LayoutParams btnParams=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,0,1);
        btnParams.setMargins(30,10,30,0);
        addView(button,btnParams);
    }

    public TestLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        super.onLayout(changed,left,top,right,bottom);
        /*Log.e("TTTT", "onLayout changed: "+changed+" left: "+left+" top: "+top+" right: "+right+" bottom: "+bottom );


        Log.e("TTTT", "onLayout: "+getChildCount() );
        // 1. 遍历子View：循环所有子View
        for (int i=0;i<getChildCount();i++){

            View child=getChildAt(i);
            child.layout(left, top, right, bottom);
        }*/
    }
}
