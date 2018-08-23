package cn.muye.base.bean;

import cn.mrobot.bean.constant.TopicConstants;
import cn.muye.base.cache.CacheInfoManager;
import cn.muye.base.listener.CheckHeartSubListenerImpl;
import cn.muye.base.listener.CurrentPoseListenerImpl;
import cn.muye.base.service.imp.ScheduledHandleServiceImp;
import cn.muye.version.bean.MyRos;
import cn.muye.version.bean.MyTopic;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用文件下载bean
 * Created by enva on 2017/5/9.
 */
@Slf4j
public class TopicHandleInfo implements Serializable {

	/** 用于SubScribe订阅的topic名单
	 *
	 */
	public final static Map<String, ? extends TopicCallback> TOPIC_SUBSCRIBE_LIST = new HashMap(){
		{
			put(TopicConstants.CHECK_HEART_TOPIC, new CheckHeartSubListenerImpl());
			put(TopicConstants.CURRENT_POSE, new CurrentPoseListenerImpl());
		}
	};

	/**用于Advertise发布的topic名单
	 *
	 */
	public final static List<String> TOPIC_ADVERTISE_LIST = Arrays.asList(
			TopicConstants.CHECK_HEART_TOPIC
	);

	/**
	 * 结注册topic和Service
	 */
	public static void clearTopicService(Ros ros) throws Exception{
		topicUnAdvertise(ros);
		//TODO Service暂时还没有用到
	}

	/**
	 * 重新注册、发布topic、service
	 * @throws Exception
	 * @param ros
	 */
	public static void reSubScribeAdvertiseTopicService(MyRos ros) throws Exception{
		if(!ros.isConnected()) {
			log.info("ros未连接，不重新订阅发布topic和service");
		}
		TopicHandleInfo.topicSubScribe(ros);//TODO 业务topic subscribe,添加topic时，此处需要添加，以保证断网后能重新订阅到
		TopicHandleInfo.topicAdvertise(ros);
		//TODO service还没用到

		//订阅完成后，马上发送一条心跳消息
		ScheduledHandleServiceImp.sendHeartCheck(ros);
	}

	/**
	 * subscribe Topic
	 * @param ros
	 * @throws Exception
	 */
	public static void topicSubScribe(Ros ros) throws Exception{
		if(TOPIC_SUBSCRIBE_LIST == null || TOPIC_SUBSCRIBE_LIST.size() == 0) {
			log.info("没有需要SUBSCRIBE的Topic");
			return;
		}
		TOPIC_SUBSCRIBE_LIST.forEach((topicName, topicCallback) -> {
			MyTopic myTopic = TopicHandleInfo.getTopic(ros, topicName);
			if(myTopic.isSubscribed()) {
				myTopic.unsubscribe();
			}
			myTopic.subscribe(topicCallback);
		});
	}

	/**
	 * unSubscribe Topic
	 * @param ros
	 * @throws Exception
	 */
	public static void topicUnSubScribe(Ros ros) throws Exception{
		if(TOPIC_SUBSCRIBE_LIST == null || TOPIC_SUBSCRIBE_LIST.size() == 0) {
			log.info("没有需要UNSUBSCRIBE的Topic");
			return;
		}
		TOPIC_SUBSCRIBE_LIST.forEach((topicName, topicCallback) -> {
			MyTopic myTopic = TopicHandleInfo.getTopic(ros, topicName);
			if(myTopic.isSubscribed()) {
				myTopic.unsubscribe();
			}
		});
	}

	/**
	 * advertise topic
	 * @param ros
	 */
	public static void topicAdvertise(Ros ros) throws Exception{
		if(TOPIC_ADVERTISE_LIST == null || TOPIC_ADVERTISE_LIST.size() == 0) {
			log.info("没有需要advertise的Topic");
			return;
		}
		TOPIC_ADVERTISE_LIST.forEach(topicName -> {
			MyTopic myTopic = TopicHandleInfo.getTopic(ros, topicName);
			if(myTopic.isAdvertised()) {
				myTopic.unadvertise();
			}
			myTopic.advertise();
		});
	}

	/**
	 * unAdvertise topic
	 * @param ros
	 */
	public static void topicUnAdvertise(Ros ros) throws Exception{
		if(TOPIC_ADVERTISE_LIST == null || TOPIC_ADVERTISE_LIST.size() == 0) {
			log.info("没有需要unAdvertise的Topic");
			return;
		}
		TOPIC_ADVERTISE_LIST.forEach(topicName -> {
			MyTopic myTopic = TopicHandleInfo.getTopic(ros, topicName);
			if(myTopic.isAdvertised()) {
				myTopic.unadvertise();
			}
		});
	}

	public static boolean checkSubNameIsNeedConsumer(String message) throws Exception{
		JSONObject jsonObject = JSON.parseObject(message);
		String data = jsonObject.getString(TopicConstants.DATA);
		JSONObject jsonObjectData = JSON.parseObject(data);
		String messageName = jsonObjectData.getString(TopicConstants.SUB_NAME);
		if(CacheInfoManager.getNameSubCache(messageName)){
		    if (TopicConstants.DEBUG)
			log.info(" ====== message.toString()===" + message);
			return true;
		}
		return false;
	}

	public static boolean checkLocalSubNameNoNeedConsumer(String message) throws Exception{
		JSONObject jsonObject = JSON.parseObject(message);
		String data = jsonObject.getString(TopicConstants.DATA);
		JSONObject jsonObjectData = JSON.parseObject(data);
		String messageName = jsonObjectData.getString(TopicConstants.SUB_NAME);
		if(CacheInfoManager.getNameLSubCache(messageName)){
			return true;
		}
		return false;
	}

	public static boolean checkPubNameIsNeedConsumer(String message) throws Exception {
		JSONObject jsonObject = JSON.parseObject(message);
		String data = jsonObject.getString(TopicConstants.DATA);
		JSONObject jsonObjectData = JSON.parseObject(data);
		String messageName = jsonObjectData.getString(TopicConstants.PUB_NAME);
		if(CacheInfoManager.getNameSubCache(messageName)){
            if (TopicConstants.DEBUG)
			log.info(" ====== message.toString()===" + message);
			return true;
		}
		return false;
	}

	public static boolean checkX86MissionHeartBeatConsumer(String message) throws Exception{
		JSONObject jsonObject = JSON.parseObject(message);
		String data = jsonObject.getString(TopicConstants.DATA);
		JSONObject jsonObjectData = JSON.parseObject(data);
		String messageName = jsonObjectData.getString(TopicConstants.DIRECTION);
		if(TopicConstants.DIRECTION_PONG.equals(messageName)){
			return true;
		}
		return false;
	}

	/**
	 * 获取topic对象
	 * @param ros
	 * @param topicName
	 * @return
	 */
	public static MyTopic getTopic(Ros ros, String topicName){
		if(topicName.equals(TopicConstants.CHECK_HEART_TOPIC)){
			try {
				return SingleFactory.checkHeartTopic(ros);
			} catch (Exception e) {
				log.error("getTopic CHECK_HEART_TOPIC Object error", e);
			}
		}
		if(topicName.equals(TopicConstants.CURRENT_POSE)){
			try {
				return SingleFactory.current_pose(ros);
			} catch (Exception e) {
				log.error("getTopic CURRENT_POSE Object error", e);
			}
		}
		return null;
	}



}
