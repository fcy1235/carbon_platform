package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Collection;

@Schema(description = "管理后台 - 排放源分页 Request VO")
@Data
public class CarbonEmissionSourceReqVO implements Serializable {

    @Schema(description = "排放源编号列表（选中导出时使用）")
    private Collection<Long> ids;

    @Schema(description = "排放源编码", example = "排放源1")
    private String sourceCode;

    @Schema(description = "排放源名称", example = "排放源1")
    private String sourceName;

    @Schema(description = "排放范围", example = "固定燃烧")
    private String scope;
}
