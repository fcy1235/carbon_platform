package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 排放因子库关联气体创建/修改 Request VO")
@Data
public class CarbonFactorLibGasSaveReqVO implements Serializable {

    @Schema(description = "气体编号", example = "GA001")
    private String gasCode;

}
