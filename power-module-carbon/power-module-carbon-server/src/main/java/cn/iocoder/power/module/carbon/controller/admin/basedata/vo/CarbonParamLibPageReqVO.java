package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 参数库分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonParamLibPageReqVO extends PageParam {

    @Schema(description = "参数名称", example = "参数1")
    private String paramName;

    @Schema(description = "分类", example = "通用参数")
    private String category;
}
