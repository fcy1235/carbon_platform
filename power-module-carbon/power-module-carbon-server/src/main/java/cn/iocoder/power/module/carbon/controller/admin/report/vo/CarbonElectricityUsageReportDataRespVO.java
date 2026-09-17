package cn.iocoder.power.module.carbon.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 电力用电量报表 Response VO")
@Data
public class CarbonElectricityUsageReportDataRespVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "电表号（用户信息表带出）")
    private String electricityId;

    @Schema(description = "采暖季，如 2025年", example = "2025年")
    private String heatingSeason;

    @Schema(description = "采暖季起始表底数")
    private BigDecimal startReading;

    @Schema(description = "采暖季终止表底数")
    private BigDecimal endReading;

    @Schema(description = "采暖季合计用电量（kWh）")
    private BigDecimal totalUsage;

    @Schema(description = "备注")
    private String remark;

}
