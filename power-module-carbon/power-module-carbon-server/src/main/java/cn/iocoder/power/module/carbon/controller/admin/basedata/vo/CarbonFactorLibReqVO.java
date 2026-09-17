package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "管理后台 - 排放因子库分页 Request VO")
@Data
public class CarbonFactorLibReqVO implements Serializable {

    @Schema(description = "排放因子编码", example = "EF20260101001")
    private String factorCode;

    @Schema(description = "排放因子名称", example = "排放因子1")
    private String factorName;

    @Schema(description = "排放源", example = "1111")
    private String emissionSource;

}
