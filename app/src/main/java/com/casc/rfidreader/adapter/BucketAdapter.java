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
        helper.setText(R.id.actv_bucket_no, String.valueOf(helper.getAdapterPosition() + 1))
                .setText(R.id.actv_bucket_epc, item.getBucket_epc());
    }
}
