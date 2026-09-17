package cn.iocoder.power.module.carbon.controller.admin.maintenance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 维保企业创建/修改 Request VO")
@Data
public class CarbonMaintenanceEnterpriseSaveReqVO {

    @Schema(description = "企业编号", example = "1024")
    private Long id;

    @Schema(description = "省编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "13")
    @NotNull(message = "省不能为空")
    private Long provinceCode;

    @Schema(description = "市编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "2222")
    private Long cityCode;

    @Schema(description = "县(市、区)编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1111")
    private Long countyCode;

    @Schema(description = "企业名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "XX维保公司")
    @NotBlank(message = "企业名称不能为空")
    private String enterpriseName;

    @Schema(description = "统一社会信用代码", requiredMode = Schema.RequiredMode.REQUIRED, example = "91130100MA0XXXXX")
    @NotBlank(message = "统一社会信用代码不能为空")
    private String unifiedSocialCreditCode;

    @Schema(description = "企业服务类型 1气代煤 2电代煤", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "企业服务类型不能为空")
    private String serviceType;

    @Schema(description = "服务状态 1服务中 2已停止", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "服务状态不能为空")
    private String serviceStatus;

    @Schema(description = "区域负责人姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "区域负责人姓名不能为空")
    private String regionalManagerName;

    @Schema(description = "区域负责人电话", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800130001")
    @NotBlank(message = "区域负责人电话不能为空")
    private String regionalManagerPhone;
}
