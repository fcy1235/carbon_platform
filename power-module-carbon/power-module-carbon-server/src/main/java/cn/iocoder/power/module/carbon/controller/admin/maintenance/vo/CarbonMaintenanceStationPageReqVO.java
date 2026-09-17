package cn.iocoder.power.module.carbon.controller.admin.maintenance.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Schema(description = "管理后台 - 维保网点分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonMaintenanceStationPageReqVO extends PageParam {

    @Schema(description = "网点编号列表（选中导出时使用）")
    private List<Long> ids;

    @Schema(description = "所属企业ID", example = "1")
    private Long enterpriseId;

    @Schema(description = "网点名称", example = "裕华区维保网点")
    private String stationName;

    @Schema(description = "网点服务类型 1气代煤 2电代煤", example = "1")
    private String serviceType;

    @Schema(description = "网点经营状态 1未经营 2经营中 3已关闭", example = "2")
    private String businessStatus;
}
