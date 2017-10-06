package com.swipe.demo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.ViewAnimator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by wwq on 2017/10/6.
 * 第一步：滚动的时候试着改变头部的高度
 * <p>
 * 第二步：逐渐让周围不需要的字体消失
 * <p>
 * 第三步：根据偏移率计算偏移量，然后把图片和需要的文字移动到顶端中心处
 */

public class SwipeView extends RelativeLayout {
    private static final String TAG = "wwq";
    private RelativeLayout rlHeader;
    private TextView tvName, tvSchool;
    private CircleImageView ivHeader;
//    private SwipeRefreshLayout swipeRefresh;


    /**
     * 几个状态
     */

    public static int TOP=0;
    public static int BOTTOM=1;
    public int currentState=BOTTOM;
    /**
     * 变量
     *
     * @param context
     */
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
     * /**
     * 初始化需要滚动的距离
     */


    public SwipeView(Context context) {
        super(context, null);
    }

    public SwipeView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public SwipeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        init();
        mScroller=new Scroller(context);
    }

    private void init() {

        mOrignHight = rlHeader.getLayoutParams().height;
        mMinHight = UIUtils.dip2px(getContext(), 45);  //设置最小的高度为这么多
        mNeedDistance = mOrignHight - mMinHight;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivHeader.getLayoutParams();
        mPhotoOriginHeight = params.height;
        mPhotoOriginWidth = params.width;
        mPhotoLeft = params.leftMargin;
        mPhotoTop = params.topMargin;
        mPhotoNeedMoveDistanceX = UIUtils.getWindowWidth(getContext()) / 2 - mPhotoLeft - mMinHight;
        mPhotoNeedMoveDistanceY = mPhotoTop - UIUtils.dip2px(getContext(), 10);
        /*******************移动的文字初始化***************************/
        RelativeLayout.LayoutParams textParams = (RelativeLayout.LayoutParams) tvName.getLayoutParams();
        mTextLeft = textParams.leftMargin;
        mTextTop = textParams.topMargin;
        mTextNeedMoveDistanceX = UIUtils.getWindowWidth(getContext()) / 2 - mTextLeft + 10;
        mTextNeedMoveDistanceY = mTextTop - mMinHight / 2 / 2;  //这里计算有点误差，正确的应该是剪去获取textview高度的一半

        Log.d("wwq","mNeedDistance= "+mNeedDistance);
    }
    RecyclerView recyclerView;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        rlHeader = (RelativeLayout) findViewById(R.id.rl_header);
        ivHeader = (CircleImageView) findViewById(R.id.iv_header);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvSchool = (TextView) findViewById(R.id.tv_school);
        rlHeader.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                init();
                rlHeader.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        recyclerView= (RecyclerView) findViewById(R.id.recleview);
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(currentState==TOP &&recyclerView.canScrollVertically(-1)){
            return false;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = (int) ev.getY();
                tempY= (int) ev.getY();
                Log.d(TAG, "ACTION_DOWN==mCurrentDistance" + mCurrentDistance);
                break;//传递事件 例如可以用来子view的点击事件等
            case MotionEvent.ACTION_MOVE:
                int y = (int) ev.getY();
                int dy = mLastY - y;
                Log.d("wwq", "dy: " + dy);
                Log.d(TAG, "ACTION_MOVE==mCurrentDistance" + mCurrentDistance);
                if (mCurrentDistance >= mNeedDistance && dy >= 0) {
                    Log.d("wwq", "event false 1");
                    return false;  //传递事件
                }
                if (mCurrentDistance <= 0 && dy <= 0) {
                    Log.d("wwq", "event false 2");
//                    swipeRefresh.setRefreshing(true);
                    return false; //把事件传递进去
                }
                Log.d("wwq","------------------");
//                //改变布局
//                changeTheLayout(dy);
//                mLastY = y;
                  return true;
            case MotionEvent.ACTION_UP:
//                checkTheHeight();
                Log.d(TAG, "ACTION_UP==mCurrentDistance" + mCurrentDistance);
                return false;
        }

        return super.onInterceptTouchEvent(ev);

    }
