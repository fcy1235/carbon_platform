package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@Schema(description = "管理后台 - 气体信息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonGasInfoPageReqVO extends PageParam {

    @Schema(description = "气体信息编号列表（选中导出时使用）")
    private Collection<Long> ids;

    @Schema(description = "气体编码", example = "G001")
    private String gasCode;

    @Schema(description = "气体编码", example = "G001")
    private String gasName;

    @Schema(description = "分类", example = "Kyoto温室气体")
    private String category;
}
