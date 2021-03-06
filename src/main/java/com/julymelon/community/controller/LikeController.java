package com.julymelon.community.controller;

import com.julymelon.community.annotation.LoginRequired;
import com.julymelon.community.entity.Event;
import com.julymelon.community.entity.User;
import com.julymelon.community.event.EventProducer;
import com.julymelon.community.service.LikeService;
import com.julymelon.community.util.CommunityConstant;
import com.julymelon.community.util.CommunityUtil;
import com.julymelon.community.util.HostHolder;
import com.julymelon.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    // 不加此注解会出现异常
    @LoginRequired
    // 处理异步请求的方法
    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        // 利用注解对登录进行检查
        User user = hostHolder.getUser();

        // 点赞：对应第一个方法
        likeService.like(user.getId(), entityType, entityId, entityUserId);

        // 数量：对应第二个方法
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 状态：对应第三个方法
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        // 返回的结果，封装到map中，因为异步请求没有model，而在定义getJSONString的时候指定了map格式
        // 异步请求都要通过js方法实现
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        // 触发点赞事件
        // 取消赞不发通知
        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }

        // 只有对帖子点赞才生效
        // 把帖子ID放入redis缓存
        if(entityType == ENTITY_TYPE_POST) {
            // 计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, postId);
        }

        // 只有返回状态码为0的状态
        return CommunityUtil.getJSONString(0, null, map);
    }

}
