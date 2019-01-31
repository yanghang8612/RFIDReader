package com.casc.rfidreader.helper.param;

import com.casc.rfidreader.MyParams;
import com.casc.rfidreader.helper.SpHelper;

import java.util.ArrayList;
import java.util.List;

public class MessageUpload {

    private String username;

    private String taskid;

    private String remarks;

    private List<Bucket> bucket_info = new ArrayList<>();

    public MessageUpload() {
        this.username = SpHelper.getString(MyParams.S_USERNAME);
        this.taskid = SpHelper.getString(MyParams.S_TASKID);
    }

    public MessageUpload(String remarks) {
        this.username = SpHelper.getString(MyParams.S_USERNAME);
        this.taskid = SpHelper.getString(MyParams.S_TASKID);
        this.remarks = remarks;
    }

    public String getUsername() {
        return username;
    }

    public String getTaskid() {
        return taskid;
    }

    public String getRemarks() {
        return remarks;
    }

    public List<Bucket> getBucket_info() {
        return bucket_info;
    }

    public void addBucket(String tid, String epc) {
        bucket_info.add(new Bucket(tid, epc));
    }

    // 桶信息的内部类
    private class Bucket {

        private String bucket_TID;

        private long bucket_time;

        private String bucket_epc;

        private Bucket(String tid, String epc) {
            this.bucket_TID = tid;
            this.bucket_time = System.currentTimeMillis() / 1000;
            this.bucket_epc = epc;
        }
    }
}
