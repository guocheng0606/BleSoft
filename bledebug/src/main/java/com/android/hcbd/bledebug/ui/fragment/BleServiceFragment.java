package com.android.hcbd.bledebug.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.android.hcbd.bledebug.R;
import com.android.hcbd.bledebug.base.BaseFragment;

import butterknife.BindView;

/**
 */
public class BleServiceFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    @BindView(R.id.rv)
    RecyclerView mRecyclerView;

    public BleServiceFragment() {
    }

    public static BleServiceFragment newInstance(String param1, String param2) {
        BleServiceFragment fragment = new BleServiceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_ble;
    }

    @Override
    protected void initEventAndData() {

    }


}
