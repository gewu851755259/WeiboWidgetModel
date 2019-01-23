package yml.com.baseapplib.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

import java.util.Arrays;

import yml.com.baseapplib.R;

public class VerticalSlidingView extends LinearLayout implements View.OnTouchListener {

    private static final String TAG = "VerticalSlidingView";

    public VerticalSlidingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public VerticalSlidingView(Context context) {
        super(context);
        init(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = this.getChildCount();
        int total_top = top;

        mTitleHeight = getChildAt(0).getMeasuredHeight();
        mBodyHeight = getChildAt(1).getMeasuredHeight();
        if (state == CLOSE_STATE) {
            total_top = top = mBodyHeight;
        } else if (state == OPEN_STATE) {
            total_top = top = 0;
        }

        /**
         * 重新绘制各个子View的位置
         */
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            child.layout(0, total_top, childWidth, total_top + childHeight);
            total_top += childHeight;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return isSliding ? titleViewTouch(view, motionEvent) : super.onTouchEvent(motionEvent);
    }

    /**
     * 自定义接口
     * --------------------位置变化监听器
     */
    public interface OnPositionChangedListener {
        void onPositionChange(int state);
    }

    private OnPositionChangedListener mPosListener;

    public void setOnPositionChangedListener(OnPositionChangedListener listener) {
        mPosListener = listener;
    }

    // init方法中的全局变量
    private int titleViewResId = -1;
    private int bodyViewResId = -1;
    private boolean isSliding = true;
    private View mTitleView;
    private View mBodyView;
    private TitleView tt;
    private float touchSlop;

    /**
     * 构造方法中初始化函数，设置一些属性及实例化一些View
     */
    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs,
                    R.styleable.VerticalSlidingView, 0, 0);
            if (ta != null) {
                titleViewResId = ta.getResourceId(R.styleable.VerticalSlidingView_titleViewInSearch, -1);
                bodyViewResId = ta.getResourceId(R.styleable.VerticalSlidingView_bodyViewInSearch, -1);
                isSliding = ta.getBoolean(R.styleable.VerticalSlidingView_isSlidingInSearch, true);
            }
            ta.recycle();
        }

        if (titleViewResId != -1 && bodyViewResId != -1) {
            mTitleView = LayoutInflater.from(context).inflate(titleViewResId, null);
            mBodyView = LayoutInflater.from(context).inflate(bodyViewResId, null);

            LinearLayout.LayoutParams titleParams = new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            tt = new TitleView(context);
            mTitleView.setBackgroundResource(R.drawable.hualan_push_background);
            tt.addView(mTitleView, titleParams);
            addView(tt, titleParams);

            LinearLayout.LayoutParams bodyParams = new LayoutParams(
                    LayoutParams.MATCH_PARENT, 0, 1);
            addView(mBodyView, bodyParams);
        } else {
            return;
        }

        ViewConfiguration vc = ViewConfiguration.get(context);
        touchSlop = vc.getScaledTouchSlop(); // 获取系统触发移动事件的最小距离
        Log.e(TAG, "touchSlop = " + touchSlop);
        tt.setOnTouchListener(this);
    }


    // 记录Touch事件的一些屏幕值、距离等
    private boolean isInterceptTouchMoveDoing = false; // 是否中断移动操作
    private int startY; // 记录按下时起始位置
    private int oldY;
    private int distanceY; // y轴反向移动距离
    private int[] location;
    private boolean isOnTouchMove = false;
    private int top;

    /**
     * TitleView的Touch处理事件函数
     */
    private boolean titleViewTouch(View v, MotionEvent event) {

        int y = (int) event.getRawY(); // 屏幕坐标

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isInterceptTouchMoveDoing = false;
                startY = (int) event.getY(); // 控件坐标
                oldY = y;
                distanceY = 0;
                location = new int[2];
                getLocationInWindow(location); // 得到该自定义View左上角在屏幕中的位置
                break;

            case MotionEvent.ACTION_MOVE:
                distanceY = y - startY - location[1]; // location[1]
                isOnTouchMove = state == OPEN_TO_CLOSE || state == CLOSE_TO_OPEN
                        || (state != CLOSE_TO_OPEN && state != OPEN_TO_CLOSE && Math.abs(y - oldY) > touchSlop);
                if (isOnTouchMove) { // 触发滑动
                    top = distanceY;
                    if (state == CLOSE_STATE) {
                        state = CLOSE_TO_OPEN;
                        if (isInterceptTouchMoveDoing) top = mBodyHeight + top;
                        if (top > mBodyHeight) top = mBodyHeight;
                        else if (top < 0) top = 0;
                    } else if (state == OPEN_STATE) {
                        state = OPEN_TO_CLOSE;
                        if (top > mBodyHeight) top = mBodyHeight;
                        else if (top < 0) top = 0;
                    } else if (state == CLOSE_TO_OPEN) {
                        if (isInterceptTouchMoveDoing) top = mBodyHeight + top;
                        if (top > mBodyHeight) top = mBodyHeight;
                        else if (top < 0) top = 0;
                    } else if (state == OPEN_TO_CLOSE) {
                        if (top > mBodyHeight) top = mBodyHeight;
                        else if (top < 0) top = 0;
                    }
                    requestLayout();
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_OUTSIDE:
                if (state == CLOSE_TO_OPEN || state == OPEN_TO_CLOSE) {
                    if (top > (mBodyHeight / 2)) pull();
                    else push();
                } else {
                    if (state == CLOSE_STATE) push();      // 弹出
                    else if (state == OPEN_STATE) pull();  // 缩进
                }
                break;
        }

        return true;

    }

    /**
     * 内部类TitleView
     */
    private class TitleView extends LinearLayout {
        public TitleView(Context context) {
            super(context);
            init(context, null);
        }

        public TitleView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init(context, attrs);
        }

        private void init(Context context, AttributeSet attrs) {

        }

        /**
         * 控制是否把滑动权提交给父控件
         *
         * @param ev
         * @return
         */
        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (isSliding) {
                int y = (int) ev.getRawY(); // 屏幕坐标
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    isInterceptTouchMoveDoing = true;
                    startY = (int) ev.getY(); // 控件坐标
                    oldY = y;
                    distanceY = 0;
                    location = new int[2];
                    getLocationInWindow(location);
                }

                if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                    if (Math.abs(y - oldY) < touchSlop)
                        return super.onInterceptTouchEvent(ev);
                    else return true;
                }
            }
            return super.onInterceptTouchEvent(ev);
        }
    }


    private PullTask mPullTask;

    /**
     * 缩进BodyView，只剩下TitleVIew，且在最底端
     */
    private void pull() {
        if (mPullTask != null && !mPullTask.isCancelled()) {
            mPullTask.cancel(true);
            mPullTask = null;
        }
        mPullTask = new PullTask();
        mPullTask.execute();
        mTitleView.setBackgroundResource(R.drawable.hualan_push_background);
    }

    private PushTask mPushTask;

    /**
     * 弹出BodyView，TitleView到达该自定义控件最顶端
     */
    private void push() {
        if (mPushTask != null && !mPushTask.isCancelled()) {
            mPushTask.cancel(true);
            mPushTask = null;
        }
        mPushTask = new PushTask();
        mPushTask.execute();
        mTitleView.setBackgroundResource(R.drawable.hualan_pull_background);
    }


    // 全局的一些标志变量
    private static final int CLOSE_STATE = 0;
    private static final int MOVE_STATE = 1;
    private static final int OPEN_STATE = 2;
    private static final int CLOSE_TO_OPEN = 3;
    private static final int OPEN_TO_CLOSE = 4;

    private static final int THREAD_STEP = 40; // 动画步长

    private int state = CLOSE_STATE;
    private int mBodyHeight;
    private int mTitleHeight;


    class PullTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            state = OPEN_TO_CLOSE;
            while (top < mBodyHeight - THREAD_STEP) {
                top += THREAD_STEP;
                mHandler.sendEmptyMessage(0);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            top = mBodyHeight;
            mHandler.sendEmptyMessage(0);
            state = CLOSE_STATE;
            mHandler.sendEmptyMessage(1);
            return true;
        }
    }

    class PushTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            state = CLOSE_TO_OPEN;
            while (top > THREAD_STEP) {
                top -= THREAD_STEP;
                mHandler.sendEmptyMessage(0);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            top = 0;
            mHandler.sendEmptyMessage(0);
            state = OPEN_STATE;
            mHandler.sendEmptyMessage(1);
            return true;
        }

    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: // 弹出或缩进过程中
                    requestLayout();
                    break;

                case 1: // 到达位置
                    if (mPosListener != null) mPosListener.onPositionChange(state);
                    break;
                default:
                    break;
            }
        }
    };

    public View getTitleView() {
        return mTitleView;
    }

    public View getBodyView() {
        return mBodyView;
    }
}
