package com.casc.rfidreader.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Bucket {

    private String bucket_TID;

    private long bucket_time;

    private String bucket_epc;

    public String getBucket_TID() {
        return bucket_TID;
    }

    public String getBucket_time() {
        return new SimpleDateFormat("HH:mm:ss", Locale.CHINA).format(new Date(bucket_time));
    }

    public String getBucket_epc() {
        return bucket_epc;
    }
}
