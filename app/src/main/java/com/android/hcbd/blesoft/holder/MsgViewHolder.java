package com.android.hcbd.blesoft.holder;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.hcbd.blesoft.MyApplication;
import com.android.hcbd.blesoft.R;
import com.android.hcbd.blesoft.entity.Msg;
import com.android.hcbd.blesoft.event.MessageEvent;
import com.android.hcbd.blesoft.utils.ToastUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import org.greenrobot.eventbus.EventBus;

public class MsgViewHolder extends BaseViewHolder<Msg> {

    LinearLayout leftLayout;
    TextView leftMsg;
    LinearLayout rightLayout;
    TextView rightMsg;
    LinearLayout background;

    public MsgViewHolder(ViewGroup parent) {
        super(parent, R.layout.msg_item);
        leftLayout = $(R.id.left_layout);
        leftMsg = $(R.id.left_msg);
        rightLayout = $(R.id.right_layout);
        rightMsg = $(R.id.right_msg);
        background = $(R.id.background);
    }

    @Override
    public void setData(final Msg data) {
        super.setData(data);
        if (data.getType() == Msg.TYPE_RECEIVED) {
            // 如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
            leftLayout.setVisibility(View.VISIBLE);
            rightLayout.setVisibility(View.GONE);
           leftMsg.setText(data.getContent());
        } else if (data.getType() == Msg.TYPE_SENT) {
            // 如果是发出的消息，则显示右边的消息布局，将左边的消息布局隐藏
            rightLayout.setVisibility(View.VISIBLE);
            leftLayout.setVisibility(View.GONE);
            rightMsg.setText(data.getContent());
        }

        XPopup.get(getContext()).watch(background);
        background.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (data.getType() == Msg.TYPE_RECEIVED) {
                    showPopupWindows(new String[]{"复制", "删除"},data);
                } else if (data.getType() == Msg.TYPE_SENT) {
                    showPopupWindows(new String[]{"重发", "复制", "删除"},data);
                }
                return true;
            }
        });

    }

    private void showPopupWindows(String[] str , final Msg msg){
        XPopup.get(getContext()).hasShadowBg(false).asAttachList(str,null,
                new OnSelectListener() {
                    @Override
                    public void onSelect(int position, String text) {
                        ToastUtils.showShortToast(MyApplication.getInstance(),"click "+text);
                        switch (text){
                            case "重发":
                                MessageEvent messageEvent = new MessageEvent(MessageEvent.EVENT_MSG_ADD);
                                messageEvent.setObj(msg);
                                EventBus.getDefault().post(messageEvent);
                                break;
                            case "复制":
                                //获取剪贴板管理器：
                                ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                // 创建普通字符型ClipData
                                ClipData mClipData = ClipData.newPlainText("Label", msg.getContent().trim());
                                // 将ClipData内容放到系统剪贴板里。
                                cm.setPrimaryClip(mClipData);
                                break;
                            case "删除":
                                MessageEvent event = new MessageEvent(MessageEvent.EVENT_MSG_DEL);
                                event.setObj(getAdapterPosition());
                                EventBus.getDefault().post(event);
                                break;
                        }

                    }
                })
                .show();
    }

}
