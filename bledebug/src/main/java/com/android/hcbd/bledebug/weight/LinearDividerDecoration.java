package com.android.hcbd.bledebug.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.hcbd.bledebug.R;


public class LinearDividerDecoration extends RecyclerView.ItemDecoration {

    private int mDividerHeight;

    private Paint mPaint;

    private boolean mDrawBottom;
    private boolean mRemoveEnd;


    public LinearDividerDecoration(Context context) {
        mPaint = new Paint();
        mPaint.setColor(context.getResources().getColor(R.color.item_line));
        mDividerHeight = context.getResources().getDimensionPixelSize(R.dimen.divider_width);
        mDrawBottom = true;
    }

    public LinearDividerDecoration(Context context, boolean removeEnd) {
        mPaint = new Paint();
        mPaint.setColor(context.getResources().getColor(R.color.item_line));
        mDividerHeight = context.getResources().getDimensionPixelSize(R.dimen.divider_width);
        mDrawBottom = true;
        mRemoveEnd = removeEnd;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (mDrawBottom)
            outRect.bottom = mDividerHeight;
        else
            outRect.top = mDividerHeight;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        if (mRemoveEnd && childCount != 0) {
            --childCount;
        }

        if (mDrawBottom) {
            for (int i = 0; i < childCount; ++i) {
                View view = parent.getChildAt(i);

                float top = view.getBottom();
                float bottom = view.getBottom() + mDividerHeight;

                c.drawRect(left, top, right, bottom, mPaint);

            }
        } else {
            for (int i = 0; i < childCount; ++i) {
                View view = parent.getChildAt(i);

                float top = view.getTop() - mDividerHeight;
                float bottom = view.getTop();

                c.drawRect(left, top, right, bottom, mPaint);

            }
        }
    }
}
