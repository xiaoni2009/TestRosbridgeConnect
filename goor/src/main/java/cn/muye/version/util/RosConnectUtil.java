package cn.muye.version.util;

import cn.mrobot.bean.constant.Constant;
import cn.muye.base.bean.RosHandlerImp;
import cn.muye.base.bean.TopicHandleInfo;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.version.bean.MyRos;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.Date;

/**
 * @author Created by chay on 2018/8/25.
 */
@Slf4j
public class RosConnectUtil {
    private final static Long RECONNECT_SLEEP_TIME = 5000L;
    /**
     * 只产生一个client的连接ros方式
     * @param ros
     * @throws Exception
     */
    public synchronized static void connectRosOnlyOneClient(MyRos ros) throws Exception{
        if(ros.isConnected()) {
            log.info("ros已连接，开始断开旧连接");
            //解注册旧连接的topic和service
            TopicHandleInfo.clearTopicService(ros);
            if(ros.disconnect()) {
                Thread.sleep(RECONNECT_SLEEP_TIME);
                log.info("ros旧连接断连成功，已等待" + RECONNECT_SLEEP_TIME + "豪秒，开始重新连接");
                //断连成功，再重连
                connectRos(ros);
            }
            else {
                log.info("ros旧连接断连失败，等待下次心跳超时处理，重新注册发布topic和service");
                //重新注册发布topic和service，因为上面解注册了
                TopicHandleInfo.reSubScribeAdvertiseTopicService(ros);
            }
        }
        //ros未连接则连接
        else {
            log.info("ros未连接，开始连接");
            connectRos(ros);
        }
    }

    /**
     * 连接ros，并注册、发布topic.可能会产生多个client。
     * @throws Exception
     * @param ros
     */
    public static void connectRos(MyRos ros) throws Exception{
        if (isHostAvailablePingFirst(new URI(ros.getURL()), Constant.NET_ROS_BRIDGE)) {
            log.info("开始ros心跳连接->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            ros.addRosHandler(new RosHandlerImp(ros, new Date().toString()));
            boolean success = ros.connect();
            if (success) {
                log.info("ros连接成功，清除心跳时间锚点");
                CacheInfoManager.setTopicHeartCheckCache();
            }
        }
    }

    /**
     * 判断连接是否可用-纯socket
     * 至于是websocket还是http，通过不同的uri前缀来区分
     * @param uri
     * @return
     */
    public static boolean isHostAvailable(URI uri , String hostLogName) throws Exception{
        log.info("[" + hostLogName + "]isHostAvailable start");
        if (uri == null) {
            return false;
        }
        Socket socket = new Socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(uri.getHost(),
                uri.getPort());
        try {
            socket.connect(inetSocketAddress);
            log.info("[" + hostLogName + "]connect successful, address = " + inetSocketAddress);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[" + hostLogName + "]connect failed, address = " + inetSocketAddress + ", exception =  " + e);
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                log.info("[" + hostLogName + "]close failed, address = " + inetSocketAddress + ", exception =  " + e);
            }
        }
    }

    /**
     * 判断连接是否可用，ping和socket同时判断
     * 至于是websocket还是http，通过不同的uri前缀来区分
     * @param uri
     * @return
     */
    public static boolean isHostAvailablePingFirst(URI uri , String hostLogName) throws Exception{
        if (PingUtil.ping(uri.getHost(), hostLogName)) {
            return isHostAvailable(uri, hostLogName);
        }
        else {
            return false;
        }
    }
}
