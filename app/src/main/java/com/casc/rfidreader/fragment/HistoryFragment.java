package com.casc.rfidreader.fragment;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.casc.rfidreader.R;
import com.casc.rfidreader.adapter.BucketAdapter;
import com.casc.rfidreader.adapter.TaskAdapter;
import com.casc.rfidreader.bean.Bucket;
import com.casc.rfidreader.bean.Task;
import com.casc.rfidreader.helper.NetHelper;
import com.casc.rfidreader.helper.param.Reply;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends BaseFragment {

    private static final String TAG = HistoryFragment.class.getSimpleName();

    private static final int MSG_UPDATE_TASK_LIST = 0;
    private static final int MSG_UPDATE_TASK_DETAIL = 1;

    @BindView(R.id.et_start_time) EditText mStartTimeEt;
    @BindView(R.id.et_end_time) EditText mEndTimeEt;
    @BindView(R.id.rv_task_list) RecyclerView mTaskListRv;
    @BindView(R.id.ll_task_detail) LinearLayout mTaskDetailLl;
    @BindView(R.id.tv_task_count) TextView mTaskCountTv;
    @BindView(R.id.tv_task_start_time) TextView mTaskStartTimeTv;
    @BindView(R.id.tv_task_end_time) TextView mTaskEndTimeTv;
    @BindView(R.id.tv_task_remarks) TextView mTaskRemarksTv;
    @BindView(R.id.rv_task_bucket_list) RecyclerView mTaskBucketListRv;

    private TaskAdapter mTaskAdapter;

    private List<Task> mTaskList = new ArrayList<>();

    private BucketAdapter mBucketAdapter;

    private List<Bucket> mBucketList = new ArrayList<>();

    private CardView mSelectedCardView;

    private Task mCurTask;

    private long mStartTime, mEndTime;

    private Handler mHandler = new InnerHandler(this);

    @Override
    protected void initFragment() {
//        Calendar now = Calendar.getInstance();
//        now.set(Calendar.HOUR, 23);
//        now.set(Calendar.MINUTE, 59);
//        now.set(Calendar.SECOND, 59);
//        mEndTime = now.getTimeInMillis();
//        mEndTimeEt.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
//                .format(now.getTime()));
//        mEndTimeEt.setInputType(InputType.TYPE_NULL);
//
//        now.add(Calendar.DAY_OF_YEAR, -7);
//        now.set(Calendar.HOUR, 0);
//        now.set(Calendar.MINUTE, 0);
//        now.set(Calendar.SECOND, 0);
//        mStartTime = now.getTimeInMillis();
//        mStartTimeEt.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
//                .format(now.getTime()));
//        mStartTimeEt.setInputType(InputType.TYPE_NULL);

        mTaskAdapter = new TaskAdapter(mTaskList);
        mTaskAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CardView card = view.findViewById(R.id.cv_task_root);
                if (card != mSelectedCardView) {
                    card.setCardBackgroundColor(mContext.getColor(R.color.colorPrimary));
                    mTaskDetailLl.setVisibility(View.VISIBLE);
                    NetHelper.getInstance().queryDetail(mTaskList.get(position).getTaskid()).enqueue(new Callback<Reply>() {
                        @Override
                        public void onResponse(@NonNull Call<Reply> call, @NonNull Response<Reply> response) {
                            Reply reply = response.body();
                            if (!response.isSuccessful() || reply == null || reply.getCode() != 200) {
                                showToast("查询详情失败");
                            } else {
                                Log.i(TAG, reply.getContent().toString());
                                mCurTask = new Gson().fromJson(reply.getContent().getAsJsonArray().get(0), Task.class);
                                mHandler.sendMessage(Message.obtain(mHandler, MSG_UPDATE_TASK_DETAIL));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Reply> call, @NonNull Throwable t) {
                            showToast("查询详情失败，请检查网络");
                        }
                    });
                    if (mSelectedCardView != null) {
                        mSelectedCardView.setCardBackgroundColor(mContext.getColor(R.color.white));
                    }
                    mSelectedCardView = card;
                }
            }
        });
        mTaskListRv.setLayoutManager(new LinearLayoutManager(mContext));
        mTaskListRv.setAdapter(mTaskAdapter);

        mBucketAdapter = new BucketAdapter(mBucketList);
        mTaskBucketListRv.setLayoutManager(new LinearLayoutManager(mContext));
        mTaskBucketListRv.setAdapter(mBucketAdapter);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_history;
    }

    @OnClick(R.id.et_start_time)
    void onStartTimeEditTextClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar date = Calendar.getInstance();
                        date.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                        mStartTime = date.getTimeInMillis();
                        mStartTimeEt.setText(
                                new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                                        .format(date.getTime()));
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.show(((Activity) mContext).getFragmentManager(), "");
    }

    @OnClick(R.id.et_end_time)
    void onEndTimeEditTextClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar date = Calendar.getInstance();
                        date.set(year, monthOfYear, dayOfMonth, 23, 59, 59);
                        mEndTime = date.getTimeInMillis();
                        mEndTimeEt.setText(
                                new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                                        .format(date.getTime()));
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.show(((Activity) mContext).getFragmentManager(), "");
    }

    @OnClick(R.id.btn_query)
    void onQueryButtonClicked() {
        if (mStartTime == 0) {
            showToast("请选择开始时间");
        } else if (mEndTime == 0) {
            showToast("请选择结束时间");
        } else if (mStartTime > mEndTime) {
            showToast("开始时间应早于结束时间");
        } else {
            mTaskDetailLl.setVisibility(View.INVISIBLE);
            NetHelper.getInstance().queryTask(mStartTime, mEndTime).enqueue(new Callback<Reply>() {
                @Override
                public void onResponse(@NonNull Call<Reply> call, @NonNull Response<Reply> response) {
                    Reply reply = response.body();
                    if (!response.isSuccessful() || reply == null || reply.getCode() != 200) {
                        showToast("查询任务历史失败");
                    } else {
                        showToast("查询任务历史成功");
                        mTaskList.clear();
                        for (JsonElement element : reply.getContent().getAsJsonArray()) {
                            mTaskList.add(new Gson().fromJson(element, Task.class));
                        }
                        mHandler.sendMessage(Message.obtain(mHandler, MSG_UPDATE_TASK_LIST));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Reply> call, @NonNull Throwable t) {
                    showToast("查询任务历史失败，请检查网络");
                }
            });
        }
    }

    private static class InnerHandler extends Handler {

        private WeakReference<HistoryFragment> mOuter;

        InnerHandler(HistoryFragment fragment) {
            this.mOuter = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            HistoryFragment outer = mOuter.get();
            switch (msg.what) {
                case MSG_UPDATE_TASK_LIST:
                    outer.mTaskAdapter.notifyDataSetChanged();
                    break;
                case MSG_UPDATE_TASK_DETAIL:
                    outer.mTaskDetailLl.setVisibility(View.VISIBLE);
                    outer.mTaskCountTv.setText(String.valueOf(outer.mCurTask.getBucketnumber()));
                    outer.mTaskStartTimeTv.setText(outer.mCurTask.getStarttime());
                    outer.mTaskEndTimeTv.setText(outer.mCurTask.getEndtime());
                    outer.mTaskRemarksTv.setText(outer.mCurTask.getRemarks());
                    outer.mBucketList.clear();
                    outer.mBucketList.addAll(outer.mCurTask.getBucket_info());
                    outer.mBucketAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
