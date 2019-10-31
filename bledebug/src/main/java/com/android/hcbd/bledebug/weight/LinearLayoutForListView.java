package com.android.hcbd.bledebug.weight;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

/**
 * Created by gc on 2019/1/21.
 */

public class LinearLayoutForListView extends LinearLayout {

    private BaseAdapter adapter;
    private OnItemClickListener onItemClickListener;

    public LinearLayoutForListView(Context context) {
        super(context);
    }

    public LinearLayoutForListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayoutForListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAdapter(BaseAdapter adapter){
        this.adapter = adapter;
        bindLinearLayout();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    private void bindLinearLayout() {
        if (adapter == null){
            return;
        }
        int count = adapter.getCount();
        for(int i=0;i<count;i++){
            View view = adapter.getView(i,null,null);
            final int tmp = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null){
                        onItemClickListener.onItemClicked(view,tmp);
                    }
                }
            });
            addView(view,i);

        }
    }

    public void removeAllView(){
        removeAllViews();
    }

    public interface OnItemClickListener{
        void onItemClicked(View v, int position);
    }

}
