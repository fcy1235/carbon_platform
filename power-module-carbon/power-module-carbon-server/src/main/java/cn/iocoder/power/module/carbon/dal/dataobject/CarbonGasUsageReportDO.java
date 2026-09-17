package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 燃气数据报表（采暖季用气量统计）DO
 *
 * 对应表 carbon_gas_usage_report。
 * 用户相关字段（姓名/身份证/用户编码/表具号）不落本表，
 * 通过 carbonUserInfoId 关联 carbon_user_info，查询时回填。
 */
@TableName("carbon_gas_usage_report")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonGasUsageReportDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户编号，关联 carbon_user_info.id（导入时按燃气表具号反查）
     */
    private Long carbonUserInfoId;

    /**
     * 采暖季年份，如 2025
     */
    private String heatingSeason;

    /**
     * 采暖季起始时间 11.15 表底数
     */
    private BigDecimal startReading;

    /**
     * 采暖季终止时间 3.15 表底数
     */
    private BigDecimal endReading;

    /**
     * 采暖季合计用气量（方/m³）
     */
    private BigDecimal totalUsage;

    /**
     * 备注
     */
    private String remark;

}
