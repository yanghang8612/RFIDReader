package com.casc.rfidreader.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Task {

    private String taskid;

    private long starttime;

    private long endtime;

    private String remarks;

    private int bucketnumber;

    private List<Bucket> bucket_info;

    public String getTaskid() {
        return taskid;
    }

    public String getStarttime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(starttime));
    }

    public String getEndtime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(endtime));
    }

    public String getRemarks() {
        return remarks;
    }

    public int getBucketnumber() {
        return bucketnumber;
    }

    public List<Bucket> getBucket_info() {
        return bucket_info;
    }
}
