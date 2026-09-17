package cn.iocoder.power.module.carbon.controller.admin.accounting.vo;

import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - Carbon用户信息 Response VO")
@Data
public class CarbonAccountUserInfoRespVO extends CarbonUserInfoRespVO {

    @Schema(description = "用户信息编号（id 字段为碳核算编号）", example = "1024")
    private Long carbonUserInfoId;

    @Schema(description = "基准线排放强度",  example = "0.45")
    private BigDecimal baselineIntensity;

    @Schema(description = "基准线排放量",  example = "40.5")
    private BigDecimal baselineEmission;

    @Schema(description = "实际排放量", example = "220")
    private BigDecimal actualEmission;

    @Schema(description = "减排量", example = "-179.5")
    private BigDecimal reduction;

    @Schema(description = "碳核算编号",  example = "1024")
    private Long accountingId;

    @Schema(description = "活动名称（多个用顿号隔开）", example = "电力消耗、燃气消耗")
    private String activityName;

    @Schema(description = "核算周期开始日期", example = "2023-01-01")
    private LocalDate accountingPeriodStart;

    @Schema(description = "核算周期结束日期", example = "2023-12-31")
    private LocalDate accountingPeriodEnd;

    @Schema(description = "核算周期内用电量（kWh）", example = "1200")
    private BigDecimal electricityUsage;

    @Schema(description = "核算周期内用气量（m³）", example = "300")
    private BigDecimal gasUsage;
}
