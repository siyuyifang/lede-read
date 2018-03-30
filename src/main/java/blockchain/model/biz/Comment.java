package blockchain.model.biz;

import java.io.Serializable;

/**
 * @author  bjyiguoqiang on 2018/3/30.
 *
 * 文章评论记录
 */
public class Comment implements Serializable{

    /**
     * 评论ID
     */
    private String CommentId;

    /**
     * 文章ID
     */
    private String articleId;

    /**
     * 评论所属参与期数，如 201804
     */
    private String period;

    /**
     * 评论人ID
     */
    private String cellphone;

    /**
     * 评论星级 （1，2，3，4，5）
     */
    private Integer star = 0;

    /**
     * 评论时间
     */
    private Long ctime;

    public String getCommentId() {
        return CommentId;
    }

    public void setCommentId(String commentId) {
        CommentId = commentId;
    }

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

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }
}
