package cn.iocoder.power.module.carbon.controller.admin.report.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 农村电代煤用户用电量统计 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonElectricityUsageReportReqVO extends PageParam {

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "身份证号/姓名（模糊查询）")
    private String keyword;

    @Schema(description = "电表号")
    private String electricityId;
}
