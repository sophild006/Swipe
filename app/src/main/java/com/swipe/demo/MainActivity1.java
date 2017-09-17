package com.swipe.demo;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LogPrinter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity1 extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private int screenWidth;
    private RelativeLayout header,rlalpha;
    private LinearLayout content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }
    private int ivX;
    private int tvX;
    private int tvY;
    private int contentY;
    private int targetX;
    private int MAX_DISTANCE;
    private int headerHeight;
    private int textTopMargin;
    private int textLeftMargin;
    private int imageLeftMargin;
    private int targetY;
    private void initView() {
        MAX_DISTANCE=dip2px(80);
        targetY=dip2px(50);
        imageView= (ImageView) findViewById(R.id.imageview);
        textView= (TextView) findViewById(R.id.textview);
        header= (RelativeLayout) findViewById(R.id.header);
        content= (LinearLayout) findViewById(R.id.content);
        rlalpha= (RelativeLayout) findViewById(R.id.rlalpha);
        screenWidth=getResources().getDisplayMetrics().widthPixels;
        targetX=screenWidth/2;
        ViewTreeObserver viewTreeObserver = findViewById(R.id.activity_main).getViewTreeObserver();

//        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
                headerHeight=header.getLayoutParams().height;
                RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) textView.getLayoutParams();
                textTopMargin=params.topMargin;
                textLeftMargin=params.leftMargin;

                RelativeLayout.LayoutParams params1= (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                imageLeftMargin=params1.leftMargin;
                tvX= (int) textView.getX();
                contentY= (int) content.getY();

                Log.d("wwq","height=="+headerHeight+"  textTopMargin="+textTopMargin);
//            }
//        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnim();
            }
        });

        initDistance();
    }

    private void startAnim() {

        ValueAnimator valueAnimatorHeader=ValueAnimator.ofFloat(header.getLayoutParams().height,mMinHight).setDuration(200);
        valueAnimatorHeader.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value= (float) animation.getAnimatedValue();
                RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) header.getLayoutParams();
                params.height= (int) value;
                header.setLayoutParams(params);
            }
        });
        valueAnimatorHeader.start();
        final RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) textView.getLayoutParams();
        ValueAnimator valueAnimator=ValueAnimator.ofFloat(params.leftMargin, (mTextLeft+(targetX-textView.getWidth()/2))).setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value= (float) animation.getAnimatedValue();
                params.leftMargin= (int) value;
                textView.setLayoutParams(params);
            }
        });
        valueAnimator.start();






        ValueAnimator valueAnimator1=ValueAnimator.ofFloat(params.topMargin,(mTextTop-(mTextTop-params.height/2-dip2px(10))-header.getPaddingTop())).setDuration(200);
        valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value= (float) animation.getAnimatedValue();
                params.topMargin= (int) value;
                textView.setLayoutParams(params);
            }
        });
        valueAnimator1.start();
        final RelativeLayout.LayoutParams params1= (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        ValueAnimator valueAnimator2=ValueAnimator.ofFloat(params1.leftMargin,mPhotoLeft + mPhotoNeedMoveDistanceX).setDuration(200);
        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value= (float) animation.getAnimatedValue();
                params1.leftMargin= (int) value;
                imageView.setLayoutParams(params1);
            }
        });

        valueAnimator2.start();
    }


    private int mLastY = 0;  //最后的点
    private static int mNeedDistance;   // 需要滑动的距离
    private static int mMinHight; //最小高度
    private static int mOrignHight; //原始的高度

    private int mCurrentDistance = 0;  //当前的距离
    private float mRate = 0;  //距离与目标距离的变化率 mRate=mCurrentDistance/mNeedDistance
    private int mPhotoOriginHeight; //图片的原始高度
    private int mPhotoOriginWidth; //图片的原始宽度
    private int mPhotoLeft;  //图片距左边的距离
    private int mPhotoTop;  //图片距离上边的距离
    private int mPhotoNeedMoveDistanceX;  // 图片需要移动的X距离
    private int mPhotoNeedMoveDistanceY;  // 图片需要移动的Y距离
    //需要移动的文字
    private int mTextLeft;  //文字距左边的距离
    private int mTextTop;  //文字距离上边的距离
    private int mTextNeedMoveDistanceX;  // 文字需要移动的X距离
    private int mTextNeedMoveDistanceY;  //文字需要移动的Y距离

    /**
     * 初始化需要滚动的距离
     */
    private void initDistance() {
        mOrignHight = header.getLayoutParams().height;
        mMinHight = dip2px(45);  //设置最小的高度为这么多
        mNeedDistance = mOrignHight - mMinHight;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        mPhotoOriginHeight = params.height;
        mPhotoOriginWidth = params.width;
        mPhotoLeft = params.leftMargin;
        mPhotoTop = params.topMargin;
        mPhotoNeedMoveDistanceX =targetX - mPhotoLeft - mMinHight-dip2px(30);
        mPhotoNeedMoveDistanceY = mPhotoTop -dip2px( 10);
        /*******************移动的文字初始化***************************/
        RelativeLayout.LayoutParams textParams = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        mTextLeft = textParams.leftMargin;
        mTextTop = textParams.topMargin;
        mTextNeedMoveDistanceX = targetX/ 2 - mTextLeft + 10;
        mTextNeedMoveDistanceY = mTextTop - mMinHight / 2 / 2;  //这里计算有点误差，正确的应该是剪去获取textview高度的一半
    }
