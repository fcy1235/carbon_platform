package cn.iocoder.power.module.carbon.controller.admin.maintenance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 维保网点 Response VO")
@Data
public class CarbonMaintenanceStationRespVO {

    @Schema(description = "网点编号", example = "1024")
    private Long id;

    @Schema(description = "所属企业ID", example = "1")
    private Long enterpriseId;

    @Schema(description = "所属企业名称", example = "XX维保公司")
    private String enterpriseName;

    @Schema(description = "网点名称", example = "裕华区维保网点")
    private String stationName;

    @Schema(description = "网点服务类型 1气代煤 2电代煤", example = "1")
    private String serviceType;

    @Schema(description = "网点经营状态 1未经营 2经营中 3已关闭", example = "2")
    private String businessStatus;

    @Schema(description = "维保人员数量", example = "10")
    private Integer maintenanceStaffCount;

    @Schema(description = "覆盖村数", example = "5")
    private Integer coveredVillages;

    @Schema(description = "覆盖户数", example = "1000")
    private Integer coveredHouseholds;

    @Schema(description = "服务范围（行政区划编码，逗号分隔）", example = "130102001,130102002")
    private String serviceScope;

    @Schema(description = "服务范围名称列表")
    private List<String> serviceScopeNames;

    @Schema(description = "网点负责人", example = "李四")
    private String managerName;

    @Schema(description = "网点负责人联系电话", example = "13900139001")
    private String managerPhone;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
