package com.android.hcbd.bledebug.weight;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by guocheng on 2017/4/13.
 */
public class CustomSpinner extends AppCompatSpinner {
    private static final String TAG = "CustomSpinner";
    private OnSpinnerEventsListener mListener;
    private boolean mOpenInitiated = false;
    private ArrayAdapter spinnerArrayAdapter;

    public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSpinner(Context context, int mode) {
        super(context, mode);
    }

    public CustomSpinner(Context context) {
        super(context);
    }

    public interface OnSpinnerEventsListener {

        void onSpinnerOpened();

        void onSpinnerClosed();

    }

    @Override
    public boolean performClick() {
        // register that the Spinner was opened so we have a status
        // indicator for the activity(which may lose focus for some other
        // reasons)
        mOpenInitiated = true;
        if (mListener != null) {
            mListener.onSpinnerOpened();
        }
        return super.performClick();
    }

    public void setSpinnerEventsListener(OnSpinnerEventsListener onSpinnerEventsListener) {
        mListener = onSpinnerEventsListener;
    }

    /**
     * Propagate the closed Spinner event to the listener from outside.
     */
    public void performClosedEvent() {
        mOpenInitiated = false;
        if (mListener != null) {
            mListener.onSpinnerClosed();
        }
    }

    /**
     * A boolean flag indicating that the Spinner triggered an open event.
     *
     * @return true for opened Spinner
     */
    public boolean hasBeenOpened() {
        return mOpenInitiated;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        android.util.Log.d(TAG, "onWindowFocusChanged");
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasBeenOpened() && hasWindowFocus) {
            android.util.Log.i(TAG, "closing popup");
            performClosedEvent();
        }
    }

    public void initializeStringValues(Object[] values) {
        spinnerArrayAdapter = new ArrayAdapter<>(this.getContext
                (), android.R.layout.simple_spinner_dropdown_item);
        spinnerArrayAdapter.addAll(values);
        setAdapter(spinnerArrayAdapter);
    }

    public void initializeStringValues(Object[] values, Object promptText) {
        spinnerArrayAdapter = new ArrayAdapter<String>(this.getContext
                (), android.R.layout.simple_spinner_dropdown_item){
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
                return view;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
                return view;
            }
        };
        spinnerArrayAdapter.add(promptText);
        spinnerArrayAdapter.addAll(values);
        setAdapter(spinnerArrayAdapter);
    }

    public @Nullable
    Object getItem(int position) {
        if(spinnerArrayAdapter != null && position < spinnerArrayAdapter.getCount()) {
            return spinnerArrayAdapter.getItem(position);
        } else {
            return null;
        }
    }
}
