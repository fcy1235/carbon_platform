package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 气体信息 Response VO")
@Data
public class CarbonGasInfoRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "气体编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "GAS20260101001")
    private String gasCode;

    @Schema(description = "气体名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "二氧化碳(CO₂)")
    private String gasName;

    @Schema(description = "GWP值", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private BigDecimal gwp;

    @Schema(description = "分类", requiredMode = Schema.RequiredMode.REQUIRED, example = "Kyoto温室气体")
    private String category;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;
}
