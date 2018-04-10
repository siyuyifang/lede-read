package com.lede.blockchain.common.pagination;

import java.util.ArrayList;


public class PaginationList<T> extends ArrayList<T> {
    private static final long serialVersionUID = -6059628280162549106L;

    private PaginationInfo paginationInfo = null;

    public PaginationInfo getPaginationInfo() {
        return paginationInfo;
    }

    public void setPaginationInfo(PaginationInfo paginationInfo) {
        this.paginationInfo = paginationInfo;
    }

    public Integer getCurrentPage() {
        return this.paginationInfo.getCurrentPage();
    }

    public Integer getRecordPerPage() {
        return this.paginationInfo.getRecordPerPage();
    }

    public Integer getTotalPage() {
        return this.paginationInfo.getTotalPage();
    }

    public Integer getTotalRecord() {
        return this.paginationInfo.getTotalRecord();
    }
}
