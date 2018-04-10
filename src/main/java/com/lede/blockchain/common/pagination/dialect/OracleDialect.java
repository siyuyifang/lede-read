package com.lede.blockchain.common.pagination.dialect;


import org.apache.commons.lang.StringUtils;

/**
 * oracle dialect接口实现，提供生成分页和总和的sql
 *
 * @author elton_liu
 */
public class OracleDialect implements Dialect {

    protected static final String SQL_END_DELIMITER = ";";

    public static void main(String[] args) {

        OracleDialect od = new OracleDialect();
        System.out.println(od.getLimitString("select * from tb_ini", 0, 10));
        System.out.println(od.getCountString("select * from tb_ini where a > 1"));
    }

    /**
     * 是否支持物理分页
     *
     * @return
     */
    @Override
    public boolean supportsLimit() {

        return true;
    }

    /**
     * 是否支持物理分页偏移量
     *
     * @return
     */
    @Override
    public boolean supportsLimitOffset() {

        return true;
    }

    /**
     * 分页查询
     *
     * @param sql
     * @param hasOffset
     * @return
     */
    @Override
    public String getLimitString(String sql, boolean hasOffset) {

        return null;
    }

    /**
     * 分页查询
     *
     * @param sql
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public String getLimitString(String sql, int offset, int limit) {

        StringBuffer pageStr = new StringBuffer();
        pageStr.append("select * from ( select row_limit.*, rownum rownum_ from (");
        pageStr.append(this.trim(sql));
        pageStr.append(" ) row_limit where rownum <= ");
        pageStr.append(limit + offset);
        pageStr.append(" ) where rownum_ >");
        pageStr.append(offset);
        return pageStr.toString();
    }

    /**
     * 生成查询总和的sql
     *
     * @param sql
     * @return
     */
    @Override
    public String getCountString(String sql) {
        String resultSql = "select count(1) " + sql.substring(StringUtils.indexOfIgnoreCase(sql, "FROM", 0), sql.length());
        if (resultSql.toLowerCase().lastIndexOf("service by") != -1) {
            resultSql = resultSql.substring(0, resultSql.toLowerCase().lastIndexOf("service by"));
        }
        return resultSql;
    }

    private String trim(String sql) {

        sql = sql.trim();
        if (sql.endsWith(SQL_END_DELIMITER)) {
            sql = sql.substring(0, sql.length() - SQL_END_DELIMITER.length());
        }
        return sql;
    }
}