//
    private int tempY;
    private boolean isUp;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = (int) ev.getY();
                Log.d(TAG, "ACTION_DOWN==mCurrentDistance" + mCurrentDistance);
                break; //传递事件 例如可以用来子view的点击事件等
            case MotionEvent.ACTION_MOVE:
                int distance=((int)getY()-tempY);
                Log.d("wwq","=====  "+distance);
                int y = (int) ev.getY();
                int dy = mLastY - y;

                if (mCurrentDistance >= mNeedDistance && dy > 0) {
//                    Log.d("wwq", "event false 1");
//                    requestDisallowInterceptTouchEvent(true);
                    return false;  //传递事件
                }
                if (mCurrentDistance <= 0 && dy < 0) {
                    Log.d("wwq", "event false 2");
//                    requestDisallowInterceptTouchEvent(true);
                    return false; //把事件传递进去
                }
                Log.d(TAG, "dy = "+dy+"             ACTION_MOVE==mCurrentDistance" + mCurrentDistance);
                //改变布局
                changeTheLayout(dy);
                mLastY = y;
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mVelocityTracker != null) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000);
                    int velocityY = (int) velocityTracker.getYVelocity();
                    if (velocityY > 400){//下划
                        startToTop(0,mOrignHight);
                    } else if (velocityY < -400){//上划
                        startToTop(1,mMinHight);
                    } else {
                        checkTheHeight();
                        tempY=0;
                        if(mCurrentDistance>mNeedDistance/2){
                            Log.d("wwq","> 1/2");
                            startToTop(1,mMinHight);
                        }else{
                            Log.d("wwq","< 1/2");
                            startToTop(0,mOrignHight);
                        }
                    }
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }

                Log.d(TAG, "ACTION_UP==mCurrentDistance" + mCurrentDistance);
                break;
        }
        return false;

    }

    /**
     * 通过滑动的偏移量
     *
     * @param dy
     */
    private void changeTheLayout(int dy) {
        final ViewGroup.LayoutParams layoutParams = rlHeader.getLayoutParams();
        layoutParams.height = layoutParams.height - dy;
        rlHeader.setLayoutParams(layoutParams);
        checkTheHeight();
        rlHeader.requestLayout();
        //计算当前移动了多少距离
        mCurrentDistance = mOrignHight - rlHeader.getLayoutParams().height;
        mRate = (float) (mCurrentDistance * 1.0 / mNeedDistance);
        Log.d("wwq","rate: "+mRate);
        changeTheAlphaAndPostion(mRate);  //获取偏移率然后改变某些控件的透明度，和位置
        Log.d(TAG, "ACTION_MOVE==dy" + dy);
    }
    //获取了偏移率（0.0-1.0），然后就可以根据这个改变透明度了和大小了。

    /**
     * 根据变化率来改变这些这些控件的变化率位置
     *
     * @param rate
     */
    private void changeTheAlphaAndPostion(float rate) {
        //先改变一些控件的透明度
        if (rate >= 1) {
//            tv_user_hosipital.setVisibility(View.GONE);
//            tv_user_hosipital_level.setVisibility(View.GONE);
//            tv_user_project.setVisibility(View.GONE);
        } else {
//            tv_user_hosipital.setVisibility(View.VISIBLE);
//            tv_user_hosipital_level.setVisibility(View.VISIBLE);
//            tv_user_project.setVisibility(View.VISIBLE);
//            tv_user_hosipital.setAlpha(1 - rate);
//            tv_user_hosipital_level.setAlpha(1 - rate);
//            tv_user_project.setAlpha(1 - rate);
//            tv_user_hosipital.setScaleY(1 - rate);
//            tv_user_hosipital.setScaleX(1 - rate);
//            tv_user_hosipital_level.setScaleY(1 - rate);
//            tv_user_hosipital_level.setScaleX(1 - rate);
//            tvName.setScaleY(1 - rate);
//            tvName.setScaleX(1 - rate);
        }

        //接下来是改变控件的大小和位置了  （这就是关键了）
        final RelativeLayout.LayoutParams photoParams = (RelativeLayout.LayoutParams) ivHeader.getLayoutParams();
        photoParams.width = (int) (mPhotoOriginWidth - (rate * (mPhotoOriginWidth - mMinHight - UIUtils.dip2px(getContext(), 10))));
        photoParams.height = (int) (mPhotoOriginWidth - (rate * (mPhotoOriginWidth - mMinHight - UIUtils.dip2px(getContext(), 10))));
        photoParams.leftMargin = (int) (mPhotoLeft + mPhotoNeedMoveDistanceX * rate);
        photoParams.topMargin = (int) (mPhotoTop - mPhotoNeedMoveDistanceY * rate);
        Log.d(TAG, "photoParams.leftMargin" + photoParams.leftMargin);
        Log.d(TAG, " photoParams.topMargin" + photoParams.topMargin);
        ivHeader.setLayoutParams(photoParams);
        /*********************文字设置****************************/
        final RelativeLayout.LayoutParams textParams = (RelativeLayout.LayoutParams) tvName.getLayoutParams();
        textParams.leftMargin = (int) (mTextLeft + mTextNeedMoveDistanceX * rate);
        textParams.topMargin = (int) (mTextTop - mTextNeedMoveDistanceY * rate);
        Log.d(TAG, "textParams.leftMargin" + textParams.leftMargin);
        Log.d(TAG, " textParams.topMargin" + textParams.topMargin);
        tvName.setLayoutParams(textParams);
    }
    //卧槽，搞忘了一个方法，滑动的时候需要检查边界值；这里写了一个方法：

    /**
     * 检查上边界和下边界
     */
    private void checkTheHeight() {
        final ViewGroup.LayoutParams layoutParams = rlHeader.getLayoutParams();
        if (layoutParams.height < mMinHight) {
            layoutParams.height = mMinHight;
            rlHeader.setLayoutParams(layoutParams);
            rlHeader.requestLayout();
        }
        if (layoutParams.height > mOrignHight) {
            layoutParams.height = mOrignHight;
            rlHeader.setLayoutParams(layoutParams);
            rlHeader.requestLayout();
        }

    }


    private void startToTop(float rate,int height){
        if(rate==1){
            currentState=TOP;

        }else{
            currentState=BOTTOM;
        }
        final RelativeLayout.LayoutParams rlParams= (RelativeLayout.LayoutParams) rlHeader.getLayoutParams();
        final RelativeLayout.LayoutParams ivParams = (RelativeLayout.LayoutParams) ivHeader.getLayoutParams();
        final RelativeLayout.LayoutParams tvNameParams = (RelativeLayout.LayoutParams) tvName.getLayoutParams();
        RelativeLayout.LayoutParams tvSchoolParams= (RelativeLayout.LayoutParams) tvSchool.getLayoutParams();
        final ValueAnimator rlHAnim=ValueAnimator.ofFloat(rlParams.height,height).setDuration(200);
        rlHAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                rlParams.height= (int) animatedValue;
                rlHeader.setLayoutParams(rlParams);
            }
        });

        ValueAnimator ivLeftAnim= ValueAnimator.ofFloat(ivParams.leftMargin,mPhotoLeft + mPhotoNeedMoveDistanceX * rate).setDuration(200);
        ivLeftAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value= (float) animation.getAnimatedValue();
                ivParams.leftMargin= (int) value;
                ivHeader.setLayoutParams(ivParams);
            }
        });
       ValueAnimator ivTopAnim=ValueAnimator.ofFloat(ivParams.topMargin,mPhotoTop - mPhotoNeedMoveDistanceY * rate).setDuration(200);
        ivTopAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value= (float) animation.getAnimatedValue();
                ivParams.topMargin= (int) value;
                ivHeader.setLayoutParams(ivParams);

            }
        });

        ValueAnimator ivScaleAnim=ValueAnimator.ofFloat(ivParams.height,mPhotoOriginWidth - (rate * (mPhotoOriginWidth - mMinHight - UIUtils.dip2px(getContext(), 10)))).setDuration(200);
        ivScaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value= (float) animation.getAnimatedValue();
                ivParams.width= (int) value;
                ivParams.height= (int) value;
                ivHeader.setLayoutParams(ivParams);
            }
        });


        final ValueAnimator tvNameLeftAnim=ValueAnimator.ofFloat(tvNameParams.leftMargin,mTextLeft + mTextNeedMoveDistanceX * rate).setDuration(200);
        tvNameLeftAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value= (float) animation.getAnimatedValue();
                tvNameParams.leftMargin= (int) value;
                tvName.setLayoutParams(tvNameParams);
            }
        });

        ValueAnimator tvNameTopAnim=ValueAnimator.ofFloat(tvNameParams.topMargin,mTextTop - mTextNeedMoveDistanceY * rate).setDuration(200);
        tvNameTopAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value= (float) animation.getAnimatedValue();
                tvNameParams.topMargin= (int) value;
                tvName.setLayoutParams(tvNameParams);
            }
        });


        ivTopAnim.start();
        rlHAnim.start();
        ivLeftAnim.start();
        tvNameTopAnim.start();
        tvNameLeftAnim.start();
        ivScaleAnim.start();

    }

    private void startToBottom(){}



}
