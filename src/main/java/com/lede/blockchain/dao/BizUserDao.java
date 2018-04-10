package com.lede.blockchain.dao;

import com.lede.blockchain.common.pagination.PaginationInfo;
import com.lede.blockchain.common.pagination.PaginationList;
import com.lede.blockchain.model.biz.BizUser;

import java.util.Map;

/**
 * @author bjyiguoqiang on 2018/4/8.
 */
public interface BizUserDao {

    PaginationList queryUserInfoByCond(Map<String, Object> cond, PaginationInfo paginationInfo);

    BizUser getUserById(String cellphone);

    BizUser saveUser(BizUser user);
}
