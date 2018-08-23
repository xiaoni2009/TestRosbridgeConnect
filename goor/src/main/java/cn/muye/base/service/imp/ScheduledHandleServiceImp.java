package cn.muye.base.service.imp;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.bean.HeartCheck;
import cn.muye.base.bean.RosHandlerImp;
import cn.muye.base.bean.TopicHandleInfo;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.ScheduledHandleService;
import cn.muye.version.bean.MyRos;
import cn.muye.version.bean.MyTopic;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.wpi.rail.jrosbridge.messages.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class ScheduledHandleServiceImp implements ScheduledHandleService, ApplicationContextAware {

    private static Logger logger = Logger.getLogger(ScheduledHandleServiceImp.class);
    private static ApplicationContext applicationContext;

//    private Ros ros;
    private MyRos ros;

    @Value("${server.mapPath}")
    private String mapPath;

    @Value(TopicConstants.TOPIC_HEARTBEAT_COMMAND)
    private String topicHeartBeatSN;

    private static final String CONFIG_FILE_PATH = "/home/robot/agent/application.properties";
    private static String serverIp;

    private String PREFIX = "http://";
    private RestTemplate restTemplate;

    private ReentrantLock robotInfoLock = new ReentrantLock();

    public ScheduledHandleServiceImp() {

    }

    static {
        Properties p = new Properties();
        try {
            if (new File(CONFIG_FILE_PATH).exists()) {
                // 如果外部配置文件存在，则加载外部的配置文件
                p.load(new FileSystemResource(CONFIG_FILE_PATH).getInputStream());
            }else {
                p.load(new ClassPathResource("application.properties").getInputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverIp = p.getProperty("goor.server.ip");
    }

    @Override
    public void rosHealthCheck() throws Exception {
//        logger.info("-->> Scheduled rosHealthCheck start");
        try {
//            ros = applicationContext.getBean(Ros.class);
            ros = applicationContext.getBean(MyRos.class);
            if (null == ros) {
                logger.error("-->> ros is not connect");
                return;
            }
            sendHeartCheck(ros);

            logger.info("心跳时间：" + CacheInfoManager.getTopicHeartCheckCache());
            logger.info("心跳时间差：" + (System.currentTimeMillis() - CacheInfoManager.getTopicHeartCheckCache()));
//            logger.info("rosHealthCheck heartTime=" + CacheInfoManager.getTopicHeartCheckCache());
            if ((System.currentTimeMillis() - CacheInfoManager.getTopicHeartCheckCache()) > TopicConstants.CHECK_HEART_TOPIC_MAX) {
                connectRosOnlyOneClient(ros);
            }
        } catch (Exception e) {
            logger.error("-->> Scheduled rosHealthCheck Exception", e);
        }
    }

    /**
     * 发送心跳
     * @param ros
     * @throws Exception
     */
    public static void sendHeartCheck(MyRos ros) throws Exception {
        MyTopic topic = TopicHandleInfo.getTopic(ros, TopicConstants.CHECK_HEART_TOPIC);
        JSONObject messageObject = new JSONObject();
        messageObject.put(TopicConstants.DATA, JSON.toJSONString(new HeartCheck(System.currentTimeMillis())));
        topic.publish(new Message(JSON.toJSONString(messageObject)));
    }

    private final static Long RECONNECT_SLEEP_TIME = 5000L;
    /**
     * 只产生一个client的连接ros方式
     * @param ros
     * @throws Exception
     */
    public static void connectRosOnlyOneClient(MyRos ros) throws Exception{
        if(ros.isConnected()) {
            logger.info("ros已连接，开始断开旧连接");
            //解注册旧连接的topic和service
            TopicHandleInfo.clearTopicService(ros);
            if(ros.disconnect()) {
                Thread.sleep(RECONNECT_SLEEP_TIME);
                logger.info("ros旧连接断连成功，已等待" + RECONNECT_SLEEP_TIME + "豪秒，开始重新连接");
                //断连成功，再重连
                connectRos(ros);
            }
            else {
                logger.info("ros旧连接断连失败，等待下次心跳超时处理，重新注册发布topic和service");
                //重新注册发布topic和service，因为上面解注册了
                TopicHandleInfo.reSubScribeAdvertiseTopicService(ros);
            }
        }
        //ros未连接则连接
        else {
            logger.info("ros未连接，开始连接");
            connectRos(ros);
        }
    }

    /**
     * 连接ros，并注册、发布topic.可能会产生多个client。
     * @throws Exception
     * @param ros
     */
    public static void connectRos(MyRos ros) throws Exception{
        if (isHostAvailable(new URI(ros.getURL()))) {
            logger.info("开始ros心跳连接->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            ros.addRosHandler(new RosHandlerImp(ros, new Date().toString()));
            boolean success = ros.connect();
            if (success) {
                logger.info("ros连接成功，清除心跳时间锚点");
                CacheInfoManager.setTopicHeartCheckCache();
            }
        }
        //                topic.subscribe(new CheckHeartSubListenerImpl());
    }

    /**
     * 判断websocket连接是否可用
     * @param uri
     * @return
     */
    private static boolean isHostAvailable(URI uri) {
        logger.info("[checkRosbridge]isHostAvailable start");
        if (uri == null) {
            return false;
        }
        Socket socket = new Socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(uri.getHost(),
                uri.getPort());
        try {
            socket.connect(inetSocketAddress);
            logger.info("[checkRosbridge]connect successful, address = " + inetSocketAddress);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[checkRosbridge]connect failed, address = " + inetSocketAddress + ", exception =  " + e);
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ScheduledHandleServiceImp.applicationContext = applicationContext;
    }
}
