package cn.iocoder.power.module.carbon.dal.mysql;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 项目 + 联系人 联表查询结果 DTO
 */
@Data
public class CarbonProjectWithContactDTO {

    // 项目字段
    private Long id;
    private String projectCode;
    private String projectName;
    private Long contactId;
    private String projectDesc;
    private Long provinceCode;
    private Long cityCode;
    private Long districtCode;
    private LocalDate planStartDate;
    private LocalDate planEndDate;
    private String projectCycle;
    private String projectStatus;
    private BigDecimal totalReduction;
    private LocalDateTime createTime;

    // 联系人字段
    private String contactName;
    private String contactPhone;
}
