package com.casc.rfidreader.fragment;

import android.widget.ArrayAdapter;
import android.widget.Switch;

import com.casc.rfidreader.MyParams;
import com.casc.rfidreader.R;
import com.casc.rfidreader.helper.SpHelper;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import butterknife.BindView;
import butterknife.OnClick;

public class ConfigFragment extends BaseFragment {

    private static String TAG = ConfigFragment.class.getSimpleName();

    @BindView(R.id.sw_reader_sensor) Switch mSensorSw;
    @BindView(R.id.spn_reader_power) BetterSpinner mReaderPowerSpn;
    @BindView(R.id.spn_reader_q_value) BetterSpinner mReaderQValueSpn;

    @Override
    protected void initFragment() {
        mReaderPowerSpn.setAdapter(new ArrayAdapter<>(mContext,
                R.layout.item_config, getResources().getStringArray(R.array.reader_power)));

        mReaderQValueSpn.setAdapter(new ArrayAdapter<>(mContext,
                R.layout.item_config, getResources().getStringArray(R.array.reader_q_value)));

        initViews();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_config;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        initViews();
    }

    @OnClick(R.id.btn_save_config)
    public void onSaveButtonClicked() {
        SpHelper.setParam(MyParams.S_SENSOR_SWITCH, String.valueOf(mSensorSw.isChecked()));
        SpHelper.setParam(MyParams.S_POWER, mReaderPowerSpn.getText().toString());
        SpHelper.setParam(MyParams.S_Q_VALUE, mReaderQValueSpn.getText().toString());
        showToast("保存成功");
    }

    private void initViews() {
        mSensorSw.setChecked(SpHelper.getBool(MyParams.S_SENSOR_SWITCH));
        mReaderPowerSpn.setText(SpHelper.getString(MyParams.S_POWER));
        mReaderQValueSpn.setText(SpHelper.getString(MyParams.S_Q_VALUE));
    }
}
