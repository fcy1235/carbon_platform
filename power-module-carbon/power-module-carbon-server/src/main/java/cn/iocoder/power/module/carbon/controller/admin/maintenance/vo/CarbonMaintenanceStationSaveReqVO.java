package cn.iocoder.power.module.carbon.controller.admin.maintenance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 维保网点创建/修改 Request VO")
@Data
public class CarbonMaintenanceStationSaveReqVO {

    @Schema(description = "网点编号", example = "1024")
    private Long id;

    @Schema(description = "所属企业ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "所属企业不能为空")
    private Long enterpriseId;

    @Schema(description = "网点名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "裕华区维保网点")
    @NotBlank(message = "网点名称不能为空")
    private String stationName;

    @Schema(description = "网点服务类型 1气代煤 2电代煤", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "网点服务类型不能为空")
    private String serviceType;

    @Schema(description = "网点经营状态 1未经营 2经营中 3已关闭", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotBlank(message = "网点经营状态不能为空")
    private String businessStatus;

    @Schema(description = "维保人员数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "维保人员数量不能为空")
    private Integer maintenanceStaffCount;

    @Schema(description = "覆盖村数", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    @NotNull(message = "覆盖村数不能为空")
    private Integer coveredVillages;

    @Schema(description = "覆盖户数", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000")
    @NotNull(message = "覆盖户数不能为空")
    private Integer coveredHouseholds;

    @Schema(description = "服务范围（行政区划编码，逗号分隔）", example = "130102001,130102002")
    private String serviceScope;

    @Schema(description = "网点负责人", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotBlank(message = "网点负责人不能为空")
    private String managerName;

    @Schema(description = "网点负责人联系电话", requiredMode = Schema.RequiredMode.REQUIRED, example = "13900139001")
    @NotBlank(message = "网点负责人联系电话不能为空")
    private String managerPhone;
}
