package com.casc.rfidreader.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.casc.rfidreader.MyParams;
import com.casc.rfidreader.MyVars;
import com.casc.rfidreader.R;
import com.casc.rfidreader.adapter.TagAdapter;
import com.casc.rfidreader.bean.Tag;
import com.casc.rfidreader.bean.TaskID;
import com.casc.rfidreader.helper.NetHelper;
import com.casc.rfidreader.helper.SpHelper;
import com.casc.rfidreader.helper.param.MessageUpload;
import com.casc.rfidreader.helper.param.Reply;
import com.casc.rfidreader.message.PollingResultMessage;
import com.casc.rfidreader.message.ReaderConnectMessage;
import com.casc.rfidreader.utils.CommonUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskFragment extends BaseFragment {

    private static String TAG = TaskFragment.class.getSimpleName();

    private static final int MSG_UPDATE_READER_STATE = 0;
    private static final int MSG_UPDATE_TABLE = 1;
    private static final int MSG_NONE = 2;
    private static final int MSG_START = 3;
    private static final int MSG_PAUSE = 4;
    private static final int MSG_RESUME = 5;

    @BindView(R.id.tv_reader_state) TextView mReaderStateTv;
    @BindView(R.id.tv_task_id) TextView mTaskIDTv;
    @BindView(R.id.btn_operate_task) Button mOperateTaskBtn;
    @BindView(R.id.btn_commit_task) Button mCommitTaskBtn;
    @BindView(R.id.rv_tag_table) RecyclerView mTagTableRv;

    private Map<String, Tag> mTagMap = new HashMap<>();

    private List<Tag> mTags = new ArrayList<>();

    private TagAdapter mAdapter;

    private Handler mHandler = new InnerHandler(this);

    private WorkState mState = WorkState.NONE;

    private String mTaskID;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReaderConnectMessage message) {
        if (message.isConnected) {
            mReaderStateTv.setText("已连接");
        } else {
            mReaderStateTv.setText("未连接");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PollingResultMessage message) {
        if (message.isRead) {
            if (!mTagMap.containsKey(message.epcStr)) {
                mTagMap.put(message.epcStr, new Tag(message.epc));
                mTags.add(mTagMap.get(message.epcStr));
            }
            mTagMap.get(message.epcStr).setRSSI(message.rssi);
        }
        mHandler.sendMessage(Message.obtain(mHandler, MSG_UPDATE_TABLE));
    }

    @Override
    protected void initFragment() {
        EventBus.getDefault().register(this);
        mAdapter = new TagAdapter(mTags);
        mTagTableRv.setLayoutManager(new LinearLayoutManager(mContext));
        mTagTableRv.setAdapter(mAdapter);

        MyVars.executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Message.obtain(mHandler, MSG_UPDATE_READER_STATE).sendToTarget();
            }
        }, 0, 1000, TimeUnit.SECONDS);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_task;
    }

    @OnClick(R.id.btn_clear_table)
    void onClearTableButtonClicked() {
        mTagMap.clear();
        mTags.clear();
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.btn_operate_task)
    void onOperateTaskButtonClicked() {
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
                            SpHelper.setParam(MyParams.S_TASKID, mTaskID);
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
        if (mState == WorkState.NONE) return;
        new MaterialDialog.Builder(mContext)
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
                        for (Tag tag : mTags) {
                            message.addBucket("", CommonUtils.bytesToHex(tag.getEPC()));
                        }
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

    private enum WorkState {
        NONE, WORKING, PAUSE
    }

    private static class InnerHandler extends Handler {

        private WeakReference<TaskFragment> mOuter;

        InnerHandler(TaskFragment fragment) {
            this.mOuter = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            TaskFragment outer = mOuter.get();
            switch (msg.what) {
                case MSG_UPDATE_READER_STATE:
                    if (MyVars.getReader().isConnected()) {
                        outer.mReaderStateTv.setText("已连接");
                        outer.mReaderStateTv.setBackground(outer.mContext.getDrawable(R.drawable.bg_status_working));
                    } else {
                        outer.mReaderStateTv.setText("未连接");
                        outer.mReaderStateTv.setBackground(outer.mContext.getDrawable(R.drawable.bg_status_normal));
                    }
                    break;
                case MSG_UPDATE_TABLE:
                    outer.mAdapter.notifyDataSetChanged();
                    break;
                case MSG_NONE:
                    outer.mState = WorkState.NONE;
                    outer.mTaskIDTv.setVisibility(View.INVISIBLE);
                    outer.mOperateTaskBtn.setText("开始任务");
                    outer.mTagMap.clear();
                    outer.mTags.clear();
                    outer.mAdapter.notifyDataSetChanged();
                    MyVars.getReader().pause();
                    break;
                case MSG_START:
                    outer.mState = WorkState.WORKING;
                    outer.mTaskIDTv.setVisibility(View.VISIBLE);
                    outer.mTaskIDTv.setText(String.valueOf(outer.mTaskID));
                    outer.mOperateTaskBtn.setText("暂停任务");
                    MyVars.getReader().start();
                    break;
                case MSG_PAUSE:
                    outer.mState = WorkState.PAUSE;
                    outer.mOperateTaskBtn.setText("继续任务");
                    MyVars.getReader().pause();
                    break;
                case MSG_RESUME:
                    outer.mState = WorkState.WORKING;
                    outer.mOperateTaskBtn.setText("暂停任务");
                    MyVars.getReader().start();
                    break;
            }
        }
    }
}
