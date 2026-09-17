package cn.iocoder.power.module.carbon.controller.admin.subsidy.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Schema(description = "管理后台 - 补贴管理分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonSubsidyPageReqVO extends PageParam {

    @Schema(description = "用户姓名", example = "张三")
    private String username;

    @Schema(description = "地址", example = "测试地址")
    private String address;

    @Schema(description = "改造类型", example = "气代煤")
    private String reformType;

    @Schema(description = "省编码", example = "130000")
    private Long provinceCode;

    @Schema(description = "市编码", example = "130100")
    private Long cityCode;

    @Schema(description = "区编码", example = "130102")
    private Long districtCode;

    @Schema(description = "编号列表（选中导出时使用）")
    private List<Long> ids;
}
