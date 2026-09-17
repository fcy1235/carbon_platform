package cn.iocoder.power.module.carbon.controller.admin.maintenance.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Schema(description = "管理后台 - 维保企业分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonMaintenanceEnterprisePageReqVO extends PageParam {

    @Schema(description = "企业编号列表（选中导出时使用）")
    private List<Long> ids;

    @Schema(description = "省编码", example = "13")
    private Long provinceCode;

    @Schema(description = "市编码", example = "2222")
    private Long cityCode;

    @Schema(description = "县(市、区)编码", example = "1111")
    private Long countyCode;

    @Schema(description = "企业名称", example = "XX维保公司")
    private String enterpriseName;

    @Schema(description = "企业服务类型 1气代煤 2电代煤", example = "1")
    private String serviceType;

    @Schema(description = "服务状态 1服务中 2已停止", example = "1")
    private String serviceStatus;

    @Schema(description = "统一社会信用代码", example = "91340100MA1H5QYX5L")
    private String unifiedSocialCreditCode;
}
