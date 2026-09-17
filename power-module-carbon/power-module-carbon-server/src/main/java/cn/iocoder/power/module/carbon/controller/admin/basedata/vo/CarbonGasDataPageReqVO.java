package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@Schema(description = "管理后台 - 燃气数据分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonGasDataPageReqVO extends PageParam {

    @Schema(description = "燃气数据编号列表（选中导出时使用）")
    private Collection<Long> ids;

    @Schema(description = "用户姓名", example = "张三")
    private String username;

    @Schema(description = "用户信息编号",  example = "1024")
    private Long carbonUserInfoId;

    @Schema(description = "用户信息编号列表")
    private Collection<Long> carbonUserInfoIds;

    @Schema(description = "行政区划-省", example = "11")
    private Long provinceCode;

    @Schema(description = "行政区划-市", example = "1111")
    private Long cityCode;

    @Schema(description = "行政区划-区", example = "111111")
    private Long districtCode;

    @Schema(description = "燃气户号", example = "G20000")
    private String gasId;
}
