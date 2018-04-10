package com.lede.blockchain.dao.impl;

import com.google.common.collect.Maps;
import com.lede.blockchain.common.dao.impl.BaseDaoImpl;
import com.lede.blockchain.common.pagination.PaginationInfo;
import com.lede.blockchain.common.pagination.PaginationList;
import com.lede.blockchain.dao.BizUserDao;
import com.lede.blockchain.model.biz.BizUser;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author bjyiguoqiang on 2018/4/8.
 */
@Repository
public class BizUserDaoImpl extends BaseDaoImpl implements BizUserDao {
    @Override
    public PaginationList queryUserInfoByCond(Map<String, Object> cond, PaginationInfo paginationInfo) {
        return this.selectPaginationList("userinfo.queryUserInfoByCond", cond, paginationInfo);
    }

    @Override
    public BizUser getUserById(String cellphone) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("cellphone", cellphone);
        return this.getSqlSession().selectOne("userinfo.getUserById", params);
    }

    @Override
    public BizUser saveUser(BizUser user) {
        int affectRows = this.getSqlSession().update("userinfo.saveUser", user);
        if (affectRows > 0) {
            return user;
        } else {
            return null;
        }
    }
}
