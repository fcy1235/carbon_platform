package cn.iocoder.power.module.carbon.controller.admin.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 设备信息导入结果 VO
 */
@Schema(description = "管理后台 - 设备信息导入结果 Response VO")
@Data
public class CarbonDeviceImportResultVO {

    @Schema(description = "总行数（不含空行）", example = "10")
    private Integer totalCount;

    @Schema(description = "成功行数", example = "8")
    private Integer successCount;

    @Schema(description = "失败行数", example = "2")
    private Integer failCount;

    @Schema(description = "逐行错误明细")
    private List<CarbonDeviceImportErrorVO> errors;
}
