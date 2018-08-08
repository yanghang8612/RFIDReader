package com.casc.rfidreader.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.casc.rfidreader.MyParams;
import com.casc.rfidreader.MyVars;
import com.casc.rfidreader.R;
import com.casc.rfidreader.adapter.TagAdapter;
import com.casc.rfidreader.backend.InsHandler;
import com.casc.rfidreader.backend.TagCache;
import com.casc.rfidreader.bean.Tag;
import com.casc.rfidreader.bean.TaskID;
import com.casc.rfidreader.helper.ConfigHelper;
import com.casc.rfidreader.helper.InsHelper;
import com.casc.rfidreader.helper.NetHelper;
import com.casc.rfidreader.helper.param.MessageUpload;
import com.casc.rfidreader.helper.param.Reply;
import com.casc.rfidreader.utils.CommonUtils;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements InsHandler {

    private static String TAG = MainActivity.class.getSimpleName();

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    private static final int MSG_UPDATE_TABLE = 0;
    private static final int MSG_NONE = 1;
    private static final int MSG_START = 2;
    private static final int MSG_PAUSE = 3;
    private static final int MSG_RESUME = 4;

    @BindView(R.id.rv_tag_table) RecyclerView mTagTableRv;
    @BindView(R.id.tv_task_status) TextView mTaskStatusTv;
    @BindView(R.id.tv_task_id) TextView mTaskIDTv;
    @BindView(R.id.tv_scan_count) TextView mScanCountTv;
    @BindView(R.id.btn_commit_task) Button mCommitTaskBtn;
    @BindView(R.id.btn_start_task) Button mStartTaskBtn;

    private List<Tag> mTags = new ArrayList<>();

    private TagAdapter mAdapter;

    private Handler mHandler = new InnerHandler(this);

    private WorkState mState = WorkState.NONE;

    private String mTaskID;

    private TagCache mCache = new TagCache();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        MyVars.usbReader.setHandler(this);
        MyVars.bleReader.setHandler(this);

        mAdapter = new TagAdapter(mTags);
        mTagTableRv.setLayoutManager(new LinearLayoutManager(this));
        mTagTableRv.setAdapter(mAdapter);

        mReaderStatusIv.setImageResource(MyVars.status.readerStatus ?
                R.drawable.ic_connection_normal : R.drawable.ic_connection_abnormal);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyVars.executor.shutdown();
    }

    @Override
    public void sensorSignal(boolean isHigh) {

    }

    @Override
    public void dealIns(byte[] ins) {
        int command = ins[2] & 0xFF;
        switch (command) {
            case 0x22: // 轮询成功的处理流程
                int pl = ((ins[3] & 0xFF) << 8) + (ins[4] & 0xFF);
                byte[] epc = Arrays.copyOfRange(ins, 8, pl + 3);
                String epcStr = CommonUtils.bytesToHex(epc);
                if (mCache.insert(epcStr)) {
                    Tag tag = new Tag(ins);
                    mTags.add(tag);
                    if (ConfigHelper.getBooleanParam(MyParams.S_TID_SWITCH)) {
                        MyVars.getReader().setMask(epc);
                        MyVars.getReader().sendCommand(InsHelper.getReadMemBank(
                                CommonUtils.hexToBytes("00000000"),
                                InsHelper.MemBankType.TID,
                                MyParams.TID_START_INDEX,
                                MyParams.TID_LENGTH), MyParams.READ_TID_MAX_TRY_COUNT);
                    }
                }
                for (Tag tag : mTags) {
                    if (tag.getEpc().equals(epcStr)) {
                        tag.setRssi((int) ins[5] + ConfigHelper.getIntegerParam(MyParams.S_POWER));
                        tag.addCnt();
                        break;
                    }
                }
                mHandler.sendMessage(Message.obtain(mHandler, MSG_UPDATE_TABLE));
                break;
            case 0x39: // 读TID成功的处理流程
                mCache.setTID(ins);
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.btn_history)
    void onHistoryButtonClicked() {
        HistoryActivity.actionStart(this);
    }

    @OnClick(R.id.btn_clear_table)
    void onClearTableButtonClicked() {
        mTags.clear();
        mCache.clear();
        mScanCountTv.setText(String.valueOf(0));
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.btn_start_task)
    void onStartTaskButtonClicked() {
        switch (mState) {
            case NONE:
                NetHelper.getInstance().requestTaskID().enqueue(new Callback<Reply>() {
                    @Override
                    public void onResponse(@NonNull Call<Reply> call, @NonNull Response<Reply> response) {
                        Reply reply = response.body();
                        if (!response.isSuccessful() || reply == null || reply.getCode() != 200) {
                            showToast("请求TaskID失败");
                        } else {
                            showToast("请求TaskID成功，任务开始");
                            mTaskID = new Gson().fromJson(reply.getContent().getAsJsonArray().get(0), TaskID.class).getTaskid();
                            ConfigHelper.setParam(MyParams.S_TASKID, mTaskID);
                            mHandler.sendMessage(Message.obtain(mHandler, MSG_START));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Reply> call, @NonNull Throwable t) {
                        showToast("请求TaskID失败，请检查网络");
                    }
                });
                break;
            case WORKING:
                mHandler.sendMessage(Message.obtain(mHandler, MSG_PAUSE));
                break;
            case PAUSE:
                mHandler.sendMessage(Message.obtain(mHandler, MSG_RESUME));
                break;
        }
    }

    @OnClick(R.id.btn_commit_task)
    void onCommitTaskButtonClicked() {
        if (mState != WorkState.NONE) {
            new MaterialDialog.Builder(this)
                    .title("确认提交该调试任务吗?")
                    .inputRange(0, -1)
                    .input("任务描述", "", new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {}
                    })
                    .positiveText("确认")
                    .positiveColorRes(R.color.white)
                    .btnSelector(R.drawable.md_btn_postive, DialogAction.POSITIVE)
                    .negativeText("取消")
                    .negativeColorRes(R.color.gray)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            MessageUpload message = new MessageUpload(
                                    dialog.getInputEditText().getText().toString());
                            NetHelper.getInstance().uploadTask(message).enqueue(new Callback<Reply>() {
                                @Override
                                public void onResponse(@NonNull Call<Reply> call, @NonNull Response<Reply> response) {
                                    Reply reply = response.body();
                                    if (!response.isSuccessful() || reply == null || reply.getCode() != 200) {
                                        showToast("上传失败");
                                    } else {
                                        showToast("上传成功");
                                        mHandler.sendMessage(Message.obtain(mHandler, MSG_NONE));
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Reply> call, @NonNull Throwable t) {
                                    showToast("上传失败，请检查网络");
                                }
                            });
                            dialog.dismiss();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    private enum WorkState {
        NONE, WORKING, PAUSE
    }

    private static class InnerHandler extends Handler {

        private WeakReference<MainActivity> mOuter;

        InnerHandler(MainActivity activity) {
            this.mOuter = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity outer = mOuter.get();
            switch (msg.what) {
                case MSG_UPDATE_TABLE:
                    outer.mScanCountTv.setText(String.valueOf(outer.mTags.size()));
                    outer.mAdapter.notifyDataSetChanged();
                    break;
                case MSG_NONE:
                    MyVars.getReader().pause();
                    outer.mTags.clear();
                    outer.mCache.clear();
                    outer.mAdapter.notifyDataSetChanged();
                    outer.mState = WorkState.NONE;
                    outer.mTaskStatusTv.setText("空闲");
                    outer.mTaskStatusTv.setBackgroundResource(R.drawable.bg_status_normal);
                    outer.mTaskIDTv.setText("0");
                    outer.mCommitTaskBtn.setEnabled(false);
                    outer.mScanCountTv.setText("0");
                    outer.mStartTaskBtn.setText("开始任务");
                    MyVars.getReader().pause();
                    break;
                case MSG_START:
                    outer.mState = WorkState.WORKING;
                    outer.mTaskStatusTv.setText("调试中");
                    outer.mTaskStatusTv.setBackgroundResource(R.drawable.bg_status_working);
                    outer.mTaskIDTv.setText(outer.mTaskID);
                    outer.mCommitTaskBtn.setEnabled(true);
                    outer.mStartTaskBtn.setText("暂停任务");
                    MyVars.getReader().start();
                    break;
                case MSG_PAUSE:
                    outer.mState = WorkState.PAUSE;
                    outer.mStartTaskBtn.setText("继续任务");
                    MyVars.getReader().pause();
                    break;
                case MSG_RESUME:
                    outer.mState = WorkState.WORKING;
                    outer.mStartTaskBtn.setText("暂停任务");
                    MyVars.getReader().start();
                    break;
            }
        }
    }
}
