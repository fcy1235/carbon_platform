package cn.iocoder.power.module.carbon.controller.admin.maintenance.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Schema(description = "管理后台 - 燃气安全员分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonGasSafetyOfficerPageReqVO extends PageParam {

    @Schema(description = "安全员编号列表（选中导出时使用）")
    private List<Long> ids;

    @Schema(description = "姓名", example = "张三")
    private String name;

    @Schema(description = "从业资格证编号", example = "ZY2024001")
    private String qualificationNo;

    @Schema(description = "所属燃气企业ID", example = "1")
    private Long enterpriseId;

    @Schema(description = "人员状态 1在岗 2离岗", example = "1")
    private String staffStatus;

    @Schema(description = "负责区域（区划代码模糊匹配，匹配area1~area5）", example = "130102")
    private String areaCode;
}
