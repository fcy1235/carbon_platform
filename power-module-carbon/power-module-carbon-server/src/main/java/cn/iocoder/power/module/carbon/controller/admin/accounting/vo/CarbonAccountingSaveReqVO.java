package cn.iocoder.power.module.carbon.controller.admin.accounting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 碳排放核算创建/修改 Request VO")
@Data
public class CarbonAccountingSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "用户信息编号", example = "1024")
    private Long carbonUserInfoId;

    @Schema(description = "核算周期开始日期", example = "2026-01-01")
    private LocalDate accountingPeriodStart;

    @Schema(description = "核算周期结束日期", example = "2026-01-31")
    private LocalDate accountingPeriodEnd;

    @Schema(description = "供暖面积", example = "90")
    private BigDecimal heatingArea;

    @Schema(description = "基准线排放强度", example = "0.45")
    private BigDecimal baselineIntensity;

    @Schema(description = "基准线排放量", example = "40.5")
    private BigDecimal baselineEmission;

    @Schema(description = "活动数据列表")
    @Valid
    private List<CarbonAccountingActivitySaveReqVO> activities;
}
