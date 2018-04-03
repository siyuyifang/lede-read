package blockchain.service.biz.impl;

import blockchain.Constants;
import blockchain.filter.JwtFilter;
import blockchain.model.biz.BizUser;
import blockchain.model.biz.EnrollUser;
import blockchain.service.ChaincodeService;
import blockchain.service.biz.BizUserService;
import blockchain.service.biz.EnrollUserService;
import blockchain.util.IdGenerateUtil;
import blockchain.util.JsonUtil;
import blockchain.util.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author bjyiguoqiang on 2018/4/3.
 */
@Service
public class EnrollUserServiceImpl implements EnrollUserService {

    private static final Logger logger = LoggerFactory.getLogger(EnrollUserServiceImpl.class);

    @Autowired
    ChaincodeService chaincodeService;

    @Resource
    BizUserService bizUserService;

    @Override
    public Map<String, Object> enrollByPeriod(String period) {

        String username = JwtFilter.uname;
        String result = chaincodeService.enrollAndRegister(username);
        if (result == "Failed to enroll user") {
            return R.error("User not enroll ");
        }

        BizUser bizUser = bizUserService.getUserByUsername(username);
        if (Objects.isNull(bizUser)) {
            return R.error(username + " User not found ");
        }

        //组装上链的用户报名信息
        EnrollUser enrollUser = new EnrollUser();
        enrollUser.setEnrollId(IdGenerateUtil.generateEnrollId());
        enrollUser.setUsername(username);
        enrollUser.setEnrollTime(Clock.systemDefaultZone().millis());
        enrollUser.setPeriod(period);
        enrollUser.setStatus(1);
        enrollUser.setCellphone(bizUser.getCellphone());

        String[] args = new String[2];
        args[0] = "enrollByPeriod";
        args[1] = JsonUtil.toJson(enrollUser);

        logger.info("[enrollUser] {} ", args[1]);
        Map<String, Object> dataMap = chaincodeService.invokeBizChaincode(Constants.BIZ_CHANNEL, Constants.INVOKE, args);
        return R.ok(dataMap);
    }

    @Override
    public List<EnrollUser> enrollListByPeriod(String period) {
        return null;
    }
}
