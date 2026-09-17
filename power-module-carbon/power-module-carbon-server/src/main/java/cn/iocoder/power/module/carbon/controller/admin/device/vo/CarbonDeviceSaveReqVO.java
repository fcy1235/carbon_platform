package cn.iocoder.power.module.carbon.controller.admin.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static cn.iocoder.power.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 设备管理创建/修改 Request VO")
@Data
public class CarbonDeviceSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "设备编码（前端按「类型前缀-6位序号」规则预生成，如 GM-000001；为空时后端兜底生成）", example = "GM-000001")
    private String deviceCode;

    @Schema(description = "用户编号", example = "1")
    private Long carbonUserInfoId;

    @Schema(description = "设备名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "设备1")
    @NotBlank(message = "设备名称不能为空")
    private String deviceName;

    @Schema(description = "设备类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "电表")
    private String deviceType;

    @Schema(description = "生产厂家", example = "厂家1")
    private String manufacturer;

    @Schema(description = "生产日期", example = "2023-01-01")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate productionDate;

    @Schema(description = "使用寿命(年)", example = "10")
    private Integer serviceLife;

    @Schema(description = "安装日期", example = "2023-03-01")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate installDate;

    @Schema(description = "投运日期", example = "2023-03-15")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate operationDate;

    @Schema(description = "停运日期", example = "")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate stopDate;

    @Schema(description = "设备状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "正常")
    private String status;

    @Schema(description = "供气企业ID", example = "1")
    private Long gasEnterpriseId;

    @Schema(description = "壁挂炉安装年份", example = "2023")
    private String wallMountedStoveInstallYear;

    @Schema(description = "燃气表类型 1物联网燃气表（无线远传） 2IC卡燃气表 3传统机器膜式燃气表 4其他", example = "1")
    private String gasMeterType;

    @Schema(description = "安全装置配备情况 1报警器-切断阀 2自闭阀 3以上两者都有", example = "1")
    private String safetyDeviceStatus;

    @Schema(description = "设备品牌及型号", example = "威星V300")
    private String deviceBrandModel;

    @Schema(description = "备注", example = "")
    private String remark;
}
