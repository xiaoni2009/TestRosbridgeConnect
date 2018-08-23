package cn.muye.base.config;

import cn.muye.base.bean.RosHandlerImp;
import cn.muye.base.service.imp.ScheduledHandleServiceImp;
import cn.muye.version.bean.MyRos;
import cn.muye.version.service.GetService;
import cn.muye.version.service.SetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

/**
 * Created by enva on 2017/6/28.
 */
@Configuration
@Slf4j
//@AutoConfigureAfter(RabbitTemplate.class)
public class RosConfig {

    @Value("${ros.path}")
    private String rosPath;

    @Autowired
    private MyRos ros;

    @Bean
//    public Ros ros() {
    public MyRos ros() {
//        Ros ros = new Ros(rosPath);
        //TODO 为防止复写的MyRos有问题，暂时先不使用。MyRos只是增加了service失败消息的处理。
        MyRos ros = new MyRos(rosPath);
        try {
            ros.addRosHandler(new RosHandlerImp(ros, new Date().toString()));
            ScheduledHandleServiceImp.connectRosOnlyOneClient(ros);
        } catch (Exception e) {
            log.error("rosConfig get x86_mission_dispatch error", e);
        }
        return ros;
    }

    @Bean
    public GetService getService() {
        GetService getService = new GetService(this.ros, "/rosapi/get_param", "rosapi/GetParam");
        return getService;
    }

    @Bean
    public SetService setService() {
        SetService setService = new SetService(this.ros, "/rosapi/set_param", "rosapi/SetParam");
        return setService;
    }
}
