package cn.iocoder.power.module.carbon.controller.admin.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 设备数据新增 Request VO")
@Data
public class CarbonDeviceDataSaveReqVO {

    @Schema(description = "用户编码（关联 carbon_user_info.user_code）", example = "USER20260101001")
    private String userCode;

    @Schema(description = "用户名", example = "张三")
    private String username;

    @Schema(description = "设备编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "ASHP-000001")
    @NotBlank(message = "设备编码不能为空")
    private String deviceCode;

    @Schema(description = "设备类型（电表/空气源热泵）", example = "空气源热泵")
    private String deviceType;

    @Schema(description = "设备名称", example = "1号空气源热泵")
    private String deviceName;

    @Schema(description = "设备状态", example = "运行中")
    private String deviceStatus;

    @Schema(description = "同步时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "同步时间不能为空")
    private LocalDateTime reportTime;

    @Schema(description = "总用电量")
    private BigDecimal totalElectricity;

    @Schema(description = "电压")
    private BigDecimal voltage;

    @Schema(description = "电流")
    private BigDecimal electricCurrent;

    @Schema(description = "设定温度")
    private BigDecimal setTemperature;

    @Schema(description = "液管温度")
    private BigDecimal liquidPipeTemperature;

    @Schema(description = "模块温度")
    private BigDecimal moduleTemperature;

    @Schema(description = "经济器进温度")
    private BigDecimal economizerInTemperature;

    @Schema(description = "经济器出温度")
    private BigDecimal economizerOutTemperature;

    @Schema(description = "回水温度")
    private BigDecimal returnWaterTemperature;

    @Schema(description = "出水温度")
    private BigDecimal outletWaterTemperature;

    @Schema(description = "室外温度")
    private BigDecimal outdoorTemperature;

    @Schema(description = "盘管温度")
    private BigDecimal coilTemperature;

    @Schema(description = "排气温度")
    private BigDecimal exhaustTemperature;

    @Schema(description = "吸水温度")
    private BigDecimal suctionWaterTemperature;

    @Schema(description = "线控器室内温度")
    private BigDecimal wireControllerIndoorTemperature;

    @Schema(description = "内机环温度")
    private BigDecimal indoorAmbientTemperature;

    @Schema(description = "内盘管温度")
    private BigDecimal indoorCoilTemperature;

    @Schema(description = "备注")
    private String remark;
}
