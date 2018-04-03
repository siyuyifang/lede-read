package blockchain.controller;

import blockchain.model.biz.EnrollUser;
import blockchain.service.biz.EnrollUserService;
import blockchain.util.DateUtil;
import blockchain.util.R;
import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author bjyiguoqiang on 2018/4/3.
 *         <p>
 *         注册登记及报名相关接口
 */
@RestController
public class EnrollController extends BaseController {

    @Resource
    EnrollUserService enrollUserService;

    /**
     * 本期报名
     *
     * @return
     */
    public R enrollByPeriod(@RequestHeader String authorization) {
        logger.info("[authorization] {}", authorization);
        if (invalidToken()) {
            return R.error(HttpStatus.SC_FORBIDDEN, "invalid token");
        }
        String period = DateUtil.getCurrentPeriod();
        Map<String, Object> result = enrollUserService.enrollByPeriod(period);
        return R.ok(result);

    }

    /**
     * 本期已报名列表
     *
     * @return
     */
    public R enrollListByPeriod(@RequestHeader String authorization) {
        logger.info("[authorization] {}", authorization);
        if (invalidToken()) {
            return R.error(HttpStatus.SC_FORBIDDEN, "invalid token");
        }
        String period = DateUtil.getCurrentPeriod();
        List<EnrollUser> result = enrollUserService.enrollListByPeriod(period);
        return R.ok().put("data", result);

    }
}
