package cn.iocoder.power.module.carbon.controller.admin.accounting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "接受")
public class CarbonAccountingUserInfoVO extends CarbonAccountingRespVO{

    @Schema(description = "用户姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String username;

    @Schema(description = "行政区划")
    private String division;
}
