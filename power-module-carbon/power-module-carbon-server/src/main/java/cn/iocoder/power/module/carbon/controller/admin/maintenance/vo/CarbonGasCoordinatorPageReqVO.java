package cn.iocoder.power.module.carbon.controller.admin.maintenance.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Schema(description = "管理后台 - 农村气代煤协管员分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonGasCoordinatorPageReqVO extends PageParam {

    @Schema(description = "协管员编号列表（选中导出时使用）")
    private List<Long> ids;

    @Schema(description = "姓名", example = "张三")
    private String name;

    @Schema(description = "联系电话", example = "13800138001")
    private String phone;

    @Schema(description = "维保企业ID", example = "1")
    private Long enterpriseId;

    @Schema(description = "人员状态 1在岗 2离岗", example = "1")
    private String staffStatus;

    @Schema(description = "是否为村两委干部成员 1是 2否", example = "1")
    private String isVillageCommitteeMember;

    @Schema(description = "负责区域（区划代码模糊匹配）", example = "130102")
    private String areaCode;
}
