package cn.iocoder.power.module.system.controller.admin.area.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 行政区 Response VO")
@Data
public class AreaRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "110000")
    private Long id;

    @Schema(description = "父级编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Long parentId;

    @Schema(description = "行政级别", requiredMode = Schema.RequiredMode.REQUIRED, example = "1:省 2:市 3：区/县 4:镇/街道")
    private Integer level;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "北京市")
    private String name;

    @Schema(description = "拼音首字母", example = "B")
    private String pinyinPrefix;

    @Schema(description = "拼音", example = "beijing")
    private String pinyin;

    @Schema(description = "扩展ID", example = "110000")
    private String extId;

    @Schema(description = "扩展名称", example = "北京")
    private String extName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
