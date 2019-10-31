package com.android.hcbd.blesoft.ui.activity;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.android.hcbd.blesoft.R;
import com.android.hcbd.blesoft.adapter.InstructAdapter;
import com.android.hcbd.blesoft.base.BaseActivity;
import com.android.hcbd.blesoft.entity.Instructions;
import com.android.hcbd.blesoft.event.MessageEvent;
import com.android.hcbd.blesoft.weight.LinearLayoutForListView;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class InstructActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.lvLayout)
    LinearLayoutForListView lvLayout;
    private InstructAdapter adapter;
    private List<Instructions> list = new ArrayList<>();

    @Override
    protected int getLayout() {
        return R.layout.activity_instruct;
    }

    @Override
    protected void initEventAndData() {
        setToolBar(toolbar,"批量发送指令");

        setData();

        adapter = new InstructAdapter(this,list);
        lvLayout.setAdapter(adapter);
    }

    private void setData() {
        list.add(new Instructions("配置IP1","SET IP1="));
        list.add(new Instructions("配置IP2","SET IP2="));
        list.add(new Instructions("配置IP3","SET IP3="));
        list.add(new Instructions("配置IP4","SET IP4="));
        list.add(new Instructions("读取IP1","SET READ IP1="));
        list.add(new Instructions("读取IP2","SET READ IP2="));
        list.add(new Instructions("读取IP3","SET READ IP3="));
        list.add(new Instructions("读取IP4","SET READ IP4="));
        list.add(new Instructions("禁用IP1","SET IP1=FALSE"));
        list.add(new Instructions("禁用IP2","SET IP2=FALSE"));
        list.add(new Instructions("禁用IP3","SET IP3=FALSE"));
        list.add(new Instructions("配置SN号","SET SN="));
        list.add(new Instructions("读取SN号","SET READ SN="));
        list.add(new Instructions("配置蓝牙名称","SET BLE_NAME="));
        list.add(new Instructions("测试看门狗","ST+TEST_WATCHDOG"));
        list.add(new Instructions("格式化第1扇区","ST+SectorErase25P32"));
        list.add(new Instructions("写入第1扇区","ST+WRITE25P32"));
        list.add(new Instructions("读第1扇区","ST+READ25P32"));
        list.add(new Instructions("查询软件版本号","ST+VER"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_instruct, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_send:
                List<String> sends = new ArrayList<>();
                List<Boolean> list01 = adapter.getIsSelected();
                for (int i=0;i<list01.size();i++){
                    if (list01.get(i)){
                        if (adapter.getIsShow()[i]){
                            sends.add(list.get(i).getContent()+adapter.getInputList().get(i));
                        } else {
                            sends.add(list.get(i).getContent());
                        }
                    }
                }

                MessageEvent messageEvent = new MessageEvent(MessageEvent.EVENT_SEND);
                messageEvent.setObj(sends);
                EventBus.getDefault().post(messageEvent);
                finishActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
