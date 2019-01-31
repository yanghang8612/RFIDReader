package com.casc.rfidreader.adapter;

import android.support.annotation.Nullable;

import com.casc.rfidreader.R;
import com.casc.rfidreader.bean.Tag;
import com.casc.rfidreader.utils.CommonUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class TagAdapter extends BaseQuickAdapter<Tag, BaseViewHolder> {

    public TagAdapter(@Nullable List<Tag> data) {
        super(R.layout.item_tag, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Tag item) {
        helper.setText(R.id.tv_no, String.valueOf(helper.getAdapterPosition() + 1))
                .setText(R.id.tv_epc, CommonUtils.bytesToHex(item.getEPC()))
                .setText(R.id.tv_rssi, String.valueOf(item.getRSSI()))
                .setText(R.id.tv_cnt, String.valueOf(item.getCnt()));
    }
}
