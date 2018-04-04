package blockchain.controller;

import blockchain.model.biz.EnrollUser;
import blockchain.service.biz.EnrollUserService;
import blockchain.util.DateUtil;
import blockchain.util.R;
import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author bjyiguoqiang on 2018/4/3.
 *         <p>
 *         注册登记及报名相关接口
 */
@RequestMapping("/api/period")
@RestController
public class EnrollController extends BaseController {

    @Resource
    EnrollUserService enrollUserService;

    /**
     * 本期报名
     *
     * @return
     */
    @PostMapping("/enroll")
    public Map<String, Object> enrollByPeriod(@RequestHeader String authorization) {
        logger.info("[authorization] {}", authorization);
        if (invalidToken()) {
            return R.error(HttpStatus.SC_FORBIDDEN, "invalid token");
        }
        String period = DateUtil.getCurrentPeriod();
        Map<String, Object> result = enrollUserService.enrollByPeriod(period);
        return result;

    }

    /**
     * 本期已报名列表
     *
     * @return
     */
    @PostMapping("/enroll/list")
    public Map<String, Object> enrollListByPeriod(@RequestHeader String authorization) {
        logger.info("[authorization] {}", authorization);
        if (invalidToken()) {
            return R.error(HttpStatus.SC_FORBIDDEN, "invalid token");
        }
        String period = DateUtil.getCurrentPeriod();
        Map<String, Object> result = enrollUserService.enrollListByPeriod(period);
        return result;

    }
}
