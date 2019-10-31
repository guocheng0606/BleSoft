package com.android.hcbd.blesoft.ui.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.hcbd.blesoft.R;
import com.android.hcbd.blesoft.adapter.CustomAddAdapter;
import com.android.hcbd.blesoft.base.BaseActivity;
import com.android.hcbd.blesoft.db.CustomDbTool;
import com.android.hcbd.blesoft.entity.Custom;
import com.android.hcbd.blesoft.entity.Instructions;
import com.android.hcbd.blesoft.event.MessageEvent;
import com.android.hcbd.blesoft.weight.CustomSpinner;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CustomAddActivity extends BaseActivity implements View.OnClickListener  {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.btn_add1)
    Button btn_add1;
    @BindView(R.id.btn_add2)
    Button btn_add2;
    @BindView(R.id.btn_add3)
    Button btn_add3;
    private CustomAddAdapter adapter;

    private List<Instructions> list = new ArrayList<>();

    private Custom custom;

    @Override
    protected int getLayout() {
        return R.layout.activity_custom_add;
    }

    @Override
    protected void initEventAndData() {
        setToolBar(toolbar,"添加");
        custom = (Custom) getIntent().getSerializableExtra("custom");

        if (custom == null){
            toolbar.setTitle("添加");
        } else {
            toolbar.setTitle("编辑");
            List<Instructions> orders = custom.getOrders();
            list.addAll(orders);
        }

        adapter = new CustomAddAdapter(this,list);
        listview.setAdapter(adapter);

        btn_add1.setOnClickListener(this);
        btn_add2.setOnClickListener(this);
        btn_add3.setOnClickListener(this);
    }

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
        getMenuInflater().inflate(R.menu.menu_save,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                if (custom == null) {
                    showNameDialog();
                } else {
                    custom.setOrders(list);
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
        final EditText edit=(EditText)view.findViewById(R.id.editText);//获得输入框对象
        new AlertDialog.Builder(CustomAddActivity.this)
                .setTitle("请输入保存名称")//提示框标题
                .setView(view)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Custom custom = new Custom();
                        custom.setName(edit.getText().toString());
                        custom.setOrders(list);
                        CustomDbTool.add(custom);
                        MessageEvent messageEvent = new MessageEvent(MessageEvent.EVENT_CUSTOM_LIST);
                        EventBus.getDefault().post(messageEvent);
                        finishActivity();
                    }
                }).create().show();
    }


    private String[] names = {"配置IP1","配置IP2","配置IP3","配置IP4","读取IP1","读取IP2","读取IP3","读取IP4",
            "禁用IP1","禁用IP2","禁用IP3","配置SN号","读取SN号","配置蓝牙名称","测试看门狗","格式化第1扇区","写入第1扇区",
            "读第1扇区","查询软件版本号"};
    private String[] orders = {"SET IP1=","SET IP2=","SET IP3=","SET IP4=","SET READ IP1=","SET READ IP2=",
            "SET READ IP3=","SET READ IP4=","SET IP1=FALSE","SET IP2=FALSE","SET IP3=FALSE","SET SN=","SET READ SN=",
            "SET BLE_NAME=","ST+TEST_WATCHDOG","ST+SectorErase25P32","ST+WRITE25P32","ST+READ25P32","ST+VER"};

    private void showAdd1Dailog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
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
                if (spinner.getSelectedItemPosition() == 0){
                    dialog.dismiss();
                    return;
                }
                String str = orders[spinner.getSelectedItemPosition()-1]+et.getText().toString().trim();
                list.add(new Instructions((String) spinner.getSelectedItem()+"："+et.getText().toString().trim(),str));
                adapter.notifyDataSetChanged();
                hideSoftInput();
                dialog.dismiss();
            }
        });
    }

    private String[] types = {"UNLOG","LOG"};
    private String[] paras1 = {"UNLOGALL","BD2EPHEMA","BD2IONUTCA","BESTPOSA","BESTCVELA","GLOEPHEMERISA"
            ,"GPSEPHEMA","GPGGA","GPGSA","GPGST","GPGSV","GPHDT","GPRMC GPS","GPVTG","GPZDA","HEADINGA"
            ,"LOGLIST","MATCHEDPOS","MATCHEDPOSH","PSRDOP","PSRVEL","RANGE","RAWEPHEM","REFSTATION","SATVIS"
            ,"TIME","VERSION"};
    private String[] paras2 = {"BD2EPHEMA","BD2IONUTCA","BESTPOSA","BESTCVELA","GLOEPHEMERISA","GPSEPHEMA"
            ,"GPGGA","GPGSA","GPGST","GPGSV","GPHDT","GPRMC GPS","GPVTG","GPZDA","HEADINGA","IONUTCA ONCHANGED"
            ,"LOGLIST","PSRDOP","PSRVEL","RANGE","RANGEH","RAWEPHEM","REFSTATION","RANGECMPA","RTKDATAA ONCHANGED"
            ,"RTKDATAHA ONCHANGED","SATVIS","TIME","VERSION","RTCM1","RTCM3","RTCM18","RTCM19","RTCM24","RTCM31","RTCM32"
            ,"RTCM63","RTCM1001","RTCM1002","RTCM1003","RTCM1004","RTCM1005","RTCM1006","RTCM1009"
            ,"RTCM1010","RTCM1011","RTCM1012","RTCM1019","RTCM1020","RTCM1033","RTCM1074","RTCM1075","RTCM1084"
            ,"RTCM1085","RTCM1104","RTCM1105","RTCM1124","RTCM1125"};
    private String[] data1 = {"FALSE","TRUE"};
    private String[] data2 = {"ONTIME","空"};
    private void showAdd2Dailog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setContentView(R.layout.dialog_add2_layout);

        final CustomSpinner spinner01 = window.findViewById(R.id.spinner01);
        final CustomSpinner spinner02 = window.findViewById(R.id.spinner02);
        final CustomSpinner spinner03 = window.findViewById(R.id.spinner03);
        final EditText et_01 = window.findViewById(R.id.et_01);
        final EditText et_02 = window.findViewById(R.id.et_02);
        Button btnCancel = window.findViewById(R.id.btnCancel);
        Button btnOk = window.findViewById(R.id.btnOk);

        spinner01.initializeStringValues(types, "请选择");
        spinner02.initializeStringValues(new String[]{}, "请选择");
        spinner03.initializeStringValues(new String[]{}, "请选择");


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinner01.getSelectedItemPosition() == 0 ){
                    dialog.dismiss();
                    return;
                }
                String str = (spinner01.getSelectedItemPosition() == 0 ? "" : spinner01.getSelectedItem())+" "+et_01.getText().toString().trim()+" "+(spinner02.getSelectedItemPosition() == 0 ? "" : spinner02.getSelectedItem())
                        +" "+(spinner03.getSelectedItemPosition() == 0 ? "" : spinner03.getSelectedItem())+" "+et_02.getText().toString().trim();
                list.add(new Instructions(str,str));
                adapter.notifyDataSetChanged();
                hideSoftInput();
                dialog.dismiss();
            }
        });
        spinner01.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        spinner02.initializeStringValues(paras1, "请选择");
                        spinner03.initializeStringValues(data1, "请选择");
                        et_02.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        spinner02.initializeStringValues(paras2, "请选择");
                        spinner03.initializeStringValues(data2, "请选择");
                        et_02.setVisibility(View.VISIBLE);
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
        dialog.setCanceledOnTouchOutside(true);
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
                if(!TextUtils.isEmpty(et.getText().toString())){
                    String str = et.getText().toString().trim();
                    list.add(new Instructions(str,str));
                    adapter.notifyDataSetChanged();
                    hideSoftInput();
                    dialog.dismiss();
                }

            }
        });
    }


}
