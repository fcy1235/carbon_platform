package cn.iocoder.power.module.iot.controller.admin.product.vo.product;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Schema(description = "管理后台 - IoT 产品分页 Request VO")
@Data
@Accessors(chain = true)
public class IotProductPageReqVO extends PageParam {

    @Schema(description = "产品名称", example = "李四")
    private String name;

    @Schema(description = "产品标识")
    private String productKey;

}