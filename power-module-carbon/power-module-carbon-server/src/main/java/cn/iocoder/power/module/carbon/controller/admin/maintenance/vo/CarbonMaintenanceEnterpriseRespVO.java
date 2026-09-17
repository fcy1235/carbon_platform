package cn.iocoder.power.module.carbon.controller.admin.maintenance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 维保企业 Response VO")
@Data
public class CarbonMaintenanceEnterpriseRespVO {

    @Schema(description = "企业编号", example = "1024")
    private Long id;

    @Schema(description = "省编码", example = "13")
    private Long provinceCode;

    @Schema(description = "市编码", example = "2222")
    private Long cityCode;

    @Schema(description = "县(市、区)编码", example = "1111")
    private Long countyCode;

    @Schema(description = "省名称", example = "河北省")
    private String provinceName;

    @Schema(description = "市名称", example = "河北省石家庄市")
    private String cityName;

    @Schema(description = "县(市、区)名称", example = "裕华区")
    private String countyName;

    @Schema(description = "企业名称", example = "XX维保公司")
    private String enterpriseName;

    @Schema(description = "统一社会信用代码", example = "91130100MA0XXXXX")
    private String unifiedSocialCreditCode;

    @Schema(description = "企业服务类型 1气代煤 2电代煤", example = "1")
    private String serviceType;

    @Schema(description = "服务状态 1服务中 2已停止", example = "1")
    private String serviceStatus;

    @Schema(description = "区域负责人姓名", example = "张三")
    private String regionalManagerName;

    @Schema(description = "区域负责人电话", example = "13800130001")
    private String regionalManagerPhone;

    @Schema(description = "维保人员总数量（由网点汇总计算）", example = "50")
    private Integer maintenanceStaffCount;

    @Schema(description = "覆盖村数（由网点汇总计算）", example = "30")
    private Integer coveredVillages;

    @Schema(description = "覆盖户数（由网点汇总计算）", example = "5000")
    private Integer coveredHouseholds;

    @Schema(description = "网点数量（由网点汇总计算）", example = "5")
    private Integer stationCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "维保网点列表")
    private List<CarbonMaintenanceStationRespVO> stationList;
}
