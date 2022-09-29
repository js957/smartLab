package com.ynusmartgrid.face_.constant;

/**
 *  常量
 * Created by wjs on 2021/09/25
 */
public class Constant {
    /**
     * SCHEDULER_JOB_CRON 格式: [秒] [分] [小时] [日] [月] [周] [年]
     * Seconds (秒) ：可以用数字0－59 表示；
     * Minutes(分) ：可以用数字0－59 表示；
     * Hours(时) ：可以用数字0-23表示；
     * Day-of-Month(天) ：可以用数字1-31 中的任一一个值，但要注意一些特别的月份2月份没有只能1-28，有些月份没有31；
     * Month(月) ：可以用0-11 或用字符串 “JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV and DEC” 表示；
     * Day-of-Week(*每周*)*：*可以用数字1-7表示（1 ＝ 星期日）或用字符口串“SUN, MON, TUE, WED, THU, FRI and SAT”表示；
     * “/”：为特别单位，表示为“每”如“0/10”表示每隔10分钟执行一次,“0”表示为从“0”分开始, “3/20”表示表示每隔20分钟执行一次，“3”表示从第3分钟开始执行；
     * “?”：表示每月的某一天，或第周的某一天；
     * “L”：用于每月，或每周，表示为每月的最后一天，或每个月的最后星期几如“6L”表示“每月的最后一个星期五”；
     * “W”：表示为最近工作日，如“15W”放在每月（day-of-month）字段上表示为“到本月15日最近的工作日”；
     * “#”：是用来指定“的”每月第n个工作日,例 在每周（day-of-week）这个字段中内容为”6#3” or “FRI#3” 则表示“每月第三个星期五”；
     * “*” 代表整个时间段。
     */
    public static final String SCHEDULER_JOB_CRON = "0/20 * * * * ?";

    //public static final String CHECK_MONITOR_ERROR_CRON= "0/10 * * * * ?";
    public static final String CHECK_MONITOR_ERROR_CRON= "0 0 0/1 * * ?";
    /**
     * 请求接口
     */
    public static final String GET_PERSONS_URL = "http://192.168.1.6/api/getPersons?type=1";

    public static final String POST_MONITOR_ABNORMAL_FEEDBACK = "http://192.168.1.6/api.php/api/deviceError";
    /**
     * 上报异常行为服务器的地址
     */
    public final static String REPORT_BEHAVIOR_URL = "http://192.168.1.6/api.php/api/postBehavior";

    /**
     * 获得某人最后的在哪个房间的位置信息
     */
    public final static String GET_PERSON_LOCATION_URL = "http://192.168.1.6/api/getLastTrack";

    /**
     * 图片保存路径
     */
    public static final String LOCAL_PICTURE_DIR = "/home/wangyaowei/smartLabFiles/images/";

    /**
     * 引擎地址
     */
    public static final String ENGINE_DIR = "D:\\WIN64";

    /**
     * appid
     */
    public static final String APP_ID = "9MmgnHRg9CUEXTLdFwb8MZEKZyqM3KtwtSFyiqfz3AgA";

    /**
     * sdkKey
     */
    public static final String SDK_KEY = "3rNDDC2dLDbUuwJa7MBXFVY6rp9hw28VuQt61aJG3sTt";

    public static final String GET_MONITORS_URL = "http://192.168.1.6/api.php/api/getMonitors";

    /**
     * RS开头常量为response反馈前端错误代码
     */
    public static final String RS_SUCCESS = "2000";

    public static final String RS_FIELD_INVALID_RECODE = "3000";

    public static final String RS_SYSTEM_ERROR = "4000";

    public static final String RS_UNKNOWN_ERROR= "4001";

    public static final String RS_FUNCTION_EXCEPTION = "5000";

    /**
    * 智慧实验室文件目录
    */
    public static final String SMART_LAB_LOCAL_FILE_DIR = "/usr/local/docker/smartLab/smartLabFiles/";
    // public static final String SMART_LAB_LOCAL_FILE_DIR = "D:/LibProject/smartLabFiles/";
    /**
    * 后端映射地址
    */
    public static final String SMART_LAB_LOCAL_STATIC_DIR = "/smartLab/";
}
