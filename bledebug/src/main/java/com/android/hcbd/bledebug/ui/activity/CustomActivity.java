package com.android.hcbd.bledebug.ui.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.hcbd.bledebug.R;
import com.android.hcbd.bledebug.adapter.CustomOrderAdapter;
import com.android.hcbd.bledebug.base.BaseActivity;
import com.android.hcbd.bledebug.db.CustomDbTool;
import com.android.hcbd.bledebug.entity.Custom;
import com.android.hcbd.bledebug.event.MessageEvent;
import com.android.hcbd.bledebug.weight.LinearDividerDecoration;
import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;

public class CustomActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecycler;

    private CustomOrderAdapter mAdapter;
    private int curPage = 1;
    private int pageSize = 20;

    @Override
    protected int getLayout() {
        return R.layout.activity_custom;
    }

    @Override
    protected void initEventAndData() {
        setToolBar(toolbar, "自定义");

        initAdapter();
        mAdapter.setEmptyView(R.layout.view_progress,(ViewGroup) mRecycler.getParent());
        initData();
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Custom> list = CustomDbTool.query(curPage,pageSize);
                final int size = list == null ? 0 : list.size();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (curPage == 1) {
                            mAdapter.setNewData(list);
                            if (size == 0) {
                                mAdapter.setEmptyView(R.layout.view_empty,(ViewGroup) mRecycler.getParent());
                            }
                        } else {
                            mAdapter.addData(list);
                        }
                        if (size < pageSize) {
                            if (curPage == 1)
                                mAdapter.loadMoreEnd(true);
                            else
                                mAdapter.loadMoreEnd(false);
                        } else {
                            mAdapter.loadMoreComplete();
                        }
                    }
                });

            }
        }).start();

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event){
        switch (event.getEventId()) {
            case MessageEvent.EVENT_CUSTOM_LIST:
                curPage = 1;
                initData();
                break;
            case MessageEvent.EVENT_CUSTOM_DELETE:
                int pos = (int) event.getObj();
                mAdapter.remove(pos);
                if (mAdapter.getData().size() == 0){
                    mAdapter.setEmptyView(R.layout.view_empty,(ViewGroup) mRecycler.getParent());
                }
                break;
        }
    }

    private void initAdapter() {
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.addItemDecoration(new LinearDividerDecoration(this));
        mAdapter = new CustomOrderAdapter(R.layout.item_custom_layout);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        },mRecycler);
        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Custom custom = mAdapter.getItem(position);
                Intent intent = new Intent(CustomActivity.this, CustomAddActivity.class);
                intent.putExtra("custom", custom);
                startActivity(intent);
            }
        });
    }

    private void loadMore() {
        curPage++;
        initData();
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
