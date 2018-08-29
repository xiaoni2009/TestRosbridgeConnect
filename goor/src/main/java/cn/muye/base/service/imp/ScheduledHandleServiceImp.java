package cn.muye.base.service.imp;

import cn.mrobot.bean.AjaxResult;
import cn.mrobot.bean.constant.Constant;
import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.bean.HeartCheck;
import cn.muye.base.bean.RosHandlerImp;
import cn.muye.base.bean.TopicHandleInfo;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.service.ScheduledHandleService;
import cn.muye.version.bean.MyRos;
import cn.muye.version.bean.MyTopic;
import cn.muye.version.util.PingUtil;
import cn.muye.version.util.RosConnectUtil;
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
                RosConnectUtil.connectRosOnlyOneClient(ros);
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ScheduledHandleServiceImp.applicationContext = applicationContext;
    }

    @Override
    public void testPing() throws Exception{
        restTemplate = applicationContext.getBean(RestTemplate.class);
        //向服务器申请未处理的消息
        String REMOTE_URL = PREFIX + serverIp + "/services/getUnDealMessages";
        AjaxResult response = PingUtil.connectAndCheckFirstUseGet(restTemplate,
                PREFIX + serverIp,
                Constant.NET_CLOUD_SERVER,
                REMOTE_URL + "?robotCode=Noah_008",
                AjaxResult.class);
    }
}
