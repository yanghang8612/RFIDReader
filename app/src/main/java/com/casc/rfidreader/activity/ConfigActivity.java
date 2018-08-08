package com.casc.rfidreader.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Switch;

import com.casc.rfidreader.MyParams;
import com.casc.rfidreader.R;
import com.casc.rfidreader.helper.ConfigHelper;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfigActivity extends AppCompatActivity {

    private static final String TAG = ConfigActivity.class.getSimpleName();

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ConfigActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.toolbar_config) Toolbar toolbar;
    @BindView(R.id.sw_config_tid) Switch mTIDSw;
    @BindView(R.id.sw_config_sensor) Switch mSensorSw;
    @BindView(R.id.spn_config_reader_power) BetterSpinner mReaderPowerSpn;
    @BindView(R.id.spn_config_reader_q_value) BetterSpinner mReaderQValueSpn;
    @BindView(R.id.spn_config_tag_lifecycle) BetterSpinner mTagLifecycleSpn;

    @BindView(R.id.met_config_main_platform_addr) MaterialEditText mMainPlatformAddrMet;
    @BindView(R.id.met_config_standby_platform_addr) MaterialEditText mStandbyPlatformAddrMet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        ButterKnife.bind(this);
        setTitle("");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        mTIDSw.setChecked(ConfigHelper.getBooleanParam(MyParams.S_TID_SWITCH));
        mSensorSw.setChecked(ConfigHelper.getBooleanParam(MyParams.S_SENSOR_SWITCH));

        mReaderPowerSpn.setText(ConfigHelper.getParam(MyParams.S_POWER));
        mReaderPowerSpn.setAdapter(new ArrayAdapter<>(this,
                R.layout.item_config, getResources().getStringArray(R.array.reader_power)));

        mReaderQValueSpn.setText(ConfigHelper.getParam(MyParams.S_Q_VALUE));
        mReaderQValueSpn.setAdapter(new ArrayAdapter<>(this,
                R.layout.item_config, getResources().getStringArray(R.array.reader_q_value)));

        mTagLifecycleSpn.setText(ConfigHelper.getParam(MyParams.S_TAG_LIFECYCLE));
        mTagLifecycleSpn.setAdapter(new ArrayAdapter<>(this,
                R.layout.item_config, getResources().getStringArray(R.array.tag_lifecycle)));

        mMainPlatformAddrMet.setText(ConfigHelper.getParam(MyParams.S_MAIN_PLATFORM_ADDR));
        mMainPlatformAddrMet.addValidator(new RegexpValidator("网址格式错误",
                "^((([hH][tT][tT][pP][sS]?|[fF][tT][pP])\\:\\/\\/)?([\\w\\.\\-]+(\\:[\\w\\.\\&%\\$\\-]+)*@)?((([^\\s\\(\\)\\<\\>\\\\\\\"\\.\\[\\]\\,@;:]+)(\\.[^\\s\\(\\)\\<\\>\\\\\\\"\\.\\[\\]\\,@;:]+)*(\\.[a-zA-Z]{2,4}))|((([01]?\\d{1,2}|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d{1,2}|2[0-4]\\d|25[0-5])))(\\b\\:(6553[0-5]|655[0-2]\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3}|0)\\b)?((\\/[^\\/][\\w\\.\\,\\?\\'\\\\\\/\\+&%\\$#\\=~_\\-@]*)*[^\\.\\,\\?\\\"\\'\\(\\)\\[\\]!;<>{}\\s\\x7F-\\xFF])?)$"));
        mStandbyPlatformAddrMet.setText(ConfigHelper.getParam(MyParams.S_STANDBY_PLATFORM_ADDR));
        mStandbyPlatformAddrMet.addValidator(new RegexpValidator("网址格式错误",
                "^((([hH][tT][tT][pP][sS]?|[fF][tT][pP])\\:\\/\\/)?([\\w\\.\\-]+(\\:[\\w\\.\\&%\\$\\-]+)*@)?((([^\\s\\(\\)\\<\\>\\\\\\\"\\.\\[\\]\\,@;:]+)(\\.[^\\s\\(\\)\\<\\>\\\\\\\"\\.\\[\\]\\,@;:]+)*(\\.[a-zA-Z]{2,4}))|((([01]?\\d{1,2}|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d{1,2}|2[0-4]\\d|25[0-5])))(\\b\\:(6553[0-5]|655[0-2]\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3}|0)\\b)?((\\/[^\\/][\\w\\.\\,\\?\\'\\\\\\/\\+&%\\$#\\=~_\\-@]*)*[^\\.\\,\\?\\\"\\'\\(\\)\\[\\]!;<>{}\\s\\x7F-\\xFF])?)$"));
    }

    @OnClick(R.id.btn_config_save)
    public void onSaveButtonClicked() {
        if (mMainPlatformAddrMet.validate() && mStandbyPlatformAddrMet.validate()) {
            ConfigHelper.setParam(MyParams.S_TID_SWITCH, String.valueOf(mTIDSw.isChecked()));
            ConfigHelper.setParam(MyParams.S_SENSOR_SWITCH, String.valueOf(mSensorSw.isChecked()));
            ConfigHelper.setParam(MyParams.S_POWER, mReaderPowerSpn.getText().toString());
            ConfigHelper.setParam(MyParams.S_Q_VALUE, mReaderQValueSpn.getText().toString());
            ConfigHelper.setParam(MyParams.S_TAG_LIFECYCLE, mTagLifecycleSpn.getText().toString());

            ConfigHelper.setParam(MyParams.S_MAIN_PLATFORM_ADDR, mMainPlatformAddrMet.getText().toString());
            ConfigHelper.setParam(MyParams.S_STANDBY_PLATFORM_ADDR, mStandbyPlatformAddrMet.getText().toString());

            finish();
        }
    }
}
