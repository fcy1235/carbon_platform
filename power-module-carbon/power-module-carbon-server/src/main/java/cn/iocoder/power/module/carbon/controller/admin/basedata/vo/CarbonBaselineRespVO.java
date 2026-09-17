package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 基准线碳排放强度 Response VO")
@Data
public class CarbonBaselineRespVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "行政区划-省", example = "11")
    private Long provinceCode;

    @Schema(description = "行政区划-市", example = "1111")
    private Long cityCode;

    @Schema(description = "行政区划-区", example = "111111")
    private Long districtCode;

    @ExcelProperty("行政区划")
    private String division;

    @Schema(description = "改造类型", example = "气代煤")
    private String reformType;

    @Schema(description = "所属气候子区", example = "1")
    private String climateZone;

    @Schema(description = "排放强度", requiredMode = Schema.RequiredMode.REQUIRED, example = "0.45")
    private BigDecimal intensity;

    @Schema(description = "单位", requiredMode = Schema.RequiredMode.REQUIRED, example = "tCO₂/㎡")
    private String unit;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;
}
