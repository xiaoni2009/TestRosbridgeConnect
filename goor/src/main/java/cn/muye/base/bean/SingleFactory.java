package cn.muye.base.bean;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.version.bean.MyTopic;
import edu.wpi.rail.jrosbridge.Ros;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by enva on 2017/6/8.
 */
@Component
@Slf4j
public class SingleFactory {
    private static volatile MyTopic current_pose;
    private static volatile MyTopic checkHeartTopic;

    static Lock lock_current_pose=new ReentrantLock();
    static Lock lock_checkHeartTopic=new ReentrantLock();

    public static MyTopic current_pose(Ros ros) throws Exception {
        if (current_pose == null) {
            if(null == ros){
                log.error("get current_pose ros is null error, return null");
                return current_pose;
            }
            lock_current_pose.lock();
            try {
                if (current_pose == null) {
                    current_pose = new MyTopic(ros, TopicConstants.CURRENT_POSE, TopicConstants.TOPIC_NAV_MSGS);
                    log.info("get topic current_pose="+current_pose);
                }
            }catch (Exception e){
                log.error("get current_pose error", e);
            }finally {
                lock_current_pose.unlock();
            }
        }
        return current_pose;
    }

    public static MyTopic checkHeartTopic(Ros ros) throws Exception {
        if (checkHeartTopic == null) {
            if(null == ros){
                log.error("get checkHeartTopic ros is null error, return null");
                return checkHeartTopic;
            }
            lock_checkHeartTopic.lock();
            try {
                if (checkHeartTopic == null) {
                    checkHeartTopic = new MyTopic(ros, TopicConstants.CHECK_HEART_TOPIC, TopicConstants.TOPIC_TYPE_STRING);
                    log.info("get topic checkHeartTopic="+checkHeartTopic);
                }
            }catch (Exception e){
                log.error("get checkHeartTopic error", e);
            }finally {
                lock_checkHeartTopic.unlock();
            }
        }
        return checkHeartTopic;
    }

}
