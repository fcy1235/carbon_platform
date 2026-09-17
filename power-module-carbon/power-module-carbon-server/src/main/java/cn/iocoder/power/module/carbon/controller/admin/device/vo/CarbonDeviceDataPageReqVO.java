package cn.iocoder.power.module.carbon.controller.admin.device.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Collection;

import static cn.iocoder.power.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 设备数据分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonDeviceDataPageReqVO extends PageParam {

    @Schema(description = "设备数据编号列表（选中导出时使用）")
    private Collection<Long> ids;

    @Schema(description = "用户编码", example = "USER20260101001")
    private String userCode;

    @Schema(description = "用户姓名", example = "张三")
    private String username;

    @Schema(description = "设备编码（模糊）", example = "ASHP-000001")
    private String deviceCode;

    @Schema(description = "设备类型", example = "空气源热泵")
    private String deviceType;

    @Schema(description = "设备名称", example = "1号空气源热泵")
    private String deviceName;

    @Schema(description = "设备状态", example = "运行中")
    private String deviceStatus;

    @Schema(description = "同步时间开始")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime reportTimeStart;

    @Schema(description = "同步时间结束")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime reportTimeEnd;
}
