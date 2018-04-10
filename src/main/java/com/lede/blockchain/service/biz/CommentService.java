package com.lede.blockchain.service.biz;

import com.lede.blockchain.model.biz.Comment;

import java.util.List;

/**
 * @author bjyiguoqiang on 2018/4/2.
 *         <p>
 *         评论管理相关接口
 */
public interface CommentService {

    /**
     * 评论某篇文章笔记，注意是否已评论判断
     *
     * @param articleId 评论笔记ID
     * @param star      评论分数 （1，2，3，4，5）
     * @return 评论结果信息
     */
    String commentByArticleId(String articleId, Integer star);

    /**
     * 获取某篇笔记的评论集
     *
     * @param articleId 评论笔记ID
     * @return 评论集
     */
    List<Comment> listCommentByArticleId(String articleId);


    /**
     * 获取指定期数，评论人一共评论过的文章笔记数量
     *
     * @param period    指定期数
     * @param cellphone 指定评论用户ID
     * @return 总评论文章数量
     */
    Integer getCommentCountByUserId(String period, String cellphone);

}
