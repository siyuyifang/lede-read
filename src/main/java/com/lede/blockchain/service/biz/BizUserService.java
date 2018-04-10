package com.lede.blockchain.service.biz;

import com.lede.blockchain.model.biz.BizUser;

import java.util.Map;

/**
 * @author bjyiguoqiang on 2018/4/3.
 */
public interface BizUserService {

    /**
     * 根据用户手机号获取用户信息
     *
     * @param cellphone 用户手机号
     * @return
     */
    BizUser getUserById(String cellphone);

    /**
     * 保存用户信息
     *
     * @param user 保存对象
     * @return
     */
    BizUser saveUser(BizUser user);

    /**
     * 用户登录
     *
     * @param cellphone 用户ID
     * @param password  用户密码
     * @return
     */
    Map<String, Object> login(String cellphone, String password);
}
