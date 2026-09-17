package cn.iocoder.power.module.carbon.controller.admin.ledger.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 当前用户行政区地址信息 Response VO")
@Data
public class LoginUserAddressRespVO {

    @Schema(description = "省级编码", example = "13")
    private Long provinceCode;

    @Schema(description = "市级编码", example = "1301")
    private Long cityCode;

    @Schema(description = "区县编码", example = "130108")
    private Long districtCode;

}
