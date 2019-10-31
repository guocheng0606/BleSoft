package com.android.hcbd.bledebug.ui.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.android.hcbd.bledebug.R;
import com.android.hcbd.bledebug.adapter.CustomAddAdapter;
import com.android.hcbd.bledebug.base.BaseActivity;
import com.android.hcbd.bledebug.db.CustomDbTool;
import com.android.hcbd.bledebug.entity.Custom;
import com.android.hcbd.bledebug.entity.Instructions;
import com.android.hcbd.bledebug.event.MessageEvent;
import com.android.hcbd.bledebug.utils.ToastUtils;
import com.android.hcbd.bledebug.weight.CustomSpinner;
import com.android.hcbd.bledebug.weight.LinearDividerDecoration;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;

public class CustomAddActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv)
    RecyclerView mRecycler;
    @BindView(R.id.btn_add1)
    Button btn_add1;
    @BindView(R.id.btn_add2)
    Button btn_add2;
    @BindView(R.id.btn_add3)
    Button btn_add3;

    private CustomAddAdapter mAdapter;

    //private List<Instructions> list = new ArrayList<>();

    private Custom custom;

    @Override
    protected int getLayout() {
        return R.layout.activity_custom_add;
    }

    @Override
    protected void initEventAndData() {
        setToolBar(toolbar, "添加");
        custom = getIntent().getParcelableExtra("custom");

        toolbar.setTitle(custom == null ? "添加" : "编辑");
        initAdapter();

        btn_add1.setOnClickListener(this);
        btn_add2.setOnClickListener(this);
        btn_add3.setOnClickListener(this);
    }


    private void initAdapter() {
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.addItemDecoration(new LinearDividerDecoration(this));
        if (custom != null) {
            mAdapter = new CustomAddAdapter(R.layout.item_custom_layout,custom.getOrders());
        } else {
            mAdapter = new CustomAddAdapter(R.layout.item_custom_layout,new ArrayList<Instructions>());
        }

        ItemDragAndSwipeCallback mItemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(mItemDragAndSwipeCallback);
        mItemTouchHelper.attachToRecyclerView(mRecycler);
        mAdapter.disableSwipeItem();
        mAdapter.enableDragItem(mItemTouchHelper);
        mAdapter.setOnItemDragListener(listener);
        mRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
    }

    OnItemDragListener listener = new OnItemDragListener() {
        @Override
        public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
            Log.d(TAG, "drag start");
        }

        @Override
        public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
            Log.d(TAG, "move from: " + from + " to: " + to);
        }

        @Override
        public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
            Log.d(TAG, "drag end");
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add1:
                showAdd1Dailog();
                break;
            case R.id.btn_add2:
                showAdd2Dailog();
                break;
            case R.id.btn_add3:
                showAdd3Dailog();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (custom == null) {
                    if (mAdapter.getData().size() > 0)
                        showNameDialog();
                    else
                        ToastUtils.showShortToast(mContext, "请先添加指令！");

                } else {
                    custom.setOrders(mAdapter.getData());
                    CustomDbTool.update(custom);
                    MessageEvent messageEvent = new MessageEvent(MessageEvent.EVENT_CUSTOM_LIST);
                    EventBus.getDefault().post(messageEvent);
                    finishActivity();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNameDialog() {
        final View view = LayoutInflater.from(CustomAddActivity.this).inflate(R.layout.dialog_edittext, null);//这里必须是final的
        final EditText edit = (EditText) view.findViewById(R.id.editText);//获得输入框对象
        new AlertDialog.Builder(CustomAddActivity.this)
                .setTitle("请输入保存名称")//提示框标题
                .setView(view)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Custom custom = new Custom();
                        custom.setName(edit.getText().toString());
                        custom.setOrders(mAdapter.getData());
                        CustomDbTool.add(custom);
                        MessageEvent messageEvent = new MessageEvent(MessageEvent.EVENT_CUSTOM_LIST);
                        EventBus.getDefault().post(messageEvent);
                        finishActivity();
                    }
                }).create().show();
    }


    private String[] names = {"配置IP1", "配置IP2", "配置IP3", "配置IP4", "读取IP1", "读取IP2", "读取IP3", "读取IP4",
            "禁用IP1", "禁用IP2", "禁用IP3", "配置SN号", "读取SN号", "配置蓝牙名称", "测试看门狗", "格式化第1扇区", "写入第1扇区",
            "读第1扇区", "查询软件版本号"};
    private String[] orders = {"SET IP1=", "SET IP2=", "SET IP3=", "SET IP4=", "SET READ IP1=", "SET READ IP2=",
            "SET READ IP3=", "SET READ IP4=", "SET IP1=FALSE", "SET IP2=FALSE", "SET IP3=FALSE", "SET SN=", "SET READ SN=",
            "SET BLE_NAME=", "ST+TEST_WATCHDOG", "ST+SectorErase25P32", "ST+WRITE25P32", "ST+READ25P32", "ST+VER"};

    private void showAdd1Dailog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setContentView(R.layout.dialog_add1_layout);

        final CustomSpinner spinner = window.findViewById(R.id.spinner);
        final EditText et = window.findViewById(R.id.et);
        Button btnCancel = window.findViewById(R.id.btnCancel);
        Button btnOk = window.findViewById(R.id.btnOk);
        spinner.initializeStringValues(names, "请选择");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 12:
                    case 14:
                        et.setVisibility(View.VISIBLE);
                        break;
                    default:
                        et.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinner.getSelectedItemPosition() == 0) {
                    dialog.dismiss();
                    return;
                }
                String str = orders[spinner.getSelectedItemPosition() - 1] + et.getText().toString().trim();
                mAdapter.addData(new Instructions((String) spinner.getSelectedItem() + "：" + et.getText().toString().trim(), str));
                hideSoftInput();
                dialog.dismiss();
            }
        });
    }

    private String[] types = {"UNLOG", "LOG"};
    private String[] ports = {"COM1", "COM2","COM3"};
    private String[] paras1 = {"UNLOGALL", "BD2EPHEMA", "BD2IONUTCA", "BESTPOSA", "BESTCVELA", "GLOEPHEMERISA"
            , "GPSEPHEMA", "GPGGA", "GPGSA", "GPGST", "GPGSV", "GPHDT", "GPRMC GPS", "GPVTG", "GPZDA", "HEADINGA"
            , "LOGLIST", "MATCHEDPOS", "MATCHEDPOSH", "PSRDOP", "PSRVEL", "RANGE", "RAWEPHEM", "REFSTATION", "SATVIS"
            , "TIME", "VERSION"};
    private String[] paras2 = {"BD2EPHEMA", "BD2IONUTCA", "BESTPOSA", "BESTCVELA", "GLOEPHEMERISA", "GPSEPHEMA"
            , "GPGGA", "GPGSA", "GPGST", "GPGSV", "GPHDT", "GPRMC GPS", "GPVTG", "GPZDA", "HEADINGA", "IONUTCA ONCHANGED"
            , "LOGLIST", "PSRDOP", "PSRVEL", "RANGE", "RANGEH", "RAWEPHEM", "REFSTATION", "RANGECMPA", "RTKDATAA ONCHANGED"
            , "RTKDATAHA ONCHANGED", "SATVIS", "TIME", "VERSION", "RTCM1", "RTCM3", "RTCM18", "RTCM19", "RTCM24", "RTCM31", "RTCM32"
            , "RTCM63", "RTCM1001", "RTCM1002", "RTCM1003", "RTCM1004", "RTCM1005", "RTCM1006", "RTCM1009"
            , "RTCM1010", "RTCM1011", "RTCM1012", "RTCM1019", "RTCM1020", "RTCM1033", "RTCM1074", "RTCM1075", "RTCM1084"
            , "RTCM1085", "RTCM1104", "RTCM1105", "RTCM1124", "RTCM1125"};
    private String[] data1 = {"FALSE", "TRUE"};
    private String[] data2 = {"ONTIME", "空"};

    private void showAdd2Dailog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setContentView(R.layout.dialog_add2_layout);

        final CustomSpinner spinner01 = window.findViewById(R.id.spinner01);
        final CustomSpinner spinner02 = window.findViewById(R.id.spinner02);
        final CustomSpinner spinner03 = window.findViewById(R.id.spinner03);
        final CustomSpinner spinner04 = window.findViewById(R.id.spinner04);
        final EditText et = window.findViewById(R.id.et);
        Button btnCancel = window.findViewById(R.id.btnCancel);
        Button btnOk = window.findViewById(R.id.btnOk);

        spinner01.initializeStringValues(types, "请选择");
        spinner02.initializeStringValues(ports, "请选择串口号");
        spinner03.initializeStringValues(new String[]{}, "请选择");
        spinner04.initializeStringValues(new String[]{}, "请选择");


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinner01.getSelectedItemPosition() == 0) {
                    dialog.dismiss();
                    return;
                }
                String str = (spinner01.getSelectedItemPosition() == 0 ? "" : spinner01.getSelectedItem()) + " " + (spinner02.getSelectedItemPosition() == 0 ? "" : spinner02.getSelectedItem()) + " " + (spinner03.getSelectedItemPosition() == 0 ? "" : spinner03.getSelectedItem())
                        + " " + (spinner04.getSelectedItemPosition() == 0 ? "" : spinner04.getSelectedItem()) + " " + et.getText().toString().trim();
                mAdapter.addData(new Instructions(str, str));
                hideSoftInput();
                dialog.dismiss();
            }
        });
        spinner01.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        spinner03.initializeStringValues(paras1, "请选择");
                        spinner04.initializeStringValues(data1, "请选择");
                        et.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        spinner03.initializeStringValues(paras2, "请选择");
                        spinner04.initializeStringValues(data2, "请选择");
                        et.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showAdd3Dailog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setContentView(R.layout.dialog_add3_layout);

        final EditText et = window.findViewById(R.id.et);
        Button btnCancel = window.findViewById(R.id.btnCancel);
        Button btnOk = window.findViewById(R.id.btnOk);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(et.getText().toString())) {
                    String str = et.getText().toString().trim();
                    mAdapter.addData(new Instructions(str, str));
                    hideSoftInput();
                    dialog.dismiss();
                }

            }
        });
    }


}
