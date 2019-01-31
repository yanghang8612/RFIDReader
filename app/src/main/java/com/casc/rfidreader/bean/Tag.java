package com.casc.rfidreader.bean;

import java.util.Objects;

public class Tag {

    private byte[] epc;

    private int rssi;

    private int cnt;

    public Tag(byte[] epc) {
        this.epc = epc;
    }

    public byte[] getEPC() {
        return epc;
    }

    public int getRSSI() {
        return rssi;
    }

    public void setRSSI(int rssi) {
        this.rssi = rssi;
        addCnt();
    }

    public int getCnt() {
        return cnt;
    }

    public synchronized void addCnt() {
        this.cnt += 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(epc, tag.epc);
    }
}
