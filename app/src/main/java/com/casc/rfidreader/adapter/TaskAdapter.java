package com.casc.rfidreader.adapter;

import android.support.annotation.Nullable;

import com.casc.rfidreader.R;
import com.casc.rfidreader.bean.Task;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class TaskAdapter extends BaseQuickAdapter<Task, BaseViewHolder> {

    public TaskAdapter(@Nullable List<Task> data) {
        super(R.layout.item_task, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Task item) {
        helper.setText(R.id.tv_task_id, item.getTaskid());
    }
}
