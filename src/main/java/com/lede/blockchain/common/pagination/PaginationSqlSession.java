package com.lede.blockchain.common.pagination;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.dao.support.PersistenceExceptionTranslator;

import java.util.List;
import java.util.Map;

/**
 * @author bjyiguoqiang
 */
public class PaginationSqlSession extends SqlSessionTemplate {

    public PaginationSqlSession(SqlSessionFactory sqlSessionFactory, ExecutorType executorType,
                                PersistenceExceptionTranslator exceptionTranslator) {
        super(sqlSessionFactory, executorType, exceptionTranslator);
    }

    public PaginationSqlSession(SqlSessionFactory sqlSessionFactory, ExecutorType executorType) {
        super(sqlSessionFactory, executorType);
    }

    public PaginationSqlSession(SqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }

    @SuppressWarnings("unchecked")
    public PaginationList selectPaginationList(String statement, Object parameter, PaginationInfo paginationInfo) {
        PaginationList paginationList = new PaginationList();

        if (parameter == null) {
            throw new RuntimeException("parameter can not be null");
        }
        if (parameter instanceof Map<?, ?>) {
            ((Map) parameter).put("paginationInfo", paginationInfo);
        }
        List result = super.selectList(statement, parameter);

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
