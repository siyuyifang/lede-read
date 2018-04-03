package blockchain.service.biz.impl;

import blockchain.model.biz.BizUser;
import blockchain.service.biz.BizUserService;
import org.springframework.stereotype.Service;

/**
 * @author bjyiguoqiang on 2018/4/3.
 */
@Service
public class BizUserServiceImpl implements BizUserService {

    @Override
    public BizUser getUserByUsername(String username) {
        BizUser user = new BizUser();
        user.setCellphone("18513146080");
        return user;
    }
}
