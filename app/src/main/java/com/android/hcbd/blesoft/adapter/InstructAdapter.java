package com.android.hcbd.blesoft.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.hcbd.blesoft.R;
import com.android.hcbd.blesoft.entity.Instructions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InstructAdapter extends BaseAdapter {

    private Context context;
    private List<Instructions> list;
    private List<Boolean> isSelected;
    private List<String> inputList;
    private boolean[] isShow = {true,true,true,true,false,false,false
            ,false,false,false,false,true,false,true,false,false,false,false,false};

    public InstructAdapter(Context context,List<Instructions> list){
        this.context = context;
        this.list = list;
        isSelected = new ArrayList<>();
        inputList = new ArrayList<>();
        initDate();
    }

    private void initDate() {
        for(int i=0;i<list.size();i++){
            isSelected.add(false);
            inputList.add("");
        }
    }

    public List<Boolean> getIsSelected() {
        return isSelected;
    }

    public List<Instructions> getAllData(){
        return list;
    }

    public boolean[] getIsShow() {
        return isShow;
    }

    public List<String> getInputList() {
        return inputList;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_instruct_layout,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (isShow[position])
            holder.et.setVisibility(View.VISIBLE);
        else
            holder.et.setVisibility(View.GONE);

        holder.tv.setText(list.get(position).getName());
        holder.et.addTextChangedListener(new TextSwitcher(holder));
        holder.et.setTag(position);
        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIsSelected().get(position)){
                    getIsSelected().set(position,false);
                } else {
                    getIsSelected().set(position,true);
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.checkbox)
        CheckBox checkbox;
        @BindView(R.id.tv)
        TextView tv;
        @BindView(R.id.et)
        EditText et;
        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }

    class TextSwitcher implements TextWatcher {
        private ViewHolder viewHolder;

        public TextSwitcher(ViewHolder viewHolder){
            this.viewHolder = viewHolder;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            int position = (int) viewHolder.et.getTag();
            if (!TextUtils.isEmpty(editable.toString())){
                inputList.set(position,editable.toString());
            }
        }

    }


}
