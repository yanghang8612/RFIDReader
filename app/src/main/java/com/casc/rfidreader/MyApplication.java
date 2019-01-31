package com.casc.rfidreader;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.casc.rfidreader.backend.impl.BLEReaderImpl;
import com.casc.rfidreader.backend.impl.USBReaderImpl;

import java.util.concurrent.Executors;

public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();

    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    private WifiManager wifiManager;

    private ConnectivityManager connectivityManager;

    @Override
    public void onCreate() {
        super.onCreate();

        // 程序崩溃捕捉并打印响应信息
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());

        // 初始化相关字段
        instance = this;
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // 初始化读写器实例（USB、蓝牙）和标签缓存
        MyVars.executor = Executors.newScheduledThreadPool(10);
        MyVars.usbReader = new USBReaderImpl(this);
        MyVars.bleReader = new BLEReaderImpl(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "App terminated");
        MyVars.executor.shutdown();
    }
}

