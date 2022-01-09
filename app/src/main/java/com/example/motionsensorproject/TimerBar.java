package com.example.motionsensorproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

public class TimerBar extends View {
    private static final int MAX_TIME = (int) SensorFragment.MAX_TIME;
    private static final int MIN_TIME = (int) SensorFragment.MIN_TIME;
    private static final int INTERVALS = (int) SensorFragment.NUM_INTERVALS;
    private static final int INTERVAL_SIZE = (int) SensorFragment.INTERVAL;
    private static final String TAG = "TimerBar";

    private long currentTime;
    private State state;
    public static enum State
    {
        NONE,
        TIMED
    }
    public TimerBar(Context context, AttributeSet set) {
        super(context, set);
        state = State.NONE;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.gray));
        int height = getHeight();
        canvas.drawRect(0, 0, getWidth(), height, paint);
        paint.setColor(getResources().getColor(R.color.dark_gray));
        double ratio = getWidth() * 1.0 / MAX_TIME;
//        Log.d(TAG, ratio + " " + getWidth() + ", " + MAX_TIME + " ");
        float widthOfRect = getWidth() / 40;
        float startOfRect = (float) (ratio * MIN_TIME);
        canvas.drawRect(startOfRect, 0, widthOfRect + startOfRect, height, paint);
//        Log.d(TAG, startOfRect + ", " + widthOfRect);
        float change = (float) (ratio * INTERVAL_SIZE);
        for (int i = 0; i < INTERVALS - 1; i++)
        {
            startOfRect += change;
            canvas.drawRect(startOfRect, 0, widthOfRect + startOfRect, height, paint);
        }

        if (state.equals(State.NONE))
        {
            return;
        }
        paint.setColor(getResources().getColor(getPaintID()));
        canvas.drawRect(0, 0, (float) (currentTime * ratio), height, paint);
        Log.d(TAG, paint.getColor() + "");

    }

    public void changeState(State newState)
    {
        this.state = newState;
        this.currentTime = 0;
        invalidate();
    }

    public void updateTime(long time)
    {
        this.currentTime = time;
        Log.d(TAG, "called");
        invalidate();
    }

    private int getPaintID()
    {
        Log.d(TAG, currentTime + " ");
        if (currentTime < MIN_TIME && currentTime > 0)
        {
            Log.d(TAG, "returning red");
            return SensorFragment.getColor(0).getId();
        }
        return SensorFragment.getColor((int) ((currentTime - MIN_TIME) / INTERVAL_SIZE + 1)).getId();
    }
}
