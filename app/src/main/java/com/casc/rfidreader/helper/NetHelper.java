package com.casc.rfidreader.helper;

import android.support.annotation.NonNull;

import com.casc.rfidreader.MyParams;
import com.casc.rfidreader.helper.param.MessageUpload;
import com.casc.rfidreader.helper.param.Reply;
import com.casc.rfidreader.utils.CommonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetHelper implements Callback<Reply> {

    private final NetInterface netInterface;

    private static class SingletonHolder{
        private static final NetHelper instance = new NetHelper();
    }

    private NetHelper() {
        this.netInterface = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(SpHelper.getString(MyParams.S_MAIN_PLATFORM_ADDR))
                .client(new OkHttpClient.Builder()
                        .connectTimeout(MyParams.NET_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                        .writeTimeout(MyParams.NET_RW_TIMEOUT, TimeUnit.MILLISECONDS)
                        .readTimeout(MyParams.NET_RW_TIMEOUT, TimeUnit.MILLISECONDS)
                        .build())
                .build()
                .create(NetInterface.class);
    }

    public static NetHelper getInstance() {
        return SingletonHolder.instance;
    }

    public Call<Reply> userLogin(String username, String password) {
        return netInterface.userLogin(
                SpHelper.getString(MyParams.S_MAIN_PLATFORM_ADDR) + "/api/debugtool/login",
                username,
                password);
    }

    public Call<Reply> sendHeartbeat() {
        return netInterface.sendHeartbeat(
                SpHelper.getString(MyParams.S_MAIN_PLATFORM_ADDR) + "/api/debugtool/heartbeat");
    }

    public Call<Reply> uploadTask(MessageUpload message) {
        return netInterface.uploadTask(
                SpHelper.getString(MyParams.S_MAIN_PLATFORM_ADDR) + "/api/debugtool/debugdata",
                CommonUtils.generateRequestBody(message));
    }

    public Call<Reply> queryTask(long starttime, long endtime) {
        Map<String, String> map = new HashMap<>();
        map.put("username", SpHelper.getString(MyParams.S_USERNAME));
        map.put("starttime", String.valueOf(starttime));
        map.put("endtime", String.valueOf(endtime));
        return netInterface.queryTask(
                SpHelper.getString(MyParams.S_MAIN_PLATFORM_ADDR) + "/api/debugtool/outlinequery",
                map);
    }

    public Call<Reply> queryDetail(String taskid) {
        return netInterface.queryDetail(
                SpHelper.getString(MyParams.S_MAIN_PLATFORM_ADDR) + "/api/debugtool/detailedquery",
                SpHelper.getString(MyParams.S_USERNAME),
                taskid);
    }

    public Call<Reply> requestTaskID() {
        String username = SpHelper.getString(MyParams.S_USERNAME);
        return netInterface.requestTaskID(
                SpHelper.getString(MyParams.S_MAIN_PLATFORM_ADDR) + "/api/debugtool/taskid",
                SpHelper.getString(MyParams.S_USERNAME));
    }

    @Override
    public void onResponse(@NonNull Call<Reply> call, @NonNull Response<Reply> response) {}

    @Override
    public void onFailure(@NonNull Call<Reply> call, @NonNull Throwable t) {}
}
