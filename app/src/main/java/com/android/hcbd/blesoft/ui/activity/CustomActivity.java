package com.android.hcbd.blesoft.ui.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.android.hcbd.blesoft.R;
import com.android.hcbd.blesoft.base.BaseActivity;
import com.android.hcbd.blesoft.db.CustomDbTool;
import com.android.hcbd.blesoft.entity.Custom;
import com.android.hcbd.blesoft.event.MessageEvent;
import com.android.hcbd.blesoft.holder.CustomAddViewHolder;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CustomActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;
    private RecyclerArrayAdapter<Custom> adapter;

    private List<Custom> list = new ArrayList<>();
    @Override
    protected int getLayout() {
        return R.layout.activity_custom;
    }

    @Override
    protected void initEventAndData() {
        setToolBar(toolbar, "自定义");
        initView();

        initData();
        initListener();
    }

    private void initData() {
        adapter.clear();
        list = CustomDbTool.queryAll();
        if (list.size() > 0) {
            adapter.addAll(list);
        } else {
            adapter.clear();
        }
    }

    private void initListener() {
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Custom custom = adapter.getItem(position);
                Intent intent = new Intent(CustomActivity.this, CustomAddActivity.class);
                intent.putExtra("custom",custom);
                startActivity(intent);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event){
        switch (event.getEventId()) {
            case MessageEvent.EVENT_CUSTOM_LIST:
                initData();
                break;
            case MessageEvent.EVENT_CUSTOM_DELETE:
                Custom custom = (Custom) event.getObj();
                adapter.remove(custom);
                break;
        }
    }

    private void initView() {
        recyclerView.setEmptyView(R.layout.view_empty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerDecoration itemDecoration = new DividerDecoration(0xFFEDEDED, 1, 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapterWithProgress(adapter = new RecyclerArrayAdapter<Custom>(this) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new CustomAddViewHolder(parent);
            }
        });
        adapter.setError(R.layout.view_error, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {
                adapter.resumeMore();
            }

            @Override
            public void onErrorClick() {
                adapter.resumeMore();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_custom, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                startActivity(new Intent(CustomActivity.this, CustomAddActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
