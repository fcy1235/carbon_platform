package cn.iocoder.power.module.iot.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Schema(description = "管理后台 - IoT 设备消息数量统计 Response VO")
@Data
@Accessors(chain = true)
public class IotStatisticsDeviceMessageSummaryByDateRespVO {

    @Schema(description = "时间轴", requiredMode = Schema.RequiredMode.REQUIRED, example = "202401")
    private String time;

    @Schema(description = "上行消息数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer upstreamCount;

    @Schema(description = "上行消息数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Integer downstreamCount;

}
