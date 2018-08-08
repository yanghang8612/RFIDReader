package com.casc.rfidreader.bean;

import com.casc.rfidreader.utils.CommonUtils;

import java.util.Arrays;
import java.util.Objects;

public class Tag {

    private String pc;

    private String epc;

    private String crc;

    private int rssi;

    private String rssiStr;

    private String cntStr;

    private int cnt;

    private int preCnt;

    private int noneCnt;

    public Tag(byte[] data) {
        int pl = ((data[3] & 0xFF) << 8) + (data[4] & 0xFF);
        this.pc = CommonUtils.bytesToHex(Arrays.copyOfRange(data, 6, 8));
        this.epc = CommonUtils.bytesToHex(Arrays.copyOfRange(data, 8, pl + 3));
        this.crc = CommonUtils.bytesToHex(Arrays.copyOfRange(data, pl + 3, pl + 5));
    }

    public String getPc() {
        return pc;
    }

    public String getEpc() {
        return epc;
    }

    public String getCrc() {
        return crc;
    }

    public String getRssi() {
        return rssiStr;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
        this.rssiStr = String.valueOf(rssi);
    }

    public String getCnt() {
        return cntStr;
    }

    public synchronized void addCnt() {
        this.cnt += 1;
        this.cntStr = String.valueOf(this.cnt);
    }

    public void updateNoneCnt() {
        if (preCnt == cnt) {
            noneCnt += 16;
        } else {
            noneCnt = 0;
        }
        preCnt = cnt;
    }

    public int getNoneCnt() {
        return noneCnt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(epc, tag.epc);
    }
}