private int dy0;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = (int) ev.getY();
                Log.d("wwq", "ACTION_MOVE==mCurrentDistance" + mCurrentDistance);
                return super.dispatchTouchEvent(ev); //传递事件 例如可以用来子view的点击事件等
            case MotionEvent.ACTION_MOVE:
                int y = (int) ev.getY();
                int dy = mLastY - y;
                Log.d("wwq", "ACTION_MOVE==mCurrentDistance" + mCurrentDistance);
                if (mCurrentDistance >= mNeedDistance && dy > 0) {
                    return super.dispatchTouchEvent(ev);  //传递事件
                }
                if (mCurrentDistance <= 0 && dy < 0) {
                    return super.dispatchTouchEvent(ev); //把事件传递进去
                }
                //改变布局
                changeTheLayout(dy);
                dy0=dy;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                checkTheHeight();
                Log.d("wwq", "ACTION_MOVE==mCurrentDistance" + mCurrentDistance);
                if (mCurrentDistance>0&&dy0>=0 ) {
                     startAnim();
                }
                if(mCurrentDistance>0&&dy0<0){
                    startAnim1();
                }
                return super.dispatchTouchEvent(ev);
        }

        return false;
    }

    private void startAnim1() {

        Log.d("wwq","------------------------");
        final RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) header.getLayoutParams();
        final RelativeLayout.LayoutParams params1= (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        final RelativeLayout.LayoutParams params2= (RelativeLayout.LayoutParams) textView.getLayoutParams();
        ValueAnimator valueAnimator=ValueAnimator.ofFloat(params.height,mOrignHight).setDuration(200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value= (float) animation.getAnimatedValue();
                params.height= (int) value;
                header.setLayoutParams(params);
            }
        });
        valueAnimator.start();

        ValueAnimator valueAnimator1=ValueAnimator.ofFloat(params2.leftMargin,textLeftMargin).setDuration(200);
        valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
             float value= (float) animation.getAnimatedValue();
                params2.leftMargin= (int) value;
                textView.setLayoutParams(params2);
            }
        });
        valueAnimator1.start();

        ValueAnimator valueAnimator2=ValueAnimator.ofFloat(params2.topMargin,textTopMargin).setDuration(200);
        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float value= (float) animation.getAnimatedValue();
                params2.topMargin= (int) value;
                textView.setLayoutParams(params2);
            }
        });
        valueAnimator2.start();

        ValueAnimator valueAnimator3=ValueAnimator.ofFloat(params1.leftMargin,mPhotoLeft).setDuration(200);
        valueAnimator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value= (float) animation.getAnimatedValue();
                params1.leftMargin= (int) value;
                imageView.setLayoutParams(params1);
            }
        });
        valueAnimator3.start();
    }

    /**
     * 通过滑动的偏移量
     *
     * @param dy
     */
    private void changeTheLayout(int dy) {
        Log.d("wwq","dy="+dy);
        final ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
        layoutParams.height = layoutParams.height - dy;
        header.setLayoutParams(layoutParams);
        checkTheHeight();
        header.requestLayout();
        //计算当前移动了多少距离
        mCurrentDistance = mOrignHight - header.getLayoutParams().height;
        mRate = (float) (mCurrentDistance * 1.0 / mNeedDistance);
//        changeTheAlphaAndPostion(mRate);  //获取偏移率然后改变某些控件的透明度，和位置

        RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) textView.getLayoutParams();
        Log.d("wwq","params.width=="+params.width);
        params.leftMargin= (int) (mTextLeft+(targetX-textView.getWidth()/2)*mRate);
