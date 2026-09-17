package cn.iocoder.power.module.system.controller.admin.area.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 行政区创建/修改 Request VO")
@Data
public class AreaSaveReqVO {

    @Schema(description = "编号", example = "110000")
    private Long id;

    @Schema(description = "父级编号", example = "0")
    @NotNull(message = "父级编号不能为空")
    private Long parentId;

    @Schema(description = "行政级别", requiredMode = Schema.RequiredMode.REQUIRED, example = "1:省 2:市 3：区/县 4:镇/街道")
    @NotBlank(message = "行政级别不能为空")
    private String level;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "北京市")
    @NotBlank(message = "名称不能为空")
    private String name;

    @Schema(description = "拼音首字母", example = "B")
    private String pinyinPrefix;

    @Schema(description = "拼音", example = "beijing")
    private String pinyin;

    @Schema(description = "扩展ID", example = "110000")
    private String extId;

    @Schema(description = "扩展名称", example = "北京")
    private String extName;

}
