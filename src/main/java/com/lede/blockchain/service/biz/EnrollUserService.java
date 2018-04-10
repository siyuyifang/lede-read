package com.lede.blockchain.service.biz;

import java.util.Map;

/**
 * @author bjyiguoqiang on 2018/4/2.
 *         <p>
 *         报名用户表
 */
public interface EnrollUserService {

    /**
     * 当前登录用户提交本期报名。注意检查本期是否已报名
     *
     * @param period 查询期数 如 201804
     * @return 是否注册成功信息
     */
    Map<String, Object> enrollByPeriod(String period);

    /**
     * 查询当前期数已报名用户列表
     *
     * @param period 查询期数 如 201804
     * @return 已报名用户列表
     */
    Map<String, Object> enrollListByPeriod(String period);
}
