package com.lede.blockchain.model.biz;

import java.io.Serializable;

/**
 * @author bjyiguoqiang on 2018/4/2.
 *         <p>
 *         报名用户表
 */
public class EnrollUser implements Serializable {

    /**
     * 报名ID
     */
    private String enrollId;

    /**
     * 手机号
     */
    private String cellphone;

    /**
     * 姓名
     */
    private String username;

    /**
     * 报名期数
     */
    private String period;

    /**
     * 状态 （0:未报名，1:已报名）
     */
    private Integer status = 0;

    /**
     * 报名时间
     */
    private Long enrollTime;

    public String getEnrollId() {
        return enrollId;
    }

    public void setEnrollId(String enrollId) {
        this.enrollId = enrollId;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
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
