package cn.iocoder.power.module.carbon.controller.admin.report.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 电力用电量报表 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonElectricityUsageReportDataPageReqVO extends PageParam {

    @Schema(description = "采暖季，如 2025年", example = "2025年")
    private String heatingSeason;

    @Schema(description = "身份证号/姓名（模糊查询）")
    private String keyword;

    @Schema(description = "电表号")
    private String electricityId;

}
