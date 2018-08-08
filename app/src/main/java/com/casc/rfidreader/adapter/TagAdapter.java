package com.casc.rfidreader.adapter;

import android.graphics.Color;
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
                .setText(R.id.tv_pc, item.getPc())
                .setText(R.id.tv_epc, item.getEpc())
                .setText(R.id.tv_crc, item.getCrc())
                .setText(R.id.tv_rssi, item.getRssi())
                .setText(R.id.tv_cnt, item.getCnt())
                .setBackgroundColor(R.id.llc_item_tag_root, Color.parseColor(CommonUtils.generateGradientRedColor(item.getNoneCnt())));
    }
}
