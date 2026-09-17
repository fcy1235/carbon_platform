package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 排放源 Response VO")
@Data
public class CarbonEmissionSourceRespVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "排放源编码", example = "ES20260101001")
    private String sourceCode;

    @Schema(description = "排放源名称", example = "排放源1")
    private String sourceName;

    @Schema(description = "排放范围", example = "固定燃烧")
    private String scope;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
