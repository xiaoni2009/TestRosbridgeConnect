package cn.muye.version.bean;

import edu.wpi.rail.jrosbridge.JRosbridge;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Created by chay on 2018/8/10.
 * 该类与原来的类完全一样，只是增加了一些打印日志
 */
@Slf4j
public class MyTopic extends Topic{
    private Logger logger = Logger.getLogger(MyTopic.class);

    private final Ros ros;
    private final String name;
    private final String type;
    private boolean isAdvertised;
    private boolean isSubscribed;
    private final JRosbridge.CompressionType compression;
    private final int throttleRate;
    private final ArrayList<TopicCallback> callbacks;
    private final ArrayList<String> ids;

    public MyTopic(Ros ros, String name, String type) {
        this(ros, name, type, JRosbridge.CompressionType.none, 0);
    }

    public MyTopic(Ros ros, String name, String type, JRosbridge.CompressionType compression) {
        this(ros, name, type, compression, 0);
    }

    public MyTopic(Ros ros, String name, String type, int throttleRate) {
        this(ros, name, type, JRosbridge.CompressionType.none, throttleRate);
    }

    public MyTopic(Ros ros, String name, String type, JRosbridge.CompressionType compression, int throttleRate) {
        super(ros, name, type, compression, throttleRate);
        this.ros = ros;
        this.name = name;
        this.type = type;
        this.isAdvertised = false;
        this.isSubscribed = false;
        this.compression = compression;
        this.throttleRate = throttleRate;
        this.callbacks = new ArrayList();
        this.ids = new ArrayList();
    }

    @Override
    public Ros getRos() {
        return this.ros;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public boolean isAdvertised() {
        return this.isAdvertised;
    }

    @Override
    public boolean isSubscribed() {
        return this.isSubscribed;
    }

    @Override
    public JRosbridge.CompressionType getCompression() {
        return this.compression;
    }

    @Override
    public int getThrottleRate() {
        return this.throttleRate;
    }

    @Override
    public void subscribe(TopicCallback cb) {
        logger.info("subscribe topic start");
        this.ros.registerTopicCallback(this.name, cb);
        this.callbacks.add(cb);
        String subscribeId = "subscribe:" + this.name + ":" + this.ros.nextId();
        this.ids.add(subscribeId);
        JsonObject call = Json.createObjectBuilder().add("op", "subscribe").add("id", subscribeId).add("type", this.type).add("topic", this.name).add("compression", this.compression.toString()).add("throttle_rate", this.throttleRate).build();
        boolean isSuccess = this.ros.send(call);
        logger.info("unsubscribe id: " + subscribeId + " send isSuccess: " + isSuccess);
        this.isSubscribed = true;
    }

    @Override
    public void unsubscribe() {
        logger.info("unsubscribe topic start");
        Iterator i$ = this.callbacks.iterator();

        while(i$.hasNext()) {
            TopicCallback cb = (TopicCallback)i$.next();
            this.ros.deregisterTopicCallback(this.name, cb);
        }

        this.callbacks.clear();
        i$ = this.ids.iterator();

        while(i$.hasNext()) {
            String id = (String)i$.next();
            JsonObject call = Json.createObjectBuilder().add("op", "unsubscribe").add("id", id).add("topic", this.name).build();
            boolean isSuccess = this.ros.send(call);
            logger.info("unsubscribe id: " + id + " send isSuccess: " + isSuccess);
        }

        this.isSubscribed = false;
    }

    @Override
    public void advertise() {
        String advertiseId = "advertise:" + this.name + ":" + this.ros.nextId();
        logger.info("advertise " + advertiseId + " start");
        JsonObject call = Json.createObjectBuilder().add("op", "advertise").add("id", advertiseId).add("type", this.type).add("topic", this.name).build();
        boolean isSuccess = this.ros.send(call);
        logger.info("advertise id: " + advertiseId + " send isSuccess: " + isSuccess);
        this.isAdvertised = true;
    }

    @Override
    public void unadvertise() {
        String unadvertiseId = "unadvertise:" + this.name + ":" + this.ros.nextId();
        logger.info("unadvertise " + unadvertiseId + " start");
        JsonObject call = Json.createObjectBuilder().add("op", "unadvertise").add("id", unadvertiseId).add("topic", this.name).build();
        boolean isSuccess = this.ros.send(call);
        logger.info("unadvertise id: " + unadvertiseId + " send isSuccess: " + isSuccess);
        this.isAdvertised = false;
    }

    @Override
    public void publish(Message message) {
        if(!this.isAdvertised()) {
            this.advertise();
        }

        String publishId = "publish:" + this.name + ":" + this.ros.nextId();
        JsonObject call = Json.createObjectBuilder().add("op", "publish").add("id", publishId).add("topic", this.name).add("msg", message.toJsonObject()).build();
        this.ros.send(call);
    }
}
