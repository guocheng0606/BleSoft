package com.android.hcbd.bledebug.adapter;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.android.hcbd.bledebug.R;
import com.android.hcbd.bledebug.entity.Instructions;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class CustomAddAdapter extends BaseItemDraggableAdapter<Instructions, BaseViewHolder> {


    public CustomAddAdapter(List<Instructions> data) {
        super(data);
    }

    public CustomAddAdapter(int layoutResId, List<Instructions> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull final BaseViewHolder helper, Instructions item) {
        helper.setText(R.id.tv, item.getName());
        helper.getView(R.id.del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("您确定删除吗？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                remove(helper.getAdapterPosition());
                            }
                        }).create().show();
            }
        });
    }


}
