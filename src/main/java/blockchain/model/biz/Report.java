package blockchain.model.biz;

import java.io.Serializable;

/**
 * @author bjyiguoqiang on 2018/4/2.
 */
public class Report implements Serializable {

    /**
     * 用户名
     */
    String username;

    /**
     * 用户ID
     */
    String cellphone;

    /**
     * 上期评论文章笔记数量
     */
    String remarks;

    /**
     * 上期文章笔记获得的评分数
     */
    String stars;

    /**
     * 上期获得奖金
     */
    String bonus;

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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getBonus() {
        return bonus;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }
}
