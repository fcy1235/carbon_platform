package cn.iocoder.power.module.system.controller.admin.area.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Schema(description = "管理后台 - 行政区分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AreaPageReqVO extends PageParam {

    @Schema(description = "父级编号", example = "0")
    private Long parentId;

    @Schema(description = "行政级别", example = "1:省 2:市 3：区/县 4:镇/街道")
    private List<Integer> levels;

    @Schema(description = "名称", example = "北京市")
    private String name;

    @Schema(description = "拼音首字母", example = "B")
    private String pinyinPrefix;

}
