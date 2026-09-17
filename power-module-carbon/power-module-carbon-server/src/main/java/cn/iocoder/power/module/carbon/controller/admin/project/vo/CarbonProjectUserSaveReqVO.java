package cn.iocoder.power.module.carbon.controller.admin.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 项目关联用户 Request VO")
@Data
public class CarbonProjectUserSaveReqVO {

    @Schema(description = "用户信息编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long carbonUserInfoId;

    @Schema(description = "碳核算编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long accountingId;

}
