package cn.muye.base.bean;

import cn.mrobot.bean.constant.TopicConstants;

/**
 * @author Created by chay on 2018/8/17.
 */
public class HeartCheck {
    String heart = TopicConstants.CHECK_HEART_MESSAGE;
    Long time = 0L;

    public HeartCheck(Long time) {
        this.time = time;
    }

    public String getHeart() {
        return heart;
    }

    public void setHeart(String heart) {
        this.heart = heart;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
