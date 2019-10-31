package com.android.hcbd.bledebug.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.android.hcbd.bledebug.R;
import com.android.hcbd.bledebug.entity.Msg;
import com.android.hcbd.bledebug.event.MessageEvent;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class BleMsgAdapter extends BaseQuickAdapter<Msg, BaseViewHolder> {

    public BleMsgAdapter(int layoutResId, @Nullable List<Msg> data) {
        super(layoutResId, data);
    }

    public BleMsgAdapter(@Nullable List<Msg> data) {
        super(data);
    }

    public BleMsgAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull final BaseViewHolder helper, final Msg item) {
        if (item.getType() == Msg.TYPE_RECEIVED) {
            // 如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
            helper.setVisible(R.id.left_layout,true);
            helper.setGone(R.id.right_layout,false);
            helper.setText(R.id.left_msg,item.getContent());
        } else if (item.getType() == Msg.TYPE_SENT) {
            // 如果是发出的消息，则显示右边的消息布局，将左边的消息布局隐藏
            helper.setGone(R.id.left_layout,false);
            helper.setVisible(R.id.right_layout,true);
            helper.setText(R.id.right_msg,item.getContent());
        }

        LinearLayout background = helper.getView(R.id.background);
        XPopup.get(mContext).watch(background);
        background.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (item.getType() == Msg.TYPE_RECEIVED) {
                    showPopupWindows(new String[]{"复制", "删除"},item,helper.getAdapterPosition());
                } else if (item.getType() == Msg.TYPE_SENT) {
                    showPopupWindows(new String[]{"重发", "复制", "删除"},item,helper.getAdapterPosition());
                }
                return true;
            }
        });

    }

    private void showPopupWindows(String[] str , final Msg msg, final int index){
        XPopup.get(mContext).hasShadowBg(false).asAttachList(str,null,
                new OnSelectListener() {
                    @Override
                    public void onSelect(int position, String text) {
                        switch (text){
                            case "重发":
                                MessageEvent messageEvent = new MessageEvent(MessageEvent.EVENT_MSG_ADD);
                                messageEvent.setObj(msg);
                                EventBus.getDefault().post(messageEvent);
                                break;
                            case "复制":
                                //获取剪贴板管理器：
                                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                // 创建普通字符型ClipData
                                ClipData mClipData = ClipData.newPlainText("Label", msg.getContent().trim());
                                // 将ClipData内容放到系统剪贴板里。
                                cm.setPrimaryClip(mClipData);
                                break;
                            case "删除":
                                MessageEvent event = new MessageEvent(MessageEvent.EVENT_MSG_DEL);
                                event.setObj(index);
                                EventBus.getDefault().post(event);
                                break;
                        }

                    }
                })
                .show();
    }

}
