package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Schema(description = "管理后台 - 排放因子库分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonFactorLibPageReqVO extends PageParam {

    @Schema(description = "排放因子编码", example = "EF20260101001")
    private String factorCode;

    @Schema(description = "排放因子名称", example = "排放因子1")
    private String factorName;

    @Schema(description = "排放源", example = "排放源1")
    private String emissionSource;

    @Schema(description = "选中的ID列表，用于选中导出", example = "1,2,3")
    private List<Long> ids;
}
