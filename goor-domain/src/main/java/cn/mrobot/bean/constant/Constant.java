package cn.mrobot.bean.constant;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/12/7.
 */
public class Constant {

    //CSV文件带中文的字符集,GB2312不支持繁体，所以用GBK
    public static final String CSV_CHARSET_CHINESE = "GBK";

    /**
     * 假删除带的后缀
     */
    public static final String DELETED = "_deleted";

    /**
     * 点坐标、朝向截取精度
     */
    public static final int POINT_X_Y_TH_SCALE = 4;

    /**
     * 是否需要对特殊点别名进行重命名
     */
    public static final boolean NEED_SPECIAL_POINT_ALIAS = true;
    public static final boolean NO_NEED_SPECIAL_POINT_ALIAS = false;

    //是地图同步状态
    public static final int UPLOAD_SUCCESS = 1;
    public static final int UPLOAD_FAIL = 2;

    /**
     * 发送时间有效范围
     */
    public static final long TIME_SEND_SCALE = 5 * 60 * 1000L;

    /**
     * systemStatus机器人系统状态参数
     */
    public static final int SYSTEM_STATUS_READY = 1;

    //排序索引默认值
    public static final int INDEX_ZERO = 0;

    //工控TH，默认认为有效的小数位数
    public static final int TH_SCALE = 3;
    public static final int XYZ_SCALE = 3;

    //工控固定路径导航可以开始的范围，单位mm
    public static final Long PATH_NAVIGATION_SCALE = 1000L;
    /**
     * 目标点可达范围(单位mm)，如果小于此范围，不会搜索路径，直接认为目标点可达
     */
    public static final Long SEARCH_TARGET_RANGE = 500L;

    //机器人离线清除所有锁的时长
    public static final Long ROBOT_OFFLINE_MINUTES = 10L;
    /**
     * 以哪个点来计算路径起点的常量
     * 以路径起点，还是以机器人所在位置在路径的投影
     */
    public static final int CAL_ROAD_PATH_START_PATH = 1;
    public static final int CAL_ROAD_PATH_START_SHADOW = 2;

    /**
     * MP3播放的默认重复时间间隔和重复次数
     */
    public static final int DEFAULT_MP3_INTERVAL = 10;
    public static final int DEFAULT_MP3_COUNT = 1;
    public static final int DEFAULT_MP3_UNLOAD_INTERVAL = 10;
    public static final int DEFAULT_MP3_UNLOAD_COUNT = 100;

    public static final String REQUEST = "request";

    /**
    * language与前端定义的变量名
    */
    public static final String LANG_PARAM_NAME = "mlang";

    /**
     * 未指定的站类型
     */
    public static final int STATION_TYPE_UNDEFINED = -1;

    /**
     * 加解锁重试间隔，单位s
     */
    public static final int LOCK_INTERVAL_TIME = 5;

    /**
     * 释放机器人锁的假ID，只要检测到是这个ID，就释放机器人所有的锁
     * */
    public final static Long RELEASE_ROBOT_LOCK_ID = -10086L;

    /**
     * 执行机器人到达结束点任务
     */
    public final static Long STATION_END_POINT_ID = -10010L;

    //数据库数据删除状态
    public final static Integer NORMAL = 0; //正常值
    public final static Integer DELETE = 1;  //删除

    //PathType
    public final static Integer PATH_TYPE_CLOUD = 0; //云端路径
    public final static Integer PATH_TYPE_X86 = 1;  //工控路径
    public final static Integer PATH_TYPE_RESTRICTED = 2;  //受管路径

    //默认工控路径权值
    public final static Long DEFAULT_ROAD_PATH_X86_WEIGHT = 1L;
    public final static Long DEFAULT_ELEVATOR_X86_WEIGHT = 999L;

    public static final String UTF_8 = "UTF-8";

    public static final String CONTENT = "content";

    public static final String MESSAGE_TYPE = "messageType";

    public static final String QUESTION_SYMBOL = "?";

    public static final String WITH_SYMBOL = "&";

    public static final String EQUAL_SYMBOL = "=";

    public static final String USER_ID = "userId";

    public static final String ID = "id";

    public static final String TEMP = ".temp";

