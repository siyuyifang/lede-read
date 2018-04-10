package com.lede.blockchain.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.lede.blockchain.constant.Rcode;
import com.lede.blockchain.service.ChaincodeService;
import com.lede.blockchain.service.biz.BizUserService;
import com.lede.blockchain.util.R;
import com.lede.blockchain.util.SignUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author bjyiguoqiang on 2018/4/4.
 */
@RestController
public class UserController extends BaseController {

    @Resource
    private SignUtil signUtil;

    @Resource
    private ChaincodeService chaincodeService;

    @Resource
    private BizUserService bizUserService;

    /**
     * 登录
     * <p>
     * cellphone 手机号
     * password  加密密码
     *
     * @param sign 签名
     * @return 是否登录成功
     */
    @PostMapping("/login")
    public Map<String, Object> login(HttpServletRequest request) {
        String cellphone = request.getParameter("cellphone");
        String password = request.getParameter("password");
        String sign = request.getParameter("sign");

        if (Strings.isNullOrEmpty(cellphone) || Strings.isNullOrEmpty(password) || Strings.isNullOrEmpty(sign)) {
            return R.other(Rcode.BIZ_PARAM_INVALID);
        }
        cellphone = cellphone.trim();
        password = password.trim();

        //验证签名是否正确
        List<String> paramValue = Lists.newArrayList();
        paramValue.add(cellphone);
        paramValue.add(password);
        boolean isValid = signUtil.validSign(paramValue, sign);
        if (!isValid) {
            return R.other(Rcode.INVALID_SIGN);
        }

        //登录验证获取token
        Map<String, Object> result = bizUserService.login(cellphone, password);

        return result;
    }

    /**
     * 注册,注册成功后直接跳转首页，无需再登录
     * <p>
     * cellphone 手机号
     * password  加密密码
     * username  用户名
     * sign      签名
     *
     * @return 是否注册成功token
     */
    @PostMapping("/register")
    public Map<String, Object> register(HttpServletRequest request) {

        String cellphone = request.getParameter("cellphone");
        String password = request.getParameter("password");
        String username = request.getParameter("username");
        String sign = request.getParameter("sign");
        System.out.println(cellphone + password + username + sign);
        if (Strings.isNullOrEmpty(cellphone) || Strings.isNullOrEmpty(password)
                || Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(sign)) {
            return R.other(Rcode.BIZ_PARAM_INVALID);
        }
        cellphone = cellphone.trim();
        password = password.trim();
        username = username.trim();

        //验证签名是否正确
        List<String> paramValue = Lists.newArrayList();
        paramValue.add(cellphone);
        paramValue.add(password);
        paramValue.add(username);
        boolean isValid = signUtil.validSign(paramValue, sign);
        if (!isValid) {
            return R.other(Rcode.INVALID_SIGN);
        }

        //注册获取token,若首次注册成功则直接进入首页
        Map<String, Object> resultMap = chaincodeService.enrollAndRegisterBiz(cellphone, username, password);
        return resultMap;
    }
}
