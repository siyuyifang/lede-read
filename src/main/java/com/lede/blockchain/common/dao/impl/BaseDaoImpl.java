package com.lede.blockchain.common.dao.impl;

/**
 * Created by dxyan on 2017/11/23.
 */

import com.lede.blockchain.common.pagination.PaginationInfo;
import com.lede.blockchain.common.pagination.PaginationList;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DaoSupport;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

@Component("baseDao")
public class BaseDaoImpl extends DaoSupport {

    private SqlSession sqlSession;

    private boolean externalSqlSession;

    @Autowired
    @Qualifier("sqlSessionFactory")
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        if (!(this.externalSqlSession)) {
            this.sqlSession = new SqlSessionTemplate(sqlSessionFactory);
        }
    }


    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSession = sqlSessionTemplate;
        this.externalSqlSession = true;
    }

    public SqlSession getSqlSession() {
        return this.sqlSession;
    }


    @Override
    protected void checkDaoConfig() {
        Assert.notNull(this.sqlSession,
                "Property 'sqlSessionFactory' or 'sqlSessionTemplate' are required");
    }

    public PaginationList selectPaginationList(String statement, Object parameter,
                                               PaginationInfo paginationInfo) {
        PaginationList paginationList = new PaginationList();

        if (parameter == null) {
            throw new RuntimeException("parameter can not be null");
        }
        if (parameter instanceof Map<?, ?>) {
            ((Map) parameter).put("paginationInfo", paginationInfo);
        }
        List result = this.getSqlSession().selectList(statement, parameter);

        paginationList.addAll(result);
        if (paginationInfo == null) {
            paginationInfo = new PaginationInfo();
            paginationInfo.setCurrentPage(1);
            paginationInfo.setRecordPerPage(result.size());
            paginationInfo.setTotalPage(1);
            paginationInfo.setTotalRecord(result.size());
        }
        paginationList.setPaginationInfo(paginationInfo);

        return paginationList;
    }

}
