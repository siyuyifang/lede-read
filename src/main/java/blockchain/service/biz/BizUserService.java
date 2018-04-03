package blockchain.service.biz;

import blockchain.model.biz.BizUser;

/**
 * @author bjyiguoqiang on 2018/4/3.
 */
public interface BizUserService {

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return
     */
    BizUser getUserByUsername(String username);
}
