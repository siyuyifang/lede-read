package com.lede.blockchain.biz;

import com.lede.blockchain.model.biz.BizUser;
import com.lede.blockchain.service.biz.BizUserService;
import com.lede.blockchain.util.JsonUtil;
import org.junit.Test;

import javax.annotation.Resource;
import java.time.Clock;


/**
 * @author bjyiguoqiang on 2018/4/8.
 */
public class MyTest extends ApplicationTests {

    @Resource
    BizUserService userService;

    @Test
    public void testHello() {
        System.out.println("hello,world!");
        BizUser user = userService.getUserById("18513146080");
        System.out.println(JsonUtil.toJson(user));

        //保存用户注册信息
        BizUser bizUser = new BizUser("17600181942", "Zj123", "gochand", 1, Clock.systemDefaultZone().millis());
        BizUser r = userService.saveUser(bizUser);
        System.out.println(JsonUtil.toJson(r));
    }
}
