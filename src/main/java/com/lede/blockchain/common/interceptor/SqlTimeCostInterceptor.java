package com.lede.blockchain.common.interceptor;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}), @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class SqlTimeCostInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger("sqlTimeCostLogger");

    /**
     * sql执行时间阈值，大于改值的会被记录日志，单位为ms，默认为0
     */
    private long thresholdInMs = 0;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = null;
        if ("query".equals(invocation.getMethod().getName()) || "update".equals(invocation.getMethod().getName())) {
            MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
            String statId = ms.getId();
            String sql = ms.getBoundSql(invocation.getArgs()[1]).getSql();
            long startTime = System.currentTimeMillis();
            long timeCost = 0;
            try {
                result = invocation.proceed();
            } finally {
                timeCost = System.currentTimeMillis() - startTime;
            }

            if (timeCost >= this.thresholdInMs) {
                /**
                 * 将sql执行时间归类，方便直接grep查询，并且去掉sql中的换行
                 */
                logger.info("TimeCost[" + String.valueOf(timeCost).length() + "]" + "statement id : " + statId + ", time cost : " + timeCost + ", raw sql : " + sql.replaceAll("\n", ""));
            }
        } else {
            result = invocation.proceed();
        }

        return result;
    }

    @Override
    public Object plugin(Object arg0) {
        return Plugin.wrap(arg0, this);
    }


    @Override
    public void setProperties(Properties arg0) {
    }

    public long getThresholdInMs() {
        return thresholdInMs;
    }

    public void setThresholdInMs(long thresholdInMs) {
        this.thresholdInMs = thresholdInMs;
    }
}
