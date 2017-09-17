package com.swipe.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ExpandedMenuView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private int screenWidth;
    private RelativeLayout header;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }
    private int ivX;
    private int tvX;
    private int tvY;
    private int targetX;
    private int MAX_DISTANCE;
    private int targetY;
    private void initView() {
        MAX_DISTANCE=dip2px(100);
        targetY=dip2px(50);
        imageView= (ImageView) findViewById(R.id.imageview);
        textView= (TextView) findViewById(R.id.textview);
        header= (RelativeLayout) findViewById(R.id.header);
        screenWidth=getResources().getDisplayMetrics().widthPixels;
        targetX=screenWidth/2;
        ViewTreeObserver viewTreeObserver = findViewById(R.id.activity_main).getViewTreeObserver();

        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) textView.getLayoutParams();
                tvY= params.topMargin;
                tvX=params.leftMargin;
                Log.d("wwq","tvY="+tvY);
                currentTopMarginY=header.getLayoutParams().height;
                Log.d("wwq","currentTopMarginY 1   ="+currentTopMarginY);
            }
        });
    }



    private float startY;
    private float progress;
    private int currentTopMarginY;
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                startY=event.getY();
                return super.dispatchTouchEvent(event);
            case MotionEvent.ACTION_MOVE:
                float currentY=event.getY();
                int distance= (int) (currentY-startY);
                progress=distance/(float)MAX_DISTANCE;
                Log.d("wwq","distance: "+distance+"  progress="+progress);
                if(distance<0&&(currentTopMarginY<targetY)){
                    return super.dispatchTouchEvent(event);
                }
                updateTv(progress);
                startY= event.getY();
                return true;
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;

    }

    private void updateTv(float progress) {
        RelativeLayout.LayoutParams headerParams= (RelativeLayout.LayoutParams) header.getLayoutParams();
        headerParams.height= (int) (headerParams.height+progress*MAX_DISTANCE);
        header.setLayoutParams(headerParams);
        Log.d("wwq","progress"+progress);
        RelativeLayout.LayoutParams tvParams= (RelativeLayout.LayoutParams) textView.getLayoutParams();
        tvParams.topMargin= (int) (tvY+MAX_DISTANCE/2*progress);
        tvParams.leftMargin= (int) (tvX-progress*targetX);
        textView.setLayoutParams(tvParams);
        currentTopMarginY=header.getLayoutParams().height;
        Log.d("wwq","currentTopMarginY="+currentTopMarginY);
    }

    public   int dip2px(float dpValue) {
        final float scale =  getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
