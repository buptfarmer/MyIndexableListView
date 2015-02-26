package com.buptfarmer.example;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.woozzu.android.indexablelistview.R;

public class DragLeftActivity extends Activity implements GestureDetector.OnGestureListener {
    private GestureDetector mGestureDetctor;
    private FrameLayout mContainer;
    private MotionEvent mLastDownEvent;
    private float mLastDownX;
    private float mLastDownY;
    private TextView mBackCount;
    private boolean mDispatchToDragLeft;
    private float mTouchSlop;
    private Scroller mScroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGestureDetctor = new GestureDetector(this, this);
        setContentView(R.layout.activity_drag_left);
        mContainer = (FrameLayout) findViewById(R.id.container);
        mBackCount = (TextView) findViewById(R.id.back_count);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        mScroller = new Scroller(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
//        Log.d("ccc", "Activity dispatchTouchEvent" + event.getAction() + ", x:" + event.getX());

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mLastDownX = event.getRawX();
                mLastDownY = event.getRawY();
                mDispatchToDragLeft = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {

                float distanceX = event.getRawX() - mLastDownX;
                float distanceY = event.getRawY() - mLastDownY;
                if (!mDispatchToDragLeft && distanceX > distanceY) {
                    mDispatchToDragLeft = true;
                }
                if (mDispatchToDragLeft) {
//                    onTouchEvent(event);
//                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                if (mDispatchToDragLeft) {
//                    onTouchEvent(event);
//                    return true;
                }
                break;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        mGestureDetctor.onTouchEvent(event);
        Log.d("ccc", "Activity onTouchEvent" + event.getAction() + ", x:" + event.getX() + ", rawX:" + event.getRawX());
        int action = event.getAction();

        float distanceX = event.getRawX() - mLastDownX;
        float distanceY = event.getRawY() - mLastDownY;
        if (distanceX < 0) {
            distanceX = 0;
        }
        mContainer.setTranslationX(distanceX);
        float percentage = distanceX / 300;
        if (percentage > 1) {
            percentage = 1;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                mBackCount.setText(percentage + "");
                Log.d("ccc", "Activity onTouchEvent: disX" + distanceX + ", disY:" + distanceY);
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                long duration = (long) (500 * percentage);
                ObjectAnimator scrollAnimtor = ObjectAnimator.ofFloat(mContainer, "translationX", distanceX, 0).setDuration(duration);
                scrollAnimtor.setInterpolator(new AccelerateDecelerateInterpolator());
                scrollAnimtor.start();
//                mContainer.setTranslationX(0);
//                mContainer.setTranslationY(0);
                break;
            }
        }
        return super.onTouchEvent(event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drag_left, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d("ccc", "mGestureDetctor onScroll: disX" + distanceX + ", disY:" + distanceY);

        mContainer.setTranslationX(distanceX);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnTouchListener {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_drag_left, container, false);
//            rootView.setOnTouchListener((View.OnTouchListener) this);
//            rootView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d("ccc", "rootView onClick");
//
//                }
//            });
            rootView.setOnTouchListener(mCommonDetailTouchListener);
            return rootView;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d("ccc", "rootView onTouchEvent" + event.getAction() + ", x:" + event.getX());
            return true;
        }

        protected View.OnTouchListener mCommonDetailTouchListener = new View.OnTouchListener() {
            private static final int DRAGGING_STALL = 0;
            private static final int DRAGGING_LEFT = 1;
            private static final int DRAGGING_RIGHT = 2;
            private static final int DRAGGING_UP = 3;
            private static final int DRAGGING_DOWN = 4;

            private static final int TOTAL_DISTANCE = 600;
            private static final int VELOCITY_TRHESHOLD = 800;


            private int mCurrentDraggingState;
            private float mLastDownX;
            private float mLastDownY;
            private VelocityTracker mVelocityTracker;

            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                final int action = ev.getAction();
                final float x = ev.getRawX();
                final float y = ev.getRawY();
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                mVelocityTracker.addMovement(ev);
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mLastDownX = x;
                        mLastDownY = y;
                        mCurrentDraggingState = DRAGGING_STALL;
                        return true;
                    case MotionEvent.ACTION_MOVE:

                        float distanceX = ev.getRawX() - mLastDownX;
                        float distanceY = ev.getRawY() - mLastDownY;
                        Log.d("ccc", "rootView ACTION_MOVE" + ", distanceY=" + distanceY);

                        //todo replace magic number 10 with ViewConfiguration.get(this).getScaledTouchSlop();
                        if (mCurrentDraggingState == DRAGGING_STALL && Math.abs(distanceX) + Math.abs(distanceY) > 10) {

                            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                                // dragging horizontal
                                if (distanceX > 0) {
                                    mCurrentDraggingState = DRAGGING_RIGHT;
                                } else {
                                    mCurrentDraggingState = DRAGGING_LEFT;
                                }
                            } else {
                                // dragging vertical
                                if (distanceY > 0) {
                                    mCurrentDraggingState = DRAGGING_DOWN;
                                } else {
                                    mCurrentDraggingState = DRAGGING_UP;
                                }
                            }
                        }
                        if (mCurrentDraggingState != DRAGGING_STALL) {
                            // in one of the dragging state
                            // consume the horizontal touch event;
                            switch (mCurrentDraggingState) {
                                case DRAGGING_UP:
                                    return true;
                                case DRAGGING_DOWN:
                                    return true;
                                case DRAGGING_LEFT:
                                    return false;
                                case DRAGGING_RIGHT:
                                    return false;
                                default:
                                    return false;
                            }
                        }
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mVelocityTracker.computeCurrentVelocity(1000);
                        float velocityX = mVelocityTracker.getXVelocity();
                        float velocityY = mVelocityTracker.getYVelocity();
                        Log.d("ccc", String.format("rootView ACTION_UP, Vx= %f, Vy=%f", velocityX, velocityY));
                        if (mCurrentDraggingState == DRAGGING_STALL) {
                            // is click event; consume the event;
//                            performClick();
                            Log.d("ccc", "rootView performClick");
                            return true;
                        } else {

                            float distanceUpX = ev.getRawX() - mLastDownX;
                            float distanceUpY = ev.getRawY() - mLastDownY;
                            // in one of the dragging state
                            // consume the horizontal touch event;
                            switch (mCurrentDraggingState) {
                                case DRAGGING_UP:
                                    //TODO check distnce alone with velocity
                                    if (distanceUpY < -TOTAL_DISTANCE / 2 || velocityY < -VELOCITY_TRHESHOLD) {
//                                      performSrollUp();
                                        Log.d("ccc", "rootView DRAGGING_UP, DO");
                                    } else {
                                        Log.d("ccc", "rootView DRAGGING_UP, CANCEL");

                                    }
                                    return true;
                                case DRAGGING_DOWN:
                                    //TODO check distnce alone with velocity
                                    if (distanceUpY > TOTAL_DISTANCE / 2 || velocityY > VELOCITY_TRHESHOLD) {
//                                      performSrollDown();
                                        Log.d("ccc", "rootView DRAGGING_DOWN, DO");
                                    } else {
                                        Log.d("ccc", "rootView DRAGGING_DOWN, CANCEL");

                                    }
                                    return true;
                                case DRAGGING_LEFT:
                                    Log.d("ccc", "rootView DRAGGING_LEFT");

                                    return false;
                                case DRAGGING_RIGHT:
                                    Log.d("ccc", "rootView DRAGGING_RIGHT");

                                    return false;
                                default:
                                    return false;
                            }
                        }
                }
                return false;
            }
        };
    }
}
