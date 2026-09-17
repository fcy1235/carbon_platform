package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

@Schema(description = "管理后台 - 气体信息分页 Request VO")
@Data
public class CarbonGasInfoReqVO implements Serializable {

    @Schema(description = "气体信息编号列表（选中导出时使用）")
    private Collection<Long> ids;

    @Schema(description = "气体编码", example = "G001")
    private String gasCode;

    @Schema(description = "气体编码", example = "G001")
    private String gasName;

    @Schema(description = "分类", example = "Kyoto温室气体")
    private String category;

    private Set<String> gasCodes;
}
