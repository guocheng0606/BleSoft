package com.android.hcbd.bledebug.adapter;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.android.hcbd.bledebug.R;
import com.android.hcbd.bledebug.db.CustomDbTool;
import com.android.hcbd.bledebug.entity.Custom;
import com.android.hcbd.bledebug.event.MessageEvent;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class CustomOrderAdapter extends BaseQuickAdapter<Custom, BaseViewHolder> {

    public CustomOrderAdapter(int layoutResId, @Nullable List<Custom> data) {
        super(layoutResId, data);
    }

    public CustomOrderAdapter(@Nullable List<Custom> data) {
        super(data);
    }

    public CustomOrderAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull final BaseViewHolder helper, final Custom item) {
        helper.setText(R.id.tv,item.getName());
        helper.getView(R.id.del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("你确定删除吗？")
                        .setNegativeButton("取消",null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CustomDbTool.deleteById(item.getId());
                                MessageEvent messageEvent = new MessageEvent(MessageEvent.EVENT_CUSTOM_DELETE);
                                messageEvent.setObj(helper.getAdapterPosition());
                                EventBus.getDefault().post(messageEvent);
                            }
                        }).create().show();
            }
        });
    }
}