    public static final String FLAGS = ".flags";

    public static final String FILE_IS_EXIST = "fileIsExist";//文件是否存在

    public static final String GOOR_SERVER = "goor-server";

    //文件上传状态
    public final static Integer FILE_OK = 0; //传输成功
    public final static Integer FILE_FAIL = 1;  //传输失败
    public final static String FILE_UPLOAD_TYPE_MAP = "maps"; //文件上传类型 ： map: 地图

    public final static String EXPORT_DIR_NAME = "export"; //导出文件夹路径
    public final static String LOG_DIR_NAME= "log"; //日志文件夹路径
    public final static String ORDER_DATA= "order"; //订单统计
    public final static String OPERATION_ORDER_DATA= "operationOrder"; //订单统计
    public static final String LOG_FILE_SUFFIX = ".csv";
    public static final String ZIP_FILE_SUFFIX = ".zip";

    public static final String AUTHORIZE_USERNAME = "web";

    public static final String AUTHORIZE_PASSWORD = "web_secret";

    public static final int ERROR_CODE_NOT_AUTHORIZED = 905; //未授权

    public static final int ERROR_CODE_NOT_LOGGED = 904; //未登录

    public static final long MANUAL_ORDER_ID = -10086; //手动调度的订单号
    public static final long POWER_ON_ORDER_ID = -10087; //开机管理的订单号

    //地图文件场景下的点和地图路径名
    public final static String MAP_FILE_PATH = "map"; // 地图
    public final static String MAP_SYNCED_FILE_PATH = "map_synced_file_path"; // 地图
    public final static String POINT_FILE_PATH = "point"; // 导航目标点
    public final static String ROS_MAP_FILE_SUFFIX = ".pgm"; // 导航目标点

    public final static String SCENE_MAP_NAME = "sceneMapName"; // 场景地图名
    public final static String FAKE_CLOUD_SCENE_NAME = "FakeCloudScene"; // 假云端场景名

    public final static String AES_KEY = "trOmVF7Uaqd4h9AN"; //自助注册AES校验码

    public final static String ROBOT_CACHE_KEY = "robotInfo"; //自动注册机器人缓存KEY

    public final static String MISSION_LIST_TYPE_PATROL = "patrol"; //巡逻任务-美亚
    public final static String MISSION_LIST_TYPE_CHARGE = "charge"; //充电任务-美亚
    public final static String MISSION_LIST_TYPE_NORMAL = "normal"; //普通任务——任务管理器

    public final static String ORDER_NAV_TYPE_PATH = "path";//下单优先执行的导航类型-固定路径导航

    public final static String PATH = "path";//下单优先执行的导航类型-固定路径导航
    public final static String STATION = "station";//站点别名必须包含的字段
    public final static String ELEVATOR_WAIT = "elevator_wait";//电梯等待点别名必须包含的字段
    public final static String ELEVATOR = "elevator";//电梯PAD消息通知
    public final static String ELEVATOR_NOTICE = "elevatorNotice";//电梯PAD消息通知
    public final static String DOOR_WAIT = "door_wait";//门等待点别名必须包含的字段
    public final static String LOAD = "load";//装货点别名必须包含的字段
    public final static String CHARGE = "charge";//充电点别名必须包含的字段
    public final static String UNLOAD = "unload";//中间卸货点别名必须包含的字段
    public final static String FINAL_UNLOAD = "final_unload";//最终卸货点别名必须包含的字段
    public final static String STATION_END = "station_end";//站结束点别名必须包含的字段

    public final static Long CHECK_IF_OFFLINE_TIME = 15000L; //15秒（rabbitMQ的connection-timeout时间为5秒，断网重连的时间为10秒，所以设置15秒）

    public final static Long LEAVE_CHARGER_DELAY_TIME = 60000L; //1分钟=60000毫秒

    //与地图点相关的指令列表
    public final static List<Long> ORDER_MAP_POINT_RELATE_LIST = Arrays.asList(
            1L,//单点导航
            4L,//自动充电
            5L//离开充电桩
    );

    /*
     *用于校验普通员工工号的特殊任务ID，该ID说明校验员工不需要区分站去校验
     */
    public final static long SPECIAL_CHECK_NORMAL_EMPLOYEE_MISSION_ITEM_ID = -1L;

