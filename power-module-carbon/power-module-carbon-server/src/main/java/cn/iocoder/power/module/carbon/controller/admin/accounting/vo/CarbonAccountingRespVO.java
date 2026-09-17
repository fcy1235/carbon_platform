package cn.iocoder.power.module.carbon.controller.admin.accounting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 碳排放核算 Response VO")
@Data
public class CarbonAccountingRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "用户信息编号", example = "1024")
    private Long carbonUserInfoId;

    @Schema(description = "用户姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String username;

    @Schema(description = "行政区划", requiredMode = Schema.RequiredMode.REQUIRED, example = "河北省石家庄市裕华区裕强街道")
    private String division;

    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "测试地址1号")
    private String address;

    @Schema(description = "改造类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "气代煤")
    private String reformType;

    @Schema(description = "用户编码", example = "USER1786002347437")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String userCode;

    @Schema(description = "数据来源（0=接口数据，1=导入数据）", example = "1")
//    @com.fasterxml.jackson.annotation.JsonIgnore
    private String dataSource;

    @Schema(description = "活动名称", example = "电力消耗")
    private String activityName;

    @Schema(description = "活动名称", example = "1,2")
    private String activityCodes;


    @Schema(description = "核算周期开始日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-01-01")
    private LocalDateTime accountingPeriodStart;

    @Schema(description = "核算周期结束日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-01-31")
    private LocalDateTime accountingPeriodEnd;

    @Schema(description = "供暖面积", requiredMode = Schema.RequiredMode.REQUIRED, example = "90")
    private BigDecimal heatingArea;

    @Schema(description = "基准线排放强度", requiredMode = Schema.RequiredMode.REQUIRED, example = "0.45")
    private BigDecimal baselineIntensity;

    @Schema(description = "基准线排放量", requiredMode = Schema.RequiredMode.REQUIRED, example = "40.5")
    private BigDecimal baselineEmission;

    @Schema(description = "实际排放量", requiredMode = Schema.RequiredMode.REQUIRED, example = "220")
    private BigDecimal actualEmission;

    @Schema(description = "减排量", requiredMode = Schema.RequiredMode.REQUIRED, example = "-179.5")
    private BigDecimal reduction;

    @Schema(description = "核算周期内用电量（kWh）", example = "1200")
    private BigDecimal electricityUsage;

    @Schema(description = "核算周期内用气量（m³）", example = "300")
    private BigDecimal gasUsage;

    @Schema(description = "活动数据列表")
    private List<CarbonAccountingActivityRespVO> activities;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    // 临时字段，用于行政区划名称填充
    @Schema(description = "省编码", hidden = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Long provinceCode;
    @Schema(description = "市编码", hidden = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Long cityCode;
    @Schema(description = "区编码", hidden = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Long districtCode;


}
