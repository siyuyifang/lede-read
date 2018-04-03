package blockchain.model.biz;

import java.io.Serializable;

/**
 * @author bjyiguoqiang on 2018/3/30.
 *         <p>
 *         业务用户信息
 */
public class BizUser implements Serializable {

    /**
     * 手机号
     */
    private String cellphone;

    /**
     * 加密 密码
     */
    private String password;

    /**
     * 姓名
     */
    private String username;

    /**
     * 状态 （0:未注册，1:已注册，9:锁定）
     */
    private Integer status;

    /**
     * 注册时间
     */
    private Long enrollTime;

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getEnrollTime() {
        return enrollTime;
    }

    public void setEnrollTime(Long enrollTime) {
        this.enrollTime = enrollTime;
    }
}
