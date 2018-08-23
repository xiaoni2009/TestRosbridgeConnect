package cn.muye.base.bean;

import cn.muye.version.bean.MyRos;
import edu.wpi.rail.jrosbridge.handler.RosHandler;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;

/**
 * Created by enva on 2017/8/8.
 */
@Slf4j
public class RosHandlerImp implements RosHandler {
    MyRos ros;
    String name;

    public RosHandlerImp(MyRos ros, String name) {
        this.ros = ros;
        this.name = name;
    }

    @Override
    public void handleConnection(Session session) {
        log.info(name + "----------->>>>>>>>>>>>>>handleConnection");
        try {
            TopicHandleInfo.reSubScribeAdvertiseTopicService(ros);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        } finally {
        }
    }

    @Override
    public void handleDisconnection(Session session) {
        log.info(name + "----------->>>>>>>>>>>>>>handleDisconnection");
    }

    @Override
    public void handleError(Session session, Throwable throwable) {
        log.info(name + "----------->>>>>>>>>>>>>>handleError");
    }
}
