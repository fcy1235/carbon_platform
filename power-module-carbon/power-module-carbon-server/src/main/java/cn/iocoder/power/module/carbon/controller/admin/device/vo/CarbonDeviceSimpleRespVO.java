package cn.iocoder.power.module.carbon.controller.admin.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 设备精简信息 Response VO")
@Data
public class CarbonDeviceSimpleRespVO {

    @Schema(description = "设备编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "设备编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "EQ20260101001")
    private String deviceCode;

    @Schema(description = "设备名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "设备1")
    private String deviceName;

    @Schema(description = "设备类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "电表")
    private String deviceType;

    @Schema(description = "用户信息编号", example = "1024")
    private Long carbonUserInfoId;

    @Schema(description = "生产厂家", example = "厂家1")
    private String manufacturer;

    @Schema(description = "设备品牌及型号", example = "威星V300")
    private String deviceBrandModel;

    @Schema(description = "设备状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "正常")
    private String status;
}