    //MissionItemTask列表用于取货校验员工工号
    public final static List<String> MISSION_ITEM_TASK_NOT_CONCERN_STATION_NAMES_FOR_EMP_NUMBER = Arrays.asList(
            "load",
            "loadNoShelf",
            "unload",
            "finalUnload"
    );

    public final static String MISSION_ITEM_TASK_CONCERN_STATION_NAMES_FOR_EMP_NUMBER = "unload";

    public final static String ROBOT_BATTERY_THRESHOLD = "local.robot.batteryThreshold"; //机器人电量阈值配置的Key

    public final static int ROBOT_LOW_BATTERY_THRESHOLD_DEFAULT = 50; //机器人低电量阈值默认值

    public final static int ROBOT_SUFFICIENT_BATTERY_THRESHOLD_DEFAULT = 80; //机器人足电量阈值默认值

    public final static long ORDER_GOTO_CHARGE_ID = 4L;//自动回充任务ID

    public final static long ORDER_LEAVE_CHARGE_ID = 5L;//离开充电桩任务ID

    public final static long ORDER_WAIT_ID = 6L;//等待任务ID

    public final static long ORDER_TIME_CHARGE_ID = 7L;//带时间长度的充电任务ID

    public final static long ORDER_LASER_NAVIGATION = 1L;//单点导航任务ID

    public final static String ORDER_TIME_CHARGE_POINT = "point"; // 导航目标点

    public final static String ROBOT_AUTO_REGISTER_PREFIX = "robot_auto_register_";

    public final static String START = "goor_domain_src_main_java_cn_mrobot_bean_constant_Constant_java_KS";//开始
    public final static String STOP = "goor_domain_src_main_java_cn_mrobot_bean_constant_Constant_java_JS";//结束

    //Session 中存储的场景（Scene 场景常量标识）
    public static final String SCENE_SESSION_TAG_PC = "scene_session_information_pc";

    //Session 中存储的场景（Scene 场景常量标识）
    public static final String SCENE_SESSION_TAG_PAD = "scene_session_information_pad";

    public static final String SESSION_STATION_ID = "stationId";

    //添加初始密码 默认
    public static final String PRIMARY_PWD = "1055";

    //默认机器人类型
    public static final Integer ROBOT_PRIMARY_TYPE_ID = 1;

    public static final String RECORD_SCENE_SOURCE_PAD = "pad"; //保存场景ID的来源是pad

    public static final String RECORD_SCENE_SOURCE_PC = "pc"; //保存场景ID的来源是pc

    public static final Integer EMPLOYEE_TYPE_ELEVATOR_ADMIN = 1; //电梯管理员
    public static final Integer EMPLOYEE_TYPE_NORMAL = 0; //普通员工

    public static final String ONLINE = "online"; //在线

    public static final Integer USE_CALCULATE_ACTION_TIME = 10;

    public static final String IS_BIND_ELEVATOR_STATION_FLAG = "isBindElevatorStationFlag";

    //语言
    public static final String DEFAULT_LANG = "zh";
    public static final String DEFAULT_COUNTRY = "CN";
    //存放所有场景的缓存的KEY
    public static final String SCENE_LIST = "sceneList";
    //存放所有机器人的缓存的KEY
    public static final String ROBOT_LIST = "robotList";
    public final static Integer SCENE_ACTIVATED = 1;  //场景启用
    public final static Integer SCENE_NOT_ACTIVATED = 0;  //场景禁用
    //存放所有站的缓存的KEY
    public static final String STATION_LIST = "stationList";
    //常用器械缓存的KEY
    public static final String APPLIANCE = "appliance";
    //常用手术类型缓存的KEY
    public static final String OPERATION_TYPE = "operationType";

    public static final Integer MESSAGE_READ_OR_PLAYED = 1; //消息已读或者语音消息已播放
    public static final Integer MESSAGE_UNREAD_OR_NOT_PLAYED = 0; //消息未读或者语音消息未播放

    public static final String NEED_RETURN = "1";
    public static final String NOT_NEED_RETURN = "0";
}
