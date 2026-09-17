package cn.iocoder.power.module.carbon.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 燃气数据报表（新表）查询 Request VO
 */
@Schema(description = "管理后台 - 燃气数据报表查询 Request VO")
@Data
public class CarbonGasUsageReportDataReqVO {

    @Schema(description = "采暖季，如 2025年", example = "2025年")
    private String heatingSeason;

    @Schema(description = "用户信息（户主姓名/身份证号，模糊查询）", example = "张三")
    private String keyword;

    @Schema(description = "燃气表具号（模糊查询）", example = "2435423")
    private String gasId;

}
