package blockchain.service.biz;

import blockchain.model.biz.Article;

import java.util.List;

/**
 * @author bjyiguoqiang on 2018/4/2.
 *         <p>
 *         文章管理相关接口
 */
public interface ArticleService {

    /**
     * 插入文章记录，注意校验插入记录是否重复（同一时期内只允许一条文章记录）
     *
     * @param articeTitle 文章标题
     * @param articleUrl  文章链接地址
     */
    Article insertArticle(String articeTitle, String articleUrl);

    /**
     * 修改文章记录
     *
     * @param articleId   文章ID
     * @param articeTitle 文章标题
     * @param articleUrl  文章链接地址
     */
    Article updateArticle(String articleId, String articeTitle, String articleUrl);

    /**
     * 查询当前登录人对应期数的文章
     *
     * @param period 待查询的期数，如 201804
     * @return 文章记录
     */
    Article queryMyArticleByPeriod(String period);

    /**
     * 获取对应期数的文章列表
     *
     * @param period 待查询的期数，如 201804
     * @return 文章列表记录
     */
    List<Article> listArticleByPeriod(String period);


}
