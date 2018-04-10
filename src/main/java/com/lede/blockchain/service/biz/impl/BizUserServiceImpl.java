package com.lede.blockchain.service.biz.impl;

import com.lede.blockchain.constant.Rcode;
import com.lede.blockchain.dao.BizUserDao;
import com.lede.blockchain.model.biz.BizUser;
import com.lede.blockchain.service.biz.BizUserService;
import com.lede.blockchain.util.MD5Util;
import com.lede.blockchain.util.R;
import com.lede.blockchain.util.TokenUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author bjyiguoqiang on 2018/4/3.
 */
@Service
public class BizUserServiceImpl implements BizUserService {
    @Resource
    BizUserDao bizUserDao;

    @Override
    public BizUser getUserById(String cellphone) {
        return bizUserDao.getUserById(cellphone);
    }

    @Override
    public BizUser saveUser(BizUser user) {
        return bizUserDao.saveUser(user);
    }

    @Override
    public Map<String, Object> login(String cellphone, String password) {
        BizUser user = bizUserDao.getUserById(cellphone);
        if (user == null) {
            return R.error(Rcode.BIZ_FAILED.getCode(), "该用户不存在");
        }

        //判断密码是否正确
        if (MD5Util.MD5toLower(MD5Util.MD5(password)).equals(user.getPassword())) {
            String jwtToken = TokenUtil.getToken(cellphone);
            return R.ok().put("token", jwtToken);
        } else {
            return R.error(Rcode.BIZ_INVALID.getCode(), "用户名或密码错误");
        }
    }
}
