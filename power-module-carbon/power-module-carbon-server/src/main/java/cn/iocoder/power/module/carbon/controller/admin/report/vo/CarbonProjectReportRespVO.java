package cn.iocoder.power.module.carbon.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 项目碳减排报表 Response VO")
@Data
public class CarbonProjectReportRespVO {

    @Schema(description = "项目编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long projectId;

    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "农村改造项目1")
    private String projectName;

    @Schema(description = "联系人名称", example = "张三")
    private String contactName;

    @Schema(description = "项目描述", example = "农村改造项目1")
    private String projectDesc;

    @Schema(description = "行政区（中文）", example = "河北省石家庄市裕华区")
    private String division;

    @Schema(description = "计划开始时间", example = "2026-01-01")
    private LocalDate planStartDate;

    @Schema(description = "计划结束时间", example = "2026-06-30")
    private LocalDate planEndDate;

    @Schema(description = "改造户数", example = "100")
    private Integer reformHouseholds;

    @Schema(description = "建造面积（㎡）", example = "12000.50")
    private BigDecimal buildingArea;

    @Schema(description = "区域基准线碳排放强度", example = "0.35")
    private BigDecimal baselineIntensity;

    @Schema(description = "区域基准线碳排放量", example = "15000.00")
    private BigDecimal baselineEmission;

    @Schema(description = "电力消耗量", example = "10000")
    private BigDecimal electricityUsage;

    @Schema(description = "电力排放因子", example = "0.5810")
    private BigDecimal electricityFactor;

    @Schema(description = "燃气消耗量", example = "8000")
    private BigDecimal gasUsage;

    @Schema(description = "燃气排放因子", example = "2.1622")
    private BigDecimal gasFactor;

    @Schema(description = "项目减排量", example = "100")
    private BigDecimal reduction;

    @Schema(description = "实际排放量", example = "220")
    private BigDecimal actualEmission;
}