//                                (mTextTop-(mTextTop-params.height/2-dip2px(10))-)
        params.topMargin= (int) (mTextTop-(mTextTop-params.height/2-dip2px(10))*mRate);
        textView.setLayoutParams(params);



        RelativeLayout.LayoutParams params1= (RelativeLayout.LayoutParams) imageView.getLayoutParams();
//        params1.leftMargin= (int) (mPhotoLeft+targetX/2*mRate);
//        imageView.setLayoutParams(params1);


        final RelativeLayout.LayoutParams photoParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
//        photoParams.width = (int) (mPhotoOriginWidth - (mRate * (mPhotoOriginWidth - mMinHight -dip2px(10))));
//        photoParams.height = (int) (mPhotoOriginWidth - (mRate * (mPhotoOriginWidth - mMinHight -dip2px( 10))));
        photoParams.leftMargin = (int) (mPhotoLeft + mPhotoNeedMoveDistanceX * mRate);
        imageView.setLayoutParams(photoParams);
       Log.d("wwq" ,"ACTION_MOVE==dy" + dy);
    }


    private void checkTheHeight() {
        final ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
        Log.d("wwq","layoutParams.height ="+layoutParams.height +"   mMinHight="+mMinHight);
        if (layoutParams.height < mMinHight) {
            layoutParams.height = mMinHight;
            header.setLayoutParams(layoutParams);
            header.requestLayout();

        }
        if (layoutParams.height > mOrignHight) {
            layoutParams.height = mOrignHight;
            header.setLayoutParams(layoutParams);
            header.requestLayout();
        }


    }


    private void updateTv(int distance,float progress) {

        RelativeLayout.LayoutParams headerParams= (RelativeLayout.LayoutParams) header.getLayoutParams();
        headerParams.height= (int) (headerHeight+(headerHeight*progress));
        Log.d("wwq","headerHeight*progress= "+headerHeight*progress+" headerHeight=="+headerHeight+"  progress=="+progress);
        header.setLayoutParams(headerParams);

        header.requestLayout();;
//        Log.d("wwq","progress"+progress);
        RelativeLayout.LayoutParams tvParams= (RelativeLayout.LayoutParams) textView.getLayoutParams();
        tvParams.topMargin= (int) (textTopMargin+(headerHeight-dip2px(20))*progress);
        tvParams.leftMargin= (int) (textLeftMargin-progress*targetX);
        textView.setLayoutParams(tvParams);
//        currentTopMarginY=headerHeight-header.getLayoutParams().height;
//        currentTopMarginY=header.getLayoutParams().height;
//        Log.d("wwq","currentTopMarginY="+currentTopMarginY);
    }

    public   int dip2px(float dpValue) {
        final float scale =  getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
