package com.casc.rfidreader.message;

import com.casc.rfidreader.utils.CommonUtils;

public class AbnormalBucketMessage {

    public boolean isReadNone;

    public byte[] epc;

    public AbnormalBucketMessage() {
        this.isReadNone = true;
    }

    public AbnormalBucketMessage(String epc) {
        this.isReadNone = false;
        this.epc = CommonUtils.hexToBytes(epc);
    }
}
