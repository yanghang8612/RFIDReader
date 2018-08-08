package com.casc.rfidreader.message;

public class BatteryStatusMessage {

    public boolean isCharging;

    public float batteryPct;

    public BatteryStatusMessage(boolean isCharging, float batteryPct) {
        this.isCharging = isCharging;
        this.batteryPct = batteryPct;
    }
}
