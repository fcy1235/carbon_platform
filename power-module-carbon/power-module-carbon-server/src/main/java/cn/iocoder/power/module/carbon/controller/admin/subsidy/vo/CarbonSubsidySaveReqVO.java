package cn.iocoder.power.module.carbon.controller.admin.subsidy.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 补贴管理创建/修改 Request VO")
@Data
public class CarbonSubsidySaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "用户信息ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "用户信息ID不能为空")
    private Long userInfoId;

    @Schema(description = "补贴金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "500")
    @NotNull(message = "补贴金额不能为空")
    private BigDecimal subsidyAmount;
    
}
