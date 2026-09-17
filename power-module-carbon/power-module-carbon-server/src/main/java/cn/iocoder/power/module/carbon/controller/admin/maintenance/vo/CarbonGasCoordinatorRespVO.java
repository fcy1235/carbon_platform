package cn.iocoder.power.module.carbon.controller.admin.maintenance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 农村气代煤协管员 Response VO")
@Data
public class CarbonGasCoordinatorRespVO {

    @Schema(description = "协管员编号", example = "1024")
    private Long id;

    @Schema(description = "姓名", example = "张三")
    private String name;

    @Schema(description = "性别", example = "1")
    private String gender;

    @Schema(description = "身份证号", example = "130102200001010001")
    private String idCardNo;

    @Schema(description = "学历", example = "大专")
    private String education;

    @Schema(description = "联系电话", example = "13800138001")
    private String phone;

    @Schema(description = "是否为村两委干部成员 1是 2否", example = "1")
    private String isVillageCommitteeMember;

    @Schema(description = "负责村（社区）区划代码（省,市,区,街道,村 逗号分隔）", example = "13,1301,130102,130102001,130102001001")
    private String area;

    @Schema(description = "负责村（社区）名称（省/市/区/街道/村拼接）", example = "河北省/石家庄市/长安区/XX街道/XX村")
    private String areaName;

    @Schema(description = "入职培训企业类型", example = "1")
    private String trainingEnterpriseType;

    @Schema(description = "维保企业ID", example = "1")
    private Long enterpriseId;

    @Schema(description = "所属维保企业名称", example = "XX维保公司")
    private String enterpriseName;

    @Schema(description = "入职专业操作技能培训成绩", example = "85.5")
    private BigDecimal trainingScore;

    @Schema(description = "人员状态 1在岗 2离岗", example = "1")
    private String staffStatus;

    @Schema(description = "到岗日期", example = "2024-01-01")
    private LocalDate onDutyDate;

    @Schema(description = "离岗日期", example = "2024-12-31")
    private LocalDate offDutyDate;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
