package com.stormphoenix.graduatedesign;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.*;
import com.stormphoenix.graduatedesign.constants.Constants;
import com.stormphoenix.graduatedesign.hotpoint.HotpointMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest
public class TrajectorMapApiApplicationTests {

    public static int count = 0;
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_TRAJECTORY_ID = "trajectory_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";


    public static final String END_POINT = "https://GPS-Sample2.cn-hangzhou.ots.aliyuncs.com";
    public static final String ACCESS_KEY_ID = "LTAIfcYPIEOTQTdO";
    public static final String ACCESS_KEY_SECRET = "fzGryKL57qkathfilUa2uUPDgtiEVN";
    public static final String INSTANCE_NAME_GPS_SAMPLE = "GPS-Sample2";
    public static final String TRAJECTOR_DATA_TABLE_NAME = "user_trajectory";
    public static SyncClient client = new SyncClient(END_POINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET, INSTANCE_NAME_GPS_SAMPLE);

    @Test
    public void contextLoads() {
    }

    @Test
    public void queryByUserId() {
        // 等同于 SELECT * FROM UserHistory WHERE user_id = '10100'
        RangeRowQueryCriteria rangeRowQueryCriteria = new RangeRowQueryCriteria(TRAJECTOR_DATA_TABLE_NAME);
        // 设置起始主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKeyBuilder.addPrimaryKeyColumn(COLUMN_USER_ID, PrimaryKeyValue.fromString("032"));
        primaryKeyBuilder.addPrimaryKeyColumn(COLUMN_TRAJECTORY_ID, PrimaryKeyValue.fromLong(1228318146000L));
        primaryKeyBuilder.addPrimaryKeyColumn(COLUMN_TIMESTAMP, PrimaryKeyValue.fromLong(1228318146000L));
        rangeRowQueryCriteria.setInclusiveStartPrimaryKey(primaryKeyBuilder.build());
//         设置结束主键
        primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        primaryKeyBuilder.addPrimaryKeyColumn(COLUMN_USER_ID, PrimaryKeyValue.fromString("032"));
        primaryKeyBuilder.addPrimaryKeyColumn(COLUMN_TRAJECTORY_ID, PrimaryKeyValue.fromLong(1228318891000L));
        primaryKeyBuilder.addPrimaryKeyColumn(COLUMN_TIMESTAMP, PrimaryKeyValue.fromLong(1228318891000L));
        rangeRowQueryCriteria.setExclusiveEndPrimaryKey(primaryKeyBuilder.build());
        // 设置读取最新版本
        rangeRowQueryCriteria.setMaxVersions(1);
        // 默认读取所有的属性列
        GetRangeResponse response = client.getRange(new GetRangeRequest(rangeRowQueryCriteria));
        List<Row> rows = response.getRows();
        for (Row row : rows) {
            List<Column> columns = row.getColumn(Constants.COLUMN_NAME_LATITUDE);
            System.out.println();
        }
    }

    @Test
    public void test() {
        System.out.println("hello world");
    }

    @Test
    public void showHotpoint() {
        int threadCount = 500;
        int count = 0;
        double widthFrom = 100;
        double widthEnd = 120;
        double heightFrom = 100;
        double heightEnd = 120;
        double gridScale = 1;
        int width = (int) ((widthEnd - widthFrom) / gridScale) + 1;
        int height = (int) ((heightEnd - heightFrom) / gridScale) + 1;
        HotpointMap hotpointMap = HotpointMap.getInstance(width, height);
        // 创建 threadCount 个线程向 HotpointMap 提交数据
        while (count < threadCount) {
            new Thread(new RandomPositionTask(widthFrom, widthEnd, heightFrom, heightEnd, gridScale, hotpointMap)).start();
            count++;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                int[][] map;
                int count = 0;
                while (count < 100) {
                    map = hotpointMap.getHotpointMap();
                    for (int row = 0; row < width; row++) {
                        for (int col = 0; col < height; col++) {
                            System.out.print(map[row][col] + " ");
                        }
                        System.out.println();
                    }
                    System.out.println("--------------------------");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    count++;
                }
            }
        }).start();
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
