/*
 * Copyright 2011 woozzu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.buptfarmer.example.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.widget.Adapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

public class ExpandableIndexScroller {

    private float mIndexbarWidth;
    private float mIndexbarMargin;
    private float mPreviewPadding;
    private float mDensity;
    private float mScaledDensity;
    private float mAlphaRate;
    private int mState = STATE_HIDDEN;
    private int mListViewWidth;
    private int mListViewHeight;
    private int mCurrentSection = -1;
    private boolean mIsIndexing = false;
    private IndexableExpandableListView mListView = null;
    private SectionIndexer mIndexer = null;
    private String[] mSections = null;
    private RectF mIndexbarRect;

    private static final int STATE_HIDDEN = 0;
    private static final int STATE_SHOWING = 1;
    private static final int STATE_SHOWN = 2;
    private static final int STATE_HIDING = 3;

    private Paint mIndexbarPaint;
    private Paint mPreviewPaint;
    private Paint mPreviewTextPaint;
    private Paint mIndexPaint;

    public ExpandableIndexScroller(Context context, IndexableExpandableListView lv, SectionIndexer indexer) {
        mDensity = context.getResources().getDisplayMetrics().density;
        mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        mListView = lv;

        setIndexer(indexer);
        mIndexbarWidth = 20 * mDensity;
        mIndexbarMargin = 10 * mDensity;
        mPreviewPadding = 5 * mDensity;

        initPaints();
    }

    private void initPaints(){

        // mAlphaRate determines the rate of opacity
        mIndexbarPaint = new Paint();
        mIndexbarPaint.setColor(Color.BLACK);
        mIndexbarPaint.setAlpha((int) (64 * mAlphaRate));
        mIndexbarPaint.setAntiAlias(true);


        mPreviewPaint = new Paint();
        mPreviewPaint.setColor(Color.BLACK);
        mPreviewPaint.setAlpha(96);
        mPreviewPaint.setAntiAlias(true);
        mPreviewPaint.setShadowLayer(3, 0, 0, Color.argb(64, 0, 0, 0));


        mPreviewTextPaint = new Paint();
        mPreviewTextPaint.setColor(Color.WHITE);
        mPreviewTextPaint.setAntiAlias(true);
        mPreviewTextPaint.setTextSize(50 * mScaledDensity);


        mIndexPaint = new Paint();
        mIndexPaint.setColor(Color.WHITE);
        mIndexPaint.setAlpha((int) (255 * mAlphaRate));
        mIndexPaint.setAntiAlias(true);
        mIndexPaint.setTextSize(12 * mScaledDensity);
    }

    public void draw(Canvas canvas) {
        if (mState == STATE_HIDDEN)
            return;

        canvas.drawRoundRect(mIndexbarRect, 5 * mDensity, 5 * mDensity, mIndexbarPaint);

        if (mSections != null && mSections.length > 0) {
            // Preview is shown when mCurrentSection is set
            if (mCurrentSection >= 0) {


                float previewTextWidth = mPreviewTextPaint.measureText(mSections[mCurrentSection]);
                float previewHeight = 2 * mPreviewPadding + mPreviewTextPaint.descent() - mPreviewTextPaint.ascent();
                float previewWidth = previewTextWidth;
                RectF previewRect = new RectF((mListViewWidth - previewWidth) / 2
                        , (mListViewHeight - previewHeight) / 2
                        , (mListViewWidth - previewWidth) / 2 + previewWidth
                        , (mListViewHeight - previewHeight) / 2 + previewHeight);

                canvas.drawRoundRect(previewRect, 5 * mDensity, 5 * mDensity, mPreviewPaint);
                canvas.drawText(mSections[mCurrentSection], previewRect.left - 1
                        , previewRect.top + mPreviewPadding - mPreviewTextPaint.ascent() + 1, mPreviewTextPaint);
            }

            float sectionHeight = (mIndexbarRect.height() - 2 * mIndexbarMargin) / mSections.length;
            float paddingTop = (sectionHeight - (mIndexPaint.descent() - mIndexPaint.ascent())) / 2;
            for (int i = 0; i < mSections.length; i++) {
                String sectionBarString = mSections[i].substring(mSections[i].length()-2,mSections[i].length());
                float paddingLeft = (mIndexbarWidth - mIndexPaint.measureText(sectionBarString)) / 2;
                canvas.drawText(sectionBarString, mIndexbarRect.left + paddingLeft
                        , mIndexbarRect.top + mIndexbarMargin + sectionHeight * i + paddingTop - mIndexPaint.ascent(), mIndexPaint);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // If down event occurs inside index bar region, start indexing
                if (mState != STATE_HIDDEN && contains(ev.getX(), ev.getY())) {
                    setState(STATE_SHOWN);

                    // It demonstrates that the motion event started from index bar
                    mIsIndexing = true;
                    // Determine which section the point is in, and move the list to that section
                    mCurrentSection = getSectionByPoint(ev.getY());
                    mListView.setSelection(mIndexer.getPositionForSection(mCurrentSection));
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsIndexing) {
                    // If this event moves inside index bar
                    if (contains(ev.getX(), ev.getY())) {
                        // Determine which section the point is in, and move the list to that section
                        mCurrentSection = getSectionByPoint(ev.getY());
                        mListView.setSelection(mIndexer.getPositionForSection(mCurrentSection));
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsIndexing) {
                    mIsIndexing = false;
                    mCurrentSection = -1;
                }
                if (mState == STATE_SHOWN)
                    setState(STATE_HIDING);
                break;
        }
        return false;
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        mListViewWidth = w;
        mListViewHeight = h;
        mIndexbarRect = new RectF(w - mIndexbarMargin - mIndexbarWidth
                , mIndexbarMargin
                , w - mIndexbarMargin
                , h - mIndexbarMargin);
    }

    public void show() {
        if (mState == STATE_HIDDEN)
            setState(STATE_SHOWING);
        else if (mState == STATE_HIDING)
            setState(STATE_HIDING);
    }

    public void hide() {
        if (mState == STATE_SHOWN)
            setState(STATE_HIDING);
    }

    public void setIndexer(SectionIndexer adapter) {
        mIndexer = adapter;
        mSections = (String[]) mIndexer.getSections();
    }

    public void setAdapter(ListAdapter adapter) {
        if (adapter instanceof SectionIndexer) {
            mIndexer = (SectionIndexer) adapter;
            mSections = (String[]) mIndexer.getSections();
        }
    }

    public void setAdapter(ExpandableListAdapter adapter) {
        if (adapter instanceof SectionIndexer) {
            mIndexer = (SectionIndexer) adapter;
            mSections = (String[]) mIndexer.getSections();
        }
    }


    private void setState(int state) {
        if (state < STATE_HIDDEN || state > STATE_HIDING)
            return;

        mState = state;
        switch (mState) {
            case STATE_HIDDEN:
                // Cancel any fade effect
                mHandler.removeMessages(0);
                break;
            case STATE_SHOWING:
                // Start to fade in
                mAlphaRate = 0;
                fade(0);
                break;
            case STATE_SHOWN:
                // Cancel any fade effect
                mHandler.removeMessages(0);
                break;
            case STATE_HIDING:
                // Start to fade out after three seconds
                mAlphaRate = 1;
                fade(3000);
                break;
        }
    }

    private boolean contains(float x, float y) {
        // Determine if the point is in index bar region, which includes the right margin of the bar
        return (x >= mIndexbarRect.left && y >= mIndexbarRect.top && y <= mIndexbarRect.top + mIndexbarRect.height());
    }

    private int getSectionByPoint(float y) {
        if (mSections == null || mSections.length == 0)
            return 0;
        if (y < mIndexbarRect.top + mIndexbarMargin)
            return 0;
        if (y >= mIndexbarRect.top + mIndexbarRect.height() - mIndexbarMargin)
            return mSections.length - 1;
        return (int) ((y - mIndexbarRect.top - mIndexbarMargin) / ((mIndexbarRect.height() - 2 * mIndexbarMargin) / mSections.length));
    }

    private void fade(long delay) {
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageAtTime(0, SystemClock.uptimeMillis() + delay);
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (mState) {
                case STATE_SHOWING:
                    // Fade in effect
                    mAlphaRate += (1 - mAlphaRate) * 0.2;
                    if (mAlphaRate > 0.9) {
                        mAlphaRate = 1;
                        setState(STATE_SHOWN);
                    }

                    mListView.invalidate();
                    fade(10);
                    break;
                case STATE_SHOWN:
                    // If no action, hide automatically
                    setState(STATE_HIDING);
                    break;
                case STATE_HIDING:
                    // Fade out effect
                    mAlphaRate -= mAlphaRate * 0.2;
                    if (mAlphaRate < 0.1) {
                        mAlphaRate = 0;
                        setState(STATE_HIDDEN);
                    }

                    mListView.invalidate();
                    fade(10);
                    break;
            }
        }

    };
}
