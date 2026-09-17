package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 电力用电量报表 DO
 *
 * 对应表 carbon_electricity_usage_report，按采暖季统计电代煤用户用电量
 */
@TableName("carbon_electricity_usage_report")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonElectricityUsageReportDataDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 能碳用户ID
     */
    private Long carbonUserInfoId;

    /**
     * 采暖季年份，如 2025
     */
    private String heatingSeason;

    /**
     * 核算开始表底数（采暖季起始表底数）
     */
    private BigDecimal startReading;

    /**
     * 核算结束表底数（采暖季终止表底数）
     */
    private BigDecimal endReading;

    /**
     * 合计用电量
     */
    private BigDecimal totalUsage;

    /**
     * 备注
     */
    private String remark;

}
