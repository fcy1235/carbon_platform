package cn.iocoder.power.module.carbon.dal.mysql;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 碳核算 + 用户信息 + 活动 联表查询结果 DTO
 */
@Data
public class CarbonAccountingWithUserDTO {

    // 碳核算字段
    private Long id;
    private Long carbonUserInfoId;
    private LocalDate accountingPeriodStart;
    private LocalDate accountingPeriodEnd;
    private BigDecimal actualEmission;
    private BigDecimal reduction;
    private LocalDateTime createTime;

    private BigDecimal emissionFactor;
    // 用户信息字段
    private String username;
    private String address;
    private Long provinceCode;
    private Long cityCode;
    private Long districtCode;
    private BigDecimal heatingArea;
    private String reformType;
    private String userCode;
    private String dataSource;

    // 活动信息字段（用于筛选）
    private String activityNameCode;
}
