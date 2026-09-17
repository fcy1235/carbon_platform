package cn.iocoder.power.module.system.controller.admin.area.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 行政区列表 Request VO")
@Data
public class AreaListReqVO {

    @Schema(description = "父级编号", example = "0")
    private Long parentId;

    @Schema(description = "行政级别", example = "0:省 1:市 2：区/县 3:镇/街道")
    private List<Integer> levels;

    @Schema(description = "名称", example = "北京市")
    private String name;

    @Schema(description = "拼音首字母", example = "B")
    private String pinyinPrefix;

}
