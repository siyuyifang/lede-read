package blockchain.controller;

import blockchain.filter.JwtFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author bjyiguoqiang on 2018/4/3.
 */
public class BaseController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 检查用户是否登录
     *
     * @return
     */
    protected boolean invalidToken() {
        String uname = JwtFilter.uname;
        if (Objects.isNull(uname)) {
            return true;
        }
        return false;
    }
}
