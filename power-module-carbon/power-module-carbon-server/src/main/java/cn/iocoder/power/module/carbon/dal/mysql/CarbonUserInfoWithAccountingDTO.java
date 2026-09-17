package cn.iocoder.power.module.carbon.dal.mysql;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 碳核算 + 用户信息 联表查询结果 DTO（碳核算为主表，一行对应一条碳核算记录）
 * id 为碳核算表主键；carbonUserInfoId 为碳核算关联的用户编号（即用户表主键）。
 * 与 CarbonAccountingMapper 保持一致的同名列 select 写法（避免 selectAs 别名映射失效）
 */
@Data
public class CarbonUserInfoWithAccountingDTO {

    // 碳核算字段（碳核算为主表，核算记录必然存在）
    private Long id;  // 碳核算表主键
    private Long carbonUserInfoId;  // 碳核算关联的用户编号（用户表主键）
    private LocalDate accountingPeriodStart;
    private LocalDate accountingPeriodEnd;
    private BigDecimal actualEmission;
    private BigDecimal reduction;

    // 用户信息字段（LEFT JOIN 关联的用户表；用户行可能为空）
    private String username;
    private String userCode;
    private String idCard;
    private String phone;
    private Long provinceCode;
    private Long cityCode;
    private Long districtCode;
    private Long townCode;
    private Long villageCode;
    private String address;
    private BigDecimal heatingArea;
    private String reformType;
    private String reformMode;
    private String dataSource;
    private String electricityId;
    private String gasId;
    private String reformYear;
    private String reformBatch;
    private String subsidyMethod;
    private String houseUsage;
    private String userCategory;
    private String gasUserCode;
    private String useStatus;
    private String remark;
    private LocalDateTime createTime;
}
