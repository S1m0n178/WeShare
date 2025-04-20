package com.weshare.component;
import com.weshare.entity.constants.Constants;
import com.weshare.redis.RedisUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

@Component
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;
    public String saveCheckCode(String code){
        String checkCodeKey = UUID.randomUUID().toString();
        redisUtils.setex(Constants.REDIS_KEY_CHECK_CODE+checkCodeKey,code,Constants.REDIS_KEY_EXPIRES_ONE_MINUTE*10);
        return checkCodeKey;
    }
    public String getCheckCode(String checkCodeKey){
        return (String)redisUtils.get(Constants.REDIS_KEY_CHECK_CODE+checkCodeKey);
    }
    public  void cleanCheckCode(String checkCodeKey){
        redisUtils.delete(Constants.REDIS_KEY_CHECK_CODE+checkCodeKey);
    }
}
