package com.lede.blockchain.common.pagination;

import java.io.Serializable;

public class PaginationInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2106869192311393164L;
    // default value
    private static final Integer defaultCurrentPage = 1;
    private static final Integer defaultRecordPerPage = 20;
    private static final Integer defaultTotalPage = 0;
    private static final Integer defaultTotalRecord = 0;

    /**
     * 当前页，1基址
     */
    private Integer currentPage = null;
    /**
     * 每页记录�?
     */
    private Integer recordPerPage = null;
    /**
     * 总页�?
     */
    private Integer totalPage = null;
    /**
     * 总记录数
     */
    private Integer totalRecord = null;

    public PaginationInfo() {

        this.initialize();
    }

    public PaginationInfo(Integer currentPage, Integer recordPerPage) {

        this.setCurrentPage(currentPage);
        this.setRecordPerPage(recordPerPage);
    }

    public static PaginationInfo getDefault() {

        return new PaginationInfo(defaultCurrentPage, defaultRecordPerPage);
    }

    private void initialize() {

        this.currentPage = defaultCurrentPage;
        this.recordPerPage = defaultRecordPerPage;
    }

    public int getOffset() {

        return this.getRecordPerPage() * (this.getCurrentPage() - 1);
    }

    public int getLimit() {

        return this.getRecordPerPage();
    }

    public Integer getCurrentPage() {

        return currentPage == null ? defaultCurrentPage : currentPage;
    }

    public void setCurrentPage(Integer currentPage) {

        this.currentPage = (currentPage == null || currentPage <= 0) ? defaultCurrentPage : currentPage;
    }

    public Integer getRecordPerPage() {

        return recordPerPage == null ? defaultRecordPerPage : recordPerPage;
    }

    public void setRecordPerPage(Integer recordPerPage) {

        this.recordPerPage = (recordPerPage == null || recordPerPage <= 0) ? defaultRecordPerPage : recordPerPage;
    }

    public Integer getTotalPage() {

        return totalPage == null ? defaultTotalPage : totalPage;
    }

    public void setTotalPage(Integer totalPage) {

        this.totalPage = totalPage;
    }

    public Integer getTotalRecord() {

        return totalRecord == null ? defaultTotalRecord : totalRecord;
    }

    public void setTotalRecord(Integer totalRecord) {

        this.totalRecord = totalRecord;
//		if ((this.getCurrentPage() - 1) * this.getRecordPerPage() >= this.totalRecord) {
//			this.initialize();
//		}
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append("[currentPage=");
        builder.append(currentPage);
        builder.append(", recordPerPage=");
        builder.append(recordPerPage);
        builder.append(", totalPage=");
        builder.append(totalPage);
        builder.append(", totalRecord=");
        builder.append(totalRecord);
        builder.append("]");
        return builder.toString();
    }
}
