package cn.iocoder.power.module.carbon.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 燃气数据报表（新表）Response VO
 */
@Schema(description = "管理后台 - 燃气数据报表 Response VO")
@Data
public class CarbonGasUsageReportDataRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "户主姓名", example = "张三")
    private String username;

    @Schema(description = "用户编码（燃气用户编码）", example = "1213243")
    private String gasUserCode;

    @Schema(description = "身份证号", example = "130101199909120123")
    private String idCard;

    @Schema(description = "燃气表具号", example = "2435423")
    private String gasId;

    @Schema(description = "采暖季", example = "2025年")
    private String heatingSeason;

    @Schema(description = "采暖季起始表底数", example = "100")
    private BigDecimal startReading;

    @Schema(description = "采暖季终止表底数", example = "210")
    private BigDecimal endReading;

    @Schema(description = "采暖季合计用气量（m³）", example = "110")
    private BigDecimal totalUsage;

    @Schema(description = "备注")
    private String remark;

}
