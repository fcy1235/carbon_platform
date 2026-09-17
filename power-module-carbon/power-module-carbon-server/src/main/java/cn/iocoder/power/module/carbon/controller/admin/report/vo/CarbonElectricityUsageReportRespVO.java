package cn.iocoder.power.module.carbon.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 农村电代煤用户用电量统计 Response VO")
@Data
public class CarbonElectricityUsageReportRespVO {

    @Schema(description = "户主姓名")
    private String username;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "电表号")
    private String electricityId;

    @Schema(description = "采暖季起始时间 表底数")
    private String startReading;

    @Schema(description = "采暖季结束时间 表底数")
    private String endReading;

    @Schema(description = "采暖季合计用电量（kWh）")
    private BigDecimal totalUsage;

    @Schema(description = "备注")
    private String remark;

    // 以下为内部字段，用于计算
    private LocalDateTime startTime;
    private BigDecimal startTotal;
    private LocalDateTime endTime;
    private BigDecimal endTotal;
}
