package cn.iocoder.power.module.carbon.controller.admin.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备信息导入单行错误明细 VO
 */
@Schema(description = "管理后台 - 设备信息导入单行错误明细 VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarbonDeviceImportErrorVO {

    @Schema(description = "行号（Excel 中的实际行号）", example = "3")
    private Integer rowNum;

    @Schema(description = "错误信息", example = "未找到对应用户")
    private String message;
}
