package com.lede.blockchain.model.biz;

import java.io.Serializable;

/**
 * @author bjyiguoqiang on 2018/3/30.
 *         <p>
 *         文章信息
 */
public class Article implements Serializable {

    /**
     * 文章ID
     */
    private String articleId;

    /**
     * 所属参与期数，如 201804
     */
    private String period;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 文章地址
     */
    private String articleUrl;

    /**
     * 作者姓名
     */
    private String username;

    /**
     * 作者ID
     */
    private String cellphone;

    /**
     * 获得评论总星数
     */
    private Integer stars = 0;

    /**
     * 文章提交时间
     */
    private Long ctime;

    /**
     * 文章修改时间
     */
    private Long utime;

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public Long getUtime() {
        return utime;
    }

    public void setUtime(Long utime) {
        this.utime = utime;
    }
}
