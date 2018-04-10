package com.lede.blockchain.service.biz;

import com.lede.blockchain.dto.ReportDto;

/**
 * @author bjyiguoqiang on 2018/4/2.
 *         <p>
 *         统计相关接口
 */
public interface ReportService {

    /**
     * 获取上期奖金统计信息
     *
     * @return 统计详情
     */
    ReportDto getReport();

}
