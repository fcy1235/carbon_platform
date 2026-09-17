package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 排放因子库关联气体 Response VO")
@Data
public class CarbonFactorLibGasRespVO {


    @Schema(description = "气体编号", example = "2048")
    private String gasCode;

    @Schema(description = "气体名称", example = "二氧化碳")
    private String gasName;

    @Schema(description = "全球变暖潜势值", example = "1")
    private BigDecimal gwp;

    @Schema(description = "类别", example = "1")
    private String category;
}
