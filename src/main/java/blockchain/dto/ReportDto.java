package blockchain.dto;

import blockchain.model.biz.Report;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author bjyiguoqiang on 2018/4/2.
 *         <p>
 *         上期奖金统计信息
 */
public class ReportDto {

    /**
     * 分配原则描述
     */
    String description;

    /**
     * 投注单位金额
     */
    BigDecimal unitMoney;

    /**
     * 上期参与人数
     */
    Integer count;

    /**
     * 总奖金
     */
    BigDecimal total;

    /**
     * 奖金分配详情
     */
    List<Report> reports = Lists.newArrayList();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getUnitMoney() {
        return unitMoney;
    }

    public void setUnitMoney(BigDecimal unitMoney) {
        this.unitMoney = unitMoney;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }
}
