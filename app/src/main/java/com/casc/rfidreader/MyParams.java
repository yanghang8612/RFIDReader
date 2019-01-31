package com.casc.rfidreader;

import java.util.HashMap;
import java.util.Map;

public class MyParams {

    private MyParams() {
    }

    /**
     * Global Parameters
     */
    public static final String API_VERSION = "1.4";

    /**
     * Setting Parameters
     */
    // APP参数配置
    public static final int NET_CONNECT_TIMEOUT = 3000; // ms
    public static final int NET_RW_TIMEOUT = 3000; // ms
    // 运维人员配置
    public static final String S_TID_SWITCH = "tid_switch";
    public static final String S_SENSOR_SWITCH = "sensor_switch";
    public static final String S_POWER = "power"; // 发射功率
    public static final String S_Q_VALUE = "q_value"; // Q值
    public static final String S_TAG_LIFECYCLE = "tag_lifecycle"; // 标签生命周期
    public static final String S_REST = "rest"; // 占空时间
    public static final String S_INTERVAL = "interval"; // 轮询指令发送间隔
    public static final String S_TIME = "time"; // 单次任务轮询指令发送次数
    public static final String S_MAIN_PLATFORM_ADDR = "main_platform_addr"; // 主平台软件地址
    public static final String S_STANDBY_PLATFORM_ADDR = "standby_platform_addr"; // 备用平台软件地址
    public static final String S_USERNAME = "username";
    public static final String S_TASKID = "taskid";
    public static final Map<String, String> CONFIG_DEFAULT_MAP = new HashMap<>();
    static {
        CONFIG_DEFAULT_MAP.put(S_TID_SWITCH, "false");
        CONFIG_DEFAULT_MAP.put(S_SENSOR_SWITCH, "false");
        CONFIG_DEFAULT_MAP.put(S_POWER, "15dBm");
        CONFIG_DEFAULT_MAP.put(S_Q_VALUE, "0");
        CONFIG_DEFAULT_MAP.put(S_TAG_LIFECYCLE, "5Min");
        CONFIG_DEFAULT_MAP.put(S_REST, "50ms");
        CONFIG_DEFAULT_MAP.put(S_INTERVAL, "10ms");
        CONFIG_DEFAULT_MAP.put(S_TIME, "20");
        CONFIG_DEFAULT_MAP.put(S_MAIN_PLATFORM_ADDR, "http://59.252.100.114");
        CONFIG_DEFAULT_MAP.put(S_STANDBY_PLATFORM_ADDR, "http://106.37.201.142:8888");
        CONFIG_DEFAULT_MAP.put(S_USERNAME, "ShaBi");
        CONFIG_DEFAULT_MAP.put(S_TASKID, "0");
    }

    public static final boolean PRINT_COMMAND = true;
    public static final boolean PRINT_JSON = true;
}
