package com.casc.rfidreader.adapter;

import android.support.annotation.Nullable;

import com.casc.rfidreader.R;
import com.casc.rfidreader.bean.Bucket;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class BucketAdapter extends BaseQuickAdapter<Bucket, BaseViewHolder> {

    public BucketAdapter(@Nullable List<Bucket> data) {
        super(R.layout.item_bucket, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Bucket item) {
        helper.setText(R.id.tv_bucket_time, item.getBucket_time())
                .setText(R.id.tv_bucket_epc, item.getBucket_epc())
                .setText(R.id.tv_bucket_tid, item.getBucket_TID());
    }
}
