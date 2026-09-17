package cn.iocoder.power.module.carbon.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 农村气代煤用户用气量统计 Response VO")
@Data
public class CarbonGasUsageReportRespVO {

    @Schema(description = "户主姓名")
    private String username;

    @Schema(description = "燃气用户编码")
    private String gasUserCode;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "燃气表具号")
    private String gasId;

    @Schema(description = "采暖季起始时间 表底数")
    private String startReading;

    @Schema(description = "采暖季结束时间 表底数")
    private String endReading;

    @Schema(description = "采暖季合计用气量（m³）")
    private BigDecimal totalUsage;

    @Schema(description = "备注")
    private String remark;

    // 以下为内部字段，用于计算
    private LocalDateTime startTime;
    private BigDecimal startTotal;
    private LocalDateTime endTime;
    private BigDecimal endTotal;
}
