package com.casc.rfidreader.backend;

import android.support.annotation.NonNull;
import android.util.Log;

import com.casc.rfidreader.MyParams;
import com.casc.rfidreader.MyVars;
import com.casc.rfidreader.helper.ConfigHelper;
import com.casc.rfidreader.helper.NetHelper;
import com.casc.rfidreader.helper.param.MessageUpload;
import com.casc.rfidreader.helper.param.Reply;
import com.casc.rfidreader.utils.CommonUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TagCache {

    private static final String TAG = TagCache.class.getSimpleName();

    // 缓存map，key为epc，value为Tag类型的实例
    private final Map<String, Tag> cache = new HashMap<>();

    public TagCache() {
        MyVars.executor.scheduleWithFixedDelay(new LifecycleCheckTask(), 0, 100, TimeUnit.MILLISECONDS);
    }

    public void clear() {
        cache.clear();
    }

    public synchronized boolean insert(String epc) {
        if (!cache.containsKey(epc)) {
            cache.put(epc, new Tag(epc));
            cache.get(epc).status = TagStatus.NONE;
            return true;
        }
        else
            return false;
    }

    public synchronized void setTID(byte[] command) {
        String tid = CommonUtils.bytesToHex(Arrays.copyOfRange(command, 6 + command[5], command.length - 2));
        String epc = CommonUtils.bytesToHex(Arrays.copyOfRange(command, 8, 6 + command[5]));
        Log.i(TAG, tid);
        Tag tag = cache.get(epc);
        if (tag != null && tag.tid.isEmpty()) {
            cache.get(epc).tid = tid;
            cache.get(epc).status = TagStatus.UPLOADING;
            upload(tid, epc);
        }
    }

    private void upload(String tid, final String epc) {
        final MessageUpload message = new MessageUpload();
        message.addBucket(tid, epc);
        cache.get(epc).status = TagStatus.UPLOADING;
        NetHelper.getInstance().uploadTask(message)
                .enqueue(new Callback<Reply>() {
                    @Override
                    public void onResponse(@NonNull Call<Reply> call, @NonNull Response<Reply> response) {
                        Reply body = response.body();
                        if (!response.isSuccessful() || body == null || body.getCode() != 200) {
                            cache.get(epc).status = TagStatus.NONE;
                        }
                        else {
                            cache.get(epc).status = TagStatus.UPLOADED;
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Reply> call, @NonNull Throwable t) {
                        cache.get(epc).status = TagStatus.NONE;
                    }
                });
    }

    private enum TagStatus {
        NONE, UPLOADING, UPLOADED
    }

    private class Tag {

        String tid;
        String epc;
        long time;
        TagStatus status;

        Tag(String epc) {
            this.tid = "";
            this.epc = epc;
            this.time = System.currentTimeMillis();
        }
    }

    class LifecycleCheckTask implements Runnable {

        @Override
        public void run() {
            synchronized (TagCache.this) {
                long lifecycle = ConfigHelper.getIntegerParam(MyParams.S_TAG_LIFECYCLE) * 60 * 1000;
                Iterator<Map.Entry<String, Tag>> it = cache.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Tag> item = it.next();
//                    if (item.getValue().status == TagStatus.NONE &&
//                            System.currentTimeMillis() - item.getValue().time > 3000) {
//                        upload(item.getValue().tid, item.getKey());
//                    }
                    if (System.currentTimeMillis() - item.getValue().time > lifecycle) {
                        Log.i(TAG, "Remove tag:" + item.getKey());
                        it.remove();
                    }
                }
            }
        }
    }
}
