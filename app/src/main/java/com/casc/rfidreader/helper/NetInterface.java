package com.casc.rfidreader.helper;

import com.casc.rfidreader.helper.param.Reply;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface NetInterface {

    @GET
    Call<Reply> userLogin(@Url String url, @Query("username") String username, @Query("password") String password);

    @GET
    Call<Reply> sendHeartbeat(@Url String url);

    @POST
    Call<Reply> uploadTask(@Url String url, @Body RequestBody body);

    @GET
    Call<Reply> queryTask(@Url String url, @QueryMap Map<String, String> map);

    @GET
    Call<Reply> queryDetail(@Url String url, @Query("username") String username, @Query("taskid") String taskid);

    @GET
    Call<Reply> requestTaskID(@Url String url, @Query("username") String username);
}
