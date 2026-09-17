package cn.iocoder.power.module.iot.controller.admin.rule.vo.data.sink;

import cn.iocoder.power.framework.common.enums.CommonStatusEnum;
import cn.iocoder.power.framework.common.validation.InEnum;
import cn.iocoder.power.module.iot.dal.dataobject.rule.config.IotAbstractDataSinkConfig;
import cn.iocoder.power.module.iot.enums.rule.IotDataSinkTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Schema(description = "管理后台 - IoT 数据流转目的新增/修改 Request VO")
@Data
@Accessors(chain = true)
public class IotDataSinkSaveReqVO {

    @Schema(description = "数据目的编号", example = "18564")
    private Long id;

    @Schema(description = "数据目的名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "数据目的名称不能为空")
    private String name;

    @Schema(description = "数据目的描述", example = "随便")
    private String description;

    @Schema(description = "数据目的状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "数据目的状态不能为空")
    @InEnum(CommonStatusEnum.class)
    private Integer status;

    @Schema(description = "数据目的类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "数据目的类型不能为空")
    @InEnum(IotDataSinkTypeEnum.class)
    private Integer type;

    @Schema(description = "数据目的配置")
    @NotNull(message = "数据目的配置不能为空")
    private IotAbstractDataSinkConfig config;

}