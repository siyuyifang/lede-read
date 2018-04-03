package blockchain.service;

import blockchain.model.biz.EnrollUser;
import blockchain.util.JsonUtil;

/**
 * Created by bjyiguoqiang on 2018/4/3.
 */
public class Test {
    public static void main(String[] args) {
        EnrollUser user = new EnrollUser();
        user.setCellphone("18513146080");
        user.setStatus(1);
        user.setUsername("gochand");
        System.out.println(JsonUtil.toJson(user));
    }
}
