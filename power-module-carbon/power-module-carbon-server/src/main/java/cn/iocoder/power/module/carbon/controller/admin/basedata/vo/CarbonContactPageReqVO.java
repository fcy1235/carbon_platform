package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Schema(description = "管理后台 - 联系人分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonContactPageReqVO extends PageParam {

    @Schema(description = "姓名", example = "联系人1")
    private String name;

    @Schema(description = "联系电话", example = "13900130001")
    private String phone;

    @Schema(description = "所属公司", example = "公司1")
    private String company;

    @Schema(description = "选中的ID列表，用于选中导出", example = "1,2,3")
    private List<Long> ids;
}
