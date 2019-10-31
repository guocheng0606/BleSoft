package com.android.hcbd.blesoft.holder;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.hcbd.blesoft.R;
import com.android.hcbd.blesoft.db.CustomDbTool;
import com.android.hcbd.blesoft.entity.Custom;
import com.android.hcbd.blesoft.event.MessageEvent;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import org.greenrobot.eventbus.EventBus;


public class CustomAddViewHolder extends BaseViewHolder<Custom> {

    TextView tv;
    RelativeLayout del;

    public CustomAddViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_custom_layout);
        tv = $(R.id.tv);
        del = $(R.id.del);
    }

    @Override
    public void setData(final Custom data) {
        super.setData(data);
        tv.setText(data.getName());
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("提示")
                        .setMessage("你确定删除吗？")
                        .setNegativeButton("取消",null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CustomDbTool.deleteById(data.getId());
                                MessageEvent messageEvent = new MessageEvent(MessageEvent.EVENT_CUSTOM_DELETE);
                                messageEvent.setObj(data);
                                EventBus.getDefault().post(messageEvent);
                            }
                        }).create().show();
            }
        });
    }
}
