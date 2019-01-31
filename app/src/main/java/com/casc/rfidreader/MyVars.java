package com.casc.rfidreader;

import com.casc.rfidreader.backend.TagReader;

import java.util.concurrent.ScheduledExecutorService;

public class MyVars {

    private MyVars(){}

    private static TagReader preReader = null;

    public static TagReader usbReader = null;

    public static TagReader bleReader = null;

    public static ScheduledExecutorService executor = null;

    public static TagReader getReader() {
        if (usbReader.isConnected()) {
            if (preReader != usbReader) {
                preReader = usbReader;
                //usbReader.start();
                bleReader.stop();
            }
            return usbReader;
        } else {
            if (preReader != bleReader) {
                preReader = bleReader;
                usbReader.stop();
                //bleReader.start();
            }
            return bleReader;
        }
    }
}
