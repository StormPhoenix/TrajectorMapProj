package com.stormphoenix.graduatedesign.constants;

import com.alicloud.openservices.tablestore.SyncClient;

/**
 * Created by Developer on 18-5-10.
 */
public class Constants {
    public static final String TABLE_NAME_USER_TRAJECTOR = "user_trajectory";
    public static final String COLUMN_NAME_USER_ID = "user_id";
    public static final String COLUMN_NAME_USER_NAME = "user_name";
    public static final String COLUMN_NAME_TRAJECTORY_ID = "trajectory_id";
    public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    public static final String COLUMN_NAME_LATITUDE = "latitude";
    public static final String COLUMN_NAME_LONGITUDE = "longitude";


    public static final String END_POINT = "https://GPS-Sample2.cn-hangzhou.ots.aliyuncs.com";
    public static final String ACCESS_KEY_ID = "LTAIfcYPIEOTQTdO";
    public static final String ACCESS_KEY_SECRET = "fzGryKL57qkathfilUa2uUPDgtiEVN";
    public static final String INSTANCE_NAME_GPS_SAMPLE = "GPS-Sample2";
    public static SyncClient client = new SyncClient(END_POINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET, INSTANCE_NAME_GPS_SAMPLE);
}
